package co.edu.uniquindio.clinicaodontologica.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
  * Entidad de dominio que representa un Procedimiento Clínico Odontológico realizado en una consulta médica.
  */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProcedimientoClinico {

    /**
      * Identificador unívoco del procedimiento (ej. PROC-0001).
      */
    @EqualsAndHashCode.Include
    private String idProcedimiento;

    /**
      * Identificador de la cita médica en la cual se efectuó el procedimiento.
      */
    private String idCita;

    /**
      * Cédula del paciente atendido.
      */
    private String cedulaPaciente;

    /**
      * Nombre completo del paciente atendido.
      */
    private String nombrePaciente;

    /**
      * Nombre completo del doctor especialista que ejecutó la atención.
      */
    private String nombreEspecialista;

    /**
      * Fecha y hora exacta de registro de la atención clínica.
      */
    private LocalDateTime fechaHora;

    /**
      * Diagnóstico odontológico estandarizado (ej. K02.1 Caries de la dentina).
      */
    private String diagnosticoEstandar;

    /**
      * Descripción médica del tratamiento aplicado (ej. Resina compuesta fotocurable en pieza 26).
      */
    private String tratamientoAplicado;

    /**
      * Notas de evolución clínica, respuesta del paciente o indicaciones posoperatorias.
      */
    private String evolucionMedica;

    /**
      * Tarifa o valor monetario oficial a facturar por el procedimiento clínico.
      */
    private double valorProcedimiento;

    /**
      * Lista de insumos odontológicos utilizados con sus cantidades consumidas.
      */
    private List<ConsumoInsumo> insumosConsumidos = new ArrayList<>();

    /**
      * Indicador de si este procedimiento ya fue liquidado y pagado en caja.
      */
    private boolean pagado;

    /**
      * Representación textual con diagnóstico, tratamiento y valor.
      *
      * @return Cadena formateada
      */
    @Override
    public String toString() {
        return "[" + idProcedimiento + "] " + diagnosticoEstandar + " - " + tratamientoAplicado
                + " ($" + String.format("%,.0f", valorProcedimiento) + ")";
    }
}
