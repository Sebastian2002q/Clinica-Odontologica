package co.edu.uniquindio.clinicaodontologica.model.entity;

import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoHistoriaClinica;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
  * Entidad de dominio que representa el Expediente de Historia Clínica Odontológica de un Paciente.
  */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HistoriaClinica {

    /**
      * Identificador unívoco del expediente de historia clínica (ej. HC-1094901001).
      */
    @EqualsAndHashCode.Include
    private final String idHistoria;

    /**
      * Enlace único permanente del expediente clínico del paciente.
      */
    private final String linkHistoriaClinica;

    /**
      * Cédula de ciudadanía del paciente titular del expediente.
      */
    private final String cedulaPaciente;

    /**
      * Nombre completo del paciente titular.
      */
    private String nombrePaciente;

    /**
      * Estado del expediente clínico (SIN_REGISTROS o ACTUALIZADA según RN-003).
      */
    private EstadoHistoriaClinica estado;

    /**
      * Lista cronológica inmutable de procedimientos odontológicos realizados.
      */
    private final List<ProcedimientoClinico> procedimientos = new ArrayList<>();

    /**
      * Constructor principal que vincula la historia clínica con el paciente recién registrado.
      * Inicia en estado EstadoHistoriaClinica#SIN_REGISTROS.
      *
      * @param paciente Paciente registrado titular de la historia clínica
      */
    public HistoriaClinica(Paciente paciente) {
        this.idHistoria = "HC-" + paciente.getCedula();
        this.linkHistoriaClinica = paciente.getLinkHistoriaClinica();
        this.cedulaPaciente = paciente.getCedula();
        this.nombrePaciente = paciente.getNombreCompleto();
        this.estado = EstadoHistoriaClinica.SIN_REGISTROS;
    }

    /**
      * Agrega un nuevo procedimiento clínico al expediente y actualiza el estado a ACTUALIZADA.
      *
      * @param procedimiento Procedimiento clínico formalmente documentado por el especialista
      */
    public void agregarProcedimiento(ProcedimientoClinico procedimiento) {
        this.procedimientos.add(procedimiento);
        this.estado = EstadoHistoriaClinica.ACTUALIZADA;
    }

    /**
      * Retorna una vista inmutable de la lista de procedimientos registrados para proteger la integridad de los datos.
      *
      * @return Lista no modificable de ProcedimientoClinico
      */
    public List<ProcedimientoClinico> getProcedimientos() {
        return Collections.unmodifiableList(procedimientos);
    }

    /**
      * Representación textual para auditoría y visualización de expedientes.
      *
      * @return Cadena con el id, link, estado y cantidad de procedimientos
      */
    @Override
    public String toString() {
        return idHistoria + " (" + linkHistoriaClinica + ") - Estado: " + estado.getEtiqueta()
                + " - Procedimientos: " + procedimientos.size();
    }
}
