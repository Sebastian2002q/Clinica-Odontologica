package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.Insumo;
import co.edu.uniquindio.clinicaodontologica.model.enums.NivelAlertaStock;
import co.edu.uniquindio.clinicaodontologica.repository.InventarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  * Servicio encargado de gestionar el inventario de insumos odontológicos y el control de stock mínimo.
  */
public class InventarioService {

    /**
      * Repositorio para la persistencia y búsqueda de insumos.
      */
    private final InventarioRepository inventarioRepository;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param inventarioRepository Repositorio de inventario
      */
    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    /**
      * Da de alta un nuevo insumo odontológico en el inventario de la clínica.
      *
      * @param codigo             Código de referencia único (ej. INS-001)
      * @param nombre             Nombre del material o fármaco
      * @param categoria          Categoría estándar del almacén
      * @param unidadMedida       Unidad física de empaque
      * @param stockActual        Cantidad física inicial disponible
      * @param stockMinimoCritico Umbral de stock mínimo de seguridad
      * @param costoUnitario      Costo unitario de compra
      * @return Insumo persistido en el inventario
      * @throws IllegalArgumentException si los campos obligatorios están vacíos o los números son negativos
      */
    public Insumo registrarInsumo(String codigo, String nombre, String categoria, String unidadMedida,
                                  int stockActual, int stockMinimoCritico, double costoUnitario) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()
                || categoria == null || categoria.isBlank() || unidadMedida == null || unidadMedida.isBlank()) {
            throw new IllegalArgumentException("Todos los datos descriptivos del insumo son obligatorios.");
        }
        if (stockActual < 0 || stockMinimoCritico < 0 || costoUnitario < 0) {
            throw new IllegalArgumentException("El stock actual, stock mínimo y costo no pueden ser negativos.");
        }
        Insumo insumo = new Insumo(
                codigo.trim().toUpperCase(),
                nombre.trim(),
                categoria.trim(),
                unidadMedida.trim(),
                stockActual,
                stockMinimoCritico,
                costoUnitario
        );
        inventarioRepository.guardar(insumo);
        return insumo;
    }

    /**
      * Descuenta existencias en tiempo real del almacén cuando un procedimiento médico consume insumos.
      *
      * @param codigoInsumo      Código del insumo a descontar
      * @param cantidadUtilizada Cantidad consumida durante la atención médica
      * @return Insumo con el stock actualizado
      * @throws IllegalArgumentException si el insumo no existe
      */
    public Insumo descontarStockEnTiempoReal(String codigoInsumo, int cantidadUtilizada) {
        Insumo insumo = inventarioRepository.buscarPorCodigo(codigoInsumo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el insumo con código: " + codigoInsumo));
        insumo.descontarStock(cantidadUtilizada);
        inventarioRepository.guardar(insumo);
        return insumo;
    }

    /**
      * Incrementa existencias por entrada o reabastecimiento de suministros al almacén.
      *
      * @param codigoInsumo      Código del insumo
      * @param cantidadAdicional Cantidad nueva ingresada
      * @return Insumo con el stock reabastecido
      * @throws IllegalArgumentException si el insumo no existe
      */
    public Insumo reabastecerStock(String codigoInsumo, int cantidadAdicional) {
        Insumo insumo = inventarioRepository.buscarPorCodigo(codigoInsumo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el insumo con código: " + codigoInsumo));
        insumo.agregarStock(cantidadAdicional);
        inventarioRepository.guardar(insumo);
        return insumo;
    }

    /**
      * Busca un insumo por su código de referencia.
      *
      * @param codigo Código a buscar
      * @return Optional con el insumo encontrado
      */
    public Optional<Insumo> buscarPorCodigo(String codigo) {
        return inventarioRepository.buscarPorCodigo(codigo);
    }

    /**
      * Retorna la lista con la totalidad de los insumos odontológicos registrados.
      *
      * @return Lista completa de insumos
      */
    public List<Insumo> listarInsumos() {
        return inventarioRepository.listarTodos();
    }

    /**
      * Filtra y retorna los insumos que se encuentran en niveles de alerta preventiva (Amarillo) o crítica (Rojo).
      *
      * @return Lista de insumos bajo alerta de stock
      */
    public List<Insumo> listarInsumosConAlerta() {
        return inventarioRepository.listarTodos().stream()
                .filter(i -> i.getNivelAlerta() != NivelAlertaStock.NORMAL)
                .collect(Collectors.toList());
    }
}
