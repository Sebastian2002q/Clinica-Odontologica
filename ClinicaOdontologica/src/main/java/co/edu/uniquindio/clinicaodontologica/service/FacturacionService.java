package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.Caja;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Pago;
import co.edu.uniquindio.clinicaodontologica.model.entity.ProcedimientoClinico;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;
import co.edu.uniquindio.clinicaodontologica.repository.CajaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.CitaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.HistoriaClinicaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
  * Servicio encargado de registrar los pagos de las consultas y calcular el consolidado de caja.
  */
public class FacturacionService {

    /**
      * Tarifa monetaria base aplicada para consultas de valoración general cuando no se registran procedimientos especializados.
      */
    private static final double TARIFA_VALORACION_BASE = 60000.0;

    /**
      * Repositorio de caja para persistir pagos y consultar balances diarios.
      */
    private final CajaRepository cajaRepository;

    /**
      * Repositorio de citas para validar existencia y actualizar estado a PAGADA.
      */
    private final CitaRepository citaRepository;

    /**
      * Repositorio de historias clínicas para obtener los procedimientos practicados en la cita.
      */
    private final HistoriaClinicaRepository historiaClinicaRepository;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param cajaRepository            Repositorio de caja
      * @param citaRepository            Repositorio de citas
      * @param historiaClinicaRepository Repositorio de historias clínicas
      */
    public FacturacionService(CajaRepository cajaRepository,
                              CitaRepository citaRepository,
                              HistoriaClinicaRepository historiaClinicaRepository) {
        this.cajaRepository = cajaRepository;
        this.citaRepository = citaRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
    }

    /**
      * Calcula el valor total de la consulta sumando las tarifas de los procedimientos aplicados.
      * Si no se realizaron procedimientos adicionales, retorna la tarifa base de valoración odontológica.
      *
      * @param idCita Identificador de la cita médica
      * @return Monto monetario total a pagar
      */
    public double calcularValorTotalConsulta(String idCita) {
        List<ProcedimientoClinico> procedimientos = historiaClinicaRepository.buscarProcedimientosPorCita(idCita);
        if (procedimientos.isEmpty()) {
            return TARIFA_VALORACION_BASE;
        }
        return procedimientos.stream()
                .mapToDouble(ProcedimientoClinico::getValorProcedimiento)
                .sum();
    }

    /**
      * Construye el desglose textual detallado de los conceptos cobrados en la consulta.
      *
      * @param cita Cita médica asociada
      * @return Cadena con el desglose línea a línea de tratamientos y valores
      */
    public String construirDesgloseConsulta(Cita cita) {
        List<ProcedimientoClinico> procedimientos = historiaClinicaRepository.buscarProcedimientosPorCita(cita.getIdCita());
        if (procedimientos.isEmpty()) {
            return "- Valoración Odontológica General (" + cita.getMotivo() + "): $"
                    + String.format("%,.0f", TARIFA_VALORACION_BASE);
        }
        return procedimientos.stream()
                .map(p -> "- " + p.getDiagnosticoEstandar() + " | " + p.getTratamientoAplicado()
                        + ": $" + String.format("%,.0f", p.getValorProcedimiento()))
                .collect(Collectors.joining("\n"));
    }

    /**
      * Registra el pago formal de una cita cumpliendo con RN-005 y F-04.
      *
      * @param idCita              Identificador de la cita médica a liquidar
      * @param metodoPago          Método de pago utilizado (EFECTIVO, TARJETA, TRANSFERENCIA)
      * @param montoRecibido       Dinero entregado por el cliente
      * @param auxiliarResponsable Nombre del auxiliar de recepción que realiza el cobro
      * @return Pago formalmente registrado con su comprobante oficial emitido
      * @throws IllegalArgumentException si los datos requeridos no son válidos
      * @throws IllegalStateException    si la cita ya está pagada o el monto recibido es insuficiente
      */
    public Pago registrarPagoConsulta(String idCita, MetodoPago metodoPago, double montoRecibido, String auxiliarResponsable) {
        if (idCita == null || idCita.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una cita o consulta para registrar el pago.");
        }
        if (metodoPago == null) {
            throw new IllegalArgumentException("Debe seleccionar un método de pago (Efectivo, Tarjeta o Transferencia).");
        }
        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita " + idCita));

        if (cita.getEstado() == EstadoCita.PAGADA || cajaRepository.buscarPagoPorCita(idCita).isPresent()) {
            throw new IllegalStateException("La cita " + idCita + " ya cuenta con un pago registrado.");
        }

        double valorTotal = calcularValorTotalConsulta(idCita);
        if (montoRecibido < valorTotal) {
            throw new IllegalStateException(
                    String.format("Monto insuficiente: El valor total es $%,.0f y se recibió $%,.0f. Faltan $%,.0f.",
                            valorTotal, montoRecibido, (valorTotal - montoRecibido)));
        }

        double cambio = montoRecibido - valorTotal;
        String idPago = cajaRepository.generarSiguienteIdPago();
        String numeroComprobante = cajaRepository.generarSiguienteComprobante();
        String desglose = construirDesgloseConsulta(cita);
        String responsable = (auxiliarResponsable == null || auxiliarResponsable.isBlank())
                ? "Auxiliar Administrativo"
                : auxiliarResponsable.trim();

        Pago pago = new Pago(
                idPago,
                numeroComprobante,
                cita.getIdCita(),
                cita.getPaciente().getCedula(),
                cita.getPaciente().getNombreCompleto(),
                desglose,
                valorTotal,
                montoRecibido,
                cambio,
                metodoPago,
                LocalDateTime.now(),
                responsable
        );

        // RN-005 y F-04: sumar exactamente el valor cobrado al flujo de caja diario activo
        cajaRepository.guardarPago(pago);

        List<ProcedimientoClinico> procedimientos = historiaClinicaRepository.buscarProcedimientosPorCita(idCita);
        procedimientos.forEach(p -> p.setPagado(true));

        cita.marcarPagada();
        citaRepository.guardar(cita);

        return pago;
    }

    /**
      * Retorna la entidad Caja activa que consolida el flujo diario de la clínica.
      *
      * @return Caja activa
      */
    public Caja obtenerCajaDiaria() {
        return cajaRepository.obtenerCajaActiva();
    }

    /**
      * Lista todos los pagos registrados cronológicamente en caja.
      *
      * @return Lista no modificable de pagos
      */
    public List<Pago> listarPagosRegistrados() {
        return cajaRepository.listarPagos();
    }
}
