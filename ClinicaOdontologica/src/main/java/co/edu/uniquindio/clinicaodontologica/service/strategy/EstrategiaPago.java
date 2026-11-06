package co.edu.uniquindio.clinicaodontologica.service.strategy;

import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;

/**
 * Interfaz que define el contrato del patrón Strategy para el procesamiento de pagos en caja.
 * Permite que cada medio de pago valide sus reglas de negocio particulares y calcule sus balances
 * de forma independiente sin acoplar la lógica del servicio principal.
 */
public interface EstrategiaPago {

    /**
     * Retorna el método de pago asociado a la estrategia.
     *
     * @return Tipo de método de pago
     */
    MetodoPago getMetodoPago();

    /**
     * Procesa la transacción de pago aplicando las reglas específicas del medio.
     *
     * @param valorTotal     Monto total de los tratamientos clínicos a liquidar
     * @param montoEntregado Dinero entregado o reportado en la transacción
     * @param referencia     Código de autorización, voucher o referencia bancaria si aplica
     * @return Resultado con el balance monetario y los datos de auditoría
     * @throws IllegalStateException    si el dinero entregado no cubre el costo
     * @throws IllegalArgumentException si falta un dato indispensable como la referencia bancaria
     */
    ResultadoPago procesarPago(double valorTotal, double montoEntregado, String referencia);
}
