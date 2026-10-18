package co.edu.uniquindio.clinicaodontologica.model.entity;

import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
  * Entidad de dominio que representa una Transacción de Pago y su Comprobante Oficial emitido.
  */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pago {

    /**
      * Identificador unívoco del registro de pago (ej. PAG-0001).
      */
    @EqualsAndHashCode.Include
    private String idPago;

    /**
      * Número consecutivo del comprobante oficial de caja (ej. COMP-2026-0001).
      */
    private String numeroComprobante;

    /**
      * Identificador de la cita médica que se está liquidando.
      */
    private String idCita;

    /**
      * Cédula del paciente que efectúa o recibe el tratamiento.
      */
    private String cedulaPaciente;

    /**
      * Nombre completo del paciente.
      */
    private String nombrePaciente;

    /**
      * Detalle clínico descriptivo de los procedimientos y tarifas liquidadas.
      */
    private String desgloseTratamientos;

    /**
      * Valor monetario total a pagar por el paciente.
      */
    private double valorTotal;

    /**
      * Monto de dinero efectivamente entregado por el cliente en caja.
      */
    private double montoRecibido;

    /**
      * Cambio o dinero devuelto al paciente (para pagos en efectivo).
      */
    private double cambioDevuelto;

    /**
      * Método o medio de pago utilizado (EFECTIVO, TARJETA, TRANSFERENCIA).
      */
    private MetodoPago metodoPago;

    /**
      * Fecha y hora exacta de registro del pago.
      */
    private LocalDateTime fechaHora;

    /**
      * Nombre del auxiliar o funcionario responsable del cobro en recepción.
      */
    private String auxiliarResponsable;

    /**
      * Genera el texto formateado del comprobante oficial de pago para impresión o visualización.
      *
      * @return Cadena con el formato estructurado del recibo oficial
      */
    public String generarTextoComprobante() {
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return """
                ====================================================
                        CLÍNICA ODONTOLÓGICA "DENTAL U" - SIGO
                               COMPROBANTE OFICIAL DE PAGO
                ====================================================
                No. Comprobante : %s
                ID Pago         : %s
                Fecha / Hora    : %s
                Cita Asociada   : %s
                ----------------------------------------------------
                Paciente        : %s
                Documento (CC)  : %s
                Atendido por    : %s
                ----------------------------------------------------
                DESGLOSE CLÍNICO:
                %s
                ----------------------------------------------------
                VALOR TOTAL     : $%,.2f
                MÉTODO DE PAGO  : %s
                MONTO RECIBIDO  : $%,.2f
                CAMBIO DEVUELTO : $%,.2f
                ====================================================
                Estado de Caja  : REGISTRADO EN FLUJO DE CAJA ACTIVO
                ====================================================
                """.formatted(
                numeroComprobante,
                idPago,
                fechaHora.format(formateador),
                idCita,
                nombrePaciente,
                cedulaPaciente,
                auxiliarResponsable,
                desgloseTratamientos,
                valorTotal,
                metodoPago.getEtiqueta(),
                montoRecibido,
                cambioDevuelto
        );
    }

    /**
      * Representación textual para tablas de pagos.
      *
      * @return Cadena con comprobante, paciente, total y método
      */
    @Override
    public String toString() {
        return numeroComprobante + " | " + nombrePaciente + " | Total: $" + String.format("%,.2f", valorTotal)
                + " (" + metodoPago.getEtiqueta() + ")";
    }
}
