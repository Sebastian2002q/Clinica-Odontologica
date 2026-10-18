package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
  * Repositorio de Especialistas y Odontólogos (equivalente conceptual a FactoryMedico en el modelo de dominio).
  */
public class EspecialistaRepository {

    /**
      * Mapa ordenado de especialistas indexado por su idDoctor único (ej. DOC-1001).
      */
    private final Map<String, Especialista> especialistasPorId = new LinkedHashMap<>();

    /**
      * Guarda o actualiza un especialista en el repositorio.
      *
      * @param especialista Instancia del especialista a persistir
      */
    public void guardar(Especialista especialista) {
        especialistasPorId.put(especialista.getIdDoctor(), especialista);
    }

    /**
      * Busca un doctor especialista por su identificador único.
      *
      * @param idDoctor Identificador del doctor
      * @return Optional con el especialista encontrado o vacío
      */
    public Optional<Especialista> buscarPorId(String idDoctor) {
        if (idDoctor == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(especialistasPorId.get(idDoctor));
    }

    /**
      * Busca un doctor especialista por su nombre completo (ignora mayúsculas y minúsculas).
      *
      * @param nombre Nombre a buscar
      * @return Optional con el especialista si coincide
      */
    public Optional<Especialista> buscarPorNombre(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        return especialistasPorId.values().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }

    /**
      * Elimina un especialista del repositorio por su identificador.
      *
      * @param idDoctor Identificador del especialista a eliminar
      * @return true si el especialista fue encontrado y removido
      */
    public boolean eliminar(String idDoctor) {
        return especialistasPorId.remove(idDoctor) != null;
    }

    /**
      * Retorna la lista con todos los doctores especialistas registrados.
      *
      * @return Lista copia de especialistas
      */
    public List<Especialista> listarTodos() {
        return new ArrayList<>(especialistasPorId.values());
    }
}
