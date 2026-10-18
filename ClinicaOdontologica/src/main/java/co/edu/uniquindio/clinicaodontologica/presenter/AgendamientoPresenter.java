package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.BloqueHorario;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.service.AgendamientoService;
import co.edu.uniquindio.clinicaodontologica.service.EspecialistaService;
import co.edu.uniquindio.clinicaodontologica.service.PacienteService;
import co.edu.uniquindio.clinicaodontologica.view.contract.IAgendamientoView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
  * Presenter MVP para el cumplimiento de:
  */
public class AgendamientoPresenter {

    /**
      * Contrato de la vista pasiva de agendamiento.
      */
    private final IAgendamientoView view;

    /**
      * Servicio de dominio de agendamiento de citas.
      */
    private final AgendamientoService agendamientoService;

    /**
      * Servicio de especialistas para listar médicos y consultar sus agendas.
      */
    private final EspecialistaService especialistaService;

    /**
      * Servicio de pacientes para cargar el listado de pacientes registrados.
      */
    private final PacienteService pacienteService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view                Vista pasiva que implementa IAgendamientoView
      * @param agendamientoService Servicio de citas
      * @param especialistaService Servicio de doctores
      * @param pacienteService     Servicio de pacientes
      */
    public AgendamientoPresenter(IAgendamientoView view,
                                 AgendamientoService agendamientoService,
                                 EspecialistaService especialistaService,
                                 PacienteService pacienteService) {
        this.view = view;
        this.agendamientoService = agendamientoService;
        this.especialistaService = especialistaService;
        this.pacienteService = pacienteService;
    }

    /**
      * Inicializa los componentes de la vista cargando especialistas y pacientes registrados.
      */
    public void inicializarVista() {
        view.mostrarEspecialistas(especialistaService.listarEspecialistas());
        view.mostrarPacientesRegistrados(pacienteService.listarPacientes());
    }

    /**
      * Maneja la selección de un especialista en la interfaz, cargando sus fechas y citas asignadas.
      *
      * @param especialista Especialista seleccionado
      */
    public void seleccionarEspecialista(Especialista especialista) {
        if (especialista == null) {
            return;
        }
        List<LocalDate> fechas = especialista.getAgenda().obtenerFechasConfiguradas();
        view.mostrarFechasAgenda(fechas);
        view.mostrarCitasEspecialista(agendamientoService.listarCitasPorEspecialista(especialista));
        if (!fechas.isEmpty()) {
            LocalDate fechaSeleccionada = fechas.get(0);
            view.seleccionarFechaEnCombo(fechaSeleccionada);
            seleccionarFecha(especialista, fechaSeleccionada);
        }
    }

    /**
      * Maneja la selección de una fecha en el calendario, actualizando la matriz de bloques horarios.
      *
      * @param especialista Especialista seleccionado
      * @param fecha        Fecha consultada
      */
    public void seleccionarFecha(Especialista especialista, LocalDate fecha) {
        if (especialista == null || fecha == null) {
            return;
        }
        view.seleccionarFechaEnCombo(fecha);
        List<BloqueHorario> bloques = agendamientoService.consultarBloquesPorFecha(especialista, fecha);
        view.mostrarCalendarioBloques(especialista, fecha, bloques);
    }

    /**
      * Procesa la solicitud para agendar una cita médica.
      *
      * @param cedulaPaciente Cédula del paciente
      * @param especialista   Doctor seleccionado
      * @param fecha          Fecha programada
      * @param hora           Hora programada
      * @param motivo         Motivo de la consulta
      */
    public void agendarCita(String cedulaPaciente, Especialista especialista, LocalDate fecha, LocalTime hora, String motivo) {
        if (especialista == null) {
            view.mostrarError("Selección requerida", "Debe seleccionar un especialista.");
            return;
        }
        try {
            Cita cita = agendamientoService.agendarCita(cedulaPaciente, especialista.getIdDoctor(), fecha, hora, motivo);
            view.limpiarFormularioReserva();
            seleccionarFecha(especialista, fecha);
            view.mostrarCitasEspecialista(agendamientoService.listarCitasPorEspecialista(especialista));
            view.mostrarExito("Cita Agendada (F-02)",
                    "Se reservó exitosamente la cita " + cita.getIdCita() + " para "
                            + cita.getPaciente().getNombreCompleto() + " el " + fecha + " a las " + hora + ".");
        } catch (RuntimeException e) {
            view.mostrarError("Alerta de Bloqueo de Agenda (RN-001 / RN-002)", e.getMessage());
        }
    }

    /**
      * Cancela una cita seleccionada y refresca la agenda médica para mostrar el bloque liberado en verde.
      *
      * @param cita Cita a cancelar
      */
    public void cancelarCita(Cita cita) {
        if (cita == null) {
            view.mostrarError("Selección requerida", "Debe seleccionar una cita para cancelar.");
            return;
        }
        try {
            Especialista especialista = cita.getEspecialista();
            LocalDate fecha = cita.getFecha();
            agendamientoService.cancelarCita(cita.getIdCita());
            seleccionarFecha(especialista, fecha);
            view.mostrarCitasEspecialista(agendamientoService.listarCitasPorEspecialista(especialista));
            view.mostrarExito("Cita Cancelada", "La cita fue cancelada y el bloque " + fecha + " " + cita.getHora() + " quedó libre (verde).");
        } catch (RuntimeException e) {
            view.mostrarError("No se pudo cancelar", e.getMessage());
        }
    }

    /**
      * Reprograma una cita a una nueva fecha y hora.
      *
      * @param cita       Cita a reprogramar
      * @param nuevaFecha Nueva fecha
      * @param nuevaHora  Nueva hora
      */
    public void reprogramarCita(Cita cita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        if (cita == null) {
            view.mostrarError("Selección requerida", "Debe seleccionar una cita para reprogramar.");
            return;
        }
        try {
            Cita actualizada = agendamientoService.reprogramarCita(cita.getIdCita(), nuevaFecha, nuevaHora);
            seleccionarFecha(actualizada.getEspecialista(), nuevaFecha);
            view.mostrarCitasEspecialista(agendamientoService.listarCitasPorEspecialista(actualizada.getEspecialista()));
            view.mostrarExito("Cita Reprogramada",
                    "La cita " + actualizada.getIdCita() + " fue reprogramada al " + nuevaFecha + " a las " + nuevaHora + ".");
        } catch (RuntimeException e) {
            view.mostrarError("Error al Reprogramar", e.getMessage());
        }
    }
}
