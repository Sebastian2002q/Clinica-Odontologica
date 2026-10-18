package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.HistoriaClinica;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;

import java.util.List;

/**
  * Contrato de Vista Pasiva (MVP) para el módulo de Gestión de Pacientes.
  */
public interface IPacienteView {

    /**
      * Muestra la lista de pacientes registrados en la tabla de la interfaz.
      *
      * @param pacientes Lista de pacientes
      */
    void mostrarPacientes(List<Paciente> pacientes);

    /**
      * Despliega la información y el estado de la historia clínica abierta para el paciente.
      *
      * @param historiaClinica Expediente clínico
      */
    void mostrarDetalleHistoriaClinica(HistoriaClinica historiaClinica);

    /**
      * Limpia los campos del formulario de registro de paciente.
      */
    void limpiarFormulario();

    /**
      * Muestra una notificación de éxito o confirmación al usuario.
      *
      * @param titulo  Título del mensaje
      * @param mensaje Detalle del mensaje
      */
    void mostrarExito(String titulo, String mensaje);

    /**
      * Muestra una notificación de error o alerta de bloqueo de regla de negocio.
      *
      * @param titulo  Título del error
      * @param mensaje Detalle del error
      */
    void mostrarError(String titulo, String mensaje);
}
