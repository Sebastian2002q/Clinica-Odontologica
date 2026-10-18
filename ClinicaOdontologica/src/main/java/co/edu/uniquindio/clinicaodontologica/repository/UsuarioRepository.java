package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
  * Repositorio en memoria para la gestión y persistencia de Usuarios.
  */
public class UsuarioRepository {

    /**
      * Almacenamiento en memoria de las cuentas de usuario activas.
      */
    private final List<Usuario> usuarios = new ArrayList<>();

    /**
      * Guarda o actualiza un usuario en el repositorio.
      * Si ya existe un usuario con el mismo username, lo reemplaza.
      *
      * @param usuario Instancia del usuario a persistir
      */
    public void guardar(Usuario usuario) {
        usuarios.removeIf(u -> u.getUsername().equalsIgnoreCase(usuario.getUsername()));
        usuarios.add(usuario);
    }

    /**
      * Busca un usuario por su nombre de usuario (ignora mayúsculas y minúsculas).
      *
      * @param username Nombre de usuario a buscar
      * @return Optional con el usuario si existe, o vacío si no se encuentra
      */
    public Optional<Usuario> buscarPorUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return usuarios.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    /**
      * Lista todos los usuarios registrados en el sistema.
      *
      * @return Lista copia con todos los usuarios
      */
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }
}
