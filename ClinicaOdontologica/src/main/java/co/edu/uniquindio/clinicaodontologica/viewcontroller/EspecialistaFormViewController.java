package co.edu.uniquindio.clinicaodontologica.viewcontroller;

import co.edu.uniquindio.clinicaodontologica.controller.EspecialistaController;
import co.edu.uniquindio.clinicaodontologica.model.Especialista;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EspecialistaFormViewController {

    private Especialista especialistaEditando;
    private boolean modoEdicion = false;

    @Getter
    private boolean guardado = false;

    private EspecialistaController especialistaController;

    private Map<LocalDate, List<LocalTime>> horariosDisponibles = new TreeMap<>();

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtEspecialidad;

    @FXML
    private DatePicker dateFecha;

    @FXML
    private TextField txtHora;

    @FXML
    private ListView<String> listaHorarios;

    @FXML
    public void initialize() {
        especialistaController = new EspecialistaController();
    }

    @FXML
    public void agregarHorario() {
        LocalDate fecha = dateFecha.getValue();
        String horaTexto = txtHora.getText();
        if (fecha == null || horaTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Debe seleccionar una fecha e ingresar una hora.");
            return;
        }
        try {
            LocalTime hora = LocalTime.parse(horaTexto);
            if (modoEdicion && especialistaController.existeCitaEspecialista(especialistaEditando, fecha, hora)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Horario ocupado", "No puede agregar este horario porque ya existe una cita agendada para esa fecha y hora.");
                return;
            }
            if (horariosDisponibles.containsKey(fecha) && horariosDisponibles.get(fecha).contains(hora)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Horario repetido", "Este horario ya fue agregado.");
                return;
            }
            horariosDisponibles.computeIfAbsent(fecha, f -> new ArrayList<>()).add(hora);
            horariosDisponibles.get(fecha).sort(LocalTime::compareTo);
            actualizarListaHorarios();
            txtHora.clear();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato inválido", "La hora debe tener el formato HH:mm. Ejemplo: 08:00");
        }
    }

    @FXML
    public void guardarEspecialista() {
        String nombre = txtNombre.getText();
        String especialidad = txtEspecialidad.getText();
        if (nombre.isEmpty() || especialidad.isEmpty() || horariosDisponibles.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Debe ingresar nombre, especialidad y al menos un horario.");
            return;
        }
        if (modoEdicion) {
            especialistaEditando.setNombre(nombre);
            especialistaEditando.setEspecialidad(especialidad);
            especialistaEditando.setHorariosDisponibles(horariosDisponibles);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Especialista actualizado", "El especialista fue actualizado correctamente.");
        } else {
            Especialista especialista = new Especialista(nombre, especialidad, horariosDisponibles);
            especialistaController.agregarEspecialista(especialista);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Especialista guardado", "El especialista fue registrado correctamente.");
        }
        guardado = true;
        cerrarVentana();
    }

    public void setEspecialistaEditar(Especialista especialista) {
        this.especialistaEditando = especialista;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Especialista");
        txtNombre.setText(especialista.getNombre());
        txtEspecialidad.setText(especialista.getEspecialidad());
        horariosDisponibles.clear();
        for (LocalDate fecha : especialista.getHorariosDisponibles().keySet()) {
            List<LocalTime> horas = new ArrayList<>(especialista.getHorariosDisponibles().get(fecha));
            horas.sort(LocalTime::compareTo);
            horariosDisponibles.put(fecha, horas);
        }
        actualizarListaHorarios();
    }

    private void actualizarListaHorarios() {
        listaHorarios.getItems().clear();
        for (LocalDate fecha : horariosDisponibles.keySet()) {
            for (LocalTime hora : horariosDisponibles.get(fecha)) {
                listaHorarios.getItems().add(fecha + " - " + hora);
            }
        }
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
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