package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.Usuario;
import co.edu.uniquindio.clinicaodontologica.model.enums.RolUsuario;
import co.edu.uniquindio.clinicaodontologica.repository.UsuarioRepository;
import lombok.Getter;

import java.util.Optional;

/**
  * Servicio encargado de validar las credenciales de inicio de sesión y gestionar los usuarios del sistema.
  */
public class AuthService {

    /**
      * Repositorio de persistencia de usuarios.
      */
    private final UsuarioRepository usuarioRepository;

    /**
      * Usuario que ha iniciado sesión actualmente en el sistema.
      */
    @Getter
    private Usuario usuarioAutenticado;

    /**
      * Constructor con inyección de dependencias del repositorio.
      *
      * @param usuarioRepository Repositorio de usuarios
      */
    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
      * Autentica las credenciales de un usuario verificando existencia y coincidencia de contraseña.
      *
      * @param username Nombre de usuario ingresado
      * @param password Contraseña ingresada
      * @return Usuario autenticado si las credenciales son válidas
      * @throws IllegalArgumentException si los campos están vacíos o las credenciales no coinciden
      */
    public Usuario autenticar(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar usuario y contraseña.");
        }
        Optional<Usuario> usuarioEncontrado = usuarioRepository.buscarPorUsername(username.trim());
        if (usuarioEncontrado.isEmpty() || !usuarioEncontrado.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        }
        this.usuarioAutenticado = usuarioEncontrado.get();
        return this.usuarioAutenticado;
    }

    /**
      * Registra un nuevo usuario operador en el sistema con un rol específico.
      *
      * @param username       Nombre de usuario único
      * @param password       Contraseña de acceso
      * @param nombreCompleto Nombres y apellidos completos
      * @param rol            Rol de acceso asignado
      * @return Usuario recién creado y persistido
      * @throws IllegalArgumentException si falta algún campo obligatorio
      * @throws IllegalStateException    si el username ya se encuentra en uso
      */
    public Usuario registrarUsuario(String username, String password, String nombreCompleto, RolUsuario rol) {
        if (username == null || username.isBlank() || password == null || password.isBlank()
                || nombreCompleto == null || nombreCompleto.isBlank() || rol == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios para registrar un usuario.");
        }
        if (usuarioRepository.buscarPorUsername(username.trim()).isPresent()) {
            throw new IllegalStateException("El nombre de usuario '" + username.trim() + "' ya existe.");
        }
        Usuario nuevoUsuario = new Usuario(username.trim(), password, nombreCompleto.trim(), rol);
        usuarioRepository.guardar(nuevoUsuario);
        return nuevoUsuario;
    }

    /**
      * Cierra la sesión activa actual del usuario en la aplicación.
      */
    public void cerrarSesion() {
        this.usuarioAutenticado = null;
    }
}
