package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Recordatorio;
import co.edu.uniquindio.clinicaodontologica.model.enums.CanalNotificacion;
import co.edu.uniquindio.clinicaodontologica.presenter.RecordatorioPresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.IRecordatorioView;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
  * Vista Pasiva JavaFX para F-05 (Enviar recordatorio automático 24 horas antes de cada cita).
  */
public class RecordatorioFxView implements IRecordatorioView {
    private final RecordatorioPresenter presenter;

    private ListView<Cita> listaCitasAgendadas;
    private ComboBox<CanalNotificacion> cmbCanal;
    private TextArea txtVistaPreviaMensaje;
    private ListView<Recordatorio> listaHistorialRecordatorios;

    public RecordatorioFxView() {
        ServiceLocator loc = ServiceLocator.getInstance();
        this.presenter = new RecordatorioPresenter(
                this,
                loc.getRecordatorioService(),
                loc.getAgendamientoService()
        );
    }

    public Parent construirVista() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        VBox panelIzquierdo = new VBox(12);
        panelIzquierdo.setPrefWidth(470);
        panelIzquierdo.setPadding(new Insets(20));
        panelIzquierdo.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblTitulo = new Label("Recordatorios Preventivos de Citas");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label lblDesc = new Label(
                "Envío automatizado de notificaciones a pacientes 24 horas antes de su consulta odontológica " +
                "vía WhatsApp o Correo Electrónico para confirmar y asegurar su asistencia."
        );
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        cmbCanal = new ComboBox<>();
        cmbCanal.getItems().addAll(CanalNotificacion.values());
        cmbCanal.setValue(CanalNotificacion.WHATSAPP);
        cmbCanal.setMaxWidth(Double.MAX_VALUE);
        cmbCanal.setOnAction(e -> presenter.previsualizarMensaje(
                listaCitasAgendadas.getSelectionModel().getSelectedItem(),
                cmbCanal.getValue()
        ));

        Button btnBarrido24h = new Button("Ejecutar Barrido Automático de Citas (Próximas 24h)");
        btnBarrido24h.setMaxWidth(Double.MAX_VALUE);
        btnBarrido24h.setPrefHeight(40);
        btnBarrido24h.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBarrido24h.setOnAction(e -> presenter.ejecutarBarridoAutomatico24Horas(cmbCanal.getValue()));

        listaCitasAgendadas = new ListView<>();
        VBox.setVgrow(listaCitasAgendadas, Priority.ALWAYS);
        listaCitasAgendadas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, citaSel) -> {
            if (citaSel != null) {
                presenter.previsualizarMensaje(citaSel, cmbCanal.getValue());
            }
        });

        Button btnEnviarSeleccionada = new Button("Disparar Recordatorio a la Cita Seleccionada");
        btnEnviarSeleccionada.setMaxWidth(Double.MAX_VALUE);
        btnEnviarSeleccionada.setPrefHeight(38);
        btnEnviarSeleccionada.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnEnviarSeleccionada.setOnAction(e -> presenter.enviarRecordatorioManual(
                listaCitasAgendadas.getSelectionModel().getSelectedItem(),
                cmbCanal.getValue()
        ));

        panelIzquierdo.getChildren().addAll(
                lblTitulo, lblDesc,
                new Label("Canal Integrado de Mensajería:"), cmbCanal,
                btnBarrido24h,
                new Separator(),
                new Label("Citas Programadas en Base de Datos:"),
                listaCitasAgendadas,
                btnEnviarSeleccionada
        );

        VBox panelDerecho = new VBox(12);
        panelDerecho.setPadding(new Insets(0, 0, 0, 18));

        Label lblPrev = new Label("Mensaje Parametrizado Dinámicamente (Variables de Cita y Paciente):");
        lblPrev.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        txtVistaPreviaMensaje = new TextArea();
        txtVistaPreviaMensaje.setEditable(false);
        txtVistaPreviaMensaje.setWrapText(true);
        txtVistaPreviaMensaje.setPrefRowCount(5);

        Label lblHist = new Label("Bandeja de Notificaciones Automáticas Disparadas (Log F-05):");
        lblHist.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        listaHistorialRecordatorios = new ListView<>();
        VBox.setVgrow(listaHistorialRecordatorios, Priority.ALWAYS);
        listaHistorialRecordatorios.getSelectionModel().selectedItemProperty().addListener((obs, oldV, recSel) -> {
            if (recSel != null) {
                txtVistaPreviaMensaje.setText(recSel.getMensajeParametrizado());
            }
        });

        panelDerecho.getChildren().addAll(lblPrev, txtVistaPreviaMensaje, lblHist, listaHistorialRecordatorios);

        root.setLeft(panelIzquierdo);
        root.setCenter(panelDerecho);

        presenter.cargarDatos();
        return root;
    }

    public void refrescar() {
        presenter.cargarDatos();
    }

    @Override
    public void mostrarCitasProgramadas(List<Cita> citas) {
        listaCitasAgendadas.setItems(FXCollections.observableArrayList(citas));
    }

    @Override
    public void mostrarHistorialRecordatorios(List<Recordatorio> recordatorios) {
        listaHistorialRecordatorios.setItems(FXCollections.observableArrayList(recordatorios));
    }

    @Override
    public void mostrarVistaPreviaMensaje(String mensaje) {
        txtVistaPreviaMensaje.setText(mensaje);
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
