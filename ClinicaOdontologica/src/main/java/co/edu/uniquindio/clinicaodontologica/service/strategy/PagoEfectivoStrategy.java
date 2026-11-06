package co.edu.uniquindio.clinicaodontologica.service.strategy;

import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;

/**
 * Estrategia para procesar pagos en efectivo en la caja de la clínica.
 * Valida que el dinero físico entregado sea suficiente y calcula el cambio correspondiente.
 */
public class PagoEfectivoStrategy implements EstrategiaPago {

    @Override
    public MetodoPago getMetodoPago() {
        return MetodoPago.EFECTIVO;
    }

    @Override
    public ResultadoPago procesarPago(double valorTotal, double montoEntregado, String referencia) {
        if (montoEntregado < valorTotal) {
            double faltante = valorTotal - montoEntregado;
            throw new IllegalStateException(String.format(
                    "Monto en efectivo insuficiente. El valor total es $%,.0f y se recibió $%,.0f (Faltan $%,.0f).",
                    valorTotal, montoEntregado, faltante));
        }

        double cambio = montoEntregado - valorTotal;
        String ref = (referencia == null || referencia.isBlank()) ? "Efectivo recibido en ventanilla" : referencia.trim();
        String detalle = String.format("Pago en efectivo. Cambio devuelto al paciente: $%,.0f", cambio);

        return new ResultadoPago(valorTotal, montoEntregado, cambio, ref, detalle);
    }
}
