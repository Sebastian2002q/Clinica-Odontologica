package co.edu.uniquindio.clinicaodontologica.model.enums;

/**
  * Enumeración que define los Niveles de Alerta Visual para el Control de Stock de Insumos.
  */
public enum NivelAlertaStock {

    /**
      * Stock abundante o seguro por encima del 150% del stock mínimo crítico.
      */
    NORMAL("Normal (Verde)", "#16A34A"),

    /**
      * Stock en rango preventivo (entre el 100% y el 150% del stock mínimo); sugiere reordenar.
      */
    ADVERTENCIA("Stock Bajo - Preventivo (Amarillo)", "#D97706"),

    /**
      * Stock igual o inferior al stock mínimo crítico; requiere compra inmediata para no paralizar cirugías.
      */
    CRITICO("Stock Crítico / Escasez (Rojo)", "#DC2626");

    private final String etiqueta;
    private final String colorHex;

    NivelAlertaStock(String etiqueta, String colorHex) {
        this.etiqueta = etiqueta;
        this.colorHex = colorHex;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColorHex() {
        return colorHex;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
