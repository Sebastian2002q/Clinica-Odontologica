package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.Usuario;
import co.edu.uniquindio.clinicaodontologica.model.enums.RolUsuario;
import co.edu.uniquindio.clinicaodontologica.service.AuthService;
import co.edu.uniquindio.clinicaodontologica.view.contract.ILoginView;

/**
  * Presenter MVP para el flujo de autenticación y registro de cuentas de usuario.
  */
public class LoginPresenter {

    /**
      * Contrato de la vista pasiva de inicio de sesión.
      */
    private final ILoginView view;

    /**
      * Servicio de autenticación y gestión de usuarios.
      */
    private final AuthService authService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view        Vista pasiva que implementa ILoginView
      * @param authService Servicio de autenticación
      */
    public LoginPresenter(ILoginView view, AuthService authService) {
        this.view = view;
        this.authService = authService;
    }

    /**
      * Procesa la solicitud de inicio de sesión del usuario.
      * Si las credenciales son válidas, navega al panel principal; de lo contrario, muestra un mensaje de error.
      *
      * @param username Nombre de usuario ingresado
      * @param password Contraseña ingresada
      */
    public void iniciarSesion(String username, String password) {
        try {
            Usuario usuario = authService.autenticar(username, password);
            view.limpiarCampos();
            view.navegarAlPanelPrincipal(usuario);
        } catch (RuntimeException e) {
            view.mostrarError("Error de Autenticación", e.getMessage());
        }
    }

    /**
      * Procesa el alta de un nuevo operador en el sistema con su correspondiente rol.
      *
      * @param username       Nombre de usuario único
      * @param password       Contraseña de acceso
      * @param nombreCompleto Nombres y apellidos
      * @param rol            Rol de acceso
      */
    public void registrarNuevoUsuario(String username, String password, String nombreCompleto, RolUsuario rol) {
        try {
            Usuario creado = authService.registrarUsuario(username, password, nombreCompleto, rol);
            view.mostrarInformacion("Usuario Registrado",
                    "Se registró exitosamente el usuario '" + creado.getUsername() + "' con rol " + creado.getRol().getEtiqueta() + ".");
        } catch (RuntimeException e) {
            view.mostrarError("Error al Registrar Usuario", e.getMessage());
        }
    }
}
