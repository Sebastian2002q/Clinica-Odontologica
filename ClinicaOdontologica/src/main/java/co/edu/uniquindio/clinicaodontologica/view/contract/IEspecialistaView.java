package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.BloqueHorario;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;

import java.util.List;

/**
  * Contrato de Vista Pasiva (MVP) para el módulo de Especialistas y Odontólogos.
  */
public interface IEspecialistaView {

    /**
      * Muestra la lista de especialistas registrados en la tabla de la interfaz.
      *
      * @param especialistas Lista de especialistas
      */
    void mostrarEspecialistas(List<Especialista> especialistas);

    /**
      * Muestra los bloques de horario configurados para el doctor seleccionado.
      *
      * @param bloques Lista de bloques horarios
      */
    void mostrarHorariosEspecialista(List<BloqueHorario> bloques);

    /**
      * Limpia los campos del formulario de registro de especialista.
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
      * Muestra una notificación de error en la interfaz.
      *
      * @param titulo  Título del error
      * @param mensaje Detalle del error
      */
    void mostrarError(String titulo, String mensaje);
}
