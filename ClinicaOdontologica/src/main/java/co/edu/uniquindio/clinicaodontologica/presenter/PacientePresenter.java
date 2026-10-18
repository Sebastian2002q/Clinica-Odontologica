package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import co.edu.uniquindio.clinicaodontologica.service.PacienteService;
import co.edu.uniquindio.clinicaodontologica.view.contract.IPacienteView;

/**
  * Presenter MVP para el cumplimiento de:
  */
public class PacientePresenter {

    /**
      * Contrato de la vista pasiva de pacientes.
      */
    private final IPacienteView view;

    /**
      * Servicio de dominio de pacientes.
      */
    private final PacienteService pacienteService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view            Vista pasiva que implementa IPacienteView
      * @param pacienteService Servicio de gestión de pacientes
      */
    public PacientePresenter(IPacienteView view, PacienteService pacienteService) {
        this.view = view;
        this.pacienteService = pacienteService;
    }

    /**
      * Consulta la lista completa de pacientes en el servicio y la envía a la vista para su visualización.
      */
    public void cargarPacientes() {
        view.mostrarPacientes(pacienteService.listarPacientes());
    }

    /**
      * Procesa la captura del formulario para dar de alta a un paciente.
      *
      * @param cedula         Cédula del paciente
      * @param nombreCompleto Nombres y apellidos completos
      * @param telefono       Número telefónico
      * @param email          Correo electrónico
      */
    public void registrarPaciente(String cedula, String nombreCompleto, String telefono, String email) {
        try {
            Paciente registrado = pacienteService.registrarPaciente(cedula, nombreCompleto, telefono, email);
            view.limpiarFormulario();
            cargarPacientes();
            pacienteService.obtenerHistoriaClinica(registrado.getCedula())
                    .ifPresent(view::mostrarDetalleHistoriaClinica);
            view.mostrarExito("Paciente Registrado (F-01)",
                    "El paciente " + registrado.getNombreCompleto() + " (CC: " + registrado.getCedula()
                            + ") fue registrado exitosamente.\nLink Historia Clínica: " + registrado.getLinkHistoriaClinica());
        } catch (RuntimeException e) {
            view.mostrarError("Bloqueo de Registro (RN-001 / F-01)", e.getMessage());
        }
    }

    /**
      * Maneja el evento de selección de un paciente en la tabla, cargando los detalles de su historia clínica.
      *
      * @param paciente Paciente seleccionado
      */
    public void seleccionarPaciente(Paciente paciente) {
        if (paciente == null) {
            return;
        }
        pacienteService.obtenerHistoriaClinica(paciente.getCedula())
                .ifPresent(view::mostrarDetalleHistoriaClinica);
    }
}
