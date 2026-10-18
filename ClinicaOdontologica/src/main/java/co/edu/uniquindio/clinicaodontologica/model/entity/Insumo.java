package co.edu.uniquindio.clinicaodontologica.model.entity;

import co.edu.uniquindio.clinicaodontologica.model.enums.NivelAlertaStock;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
  * Entidad de dominio que representa un Insumo o Material Odontológico en el almacén de la clínica.
  */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Insumo {

    /**
      * Código de referencia único del insumo (ej. INS-001).
      */
    @EqualsAndHashCode.Include
    private String codigo;

    /**
      * Nombre comercial o genérico del material odontológico.
      */
    private String nombre;

    /**
      * Categoría estándar del almacén (Operatoria, Cirugía, Anestesia, Bioseguridad, Endodoncia, Periodoncia).
      */
    private String categoria;

    /**
      * Unidad de medida física estándar (Unidad, Caja, Jeringa, Carpule, Paquete, Frasco, Rollo).
      */
    private String unidadMedida;

    /**
      * Cantidad actual disponible físicamente en el almacén.
      */
    private int stockActual;

    /**
      * Umbral mínimo crítico de existencias; si el stock llega o baja de aquí, se emite alerta crítica.
      */
    private int stockMinimoCritico;

    /**
      * Costo monetario unitario de adquisición del insumo.
      */
    private double costoUnitario;

    /**
      * Calcula en tiempo real el nivel de alerta visual según el stock actual y el umbral crítico.
      *
      * @return NivelAlertaStock#CRITICO si stock &le; mínimo; NivelAlertaStock#ADVERTENCIA si stock &le; 1.5 * mínimo; o NivelAlertaStock#NORMAL en caso contrario.
      */
    public NivelAlertaStock getNivelAlerta() {
        if (stockActual <= stockMinimoCritico) {
            return NivelAlertaStock.CRITICO;
        }
        if (stockActual <= Math.ceil(stockMinimoCritico * 1.5)) {
            return NivelAlertaStock.ADVERTENCIA;
        }
        return NivelAlertaStock.NORMAL;
    }

    /**
      * Valida si el insumo cuenta con existencias suficientes para satisfacer un requerimiento clínico.
      *
      * @param cantidadRequerida Cantidad solicitada
      * @return true si hay existencias iguales o superiores a la cantidad requerida
      */
    public boolean tieneStockSuficiente(int cantidadRequerida) {
        return cantidadRequerida > 0 && stockActual >= cantidadRequerida;
    }

    /**
      * Descuenta existencias del inventario por consumo médico.
      *
      * @param cantidad Cantidad a descontar
      * @throws IllegalArgumentException si la cantidad es menor o igual a cero
      * @throws IllegalStateException    si el stock disponible es insuficiente
      */
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
        }
        if (stockActual < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el insumo '" + nombre + "'. Disponible: " + stockActual);
        }
        this.stockActual -= cantidad;
    }

    /**
      * Incrementa existencias en el inventario por reabastecimiento o compra de suministros.
      *
      * @param cantidad Cantidad a adicionar al stock actual
      * @throws IllegalArgumentException si la cantidad es menor o igual a cero
      */
    public void agregarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a ingresar debe ser mayor a cero.");
        }
        this.stockActual += cantidad;
    }

    /**
      * Representación textual con nombre, código y stock disponible.
      *
      * @return Cadena formateada
      */
    @Override
    public String toString() {
        return nombre + " [" + codigo + "] - Stock: " + stockActual + " " + unidadMedida
                + " (" + getNivelAlerta().getEtiqueta() + ")";
    }
}
