package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.*;
import co.edu.uniquindio.clinicaodontologica.service.AgendamientoService;
import co.edu.uniquindio.clinicaodontologica.service.AtencionClinicaService;
import co.edu.uniquindio.clinicaodontologica.service.InventarioService;
import co.edu.uniquindio.clinicaodontologica.view.contract.IAtencionClinicaView;

import java.util.List;

/**
  * Presenter MVP para el cumplimiento de:
  */
public class AtencionClinicaPresenter {

    /**
      * Contrato de la vista pasiva de atención clínica.
      */
    private final IAtencionClinicaView view;

    /**
      * Servicio de dominio de atención clínica e historias clínicas.
      */
    private final AtencionClinicaService atencionClinicaService;

    /**
      * Servicio de citas para cargar las citas activas pendientes por atención.
      */
    private final AgendamientoService agendamientoService;

    /**
      * Servicio de inventario para desplegar los insumos médicos disponibles para uso.
      */
    private final InventarioService inventarioService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view                   Vista pasiva que implementa IAtencionClinicaView
      * @param atencionClinicaService Servicio de atención médica
      * @param agendamientoService    Servicio de citas
      * @param inventarioService      Servicio de inventario
      */
    public AtencionClinicaPresenter(IAtencionClinicaView view,
                                    AtencionClinicaService atencionClinicaService,
                                    AgendamientoService agendamientoService,
                                    InventarioService inventarioService) {
        this.view = view;
        this.atencionClinicaService = atencionClinicaService;
        this.agendamientoService = agendamientoService;
        this.inventarioService = inventarioService;
    }

    /**
      * Carga y refresca los datos iniciales de la pantalla (citas activas, catálogo CIE-10 e insumos).
      */
    public void cargarDatosIniciales() {
        view.mostrarCitasActivas(agendamientoService.listarTodasLasCitasActivas());
        view.mostrarCatalogoDiagnosticos(atencionClinicaService.obtenerCatalogoDiagnosticos());
        view.mostrarInsumosDisponibles(inventarioService.listarInsumos());
    }

    /**
      * Maneja el evento de selección de una cita médica, consultando y mostrando la historia clínica del paciente.
      *
      * @param cita Cita seleccionada
      */
    public void seleccionarCita(Cita cita) {
        if (cita == null) {
            return;
        }
        try {
            HistoriaClinica historiaClinica = atencionClinicaService.obtenerHistoriaClinicaPaciente(cita.getPaciente().getCedula());
            view.mostrarHistoriaClinicaActualizada(historiaClinica);
        } catch (RuntimeException e) {
            view.mostrarError("Error al consultar Historia Clínica", e.getMessage());
        }
    }

    /**
      * Procesa el registro formal de un procedimiento clínico en la historia clínica del paciente.
      *
      * @param cita                Cita médica asociada
      * @param diagnosticoEstandar Diagnóstico CIE-10
      * @param tratamientoAplicado Tratamiento odontológico ejecutado
      * @param evolucionMedica     Notas de evolución médica
      * @param valorProcedimiento  Tarifa económica a cobrar
      * @param insumosConsumidos   Lista de insumos utilizados en la sesión
      */
    public void registrarProcedimiento(Cita cita,
                                       String diagnosticoEstandar,
                                       String tratamientoAplicado,
                                       String evolucionMedica,
                                       double valorProcedimiento,
                                       List<ConsumoInsumo> insumosConsumidos) {
        if (cita == null) {
            view.mostrarError("Selección requerida", "Debe seleccionar la cita del paciente que está atendiendo.");
            return;
        }
        try {
            ProcedimientoClinico procedimiento = atencionClinicaService.registrarProcedimiento(
                    cita.getIdCita(),
                    diagnosticoEstandar,
                    tratamientoAplicado,
                    evolucionMedica,
                    valorProcedimiento,
                    insumosConsumidos
            );
            HistoriaClinica historiaActualizada = atencionClinicaService.obtenerHistoriaClinicaPaciente(cita.getPaciente().getCedula());
            view.limpiarFormularioClinico();
            cargarDatosIniciales();
            view.mostrarHistoriaClinicaActualizada(historiaActualizada);
            view.mostrarExito("Procedimiento Registrado (F-03 / RN-003 / RN-004)",
                    "Se indexó el procedimiento " + procedimiento.getIdProcedimiento() + " en la Historia Clínica ("
                            + historiaActualizada.getLinkHistoriaClinica() + ").\nEstado del expediente: "
                            + historiaActualizada.getEstado().getEtiqueta() + ".\nInsumos descontados en tiempo real.");
        } catch (RuntimeException e) {
            view.mostrarError("Error en Atención Clínica", e.getMessage());
        }
    }

    /**
      * Marca la inasistencia del paciente a su cita médica programada.
      *
      * @param cita Cita a marcar como no asistida
      */
    public void marcarInasistencia(Cita cita) {
        if (cita == null) {
            view.mostrarError("Selección requerida", "Debe seleccionar una cita.");
            return;
        }
        try {
            atencionClinicaService.marcarCitaNoAsistida(cita.getIdCita());
            cargarDatosIniciales();
            view.mostrarExito("Inasistencia Registrada", "La cita " + cita.getIdCita() + " fue marcada como No Asistida.");
        } catch (RuntimeException e) {
            view.mostrarError("Error", e.getMessage());
        }
    }
}
