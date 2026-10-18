package co.edu.uniquindio.clinicaodontologica.model.entity;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
  * Entidad de dominio que administra la disponibilidad temporal de un especialista.
  */
@Getter
public class Agenda {

    /**
      * Identificador unívoco de la agenda médica (ej. AG-DOC-1001).
      */
    private final String idAgenda;

    /**
      * Identificador del doctor titular al que pertenece esta agenda.
      */
    private final String idDoctor;

    /**
      * Estructura ordenada que mapea Fecha -> (Hora -> Disponibilidad booleana).
      * true = Bloque libre / Verde (disponible para reserva).
      * false = Bloque ocupado / Rojo (reservado por una cita activa).
      */
    private final Map<LocalDate, Map<LocalTime, Boolean>> disponibilidadPorFecha = new TreeMap<>();

    /**
      * Constructor principal de la agenda.
      *
      * @param idAgenda Identificador de la agenda
      * @param idDoctor Identificador del doctor propietario
      */
    public Agenda(String idAgenda, String idDoctor) {
        this.idAgenda = idAgenda;
        this.idDoctor = idDoctor;
    }

    /**
      * Registra un nuevo horario disponible para el doctor en una fecha determinada.
      *
      * @param fecha Fecha del bloque a habilitar
      * @param hora  Hora exacta del bloque
      */
    public void agregarHorarioDisponible(LocalDate fecha, LocalTime hora) {
        Map<LocalTime, Boolean> horasDelDia = disponibilidadPorFecha.computeIfAbsent(fecha, f -> new TreeMap<>());
        horasDelDia.putIfAbsent(hora, true);
    }

    /**
      * Elimina un horario configurado de la agenda, siempre y cuando no esté ocupado por una cita.
      *
      * @param fecha Fecha del bloque a remover
      * @param hora  Hora del bloque a remover
      * @return true si el bloque se eliminó con éxito, o false si no existía o estaba ocupado
      */
    public boolean eliminarHorarioConfigurado(LocalDate fecha, LocalTime hora) {
        Map<LocalTime, Boolean> horasDelDia = disponibilidadPorFecha.get(fecha);
        if (horasDelDia == null || !horasDelDia.containsKey(hora)) {
            return false;
        }
        // No se permite borrar un bloque que actualmente está ocupado por una cita activa
        if (!Boolean.TRUE.equals(horasDelDia.get(hora))) {
            return false;
        }
        horasDelDia.remove(hora);
        if (horasDelDia.isEmpty()) {
            disponibilidadPorFecha.remove(fecha);
        }
        return true;
    }

    /**
      * Verifica si un bloque horario está configurado y libre para ser reservado.
      *
      * @param fecha Fecha a consultar
      * @param hora  Hora a consultar
      * @return true si el bloque existe y está libre (verde)
      */
    public boolean verificarDisponibilidad(LocalDate fecha, LocalTime hora) {
        Map<LocalTime, Boolean> horasDelDia = disponibilidadPorFecha.get(fecha);
        return horasDelDia != null && Boolean.TRUE.equals(horasDelDia.get(hora));
    }

    /**
      * Verifica si existe un bloque configurado en la agenda para la fecha y hora indicadas.
      *
      * @param fecha Fecha a verificar
      * @param hora  Hora a verificar
      * @return true si el bloque existe en la agenda
      */
    public boolean existeBloqueConfigurado(LocalDate fecha, LocalTime hora) {
        Map<LocalTime, Boolean> horasDelDia = disponibilidadPorFecha.get(fecha);
        return horasDelDia != null && horasDelDia.containsKey(hora);
    }

    /**
      * Marca un bloque como ocupado (rojo) al agendar una cita.
      *
      * @param fecha Fecha del bloque a ocupar
      * @param hora  Hora del bloque a ocupar
      * @return true si el bloque estaba libre y se ocupó exitosamente; false si no estaba disponible
      */
    public boolean ocuparBloque(LocalDate fecha, LocalTime hora) {
        if (!verificarDisponibilidad(fecha, hora)) {
            return false;
        }
        disponibilidadPorFecha.get(fecha).put(hora, false);
        return true;
    }

    /**
      * Libera un bloque horario previamente ocupado (lo devuelve al estado libre/verde).
      * Se invoca cuando una cita es cancelada o reprogramada.
      *
      * @param fecha Fecha del bloque a liberar
      * @param hora  Hora del bloque a liberar
      */
    public void liberarBloque(LocalDate fecha, LocalTime hora) {
        disponibilidadPorFecha.computeIfAbsent(fecha, f -> new TreeMap<>()).put(hora, true);
    }

    /**
      * Obtiene la lista ordenada de todas las fechas con bloques configurados en la agenda.
      *
      * @return Lista inmutable de fechas disponibles
      */
    public List<LocalDate> obtenerFechasConfiguradas() {
        return new ArrayList<>(disponibilidadPorFecha.keySet());
    }

    /**
      * Obtiene los bloques de una fecha determinada representados como objetos BloqueHorario.
      *
      * @param fecha Fecha a consultar
      * @return Lista de bloques con su estado de disponibilidad
      */
    public List<BloqueHorario> obtenerBloquesPorFecha(LocalDate fecha) {
        Map<LocalTime, Boolean> horasDelDia = disponibilidadPorFecha.get(fecha);
        if (horasDelDia == null) {
            return Collections.emptyList();
        }
        List<BloqueHorario> listaBloques = new ArrayList<>();
        for (Map.Entry<LocalTime, Boolean> entry : horasDelDia.entrySet()) {
            listaBloques.add(new BloqueHorario(fecha, entry.getKey(), entry.getValue()));
        }
        return listaBloques;
    }

    /**
      * Obtiene todos los bloques configurados en la totalidad de la agenda.
      *
      * @return Lista completa de bloques horarios
      */
    public List<BloqueHorario> obtenerTodosLosBloques() {
        List<BloqueHorario> resultado = new ArrayList<>();
        for (LocalDate fecha : disponibilidadPorFecha.keySet()) {
            resultado.addAll(obtenerBloquesPorFecha(fecha));
        }
        return resultado;
    }

    /**
      * Obtiene únicamente las horas libres (verdes) en una fecha dada.
      *
      * @param fecha Fecha de consulta
      * @return Lista de horas disponibles para agendamiento
      */
    public List<LocalTime> obtenerHorasLibres(LocalDate fecha) {
        Map<LocalTime, Boolean> horasDelDia = disponibilidadPorFecha.get(fecha);
        if (horasDelDia == null) {
            return Collections.emptyList();
        }
        List<LocalTime> horasLibres = new ArrayList<>();
        for (Map.Entry<LocalTime, Boolean> entry : horasDelDia.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                horasLibres.add(entry.getKey());
            }
        }
        return horasLibres;
    }
}
