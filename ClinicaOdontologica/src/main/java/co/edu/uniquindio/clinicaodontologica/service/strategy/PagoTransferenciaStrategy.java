package co.edu.uniquindio.clinicaodontologica.service.strategy;

import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;

/**
 * Estrategia para procesar pagos por transferencia electrónica o billeteras digitales (Nequi, Daviplata, Bancolombia, PSE).
 * Valida de forma obligatoria que se ingrese el código de soporte o comprobante digital emitido por la entidad.
 */
public class PagoTransferenciaStrategy implements EstrategiaPago {

    @Override
    public MetodoPago getMetodoPago() {
        return MetodoPago.TRANSFERENCIA;
    }

    @Override
    public ResultadoPago procesarPago(double valorTotal, double montoEntregado, String referencia) {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el número de comprobante o referencia de la transferencia (Nequi, Daviplata, PSE).");
        }

        String ref = referencia.trim();
        String detalle = "Transferencia electrónica validada. Comprobante digital No: " + ref;

        // En transferencias se transfiere el valor exacto liquidado
        return new ResultadoPago(valorTotal, valorTotal, 0.0, ref, detalle);
    }
}
