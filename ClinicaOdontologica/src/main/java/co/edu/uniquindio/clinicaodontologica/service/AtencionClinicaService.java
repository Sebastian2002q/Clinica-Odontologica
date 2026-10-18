package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.*;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import co.edu.uniquindio.clinicaodontologica.repository.CitaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.HistoriaClinicaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
  * Servicio encargado de registrar los procedimientos odontológicos en la historia clínica y descontar los insumos utilizados.
  */
public class AtencionClinicaService {

    /**
      * Repositorio de historias clínicas y procedimientos.
      */
    private final HistoriaClinicaRepository historiaClinicaRepository;

    /**
      * Repositorio de citas para actualizar el estado a ATENDIDA.
      */
    private final CitaRepository citaRepository;

    /**
      * Servicio de inventario para validar y descontar insumos en tiempo real.
      */
    private final InventarioService inventarioService;

    /**
      * Catálogo local de diagnósticos estándar según CIE-10 con tarifas y tratamientos sugeridos.
      */
    private final List<CatalogoDiagnostico> catalogoDiagnosticos = new ArrayList<>();

    /**
      * Constructor con inyección de dependencias.
      *
      * @param historiaClinicaRepository Repositorio de historias clínicas
      * @param citaRepository            Repositorio de citas
      * @param inventarioService         Servicio de inventario
      */
    public AtencionClinicaService(HistoriaClinicaRepository historiaClinicaRepository,
                                  CitaRepository citaRepository,
                                  InventarioService inventarioService) {
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.citaRepository = citaRepository;
        this.inventarioService = inventarioService;
        inicializarCatalogoEstandar();
    }

    /**
      * Inicializa el catálogo base de diagnósticos estandarizados según la codificación CIE-10.
      */
    private void inicializarCatalogoEstandar() {
        catalogoDiagnosticos.add(new CatalogoDiagnostico("K02.1", "Caries de la dentina", "Obturación dental con resina fotocurada", 120000));
        catalogoDiagnosticos.add(new CatalogoDiagnostico("K04.0", "Pulpitis aguda / reversible", "Endodoncia unirradicular o birradicular", 380000));
        catalogoDiagnosticos.add(new CatalogoDiagnostico("K05.1", "Gingivitis crónica", "Profilaxis y detartraje ultrasónico", 95000));
        catalogoDiagnosticos.add(new CatalogoDiagnostico("K05.3", "Periodontitis crónica", "Raspaje y alisado radicular por cuadrante", 210000));
        catalogoDiagnosticos.add(new CatalogoDiagnostico("K07.2", "Anomalías de la relación entre arcadas", "Control mensual de ortodoncia y ajuste de arco", 150000));
        catalogoDiagnosticos.add(new CatalogoDiagnostico("K01.1", "Diente impactado / tercer molar", "Exodoncia quirúrgica de tercer molar", 320000));
        catalogoDiagnosticos.add(new CatalogoDiagnostico("K03.6", "Depósitos (acreciones) en los dientes", "Limpieza dental profunda y fluorización", 85000));
        catalogoDiagnosticos.add(new CatalogoDiagnostico("Z01.2", "Examen odontológico general / Estética", "Blanqueamiento dental LED / Diseño de sonrisa", 450000));
    }

    /**
      * Retorna una copia de la lista de diagnósticos estandarizados disponibles en el catálogo.
      *
      * @return Lista de CatalogoDiagnostico
      */
    public List<CatalogoDiagnostico> obtenerCatalogoDiagnosticos() {
        return new ArrayList<>(catalogoDiagnosticos);
    }

    /**
      * Registra formalmente un procedimiento clínico odontológico cumpliendo con RN-003 y RN-004.
      *
      * @param idCita              Identificador de la cita médica activa
      * @param diagnosticoEstandar Diagnóstico CIE-10
      * @param tratamientoAplicado Tratamiento odontológico realizado
      * @param evolucionMedica     Notas médicas y recomendaciones
      * @param valorProcedimiento  Tarifa económica a cobrar
      * @param insumosDeclarados   Lista de insumos odontológicos utilizados
      * @return ProcedimientoClinico registrado formalmente
      * @throws IllegalArgumentException si falta algún campo obligatorio o la tarifa es inválida
      * @throws IllegalStateException    si la cita está cancelada o no hay stock suficiente de insumos
      */
    public ProcedimientoClinico registrarProcedimiento(String idCita,
                                                       String diagnosticoEstandar,
                                                       String tratamientoAplicado,
                                                       String evolucionMedica,
                                                       double valorProcedimiento,
                                                       List<ConsumoInsumo> insumosDeclarados) {
        if (idCita == null || idCita.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una cita activa para registrar el procedimiento.");
        }
        if (diagnosticoEstandar == null || diagnosticoEstandar.isBlank()
                || tratamientoAplicado == null || tratamientoAplicado.isBlank()
                || evolucionMedica == null || evolucionMedica.isBlank()) {
            throw new IllegalArgumentException("El diagnóstico, el tratamiento aplicado y la evolución médica son obligatorios.");
        }
        if (valorProcedimiento <= 0) {
            throw new IllegalArgumentException("El valor del procedimiento debe ser mayor a cero.");
        }

        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("No existe la cita con ID: " + idCita));

        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.NO_ASISTIDA) {
            throw new IllegalStateException("No se puede registrar procedimiento sobre una cita cancelada o sin asistencia.");
        }

        List<ConsumoInsumo> consumosConfirmados = new ArrayList<>();
        if (insumosDeclarados != null && !insumosDeclarados.isEmpty()) {
            // Primero se verifica que todos los insumos tengan stock suficiente antes de descontar
            for (ConsumoInsumo consumo : insumosDeclarados) {
                Insumo insumo = inventarioService.buscarPorCodigo(consumo.getCodigoInsumo())
                        .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + consumo.getCodigoInsumo()));
                if (!insumo.tieneStockSuficiente(consumo.getCantidad())) {
                    throw new IllegalStateException("Stock insuficiente para '" + insumo.getNombre()
                            + "'. Disponible: " + insumo.getStockActual() + ", solicitado: " + consumo.getCantidad());
                }
            }
            // RN-004 y F-06: Descontar en tiempo real cada insumo utilizado
            for (ConsumoInsumo consumo : insumosDeclarados) {
                inventarioService.descontarStockEnTiempoReal(consumo.getCodigoInsumo(), consumo.getCantidad());
                consumosConfirmados.add(consumo);
            }
        }

        Paciente paciente = cita.getPaciente();
        HistoriaClinica historia = historiaClinicaRepository.buscarPorCedulaPaciente(paciente.getCedula())
                .orElseGet(() -> {
                    HistoriaClinica nuevaHistoria = new HistoriaClinica(paciente);
                    historiaClinicaRepository.guardar(nuevaHistoria);
                    return nuevaHistoria;
                });

        String idProcedimiento = historiaClinicaRepository.generarSiguienteIdProcedimiento();
        ProcedimientoClinico procedimiento = new ProcedimientoClinico(
                idProcedimiento,
                cita.getIdCita(),
                paciente.getCedula(),
                paciente.getNombreCompleto(),
                cita.getEspecialista().getNombre(),
                LocalDateTime.now(),
                diagnosticoEstandar.trim(),
                tratamientoAplicado.trim(),
                evolucionMedica.trim(),
                valorProcedimiento,
                consumosConfirmados,
                false
        );

        // RN-003 y F-03: Indexar en la historia clínica y actualizar estado a ACTUALIZADA
        historia.agregarProcedimiento(procedimiento);
        historiaClinicaRepository.guardar(historia);

        cita.marcarAtendida();
        citaRepository.guardar(cita);

        return procedimiento;
    }

    /**
      * Registra la no asistencia del paciente a su cita y libera el horario en la agenda del especialista.
      *
      * @param idCita Identificador de la cita médica
      * @throws IllegalArgumentException si la cita no existe
      */
    public void marcarCitaNoAsistida(String idCita) {
        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada: " + idCita));
        cita.marcarNoAsistida();
        cita.getEspecialista().getAgenda().liberarBloque(cita.getFecha(), cita.getHora());
        citaRepository.guardar(cita);
    }

    /**
      * Obtiene el expediente de historia clínica de un paciente según su número de cédula.
      *
      * @param cedulaPaciente Cédula del paciente
      * @return HistoriaClinica correspondiente
      * @throws IllegalArgumentException si no existe historia clínica para esa cédula
      */
    public HistoriaClinica obtenerHistoriaClinicaPaciente(String cedulaPaciente) {
        return historiaClinicaRepository.buscarPorCedulaPaciente(cedulaPaciente)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró historia clínica para la cédula " + cedulaPaciente));
    }

    /**
      * Lista todos los procedimientos odontológicos que fueron practicados en una cita médica dada.
      *
      * @param idCita Identificador de la cita médica
      * @return Lista de procedimientos vinculados
      */
    public List<ProcedimientoClinico> listarProcedimientosDeCita(String idCita) {
        return historiaClinicaRepository.buscarProcedimientosPorCita(idCita);
    }
}
