package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Recordatorio;
import co.edu.uniquindio.clinicaodontologica.model.enums.CanalNotificacion;
import co.edu.uniquindio.clinicaodontologica.service.AgendamientoService;
import co.edu.uniquindio.clinicaodontologica.service.RecordatorioService;
import co.edu.uniquindio.clinicaodontologica.view.contract.IRecordatorioView;

import java.time.LocalDateTime;
import java.util.List;

/**
  * Presenter MVP para el cumplimiento de:
  */
public class RecordatorioPresenter {

    /**
      * Contrato de la vista pasiva de notificaciones y recordatorios.
      */
    private final IRecordatorioView view;

    /**
      * Servicio de dominio de recordatorios y mensajería.
      */
    private final RecordatorioService recordatorioService;

    /**
      * Servicio de citas para consultar las consultas agendadas.
      */
    private final AgendamientoService agendamientoService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view                Vista pasiva que implementa IRecordatorioView
      * @param recordatorioService Servicio de recordatorios
      * @param agendamientoService Servicio de citas
      */
    public RecordatorioPresenter(IRecordatorioView view,
                                 RecordatorioService recordatorioService,
                                 AgendamientoService agendamientoService) {
        this.view = view;
        this.recordatorioService = recordatorioService;
        this.agendamientoService = agendamientoService;
    }

    /**
      * Carga y despliega en la vista las citas agendadas y la bitácora histórica de recordatorios emitidos.
      */
    public void cargarDatos() {
        view.mostrarCitasProgramadas(agendamientoService.listarTodasLasCitasActivas());
        view.mostrarHistorialRecordatorios(recordatorioService.listarRecordatoriosEnviados());
    }

    /**
      * Genera y muestra en la vista una previsualización en tiempo real del mensaje que recibiría el paciente.
      *
      * @param cita  Cita seleccionada
      * @param canal Canal digital seleccionado
      */
    public void previsualizarMensaje(Cita cita, CanalNotificacion canal) {
        if (cita == null) {
            return;
        }
        CanalNotificacion canalEfectivo = (canal == null) ? CanalNotificacion.WHATSAPP : canal;
        view.mostrarVistaPreviaMensaje(recordatorioService.formatearMensaje(cita, canalEfectivo));
    }

    /**
      * Ejecuta el barrido automatizado para detectar todas las citas dentro de las 24 horas y enviar notificaciones.
      *
      * @param canal Canal preferente seleccionado
      */
    public void ejecutarBarridoAutomatico24Horas(CanalNotificacion canal) {
        try {
            List<Recordatorio> disparados = recordatorioService.dispararRecordatoriosAutomaticos24Horas(LocalDateTime.now(), canal);
            cargarDatos();
            if (!disparados.isEmpty()) {
                view.mostrarVistaPreviaMensaje(disparados.get(0).getMensajeParametrizado());
            }
            view.mostrarExito("Servicio Automático 24h (F-05)",
                    "Se procesaron las citas de las próximas 24 horas y se dispararon "
                            + disparados.size() + " nuevas notificaciones automáticas.");
        } catch (RuntimeException e) {
            view.mostrarError("Error en Servicio de Recordatorios", e.getMessage());
        }
    }

    /**
      * Procesa el envío manual e individual de un recordatorio para una cita seleccionada.
      *
      * @param cita  Cita a notificar
      * @param canal Canal a utilizar
      */
    public void enviarRecordatorioManual(Cita cita, CanalNotificacion canal) {
        if (cita == null) {
            view.mostrarError("Selección requerida", "Debe seleccionar una cita para enviar el recordatorio.");
            return;
        }
        try {
            Recordatorio enviado = recordatorioService.enviarRecordatorioIndividual(cita.getIdCita(), canal);
            cargarDatos();
            view.mostrarVistaPreviaMensaje(enviado.getMensajeParametrizado());
            view.mostrarExito("Recordatorio Enviado (F-05)",
                    "Notificación enviada exitosamente por " + enviado.getCanal().getEtiqueta()
                            + " a " + enviado.getDestinoContacto() + ".");
        } catch (RuntimeException e) {
            view.mostrarError("Error al Enviar Recordatorio", e.getMessage());
        }
    }
}
