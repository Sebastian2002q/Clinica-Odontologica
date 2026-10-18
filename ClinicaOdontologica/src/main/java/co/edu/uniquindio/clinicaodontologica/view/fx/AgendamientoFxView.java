package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.BloqueHorario;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import co.edu.uniquindio.clinicaodontologica.presenter.AgendamientoPresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.IAgendamientoView;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
  * Vista Pasiva JavaFX para F-02 (Agendar cita, bloques Libres en Verde y Ocupados en Rojo,
  * Cancelar y Reprogramar) y validación de RN-001 y RN-002.
  */
public class AgendamientoFxView implements IAgendamientoView {
    private final AgendamientoPresenter presenter;
    private final Runnable onDataChangedCallback;

    private ListView<Especialista> listaEspecialistas;
    private ComboBox<LocalDate> cmbFechas;
    private GridPane gridBloquesCalendario;
    private Label lblEncabezadoCalendario;

    private ComboBox<Paciente> cmbPacientes;
    private TextField txtCedulaManual;
    private Label lblHoraSeleccionada;
    private TextArea txtMotivoConsulta;
    private LocalTime horaSeleccionadaActual;

    private ListView<Cita> listaCitasEspecialista;

    public AgendamientoFxView(Runnable onDataChangedCallback) {
        this.onDataChangedCallback = onDataChangedCallback;
        ServiceLocator loc = ServiceLocator.getInstance();
        this.presenter = new AgendamientoPresenter(
                this,
                loc.getAgendamientoService(),
                loc.getEspecialistaService(),
                loc.getPacienteService()
        );
    }

    public Parent construirVista() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        // Columna Izquierda: Selección de Especialista y sus Citas Activas
        VBox panelIzquierdo = new VBox(12);
        panelIzquierdo.setPrefWidth(310);
        panelIzquierdo.setPadding(new Insets(16));
        panelIzquierdo.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblEsp = new Label("1. Doctores Especialistas");
        lblEsp.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        listaEspecialistas = new ListView<>();
        listaEspecialistas.setPrefHeight(220);
        listaEspecialistas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nuevo) -> {
            if (nuevo != null) {
                presenter.seleccionarEspecialista(nuevo);
            }
        });

        Label lblCitas = new Label("Citas del Especialista");
        lblCitas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        listaCitasEspecialista = new ListView<>();
        VBox.setVgrow(listaCitasEspecialista, Priority.ALWAYS);
        listaCitasEspecialista.getSelectionModel().selectedItemProperty().addListener((obs, oldV, citaSel) -> {
            if (citaSel != null) {
                seleccionarFechaEnCombo(citaSel.getFecha());
                Especialista esp = listaEspecialistas.getSelectionModel().getSelectedItem();
                if (esp != null) {
                    presenter.seleccionarFecha(esp, citaSel.getFecha());
                }
            }
        });

        HBox botonesCita = new HBox(8);
        Button btnCancelarCita = new Button("Cancelar Cita");
        btnCancelarCita.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnCancelarCita, Priority.ALWAYS);
        btnCancelarCita.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnCancelarCita.setOnAction(e -> {
            Cita sel = listaCitasEspecialista.getSelectionModel().getSelectedItem();
            if (sel != null && UiAlerts.confirmar("Cancelar Cita", "¿Desea cancelar la cita " + sel.getIdCita() + " y liberar el horario?")) {
                presenter.cancelarCita(sel);
                notificarCambio();
            } else if (sel == null) {
                mostrarError("Sin selección", "Seleccione una cita de la lista para cancelar.");
            }
        });

        Button btnReprogramar = new Button("Reprogramar a Hora Elegida");
        btnReprogramar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnReprogramar, Priority.ALWAYS);
        btnReprogramar.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnReprogramar.setOnAction(e -> {
            Cita sel = listaCitasEspecialista.getSelectionModel().getSelectedItem();
            LocalDate fechaDestino = cmbFechas.getValue();
            if (horaSeleccionadaActual == null || fechaDestino == null) {
                mostrarError("Seleccione nuevo horario", "Primero haga clic en un bloque horario del calendario y luego pulse Reprogramar.");
                return;
            }
            presenter.reprogramarCita(sel, fechaDestino, horaSeleccionadaActual);
            notificarCambio();
        });

        botonesCita.getChildren().addAll(btnCancelarCita, btnReprogramar);
        panelIzquierdo.getChildren().addAll(lblEsp, listaEspecialistas, new Separator(), lblCitas, listaCitasEspecialista, botonesCita);

        // Columna Central: Calendario Visual de Bloques Libres (Verde) y Ocupados (Rojo)
        VBox panelCentral = new VBox(14);
        panelCentral.setPadding(new Insets(0, 16, 0, 16));

        VBox cardCalendario = new VBox(14);
        cardCalendario.setPadding(new Insets(20));
        VBox.setVgrow(cardCalendario, Priority.ALWAYS);
        cardCalendario.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        lblEncabezadoCalendario = new Label("2. Calendario de Disponibilidad y Citas");
        lblEncabezadoCalendario.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        HBox barraFechaLeyenda = new HBox(14);
        barraFechaLeyenda.setAlignment(Pos.CENTER_LEFT);

        cmbFechas = new ComboBox<>();
        cmbFechas.setPromptText("Seleccione fecha");
        cmbFechas.setOnAction(e -> {
            Especialista esp = listaEspecialistas.getSelectionModel().getSelectedItem();
            LocalDate f = cmbFechas.getValue();
            if (esp != null && f != null) {
                presenter.seleccionarFecha(esp, f);
            }
        });

        Label badgeVerde = new Label("● LIBRE (Verde)");
        badgeVerde.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-weight: bold;");

        Label badgeRojo = new Label("● OCUPADO (Rojo)");
        badgeRojo.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-weight: bold;");

        barraFechaLeyenda.getChildren().addAll(new Label("Fecha de agenda:"), cmbFechas, badgeVerde, badgeRojo);

        gridBloquesCalendario = new GridPane();
        gridBloquesCalendario.setHgap(12);
        gridBloquesCalendario.setVgap(12);
        gridBloquesCalendario.setPadding(new Insets(10));

        ScrollPane scrollGrid = new ScrollPane(gridBloquesCalendario);
        scrollGrid.setFitToWidth(true);
        scrollGrid.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scrollGrid, Priority.ALWAYS);

        cardCalendario.getChildren().addAll(lblEncabezadoCalendario, barraFechaLeyenda, new Separator(), scrollGrid);
        panelCentral.getChildren().add(cardCalendario);

        // Columna Derecha: Formulario de Confirmación de Cita (valida RN-001 y RN-002)
        VBox panelDerecho = new VBox(12);
        panelDerecho.setPrefWidth(340);
        panelDerecho.setPadding(new Insets(20));
        panelDerecho.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblReserva = new Label("3. Asignar Cita Médica");
        lblReserva.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        lblHoraSeleccionada = new Label("Bloque seleccionado: (Haga clic en un bloque del calendario)");
        lblHoraSeleccionada.setWrapText(true);
        lblHoraSeleccionada.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        cmbPacientes = new ComboBox<>();
        cmbPacientes.setMaxWidth(Double.MAX_VALUE);
        cmbPacientes.setPromptText("Seleccionar paciente registrado...");
        cmbPacientes.setOnAction(e -> {
            Paciente p = cmbPacientes.getValue();
            if (p != null) {
                txtCedulaManual.setText(p.getCedula());
            }
        });

        txtCedulaManual = new TextField();
        txtCedulaManual.setPromptText("Cédula del paciente");

        txtMotivoConsulta = new TextArea();
        txtMotivoConsulta.setPromptText("Motivo de la consulta odontológica...");
        txtMotivoConsulta.setPrefRowCount(4);
        txtMotivoConsulta.setWrapText(true);

        Button btnConfirmarCita = new Button("Confirmar y Registrar Cita");
        btnConfirmarCita.setMaxWidth(Double.MAX_VALUE);
        btnConfirmarCita.setPrefHeight(42);
        btnConfirmarCita.setStyle(
                "-fx-background-color: #2563EB; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        btnConfirmarCita.setOnAction(e -> {
            Especialista esp = listaEspecialistas.getSelectionModel().getSelectedItem();
            LocalDate fecha = cmbFechas.getValue();
            presenter.agendarCita(
                    txtCedulaManual.getText(),
                    esp,
                    fecha,
                    horaSeleccionadaActual,
                    txtMotivoConsulta.getText()
            );
            notificarCambio();
        });

        Label lblNotaVerificacion = new Label(
                "Guía de Uso:\n" +
                "• Los bloques en VERDE están disponibles para agendar.\n" +
                "• Los bloques en ROJO ya tienen cita asignada y el sistema bloquea cruces de horario."
        );
        lblNotaVerificacion.setWrapText(true);
        lblNotaVerificacion.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px; -fx-background-color: #F3F4F6; -fx-padding: 10; -fx-background-radius: 8;");

        panelDerecho.getChildren().addAll(
                lblReserva,
                lblHoraSeleccionada,
                new Separator(),
                new Label("Buscar Paciente Registrado:"),
                cmbPacientes,
                new Label("Cédula del Paciente *:"),
                txtCedulaManual,
                new Label("Motivo de Consulta *:"),
                txtMotivoConsulta,
                btnConfirmarCita,
                lblNotaVerificacion
        );

        root.setLeft(panelIzquierdo);
        root.setCenter(panelCentral);
        root.setRight(panelDerecho);

        presenter.inicializarVista();
        return root;
    }

    public void refrescar() {
        Especialista prev = listaEspecialistas.getSelectionModel().getSelectedItem();
        presenter.inicializarVista();
        if (prev != null && listaEspecialistas.getItems().contains(prev)) {
            listaEspecialistas.getSelectionModel().select(prev);
        } else if (!listaEspecialistas.getItems().isEmpty()) {
            listaEspecialistas.getSelectionModel().selectFirst();
        }
    }

    private void notificarCambio() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }
    }

    @Override
    public void mostrarEspecialistas(List<Especialista> especialistas) {
        listaEspecialistas.setItems(FXCollections.observableArrayList(especialistas));
        if (!especialistas.isEmpty() && listaEspecialistas.getSelectionModel().getSelectedItem() == null) {
            listaEspecialistas.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void mostrarPacientesRegistrados(List<Paciente> pacientes) {
        cmbPacientes.setItems(FXCollections.observableArrayList(pacientes));
    }

    @Override
    public void mostrarFechasAgenda(List<LocalDate> fechas) {
        cmbFechas.setOnAction(null);
        cmbFechas.setItems(FXCollections.observableArrayList(fechas));
        if (!fechas.isEmpty()) {
            cmbFechas.setValue(fechas.get(0));
        }
        cmbFechas.setOnAction(e -> {
            Especialista esp = listaEspecialistas.getSelectionModel().getSelectedItem();
            LocalDate f = cmbFechas.getValue();
            if (esp != null && f != null) {
                presenter.seleccionarFecha(esp, f);
            }
        });
    }

    @Override
    public void seleccionarFechaEnCombo(LocalDate fecha) {
        if (fecha != null && cmbFechas != null) {
            if (!cmbFechas.getItems().contains(fecha)) {
                cmbFechas.getItems().add(fecha);
            }
            cmbFechas.setValue(fecha);
        }
    }

    @Override
    public void mostrarCalendarioBloques(Especialista especialista, LocalDate fecha, List<BloqueHorario> bloques) {
        lblEncabezadoCalendario.setText("2. Agenda de " + especialista.getNombre() + " (" + especialista.getConsultorio() + ") — " + fecha);
        gridBloquesCalendario.getChildren().clear();
        gridBloquesCalendario.getColumnConstraints().clear();

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            gridBloquesCalendario.getColumnConstraints().add(col);
        }

        int colIdx = 0;
        int rowIdx = 0;
        for (BloqueHorario bloque : bloques) {
            boolean libre = bloque.isDisponible();
            String textoBoton = bloque.getHora().toString() + (libre ? "\n[LIBRE]" : "\n[OCUPADO]");
            Button btnBloque = new Button(textoBoton);
            btnBloque.setMaxWidth(Double.MAX_VALUE);
            btnBloque.setPrefHeight(64);

            // SWR F-02: bloques libres en VERDE y ocupados en ROJO
            if (libre) {
                btnBloque.setStyle(
                        "-fx-background-color: #16A34A; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 10; -fx-cursor: hand;"
                );
            } else {
                btnBloque.setStyle(
                        "-fx-background-color: #DC2626; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 10; -fx-cursor: hand;"
                );
            }

            btnBloque.setOnAction(e -> {
                this.horaSeleccionadaActual = bloque.getHora();
                this.lblHoraSeleccionada.setText("Bloque seleccionado: " + fecha + " a las " + bloque.getHora()
                        + (libre ? " (Disponible - Verde)" : " (Ocupado - Rojo)"));
            });

            gridBloquesCalendario.add(btnBloque, colIdx, rowIdx);
            colIdx++;
            if (colIdx == 3) {
                colIdx = 0;
                rowIdx++;
            }
        }
    }

    @Override
    public void mostrarCitasEspecialista(List<Cita> citas) {
        listaCitasEspecialista.setItems(FXCollections.observableArrayList(citas));
    }

    @Override
    public void limpiarFormularioReserva() {
        txtMotivoConsulta.clear();
        horaSeleccionadaActual = null;
        lblHoraSeleccionada.setText("Bloque seleccionado: (Ninguno)");
    }

    @Override
    public void mostrarExito(String titulo, String mensaje) {
        UiAlerts.mostrarInformacion(titulo, mensaje);
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
        UiAlerts.mostrarError(titulo, mensaje);
    }
}
