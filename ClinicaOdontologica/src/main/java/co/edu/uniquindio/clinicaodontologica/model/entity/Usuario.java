package co.edu.uniquindio.clinicaodontologica.model.entity;

import co.edu.uniquindio.clinicaodontologica.model.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
  * Entidad de dominio que representa a un Usuario del sistema con credenciales de acceso.
  */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    /**
      * Nombre de usuario único para inicio de sesión en el sistema.
      */
    @EqualsAndHashCode.Include
    private String username;

    /**
      * Contraseña de autenticación del usuario.
      */
    private String password;

    /**
      * Nombre completo y apellidos legibles del usuario operador.
      */
    private String nombreCompleto;

    /**
      * Rol asignado que define los privilegios en la aplicación (ADMINISTRADOR, AUXILIAR, DOCTOR).
      */
    private RolUsuario rol;

    /**
      * Constructor por defecto para auxiliares administrativos rápidos.
      *
      * @param username Identificador de acceso
      * @param password Contraseña de acceso
      */
    public Usuario(String username, String password) {
        this(username, password, username, RolUsuario.AUXILIAR_ADMINISTRATIVO);
    }

    /**
      * Representación textual amigable para componentes de selección en la interfaz gráfica.
      *
      * @return Cadena con el nombre y el rol entre paréntesis
      */
    @Override
    public String toString() {
        return nombreCompleto + " (" + rol.getEtiqueta() + ")";
    }
}
