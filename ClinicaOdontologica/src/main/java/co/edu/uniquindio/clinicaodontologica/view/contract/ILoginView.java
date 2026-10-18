package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.Usuario;

/**
  * Contrato de Vista Pasiva (MVP) para la pantalla de Autenticación e Inicio de Sesión.
  */
public interface ILoginView {

    /**
      * Limpia los campos de texto del formulario de acceso.
      */
    void limpiarCampos();

    /**
      * Navega y da paso al panel de control principal con el usuario autenticado.
      *
      * @param usuario Usuario autenticado
      */
    void navegarAlPanelPrincipal(Usuario usuario);

    /**
      * Muestra una notificación informativa en pantalla.
      *
      * @param titulo  Título de la alerta
      * @param mensaje Mensaje descriptivo
      */
    void mostrarInformacion(String titulo, String mensaje);

    /**
      * Despliega una alerta de error en la interfaz.
      *
      * @param titulo  Título del error
      * @param mensaje Detalle del error
      */
    void mostrarError(String titulo, String mensaje);
}
