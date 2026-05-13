package co.edu.uniquindio.clinicaodontologica.viewcontroller;

import co.edu.uniquindio.clinicaodontologica.controller.AgendamientoController;
import co.edu.uniquindio.clinicaodontologica.controller.CrearCitaController;
import co.edu.uniquindio.clinicaodontologica.model.Cita;
import co.edu.uniquindio.clinicaodontologica.model.Especialista;
import co.edu.uniquindio.clinicaodontologica.model.Paciente;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;

public class CrearCitaViewController {

    private CrearCitaController crearCitaController;

    private Especialista especialista;
    private LocalDate fecha;
    private LocalTime hora;

    @FXML
    private Label lblEspecialista;

    @FXML
    private Label lblEspecialidad;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblHora;

    @FXML
    private TextField txtNombrePaciente;

    @FXML
    private TextField txtCedulaPaciente;

    @FXML
    private TextField txtTelefonoPaciente;

    @FXML
    private TextArea txtMotivo;

    public void setDatosCita(Especialista especialista, LocalDate fecha, LocalTime hora) {

        this.crearCitaController = new CrearCitaController();

        this.especialista = especialista;
        this.fecha = fecha;
        this.hora = hora;

        lblEspecialista.setText(especialista.getNombre());
        lblEspecialidad.setText(especialista.getEspecialidad());
        lblFecha.setText(fecha.toString());
        lblHora.setText(hora.toString());
    }

    @FXML
    public void agendarCita() {

        String nombre = txtNombrePaciente.getText();
        String cedula = txtCedulaPaciente.getText();
        String telefono = txtTelefonoPaciente.getText();
        String motivo = txtMotivo.getText();

        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || motivo.isEmpty()) {

            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Debe completar todos los campos del paciente");

            return;
        }

        Paciente paciente = new Paciente(nombre, cedula, telefono);

        Cita cita = new Cita(paciente, especialista, motivo, fecha, hora);

        crearCitaController.agregarCita(cita);
        crearCitaController.eliminarHorarioEspecialista(especialista, fecha, hora);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Cita agendada", "La cita fue registrada correctamente");

        cerrarVentana();
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {

        Stage stage = (Stage) lblHora.getScene().getWindow();

        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}
