package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.HistoriaClinica;
import co.edu.uniquindio.clinicaodontologica.model.entity.ProcedimientoClinico;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  * Repositorio para la gestión de Historias Clínicas y Procedimientos Médicos.
  */
public class HistoriaClinicaRepository {

    /**
      * Mapa de expedientes clínicos indexados por la cédula de ciudadanía del paciente titular.
      */
    private final Map<String, HistoriaClinica> historiasPorCedula = new LinkedHashMap<>();

    /**
      * Secuencia numérica para la generación correlativa de identificadores de procedimientos.
      */
    private int secuenciaProcedimiento = 1;

    /**
      * Genera el siguiente identificador correlativo para un nuevo procedimiento clínico (ej. PROC-0001).
      *
      * @return Identificador en formato PROC-XXXX
      */
    public String generarSiguienteIdProcedimiento() {
        return String.format("PROC-%04d", secuenciaProcedimiento++);
    }

    /**
      * Guarda o actualiza el expediente de historia clínica en el repositorio.
      *
      * @param historiaClinica Instancia del expediente a persistir
      */
    public void guardar(HistoriaClinica historiaClinica) {
        historiasPorCedula.put(historiaClinica.getCedulaPaciente(), historiaClinica);
    }

    /**
      * Busca la historia clínica correspondiente a un paciente a partir de su número de cédula.
      *
      * @param cedulaPaciente Cédula del paciente
      * @return Optional con la historia clínica si se encuentra registrada
      */
    public Optional<HistoriaClinica> buscarPorCedulaPaciente(String cedulaPaciente) {
        if (cedulaPaciente == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(historiasPorCedula.get(cedulaPaciente.trim()));
    }

    /**
      * Busca la historia clínica a partir del enlace único permanente (linkHistoriaClinica).
      *
      * @param linkHistoria Enlace URI del expediente
      * @return Optional con la historia clínica correspondiente
      */
    public Optional<HistoriaClinica> buscarPorLink(String linkHistoria) {
        if (linkHistoria == null) {
            return Optional.empty();
        }
        return historiasPorCedula.values().stream()
                .filter(h -> h.getLinkHistoriaClinica().equalsIgnoreCase(linkHistoria.trim()))
                .findFirst();
    }

    /**
      * Busca y retorna todos los procedimientos clínicos que fueron practicados dentro de una cita médica específica.
      *
      * @param idCita Identificador de la cita médica
      * @return Lista de procedimientos asociados a esa consulta
      */
    public List<ProcedimientoClinico> buscarProcedimientosPorCita(String idCita) {
        return historiasPorCedula.values().stream()
                .flatMap(h -> h.getProcedimientos().stream())
                .filter(p -> p.getIdCita().equals(idCita))
                .collect(Collectors.toList());
    }

    /**
      * Lista todas las historias clínicas registradas en el sistema.
      *
      * @return Lista copia de todos los expedientes clínicos
      */
    public List<HistoriaClinica> listarTodas() {
        return new ArrayList<>(historiasPorCedula.values());
    }
}
