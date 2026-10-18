package co.edu.uniquindio.clinicaodontologica.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
  * Objeto de valor que representa la cantidad consumida de un insumo médico en un procedimiento clínico.
  */
@Getter
@Setter
@AllArgsConstructor
public class ConsumoInsumo {

    /**
      * Código identificador del insumo consumido (ej. INS-001).
      */
    private String codigoInsumo;

    /**
      * Nombre legible del insumo utilizado (ej. Resina Compuesta Fotocurable A2).
      */
    private String nombreInsumo;

    /**
      * Cantidad numérica consumida en la atención médica.
      */
    private int cantidad;

    /**
      * Unidad de medida física del insumo (Jeringa, Carpule, Caja, Unidad).
      */
    private String unidadMedida;

    /**
      * Representación textual con nombre, cantidad y unidad.
      *
      * @return Cadena formateada
      */
    @Override
    public String toString() {
        return nombreInsumo + " x " + cantidad + " " + unidadMedida;
    }
}
