package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.*;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;
import co.edu.uniquindio.clinicaodontologica.repository.*;
import co.edu.uniquindio.clinicaodontologica.service.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para validar la implementación del patrón de diseño Strategy
 * en el módulo de facturación y control de caja.
 */
class FacturacionStrategyTest {

    private CajaRepository cajaRepository;
    private CitaRepository citaRepository;
    private HistoriaClinicaRepository historiaClinicaRepository;
    private FacturacionService facturacionService;

    private Paciente pacienteTest;
    private Especialista especialistaTest;
    private Cita citaTest;

    @BeforeEach
    void setUp() {
        cajaRepository = new CajaRepository();
        citaRepository = new CitaRepository();
        historiaClinicaRepository = new HistoriaClinicaRepository();
        PacienteRepository pacienteRepository = new PacienteRepository();
        EspecialistaRepository especialistaRepository = new EspecialistaRepository();

        facturacionService = new FacturacionService(cajaRepository, citaRepository, historiaClinicaRepository);

        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(9, 0);

        pacienteTest = new Paciente("1094000111", "Santiago Pérez", "3109998877", "santiago@correo.com");
        pacienteRepository.guardar(pacienteTest);

        especialistaTest = new Especialista("Dr. Daniel Rincón", "Periodoncia", Map.of(fecha, new ArrayList<>(List.of(hora))));
        especialistaRepository.guardar(especialistaTest);

        citaTest = new Cita("CIT-TEST-01", pacienteTest, especialistaTest, "Profilaxis y valoración", fecha, hora);
        citaRepository.guardar(citaTest);
    }

    @Test
    @DisplayName("Efectivo: Debe procesar pago y calcular cambio exacto cuando el monto recibido es suficiente")
    void testPagoEfectivoExitosoConCambio() {
        EstrategiaPago estrategia = new PagoEfectivoStrategy();
        double valorTotal = 60000.0;
        double montoEntregado = 100000.0;

        ResultadoPago resultado = estrategia.procesarPago(valorTotal, montoEntregado, "Billete de $100.000");

        assertNotNull(resultado);
        assertEquals(valorTotal, resultado.getValorCobrado());
        assertEquals(montoEntregado, resultado.getMontoRecibido());
        assertEquals(40000.0, resultado.getCambioDevuelto());
        assertEquals(MetodoPago.EFECTIVO, estrategia.getMetodoPago());
    }

    @Test
    @DisplayName("Efectivo: Debe fallar si el dinero entregado es menor al total de la consulta")
    void testPagoEfectivoMontoInsuficiente() {
        EstrategiaPago estrategia = new PagoEfectivoStrategy();
        double valorTotal = 60000.0;
        double montoEntregado = 40000.0;

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                estrategia.procesarPago(valorTotal, montoEntregado, null));

        assertTrue(ex.getMessage().contains("insuficiente"));
    }

    @Test
    @DisplayName("Tarjeta: Debe procesar el cobro exacto y registrar voucher de autorización")
    void testPagoTarjetaExitoso() {
        EstrategiaPago estrategia = new PagoTarjetaStrategy();
        double valorTotal = 120000.0;

        ResultadoPago resultado = estrategia.procesarPago(valorTotal, valorTotal, "VOUCHER-DATAFONO-778899");

        assertNotNull(resultado);
        assertEquals(valorTotal, resultado.getValorCobrado());
        assertEquals(0.0, resultado.getCambioDevuelto());
        assertEquals("VOUCHER-DATAFONO-778899", resultado.getReferenciaTransaccion());
        assertEquals(MetodoPago.TARJETA, estrategia.getMetodoPago());
    }

    @Test
    @DisplayName("Transferencia: Debe registrar el código digital de Nequi/Daviplata/PSE y exigir referencia")
    void testPagoTransferenciaExitoso() {
        EstrategiaPago estrategia = new PagoTransferenciaStrategy();
        double valorTotal = 85000.0;

        ResultadoPago resultado = estrategia.procesarPago(valorTotal, valorTotal, "NEQUI-TRF-554433");

        assertNotNull(resultado);
        assertEquals(valorTotal, resultado.getValorCobrado());
        assertEquals(0.0, resultado.getCambioDevuelto());
        assertEquals("NEQUI-TRF-554433", resultado.getReferenciaTransaccion());
        assertEquals(MetodoPago.TRANSFERENCIA, estrategia.getMetodoPago());

        // Debe fallar si no se proporciona el número de comprobante
        assertThrows(IllegalArgumentException.class, () ->
                estrategia.procesarPago(valorTotal, valorTotal, "   "));
    }

    @Test
    @DisplayName("Convenio / Seguro: Debe validar número de póliza y registrar liquidación institucional")
    void testPagoConvenioSeguroExitoso() {
        EstrategiaPago estrategia = new PagoConvenioStrategy();
        double valorTotal = 150000.0;

        ResultadoPago resultado = estrategia.procesarPago(valorTotal, valorTotal, "POL-SURA-882104");

        assertNotNull(resultado);
        assertEquals(valorTotal, resultado.getValorCobrado());
        assertEquals(0.0, resultado.getCambioDevuelto());
        assertEquals("POL-SURA-882104", resultado.getReferenciaTransaccion());
        assertEquals(MetodoPago.CONVENIO, estrategia.getMetodoPago());

        // Debe fallar si falta el número de póliza
        assertThrows(IllegalArgumentException.class, () ->
                estrategia.procesarPago(valorTotal, valorTotal, ""));
    }

    @Test
    @DisplayName("FacturacionService (Contexto): Procesa liquidación con Strategy, actualiza estado de cita y caja")
    void testFacturacionServiceIntegraStrategyEnCaja() {
        double saldoInicialCaja = facturacionService.obtenerCajaDiaria().getFlujoCajaTotal();

        Pago pago = facturacionService.registrarPagoConsulta(
                citaTest.getIdCita(),
                new PagoTarjetaStrategy(),
                60000.0,
                "DATAFONO-AUT-4499",
                "Recepcionista Juan"
        );

        assertNotNull(pago);
        assertEquals(EstadoCita.PAGADA, citaTest.getEstado());
        assertEquals("DATAFONO-AUT-4499", pago.getReferenciaTransaccion());
        assertEquals(60000.0, pago.getValorTotal());

        // El flujo de caja aumentó exactamente en el valor cobrado
        assertEquals(saldoInicialCaja + 60000.0, facturacionService.obtenerCajaDiaria().getFlujoCajaTotal());
        assertEquals(60000.0, facturacionService.obtenerCajaDiaria().getTotalPorMetodo(MetodoPago.TARJETA));
    }

    @Test
    @DisplayName("Principio Abierto/Cerrado (OCP): Permite registrar una nueva estrategia personalizada sin modificar FacturacionService")
    void testExtensibilidadOpenClosedPrinciple() {
        // Se define una nueva estrategia en caliente (ej. Bono de Descuento Empresarial)
        EstrategiaPago estrategiaPersonalizada = new EstrategiaPago() {
            @Override
            public MetodoPago getMetodoPago() {
                return MetodoPago.CONVENIO;
            }

            @Override
            public ResultadoPago procesarPago(double valorTotal, double montoEntregado, String referencia) {
                return new ResultadoPago(valorTotal, valorTotal, 0.0, "BONO-EMPRESARIAL-500", "Bono empresarial canjeado");
            }
        };

        // El contexto la registra dinámicamente
        facturacionService.registrarEstrategia(estrategiaPersonalizada);
        assertEquals(estrategiaPersonalizada, facturacionService.obtenerEstrategia(MetodoPago.CONVENIO));

        Pago pago = facturacionService.registrarPagoConsulta(
                citaTest.getIdCita(),
                estrategiaPersonalizada,
                60000.0,
                "BONO-EMPRESARIAL-500",
                "Cajero"
        );

        assertEquals("BONO-EMPRESARIAL-500", pago.getReferenciaTransaccion());
    }
}
