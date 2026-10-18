package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.BloqueHorario;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import co.edu.uniquindio.clinicaodontologica.repository.CitaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.EspecialistaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.PacienteRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
  * Servicio encargado de gestionar el agendamiento, cancelación y reprogramación de citas odontológicas.
  */
public class AgendamientoService {

    /**
      * Repositorio para la persistencia y consulta de citas.
      */
    private final CitaRepository citaRepository;

    /**
      * Repositorio para validar la existencia previa del paciente.
      */
    private final PacienteRepository pacienteRepository;

    /**
      * Repositorio para consultar la disponibilidad del especialista.
      */
    private final EspecialistaRepository especialistaRepository;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param citaRepository         Repositorio de citas
      * @param pacienteRepository     Repositorio de pacientes
      * @param especialistaRepository Repositorio de especialistas
      */
    public AgendamientoService(CitaRepository citaRepository,
                               PacienteRepository pacienteRepository,
                               EspecialistaRepository especialistaRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.especialistaRepository = especialistaRepository;
    }

    /**
      * Agenda una nueva cita odontológica validando los datos requeridos.
      *
      * @param cedulaPaciente Cédula del paciente que solicita la cita
      * @param idDoctor       Identificador del doctor especialista
      * @param fecha          Fecha programada para la cita
      * @param hora           Hora programada para la cita
      * @param motivo         Motivo o síntoma de la consulta
      * @return Cita agendada formalmente
      * @throws IllegalArgumentException si falta algún dato obligatorio o la fecha es pasada
      * @throws IllegalStateException    si el paciente no está registrado, el doctor no está libre o hay cruce
      */
    public Cita agendarCita(String cedulaPaciente, String idDoctor, LocalDate fecha, LocalTime hora, String motivo) {
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas (" + fecha + "). Debe seleccionar una fecha a partir de hoy (" + LocalDate.now() + ").");
        }
        if (cedulaPaciente == null || cedulaPaciente.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar la cédula del paciente.");
        }
        if (idDoctor == null || idDoctor.isBlank() || hora == null) {
            throw new IllegalArgumentException("Debe seleccionar especialista, fecha y hora.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el motivo de la consulta.");
        }

        // Todo paciente debe estar registrado previamente en el sistema antes de agendar
        Paciente paciente = pacienteRepository.buscarPorCedula(cedulaPaciente.trim())
                .orElseThrow(() -> new IllegalStateException(
                        "RN-001: El paciente con cédula " + cedulaPaciente.trim()
                                + " no está registrado en el sistema. Debe registrarlo primero en el módulo de Pacientes."));

        Especialista especialista = especialistaRepository.buscarPorId(idDoctor)
                .orElseThrow(() -> new IllegalArgumentException("El especialista seleccionado no existe."));

        // Bloquear si el doctor ya tiene cita o el bloque no está disponible
        if (citaRepository.buscarActivaPorEspecialistaFechaHora(especialista, fecha, hora).isPresent()
                || !especialista.getAgenda().verificarDisponibilidad(fecha, hora)) {
            throw new IllegalStateException(
                    "RN-002 Alerta de Bloqueo de Agenda: El especialista " + especialista.getNombre()
                            + " ya tiene el bloque ocupado o no está disponible el " + fecha + " a las " + hora + ".");
        }

        // Evitar cruce de horario del mismo paciente con otro especialista a la misma hora
        if (citaRepository.existeCitaActivaPacienteEnHorario(paciente.getCedula(), fecha, hora)) {
            throw new IllegalStateException(
                    "Cruce de horario: El paciente " + paciente.getNombreCompleto()
                            + " ya tiene otra cita programada el " + fecha + " a las " + hora + ".");
        }

        // Operación atómica: ocupar bloque en la agenda del especialista y guardar la cita
        especialista.getAgenda().ocuparBloque(fecha, hora);
        String idCita = citaRepository.generarSiguienteId();
        Cita nuevaCita = new Cita(idCita, paciente, especialista, motivo.trim(), fecha, hora);
        citaRepository.guardar(nuevaCita);
        return nuevaCita;
    }

    /**
      * Cancela una cita médica existente liberando automáticamente el bloque horario en la agenda.
      *
      * @param idCita Identificador de la cita médica
      * @throws IllegalArgumentException si la cita no existe
      * @throws IllegalStateException    si la cita ya fue atendida o pagada
      */
    public void cancelarCita(String idCita) {
        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita " + idCita));
        if (cita.getEstado() == EstadoCita.PAGADA || cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new IllegalStateException("No se puede cancelar una cita que ya fue atendida o pagada.");
        }
        cita.cancelar();
        cita.getEspecialista().getAgenda().liberarBloque(cita.getFecha(), cita.getHora());
        citaRepository.eliminar(idCita);
    }

    /**
      * Reprograma una cita a una nueva fecha y hora, liberando el bloque horario previo
      * y ocupando el nuevo bloque en la agenda del especialista.
      *
      * @param idCita     Identificador de la cita a reprogramar
      * @param nuevaFecha Nueva fecha seleccionada
      * @param nuevaHora  Nueva hora seleccionada
      * @return Cita con los nuevos datos actualizados
      * @throws IllegalArgumentException si la fecha es pasada o la cita no existe
      * @throws IllegalStateException    si el nuevo bloque no está disponible o el paciente tiene cruce
      */
    public Cita reprogramarCita(String idCita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita " + idCita));
        if (nuevaFecha == null || nuevaFecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La nueva fecha seleccionada (" + nuevaFecha + ") no puede ser anterior a la fecha actual (" + LocalDate.now() + ").");
        }
        if (cita.getEstado() != EstadoCita.AGENDADA) {
            throw new IllegalStateException("Solo se pueden reprogramar citas en estado Agendada.");
        }
        Especialista especialista = cita.getEspecialista();
        if (!especialista.getAgenda().verificarDisponibilidad(nuevaFecha, nuevaHora)) {
            throw new IllegalStateException("El nuevo bloque seleccionado (" + nuevaFecha + " " + nuevaHora + ") no está disponible.");
        }
        if (citaRepository.existeCitaActivaPacienteEnHorario(cita.getPaciente().getCedula(), nuevaFecha, nuevaHora)) {
            throw new IllegalStateException("El paciente ya tiene otra cita en el nuevo horario seleccionado.");
        }

        especialista.getAgenda().liberarBloque(cita.getFecha(), cita.getHora());
        especialista.getAgenda().ocuparBloque(nuevaFecha, nuevaHora);
        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        return cita;
    }

    /**
      * Consulta los bloques horarios de un especialista para una fecha determinada,
      * sincronizando el estado con las citas activas para asegurar que los ocupados figuren en rojo.
      *
      * @param especialista Especialista médico a consultar
      * @param fecha        Fecha de consulta
      * @return Lista de bloques horarios evaluados
      */
    public List<BloqueHorario> consultarBloquesPorFecha(Especialista especialista, LocalDate fecha) {
        List<BloqueHorario> bloques = especialista.getAgenda().obtenerBloquesPorFecha(fecha);
        // Sincronización estricta: Si existe una cita activa en citaRepository, el bloque se fuerza a ocupado (rojo)
        for (BloqueHorario bloque : bloques) {
            boolean hayCitaActiva = citaRepository.buscarActivaPorEspecialistaFechaHora(especialista, fecha, bloque.getHora()).isPresent();
            if (hayCitaActiva) {
                bloque.setDisponible(false);
            }
        }
        return bloques;
    }

    /**
      * Lista todas las citas asignadas a un especialista en particular.
      *
      * @param especialista Especialista a consultar
      * @return Lista de citas activas del doctor
      */
    public List<Cita> listarCitasPorEspecialista(Especialista especialista) {
        return citaRepository.listarPorEspecialista(especialista);
    }

    /**
      * Lista la totalidad de citas activas en la clínica.
      *
      * @return Lista de citas activas
      */
    public List<Cita> listarTodasLasCitasActivas() {
        return citaRepository.listarActivas();
    }
}
