package co.edu.uniquindio.clinicaodontologica.viewcontroller;

import co.edu.uniquindio.clinicaodontologica.controller.AgendamientoController;
import co.edu.uniquindio.clinicaodontologica.model.Cita;
import co.edu.uniquindio.clinicaodontologica.model.Especialista;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AgendamientoViewController implements Initializable {

    private AgendamientoController agendamientoController;

    @FXML
    private Label lblUsuarioLogueado;

    @FXML
    private Label lblTituloAgenda;

    @FXML
    private ListView<Especialista> listaEspecialistas;

    @FXML
    private TextArea txtResumenCitas;

    @FXML
    private GridPane gridHorarios;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        agendamientoController = new AgendamientoController();

        txtResumenCitas.setText("No hay citas agendadas.");

        cargarEspecialistas();

        listaEspecialistas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, especialistaSeleccionado) -> {

            if (especialistaSeleccionado != null) {
                cargarDisponibilidadEspecialista(especialistaSeleccionado);
                cargarCitasEspecialista(especialistaSeleccionado);
            }
        });
    }

    public void setUsuarioLogueado(String usuario) {
        lblUsuarioLogueado.setText("Sesión: " + usuario);
    }

    private void cargarEspecialistas() {

        Especialista seleccionadoAnterior = listaEspecialistas.getSelectionModel().getSelectedItem();

        listaEspecialistas.getItems().clear();

        listaEspecialistas.getItems().addAll(agendamientoController.obtenerEspecialistas());

        if (seleccionadoAnterior != null && listaEspecialistas.getItems().contains(seleccionadoAnterior)) {

            listaEspecialistas.getSelectionModel().select(seleccionadoAnterior);
        }
    }

    private void cargarDisponibilidadEspecialista(Especialista especialista) {

        gridHorarios.getChildren().clear();
        gridHorarios.getColumnConstraints().clear();
        gridHorarios.setMaxWidth(Double.MAX_VALUE);

        lblTituloAgenda.setText("Agenda de Horarios - " + especialista.getNombre());

        ColumnConstraints columnaPrincipal = new ColumnConstraints();
        columnaPrincipal.setPercentWidth(100);
        columnaPrincipal.setHgrow(Priority.ALWAYS);

        gridHorarios.getColumnConstraints().add(columnaPrincipal);

        Accordion accordion = new Accordion();
        accordion.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(accordion, Priority.ALWAYS);
        GridPane.setFillWidth(accordion, true);

        Map<LocalDate, List<LocalTime>> disponibilidad = especialista.getHorariosDisponibles();

        if (disponibilidad == null || disponibilidad.isEmpty()) {

            Label label = new Label("Este especialista no tiene horarios disponibles.");

            label.setStyle("-fx-text-fill: #6B7280;" + "-fx-font-size: 14px;");

            gridHorarios.add(label, 0, 0);
            return;
        }

        for (LocalDate fecha : disponibilidad.keySet()) {

            GridPane gridHoras = new GridPane();

            gridHoras.setHgap(15);
            gridHoras.setVgap(15);
            gridHoras.setPadding(new Insets(15));
            gridHoras.setMaxWidth(Double.MAX_VALUE);

            gridHoras.getColumnConstraints().clear();

            for (int i = 0; i < 4; i++) {

                ColumnConstraints columnaConstraint = new ColumnConstraints();

                columnaConstraint.setPercentWidth(25);
                columnaConstraint.setHgrow(Priority.ALWAYS);

                gridHoras.getColumnConstraints().add(columnaConstraint);
            }

            int columna = 0;
            int fila = 0;

            for (LocalTime hora : disponibilidad.get(fecha)) {

                Button botonHorario = new Button(hora.toString());

                botonHorario.setMaxWidth(Double.MAX_VALUE);
                botonHorario.setPrefHeight(45);

                botonHorario.setStyle("-fx-background-color: #2563EB;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-background-radius: 10;");

                GridPane.setHgrow(botonHorario, Priority.ALWAYS);
                GridPane.setFillWidth(botonHorario, true);

                botonHorario.setOnAction(e -> abrirVentanaCrearCita(especialista, fecha, hora));

                gridHoras.add(botonHorario, columna, fila);

                columna++;

                if (columna == 4) {
                    columna = 0;
                    fila++;
                }
            }

            TitledPane paneFecha = new TitledPane("Fecha: " + fecha, gridHoras);

            paneFecha.setMaxWidth(Double.MAX_VALUE);

            accordion.getPanes().add(paneFecha);
        }

        gridHorarios.add(accordion, 0, 0);
    }

    private void abrirVentanaCrearCita(Especialista especialista, LocalDate fecha, LocalTime hora) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/clinicaodontologica/view/crear-cita.fxml"));

            Scene scene = new Scene(loader.load());

            CrearCitaViewController controller = loader.getController();

            controller.setDatosCita(especialista, fecha, hora);

            Stage stage = new Stage();
            stage.setTitle("Crear Cita");
            stage.setScene(scene);
            stage.showAndWait();

            cargarDisponibilidadEspecialista(especialista);
            cargarCitasEspecialista(especialista);

        } catch (IOException e) {

            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana para crear la cita.");

            e.printStackTrace();
        }
    }

    private void cargarCitasEspecialista(Especialista especialista) {

        txtResumenCitas.clear();

        List<Cita> citas = agendamientoController.obtenerCitasEspecialista(especialista);

        if (citas.isEmpty()) {
            txtResumenCitas.setText("No hay citas agendadas.");
            return;
        }

        for (Cita cita : citas) {

            txtResumenCitas.appendText("Paciente: " + cita.getPaciente().getNombre() + "\n" + "Fecha: " + cita.getFecha() + "\n" + "Hora: " + cita.getHora() + "\n" + "Motivo: " + cita.getMotivo() + "\n\n");
        }
    }

    public void abrirVentanaEspecialista(ActionEvent actionEvent) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/clinicaodontologica/view/especialista-form.fxml"));

            Scene scene = new Scene(loader.load());

            EspecialistaFormViewController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Especialista");
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isGuardado()) {
                cargarEspecialistas();
            }

        } catch (IOException e) {

            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana para crear especialista.");

            e.printStackTrace();
        }
    }

    public void editarEspecialista(ActionEvent actionEvent) {

        Especialista especialistaSeleccionado = listaEspecialistas.getSelectionModel().getSelectedItem();

        if (especialistaSeleccionado == null) {

            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Debe seleccionar un especialista para editar.");

            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/clinicaodontologica/view/especialista-form.fxml"));

            Scene scene = new Scene(loader.load());

            EspecialistaFormViewController controller = loader.getController();

            controller.setEspecialistaEditar(especialistaSeleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Especialista");
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isGuardado()) {

                cargarEspecialistas();
                listaEspecialistas.getSelectionModel().select(especialistaSeleccionado);
                cargarDisponibilidadEspecialista(especialistaSeleccionado);
                cargarCitasEspecialista(especialistaSeleccionado);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana para editar especialista.");
            e.printStackTrace();
        }
    }

    public void eliminarEspecialista(ActionEvent actionEvent) {
        Especialista especialistaSeleccionado = listaEspecialistas.getSelectionModel().getSelectedItem();
        if (especialistaSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Debe seleccionar un especialista para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar especialista");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de eliminar el especialista " + especialistaSeleccionado.getNombre() + "?");
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                agendamientoController.eliminarEspecialista(especialistaSeleccionado);
                cargarEspecialistas();
                gridHorarios.getChildren().clear();
                txtResumenCitas.setText("No hay citas agendadas.");
                lblTituloAgenda.setText("Agenda de Horarios");
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}