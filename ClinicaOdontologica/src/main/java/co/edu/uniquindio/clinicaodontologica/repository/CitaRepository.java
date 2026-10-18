package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  * Repositorio de Citas Odontológicas (equivalente conceptual a FactoryAgendamiento en el modelo de dominio).
  */
public class CitaRepository {

    /**
      * Mapa ordenado de citas indexadas por su código único de cita (ej. CIT-0001).
      */
    private final Map<String, Cita> citasPorId = new LinkedHashMap<>();

    /**
      * Secuencia numérica autoincremental para la generación de identificadores únicos.
      */
    private int secuencia = 1;

    /**
      * Genera el siguiente identificador correlativo único para una nueva cita médica.
      *
      * @return Identificador en formato CIT-XXXX
      */
    public String generarSiguienteId() {
        String id;
        do {
            id = String.format("CIT-%04d", secuencia++);
        } while (citasPorId.containsKey(id));
        return id;
    }

    /**
      * Guarda o actualiza una cita en el repositorio.
      *
      * @param cita Cita a persistir
      */
    public void guardar(Cita cita) {
        citasPorId.put(cita.getIdCita(), cita);
    }

    /**
      * Busca una cita por su código de identificación.
      *
      * @param idCita Identificador de la cita
      * @return Optional con la cita si existe
      */
    public Optional<Cita> buscarPorId(String idCita) {
        return Optional.ofNullable(citasPorId.get(idCita));
    }

    /**
      * Elimina una cita del repositorio.
      *
      * @param idCita Identificador de la cita a remover
      * @return true si la cita fue removida
      */
    public boolean eliminar(String idCita) {
        return citasPorId.remove(idCita) != null;
    }

    /**
      * Elimina en cascada todas las citas asignadas a un especialista cuando este es dado de baja.
      *
      * @param especialista Especialista cuyas citas serán removidas
      */
    public void eliminarPorEspecialista(Especialista especialista) {
        citasPorId.values().removeIf(c -> c.getEspecialista().equals(especialista));
    }

    /**
      * Lista todas las citas existentes históricamente (incluyendo canceladas y pagadas).
      *
      * @return Lista de todas las citas
      */
    public List<Cita> listarTodas() {
        return new ArrayList<>(citasPorId.values());
    }

    /**
      * Lista únicamente las citas activas (excluyendo aquellas en estado EstadoCita#CANCELADA).
      *
      * @return Lista de citas activas
      */
    public List<Cita> listarActivas() {
        return citasPorId.values().stream()
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
                .collect(Collectors.toList());
    }

    /**
      * Lista las citas activas correspondientes a un especialista determinado.
      *
      * @param especialista Especialista a consultar
      * @return Lista de citas activas del doctor
      */
    public List<Cita> listarPorEspecialista(Especialista especialista) {
        return citasPorId.values().stream()
                .filter(c -> c.getEspecialista().equals(especialista) && c.getEstado() != EstadoCita.CANCELADA)
                .collect(Collectors.toList());
    }

    /**
      * Lista las citas activas correspondientes a un paciente según su cédula.
      *
      * @param cedulaPaciente Cédula del paciente
      * @return Lista de citas del paciente
      */
    public List<Cita> listarPorPaciente(String cedulaPaciente) {
        return citasPorId.values().stream()
                .filter(c -> c.getPaciente().getCedula().equals(cedulaPaciente) && c.getEstado() != EstadoCita.CANCELADA)
                .collect(Collectors.toList());
    }

    /**
      * Busca si existe una cita activa para un especialista en una fecha y hora específicas.
      *
      * @param especialista Doctor especialista
      * @param fecha        Fecha programada
      * @param hora         Hora programada
      * @return Optional con la cita si ya existe un cruce de horario activo
      */
    public Optional<Cita> buscarActivaPorEspecialistaFechaHora(Especialista especialista, LocalDate fecha, LocalTime hora) {
        return citasPorId.values().stream()
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA
                        && c.getEspecialista().equals(especialista)
                        && c.getFecha().equals(fecha)
                        && c.getHora().equals(hora))
                .findFirst();
    }

    /**
      * Valida si un paciente ya tiene otra cita activa reservada en ese mismo horario.
      *
      * @param cedulaPaciente Cédula del paciente
      * @param fecha          Fecha programada
      * @param hora           Hora programada
      * @return true si el paciente ya tiene cita en ese mismo instante
      */
    public boolean existeCitaActivaPacienteEnHorario(String cedulaPaciente, LocalDate fecha, LocalTime hora) {
        return citasPorId.values().stream()
                .anyMatch(c -> c.getEstado() != EstadoCita.CANCELADA
                        && c.getPaciente().getCedula().equals(cedulaPaciente)
                        && c.getFecha().equals(fecha)
                        && c.getHora().equals(hora));
    }
}
