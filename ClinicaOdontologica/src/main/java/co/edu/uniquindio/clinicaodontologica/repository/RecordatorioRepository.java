package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.Recordatorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
  * Repositorio para la gestión y auditoría de Recordatorios y Notificaciones.
  */
public class RecordatorioRepository {

    /**
      * Mapa de recordatorios ordenado cronológicamente indexado por su identificador único.
      */
    private final Map<String, Recordatorio> recordatoriosPorId = new LinkedHashMap<>();

    /**
      * Secuencia numérica para la generación correlativa de identificadores de recordatorios.
      */
    private int secuencia = 1;

    /**
      * Genera el siguiente identificador correlativo para un recordatorio (ej. REC-0001).
      *
      * @return Identificador en formato REC-XXXX
      */
    public String generarSiguienteId() {
        return String.format("REC-%04d", secuencia++);
    }

    /**
      * Guarda un recordatorio emitido en el repositorio.
      *
      * @param recordatorio Registro de la notificación emitida
      */
    public void guardar(Recordatorio recordatorio) {
        recordatoriosPorId.put(recordatorio.getIdRecordatorio(), recordatorio);
    }

    /**
      * Comprueba si una cita médica ya cuenta con un recordatorio emitido previamente.
      *
      * @param idCita Identificador de la cita médica
      * @return true si ya existe un recordatorio para esa cita
      */
    public boolean existeRecordatorioParaCita(String idCita) {
        return recordatoriosPorId.values().stream()
                .anyMatch(r -> r.getIdCita().equals(idCita));
    }

    /**
      * Lista todos los recordatorios emitidos históricamente por el sistema.
      *
      * @return Lista copia de todos los recordatorios
      */
    public List<Recordatorio> listarTodos() {
        return new ArrayList<>(recordatoriosPorId.values());
    }
}
