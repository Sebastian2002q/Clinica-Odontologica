package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import co.edu.uniquindio.clinicaodontologica.repository.CitaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.EspecialistaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.HistoriaClinicaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de verificación para la funcionalidad de agendamiento y ciclo de vida de citas.
 */
class AgendamientoServiceTest {

    private CitaRepository citaRepository;
    private PacienteRepository pacienteRepository;
    private EspecialistaRepository especialistaRepository;
    private AgendamientoService agendamientoService;

    private Paciente pacienteTest;
    private Especialista especialistaTest;
    private LocalDate fechaTest;
    private LocalTime horaTest;

    @BeforeEach
    void setUp() {
        citaRepository = new CitaRepository();
        pacienteRepository = new PacienteRepository();
        especialistaRepository = new EspecialistaRepository();
        HistoriaClinicaRepository historiaClinicaRepository = new HistoriaClinicaRepository();

        PacienteService pacienteService = new PacienteService(pacienteRepository, historiaClinicaRepository);
        EspecialistaService especialistaService = new EspecialistaService(especialistaRepository, citaRepository);
        agendamientoService = new AgendamientoService(citaRepository, pacienteRepository, especialistaRepository);

        fechaTest = LocalDate.now().plusDays(1);
        horaTest = LocalTime.of(8, 0);

        pacienteTest = pacienteService.registrarPaciente(
                "1094900001", "Carlos Mendoza", "3151234567", "carlos@correo.com"
        );

        especialistaTest = especialistaService.registrarEspecialista(
                "Dra. Laura Gómez",
                "Ortodoncia",
                "TP-98765",
                "Consultorio 201",
                Map.of(fechaTest, new ArrayList<>(List.of(horaTest, LocalTime.of(8, 30))))
        );
    }

    @Test
    @DisplayName("Debe agendar cita exitosamente y marcar el bloque como ocupado en la agenda")
    void testAgendarCitaExitosamente() {
        Cita cita = agendamientoService.agendarCita(
                pacienteTest.getCedula(),
                especialistaTest.getIdDoctor(),
                fechaTest,
                horaTest,
                "Control mensual de ortodoncia"
        );

        assertNotNull(cita);
        assertEquals(EstadoCita.AGENDADA, cita.getEstado());
        assertEquals(pacienteTest, cita.getPaciente());
        assertEquals(especialistaTest, cita.getEspecialista());

        // Verificar que el bloque pasó a ocupado (rojo / false)
        assertFalse(especialistaTest.getAgenda().verificarDisponibilidad(fechaTest, horaTest));

        // Verificar que está en el repositorio
        Optional<Cita> citaGuardada = citaRepository.buscarPorId(cita.getIdCita());
        assertTrue(citaGuardada.isPresent());
        assertEquals(EstadoCita.AGENDADA, citaGuardada.get().getEstado());
    }

    @Test
    @DisplayName("H-01: Al cancelar cita debe liberar horario y conservar trazabilidad histórica con estado CANCELADA")
    void testCancelarCitaPreservaTrazabilidadHistoricaYLiberaHorario() {
        // 1. Agendar cita inicial
        Cita cita = agendamientoService.agendarCita(
                pacienteTest.getCedula(),
                especialistaTest.getIdDoctor(),
                fechaTest,
                horaTest,
                "Valoración de brackets"
        );
        String idCita = cita.getIdCita();

        // Verificar que está activa
        assertEquals(1, citaRepository.listarActivas().size());

        // 2. Ejecutar cancelación
        agendamientoService.cancelarCita(idCita);

        // A. Comprobación de liberación de agenda: el bloque vuelve a estar libre (verde / true)
        assertTrue(especialistaTest.getAgenda().verificarDisponibilidad(fechaTest, horaTest),
                "El bloque horario debe quedar liberado en la agenda del especialista tras cancelar.");

        // B. Comprobación de exclusión activa: la cita no debe aparecer en las activas
        assertTrue(citaRepository.listarActivas().isEmpty(),
                "La lista de citas activas no debe contener la cita cancelada.");

        // C. Comprobación de trazabilidad histórica (H-01):
        Optional<Cita> citaCancelada = citaRepository.buscarPorId(idCita);
        assertTrue(citaCancelada.isPresent(),
                "H-01: La cita no debe ser eliminada físicamente; debe persistirse en el repositorio para auditoría.");

        assertEquals(EstadoCita.CANCELADA, citaCancelada.get().getEstado(),
                "H-01: El estado de la cita debe ser CANCELADA.");

        assertTrue(citaRepository.listarTodas().stream().anyMatch(c -> c.getIdCita().equals(idCita)),
                "H-01: El historial general (listarTodas) debe incluir la cita cancelada.");
    }
}
