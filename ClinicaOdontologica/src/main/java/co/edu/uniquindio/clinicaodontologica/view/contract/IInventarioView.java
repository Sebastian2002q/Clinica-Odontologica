package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.Insumo;

import java.util.List;

/**
  * Contrato de Vista Pasiva (MVP) para la Gestión de Inventario y Almacén.
  */
public interface IInventarioView {

    /**
      * Muestra la totalidad de los insumos en la tabla principal del almacén con sus semáforos de stock.
      *
      * @param insumos Lista de todos los insumos
      */
    void mostrarInventarioCompleto(List<Insumo> insumos);

    /**
      * Muestra la lista de insumos que han alcanzado niveles de stock preventivo o crítico.
      *
      * @param insumosEnAlerta Lista de insumos en alerta
      */
    void mostrarAlertasCriticas(List<Insumo> insumosEnAlerta);

    /**
      * Limpia los campos del formulario de registro de insumo.
      */
    void limpiarFormularioInsumo();

    /**
      * Muestra una notificación de éxito o confirmación al usuario.
      *
      * @param titulo  Título del mensaje
      * @param mensaje Detalle del mensaje
      */
    void mostrarExito(String titulo, String mensaje);

    /**
      * Muestra una notificación de error en la interfaz.
      *
      * @param titulo  Título del error
      * @param mensaje Detalle del error
      */
    void mostrarError(String titulo, String mensaje);
}
