package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.HistoriaClinica;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import co.edu.uniquindio.clinicaodontologica.model.entity.ProcedimientoClinico;
import co.edu.uniquindio.clinicaodontologica.presenter.PacientePresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.IPacienteView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
  * Vista Pasiva JavaFX para F-01 (Registrar paciente) y RN-001.
  * Incluye campos obligatorios para Cédula, Nombre Completo, Teléfono y Correo Electrónico,
  * tabla de pacientes registrados y visor de su link de Historia Clínica.
  */
public class PacienteFxView implements IPacienteView {
    private final PacientePresenter presenter;
    private final Runnable onDataChangedCallback;

    private TextField txtCedula;
    private TextField txtNombreCompleto;
    private TextField txtTelefono;
    private TextField txtEmail;
    private TableView<Paciente> tablaPacientes;
    private TextArea txtExpedienteClinico;

    public PacienteFxView(Runnable onDataChangedCallback) {
        this.onDataChangedCallback = onDataChangedCallback;
        this.presenter = new PacientePresenter(this, ServiceLocator.getInstance().getPacienteService());
    }

    public Parent construirVista() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Panel Izquierdo: Formulario F-01
        VBox cardFormulario = new VBox(12);
        cardFormulario.setPrefWidth(360);
        cardFormulario.setPadding(new Insets(22));
        cardFormulario.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblTitulo = new Label("Registro de Pacientes");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label lblSub = new Label("Ingrese los datos para registrar al paciente y aperturar su Historia Clínica digital inmediatamente.");
        lblSub.setWrapText(true);
        lblSub.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        txtCedula = new TextField();
        txtCedula.setPromptText("Ej: 1094950123");

        txtNombreCompleto = new TextField();
        txtNombreCompleto.setPromptText("Ej: Laura Sofía Quintero");

        txtTelefono = new TextField();
        txtTelefono.setPromptText("Ej: 3158009911");

        txtEmail = new TextField();
        txtEmail.setPromptText("Ej: laura.quintero@correo.com");

        Button btnGuardar = new Button("Registrar Paciente y Abrir Historia Clínica");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        btnGuardar.setPrefHeight(40);
        btnGuardar.setStyle(
                "-fx-background-color: #2563EB; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        btnGuardar.setOnAction(e -> {
            presenter.registrarPaciente(
                    txtCedula.getText(),
                    txtNombreCompleto.getText(),
                    txtTelefono.getText(),
                    txtEmail.getText()
            );
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });

        Button btnLimpiar = new Button("Limpiar Formulario");
        btnLimpiar.setMaxWidth(Double.MAX_VALUE);
        btnLimpiar.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        cardFormulario.getChildren().addAll(
                lblTitulo, lblSub, new Separator(),
                new Label("Cédula / Documento de Identidad *:"), txtCedula,
                new Label("Nombre Completo *:"), txtNombreCompleto,
                new Label("Teléfono Celular *:"), txtTelefono,
                new Label("Correo Electrónico *:"), txtEmail,
                btnGuardar, btnLimpiar
        );

        // Panel Central/Derecho: Tabla de Pacientes + Visor de Historia Clínica
        VBox panelDerecho = new VBox(16);
        panelDerecho.setPadding(new Insets(0, 0, 0, 20));

        Label lblTabla = new Label("Base de Datos de Pacientes Registrados");
        lblTabla.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        tablaPacientes = new TableView<>();
        tablaPacientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(tablaPacientes, Priority.ALWAYS);

        TableColumn<Paciente, String> colCedula = new TableColumn<>("Cédula");
        colCedula.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCedula()));

        TableColumn<Paciente, String> colNombre = new TableColumn<>("Nombre Completo");
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreCompleto()));

        TableColumn<Paciente, String> colTel = new TableColumn<>("Teléfono");
        colTel.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefono()));

        TableColumn<Paciente, String> colEmail = new TableColumn<>("Correo Electrónico");
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        TableColumn<Paciente, String> colLink = new TableColumn<>("Link Historia Clínica");
        colLink.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLinkHistoriaClinica()));

        tablaPacientes.getColumns().addAll(colCedula, colNombre, colTel, colEmail, colLink);
        tablaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nuevo) -> {
            if (nuevo != null) {
                presenter.seleccionarPaciente(nuevo);
            }
        });

        Label lblExpediente = new Label("Expediente de Historia Clínica Indexado (Seleccione un paciente):");
        lblExpediente.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        txtExpedienteClinico = new TextArea();
        txtExpedienteClinico.setEditable(false);
        txtExpedienteClinico.setPrefHeight(175);
        txtExpedienteClinico.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");

        panelDerecho.getChildren().addAll(lblTabla, tablaPacientes, lblExpediente, txtExpedienteClinico);

        root.setLeft(cardFormulario);
        root.setCenter(panelDerecho);

        presenter.cargarPacientes();
        return root;
    }

    public void refrescar() {
        presenter.cargarPacientes();
    }

    @Override
    public void mostrarPacientes(List<Paciente> pacientes) {
        tablaPacientes.setItems(FXCollections.observableArrayList(pacientes));
    }

    @Override
    public void mostrarDetalleHistoriaClinica(HistoriaClinica hc) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID Expediente : ").append(hc.getIdHistoria()).append("\n");
        sb.append("Link Digital  : ").append(hc.getLinkHistoriaClinica()).append("\n");
        sb.append("Paciente      : ").append(hc.getNombrePaciente()).append(" (CC: ").append(hc.getCedulaPaciente()).append(")\n");
        sb.append("Estado Actual : ").append(hc.getEstado().getEtiqueta()).append("\n");
        sb.append("--------------------------------------------------------------------------------\n");
        if (hc.getProcedimientos().isEmpty()) {
            sb.append("Sin procedimientos clínicos registrados aún.\n");
        } else {
            for (ProcedimientoClinico p : hc.getProcedimientos()) {
                sb.append("• [").append(p.getIdProcedimiento()).append("] Cita: ").append(p.getIdCita())
                        .append(" | Especialista: ").append(p.getNombreEspecialista()).append("\n")
                        .append("  Diagnóstico : ").append(p.getDiagnosticoEstandar()).append("\n")
                        .append("  Tratamiento : ").append(p.getTratamientoAplicado())
                        .append(" ($").append(String.format("%,.0f", p.getValorProcedimiento())).append(")\n")
                        .append("  Evolución   : ").append(p.getEvolucionMedica()).append("\n")
                        .append("  Insumos     : ").append(p.getInsumosConsumidos()).append("\n\n");
            }
        }
        txtExpedienteClinico.setText(sb.toString());
    }

    @Override
    public void limpiarFormulario() {
        txtCedula.clear();
        txtNombreCompleto.clear();
        txtTelefono.clear();
        txtEmail.clear();
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
