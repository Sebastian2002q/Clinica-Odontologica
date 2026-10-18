package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Pago;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;
import co.edu.uniquindio.clinicaodontologica.service.AgendamientoService;
import co.edu.uniquindio.clinicaodontologica.service.FacturacionService;
import co.edu.uniquindio.clinicaodontologica.view.contract.IFacturacionView;

import java.util.List;
import java.util.stream.Collectors;

/**
  * Presenter MVP para el cumplimiento de:
  */
public class FacturacionPresenter {

    /**
      * Contrato de la vista pasiva de facturación y caja.
      */
    private final IFacturacionView view;

    /**
      * Servicio de dominio de facturación y flujo de caja.
      */
    private final FacturacionService facturacionService;

    /**
      * Servicio de citas para cargar las consultas pendientes de cobro.
      */
    private final AgendamientoService agendamientoService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view               Vista pasiva que implementa IFacturacionView
      * @param facturacionService Servicio de facturación y pagos
      * @param agendamientoService Servicio de citas
      */
    public FacturacionPresenter(IFacturacionView view,
                                FacturacionService facturacionService,
                                AgendamientoService agendamientoService) {
        this.view = view;
        this.facturacionService = facturacionService;
        this.agendamientoService = agendamientoService;
    }

    /**
      * Consulta y actualiza en la vista las citas pendientes de cobro y el resumen del flujo de caja diario.
      */
    public void cargarEstadoFacturacion() {
        List<Cita> citasPendientes = agendamientoService.listarTodasLasCitasActivas().stream()
                .filter(c -> c.getEstado() != EstadoCita.PAGADA && c.getEstado() != EstadoCita.NO_ASISTIDA)
                .collect(Collectors.toList());
        view.mostrarCitasPendientesDeCobro(citasPendientes);
        view.actualizarPanelFlujoCaja(facturacionService.obtenerCajaDiaria(), facturacionService.listarPagosRegistrados());
    }

    /**
      * Maneja el evento de selección de una consulta para cobro, calculando su desglose y valor total.
      *
      * @param cita Cita seleccionada
      */
    public void seleccionarCitaParaCobro(Cita cita) {
        if (cita == null) {
            return;
        }
        String desglose = facturacionService.construirDesgloseConsulta(cita);
        double total = facturacionService.calcularValorTotalConsulta(cita.getIdCita());
        view.mostrarDesgloseYValorTotal(desglose, total);
    }

    /**
      * Procesa la captura del pago en caja, valida el monto entregado y emite el recibo oficial.
      *
      * @param cita                Cita médica asociada
      * @param metodoPago          Método de pago (EFECTIVO, TARJETA, TRANSFERENCIA)
      * @param montoRecibidoTexto  Monto de dinero digitado en la interfaz
      * @param auxiliarResponsable Nombre del operador de caja
      */
    public void registrarPago(Cita cita, MetodoPago metodoPago, String montoRecibidoTexto, String auxiliarResponsable) {
        if (cita == null) {
            view.mostrarError("Selección requerida", "Debe seleccionar la consulta o cita a cobrar.");
            return;
        }
        try {
            double montoRecibido = Double.parseDouble(montoRecibidoTexto.trim());
            Pago pago = facturacionService.registrarPagoConsulta(
                    cita.getIdCita(),
                    metodoPago,
                    montoRecibido,
                    auxiliarResponsable
            );
            view.mostrarComprobanteGenerado(pago.generarTextoComprobante());
            cargarEstadoFacturacion();
            view.mostrarExito("Pago Registrado en Caja (F-04 / RN-005)",
                    "Comprobante " + pago.getNumeroComprobante() + " generado por $"
                            + String.format("%,.0f", pago.getValorTotal())
                            + ".\nEl flujo de caja diario aumentó exactamente a $"
                            + String.format("%,.0f", facturacionService.obtenerCajaDiaria().getFlujoCajaTotal()) + ".");
        } catch (NumberFormatException e) {
            view.mostrarError("Monto inválido", "Debe ingresar un valor numérico válido para el monto recibido.");
        } catch (RuntimeException e) {
            view.mostrarError("Error de Facturación (RN-005)", e.getMessage());
        }
    }
}
