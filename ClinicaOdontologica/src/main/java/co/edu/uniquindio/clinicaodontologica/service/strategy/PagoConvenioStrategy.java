package co.edu.uniquindio.clinicaodontologica.service.strategy;

import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;

/**
 * Estrategia para procesar liquidaciones de tratamientos cubiertos por pólizas, convenios o seguros odontológicos.
 * Demuestra la extensibilidad del patrón Strategy (Principio Abierto/Cerrado - OCP).
 */
public class PagoConvenioStrategy implements EstrategiaPago {

    @Override
    public MetodoPago getMetodoPago() {
        return MetodoPago.CONVENIO;
    }

    @Override
    public ResultadoPago procesarPago(double valorTotal, double montoEntregado, String referencia) {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe registrar el número de póliza, autorización o carnet del convenio odontológico.");
        }

        String ref = referencia.trim();
        String detalle = "Tratamiento cargado a cuenta institucional del convenio/seguro. Póliza/Autorización: " + ref;

        // Se liquida con cargo a la aseguradora
        return new ResultadoPago(valorTotal, valorTotal, 0.0, ref, detalle);
    }
}
