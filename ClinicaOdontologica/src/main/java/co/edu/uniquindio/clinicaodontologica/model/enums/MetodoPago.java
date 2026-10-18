package co.edu.uniquindio.clinicaodontologica.model.enums;

/**
  * Enumeración que define los Métodos de Pago aceptados en la clínica.
  */
public enum MetodoPago {

    /**
      * Pago en papel moneda físico con cálculo de cambio o devuelta.
      */
    EFECTIVO("Efectivo"),

    /**
      * Pago mediante tarjeta de débito o crédito en datáfono bancario.
      */
    TARJETA("Tarjeta Débito / Crédito"),

    /**
      * Pago electrónico mediante transferencia o billetera digital (PSE, Bancolombia, Nequi, Daviplata).
      */
    TRANSFERENCIA("Transferencia Bancaria");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
