package co.edu.uniquindio.clinicaodontologica.service.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Encapsula el resultado del procesamiento de un pago realizado bajo una estrategia específica.
 */
@Getter
@AllArgsConstructor
public class ResultadoPago {

    /**
     * Valor monetario total que se cobró y que ingresa a la caja de la clínica.
     */
    private final double valorCobrado;

    /**
     * Monto de dinero efectivamente entregado por el paciente o registrado en la transacción.
     */
    private final double montoRecibido;

    /**
     * Dinero devuelto al paciente (aplica para pagos en efectivo).
     */
    private final double cambioDevuelto;

    /**
     * Referencia o código de autorización de la transacción (voucher, comprobante digital o recibo).
     */
    private final String referenciaTransaccion;

    /**
     * Detalle explicativo de la operación según las reglas del medio de pago.
     */
    private final String detalle;
}
