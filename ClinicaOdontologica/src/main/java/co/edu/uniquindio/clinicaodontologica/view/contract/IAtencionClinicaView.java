package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.*;

import java.util.List;

/**
  * Contrato de Vista Pasiva (MVP) para la Atención Clínica y Registro de Procedimientos.
  */
public interface IAtencionClinicaView {

    /**
      * Muestra la lista de citas activas pendientes de atención odontológica.
      *
      * @param citas Lista de citas
      */
    void mostrarCitasActivas(List<Cita> citas);

    /**
      * Carga el catálogo estandarizado de diagnósticos CIE-10 en el selector desplegable.
      *
      * @param diagnosticos Lista de diagnósticos del catálogo
      */
    void mostrarCatalogoDiagnosticos(List<CatalogoDiagnostico> diagnosticos);

    /**
      * Muestra la lista de insumos odontológicos disponibles en el almacén para seleccionar consumos.
      *
      * @param insumos Lista de insumos
      */
    void mostrarInsumosDisponibles(List<Insumo> insumos);

    /**
      * Actualiza y renderiza el historial de procedimientos del expediente clínico del paciente.
      *
      * @param historiaClinica Expediente clínico
      */
    void mostrarHistoriaClinicaActualizada(HistoriaClinica historiaClinica);

    /**
      * Limpia los campos del formulario de atención clínica.
      */
    void limpiarFormularioClinico();

    /**
      * Muestra una notificación de éxito o confirmación al usuario.
      *
      * @param titulo  Título del mensaje
      * @param mensaje Detalle del mensaje
      */
    void mostrarExito(String titulo, String mensaje);

    /**
      * Muestra una notificación de error o alerta de regla de negocio.
      *
      * @param titulo  Título del error
      * @param mensaje Detalle del error
      */
    void mostrarError(String titulo, String mensaje);
}
