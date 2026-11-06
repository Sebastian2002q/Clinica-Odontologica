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
import co.edu.uniquindio.clinicaodontologica.service.strategy.*;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio encargado de registrar los pagos de las consultas y calcular el consolidado de caja.
 * Actúa como Contexto dentro del patrón de diseño Strategy, delegando el cálculo y las reglas
 * de cada medio de pago a su respectiva implementación de EstrategiaPago.
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
     * Registro de estrategias de pago configuradas en el sistema (Patrón Strategy).
     */
    private final Map<MetodoPago, EstrategiaPago> estrategias = new EnumMap<>(MetodoPago.class);

    /**
     * Constructor con inyección de dependencias e inicialización de estrategias por defecto.
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

        // Registro de estrategias de pago por defecto (cumpliendo OCP)
        registrarEstrategia(new PagoEfectivoStrategy());
        registrarEstrategia(new PagoTarjetaStrategy());
        registrarEstrategia(new PagoTransferenciaStrategy());
        registrarEstrategia(new PagoConvenioStrategy());
    }

    /**
     * Registra o sustituye una estrategia de pago en el contexto.
     *
     * @param estrategia Implementación de EstrategiaPago a registrar
     */
    public void registrarEstrategia(EstrategiaPago estrategia) {
        if (estrategia != null && estrategia.getMetodoPago() != null) {
            this.estrategias.put(estrategia.getMetodoPago(), estrategia);
        }
    }

    /**
     * Obtiene la estrategia correspondiente a un método de pago.
     *
     * @param metodoPago Tipo de método de pago solicitado
     * @return Estrategia concreta encargada de procesarlo
     * @throws IllegalArgumentException si no existe una estrategia para el método
     */
    public EstrategiaPago obtenerEstrategia(MetodoPago metodoPago) {
        EstrategiaPago estrategia = estrategias.get(metodoPago);
        if (estrategia == null) {
            throw new IllegalArgumentException("No hay una estrategia de pago configurada para: " + metodoPago);
        }
        return estrategia;
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
     * Registra el pago formal de una cita médica aplicando el patrón Strategy.
     *
     * @param idCita              Identificador de la cita médica a liquidar
     * @param estrategia          Estrategia de pago a aplicar
     * @param montoEntregado      Dinero entregado o valor reportado
     * @param referencia          Código de voucher, referencia digital o comprobante
     * @param auxiliarResponsable Nombre del auxiliar de recepción que realiza el cobro
     * @return Pago formalmente registrado con su comprobante oficial emitido
     * @throws IllegalArgumentException si falta algún dato requerido
     * @throws IllegalStateException    si la cita ya está pagada o las reglas de la estrategia fallan
     */
    public Pago registrarPagoConsulta(String idCita, EstrategiaPago estrategia, double montoEntregado, String referencia, String auxiliarResponsable) {
        if (idCita == null || idCita.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una cita o consulta para registrar el pago.");
        }
        if (estrategia == null) {
            throw new IllegalArgumentException("Debe proporcionar una estrategia de pago válida.");
        }
        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita " + idCita));

        if (cita.getEstado() == EstadoCita.PAGADA || cajaRepository.buscarPagoPorCita(idCita).isPresent()) {
            throw new IllegalStateException("La cita " + idCita + " ya cuenta con un pago registrado.");
        }

        double valorTotal = calcularValorTotalConsulta(idCita);

        // Delegación de la lógica de procesamiento a la Estrategia (Strategy Pattern)
        ResultadoPago resultado = estrategia.procesarPago(valorTotal, montoEntregado, referencia);

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
                resultado.getValorCobrado(),
                resultado.getMontoRecibido(),
                resultado.getCambioDevuelto(),
                estrategia.getMetodoPago(),
                resultado.getReferenciaTransaccion(),
                LocalDateTime.now(),
                responsable
        );

        // Sumar exactamente el valor cobrado al flujo de caja diario activo
        cajaRepository.guardarPago(pago);

        List<ProcedimientoClinico> procedimientos = historiaClinicaRepository.buscarProcedimientosPorCita(idCita);
        procedimientos.forEach(p -> p.setPagado(true));

        cita.marcarPagada();
        citaRepository.guardar(cita);

        return pago;
    }

    /**
     * Sobrecarga de conveniencia que resuelve automáticamente la estrategia a partir del enum MetodoPago.
     *
     * @param idCita              Identificador de la cita médica a liquidar
     * @param metodoPago          Método de pago utilizado
     * @param montoRecibido       Dinero entregado por el cliente
     * @param auxiliarResponsable Nombre del auxiliar de recepción que realiza el cobro
     * @return Pago formalmente registrado
     */
    public Pago registrarPagoConsulta(String idCita, MetodoPago metodoPago, double montoRecibido, String auxiliarResponsable) {
        return registrarPagoConsulta(idCita, obtenerEstrategia(metodoPago), montoRecibido, "", auxiliarResponsable);
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
