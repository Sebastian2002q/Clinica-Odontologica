package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.Insumo;
import co.edu.uniquindio.clinicaodontologica.service.InventarioService;
import co.edu.uniquindio.clinicaodontologica.view.contract.IInventarioView;

/**
  * Presenter MVP para el cumplimiento de:
  */
public class InventarioPresenter {

    /**
      * Contrato de la vista pasiva de inventario y almacén.
      */
    private final IInventarioView view;

    /**
      * Servicio de dominio de inventario.
      */
    private final InventarioService inventarioService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view              Vista pasiva que implementa IInventarioView
      * @param inventarioService Servicio de almacén y suministros
      */
    public InventarioPresenter(IInventarioView view, InventarioService inventarioService) {
        this.view = view;
        this.inventarioService = inventarioService;
    }

    /**
      * Carga y refresca en la vista el inventario completo y la lista filtrada de materiales bajo alerta crítica.
      */
    public void cargarInventario() {
        view.mostrarInventarioCompleto(inventarioService.listarInsumos());
        view.mostrarAlertasCriticas(inventarioService.listarInsumosConAlerta());
    }

    /**
      * Procesa la captura del formulario para dar de alta un nuevo insumo odontológico.
      *
      * @param codigo         Código de referencia único (ej. INS-001)
      * @param nombre         Nombre del insumo
      * @param categoria      Categoría médica
      * @param unidadMedida   Unidad de medida física
      * @param stockActualTxt Texto con la cantidad inicial
      * @param stockMinimoTxt Texto con el umbral mínimo crítico
      * @param costoTxt       Texto con el costo unitario
      */
    public void registrarInsumo(String codigo, String nombre, String categoria, String unidadMedida,
                                String stockActualTxt, String stockMinimoTxt, String costoTxt) {
        try {
            int stockActual = Integer.parseInt(stockActualTxt.trim());
            int stockMinimo = Integer.parseInt(stockMinimoTxt.trim());
            double costo = Double.parseDouble(costoTxt.trim());
            Insumo insumo = inventarioService.registrarInsumo(
                    codigo, nombre, categoria, unidadMedida, stockActual, stockMinimo, costo
            );
            view.limpiarFormularioInsumo();
            cargarInventario();
            view.mostrarExito("Insumo Registrado (F-06)",
                    "Se guardó el insumo " + insumo.getNombre() + " [" + insumo.getCodigo()
                            + "] con estado: " + insumo.getNivelAlerta().getEtiqueta() + ".");
        } catch (NumberFormatException e) {
            view.mostrarError("Datos numéricos inválidos", "Stock actual, stock mínimo y costo deben ser valores numéricos.");
        } catch (RuntimeException e) {
            view.mostrarError("Error en Inventario", e.getMessage());
        }
    }

    /**
      * Procesa la adición de unidades por reabastecimiento o compra de existencias al almacén.
      *
      * @param insumoSeleccionado Insumo seleccionado en la tabla
      * @param cantidadTexto      Cantidad de unidades ingresadas
      */
    public void reabastecerInsumo(Insumo insumoSeleccionado, String cantidadTexto) {
        if (insumoSeleccionado == null) {
            view.mostrarError("Selección requerida", "Seleccione un insumo de la tabla para reabastecer.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(cantidadTexto.trim());
            Insumo actualizado = inventarioService.reabastecerStock(insumoSeleccionado.getCodigo(), cantidad);
            cargarInventario();
            view.mostrarExito("Stock Actualizado",
                    "Nuevo stock de " + actualizado.getNombre() + ": " + actualizado.getStockActual()
                            + " " + actualizado.getUnidadMedida() + " (" + actualizado.getNivelAlerta().getEtiqueta() + ").");
        } catch (NumberFormatException e) {
            view.mostrarError("Cantidad inválida", "Ingrese un número entero positivo para reabastecer.");
        } catch (RuntimeException e) {
            view.mostrarError("Error al Reabastecer", e.getMessage());
        }
    }
}
