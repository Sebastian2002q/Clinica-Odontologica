package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
  * Repositorio de Pacientes (equivalente conceptual a FactoryUsuarios en el modelo de dominio).
  */
public class PacienteRepository {

    /**
      * Mapa ordenado de pacientes indexado por su cédula de ciudadanía única.
      */
    private final Map<String, Paciente> pacientesPorCedula = new LinkedHashMap<>();

    /**
      * Verifica si ya existe un paciente registrado con el número de cédula indicado.
      *
      * @param cedula Cédula a verificar
      * @return true si el paciente ya se encuentra en el repositorio
      */
    public boolean existePorCedula(String cedula) {
        if (cedula == null) {
            return false;
        }
        return pacientesPorCedula.containsKey(cedula.trim());
    }

    /**
      * Guarda o actualiza un paciente en el repositorio indexándolo por su cédula.
      *
      * @param paciente Paciente a persistir
      */
    public void guardar(Paciente paciente) {
        pacientesPorCedula.put(paciente.getCedula().trim(), paciente);
    }

    /**
      * Busca un paciente por su cédula de ciudadanía.
      *
      * @param cedula Cédula del paciente a buscar
      * @return Optional con el paciente si existe, o vacío en caso contrario
      */
    public Optional<Paciente> buscarPorCedula(String cedula) {
        if (cedula == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(pacientesPorCedula.get(cedula.trim()));
    }

    /**
      * Lista todos los pacientes registrados en orden de inserción.
      *
      * @return Lista copia con todos los pacientes
      */
    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientesPorCedula.values());
    }
}
