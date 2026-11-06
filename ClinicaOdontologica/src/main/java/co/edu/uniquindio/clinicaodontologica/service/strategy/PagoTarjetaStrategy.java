package co.edu.uniquindio.clinicaodontologica.service.strategy;

import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;

/**
 * Estrategia para procesar pagos con tarjeta de débito o crédito a través de datáfono.
 * Cobra exactamente el valor total de la consulta, no genera devuelta y registra el número de autorización.
 */
public class PagoTarjetaStrategy implements EstrategiaPago {

    @Override
    public MetodoPago getMetodoPago() {
        return MetodoPago.TARJETA;
    }

    @Override
    public ResultadoPago procesarPago(double valorTotal, double montoEntregado, String referencia) {
        String ref = (referencia != null && !referencia.isBlank())
                ? referencia.trim()
                : "AUT-DATAFONO-" + (Math.abs(System.currentTimeMillis() % 900000) + 100000);

        String detalle = "Transacción bancaria aprobada en datáfono. Voucher / Autorización: " + ref;

        // En pagos con tarjeta el débito/crédito se efectúa por el valor exacto (sin cambio)
        return new ResultadoPago(valorTotal, valorTotal, 0.0, ref, detalle);
    }
}
