package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.Insumo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
  * Repositorio para la gestión del Almacén e Inventario de Insumos Odontológicos.
  */
public class InventarioRepository {

    /**
      * Mapa de insumos médicos indexados por su código de referencia en mayúsculas (ej. INS-001).
      */
    private final Map<String, Insumo> insumosPorCodigo = new LinkedHashMap<>();

    /**
      * Guarda o actualiza un insumo en el almacén.
      *
      * @param insumo Insumo odontológico a persistir
      */
    public void guardar(Insumo insumo) {
        insumosPorCodigo.put(insumo.getCodigo().trim().toUpperCase(), insumo);
    }

    /**
      * Busca un insumo por su código de referencia.
      *
      * @param codigo Código de referencia (ej. INS-001)
      * @return Optional con el insumo si existe
      */
    public Optional<Insumo> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(insumosPorCodigo.get(codigo.trim().toUpperCase()));
    }

    /**
      * Busca un insumo por su nombre descriptivo (ignora mayúsculas y minúsculas).
      *
      * @param nombre Nombre del insumo
      * @return Optional con el insumo coincidente
      */
    public Optional<Insumo> buscarPorNombre(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        return insumosPorCodigo.values().stream()
                .filter(i -> i.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }

    /**
      * Retorna la lista con todos los insumos almacenados en el inventario.
      *
      * @return Lista copia de todos los insumos
      */
    public List<Insumo> listarTodos() {
        return new ArrayList<>(insumosPorCodigo.values());
    }
}
