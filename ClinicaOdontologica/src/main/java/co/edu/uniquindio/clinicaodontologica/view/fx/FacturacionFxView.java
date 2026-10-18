package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.Caja;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Pago;
import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;
import co.edu.uniquindio.clinicaodontologica.presenter.FacturacionPresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.IFacturacionView;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
  * Vista Pasiva JavaFX para F-04 (Registrar pago y flujo de caja diario) y RN-005.
  * Desglosa el tratamiento clínico, permite elegir Efectivo/Tarjeta/Transferencia,
  * posee el botón "Registrar Pago" y actualiza el flujo de caja diario exactamente en el valor cobrado.
  */
public class FacturacionFxView implements IFacturacionView {
    private final FacturacionPresenter presenter;
    private final String nombreAuxiliarActual;
    private final Runnable onDataChangedCallback;

    private ComboBox<Cita> cmbConsultasPorCobrar;
    private TextArea txtDesgloseClinico;
    private Label lblValorTotal;
    private ComboBox<MetodoPago> cmbMetodoPago;
    private TextField txtMontoRecibido;

    private Label lblFlujoCajaTotal;
    private Label lblDetalleMetodosCaja;
    private ListView<Pago> listaPagosCaja;
    private TextArea txtComprobante;

    public FacturacionFxView(String nombreAuxiliarActual, Runnable onDataChangedCallback) {
        this.nombreAuxiliarActual = nombreAuxiliarActual;
        this.onDataChangedCallback = onDataChangedCallback;
        ServiceLocator loc = ServiceLocator.getInstance();
        this.presenter = new FacturacionPresenter(
                this,
                loc.getFacturacionService(),
                loc.getAgendamientoService()
        );
    }

    public Parent construirVista() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        // Izquierda: Pantalla de Cobro Rápido (F-04 SWR)
        VBox cardCobro = new VBox(12);
        cardCobro.setPrefWidth(460);
        cardCobro.setPadding(new Insets(22));
        cardCobro.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblTitulo = new Label("Cobro y Facturación en Caja");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        cmbConsultasPorCobrar = new ComboBox<>();
        cmbConsultasPorCobrar.setMaxWidth(Double.MAX_VALUE);
        cmbConsultasPorCobrar.setPromptText("Seleccione la consulta / cita a facturar...");
        cmbConsultasPorCobrar.setOnAction(e -> presenter.seleccionarCitaParaCobro(cmbConsultasPorCobrar.getValue()));

        txtDesgloseClinico = new TextArea();
        txtDesgloseClinico.setEditable(false);
        txtDesgloseClinico.setPrefRowCount(4);
        txtDesgloseClinico.setPromptText("El desglose de procedimientos clínicos aparecerá aquí...");

        lblValorTotal = new Label("VALOR TOTAL A COBRAR: $0");
        lblValorTotal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #15803D;");

        cmbMetodoPago = new ComboBox<>();
        cmbMetodoPago.getItems().addAll(MetodoPago.values());
        cmbMetodoPago.setValue(MetodoPago.EFECTIVO);
        cmbMetodoPago.setMaxWidth(Double.MAX_VALUE);

        txtMontoRecibido = new TextField();
        txtMontoRecibido.setPromptText("Monto recibido del paciente ($)");

        Button btnRegistrarPago = new Button("Registrar Pago");
        btnRegistrarPago.setMaxWidth(Double.MAX_VALUE);
        btnRegistrarPago.setPrefHeight(44);
        btnRegistrarPago.setStyle(
                "-fx-background-color: #16A34A; -fx-text-fill: white; " +
                "-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        btnRegistrarPago.setOnAction(e -> {
            presenter.registrarPago(
                    cmbConsultasPorCobrar.getValue(),
                    cmbMetodoPago.getValue(),
                    txtMontoRecibido.getText(),
                    nombreAuxiliarActual
            );
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });

        cardCobro.getChildren().addAll(
                lblTitulo,
                new Label("1. Seleccione Cita / Consulta Pendiente de Pago *:"), cmbConsultasPorCobrar,
                new Label("2. Desglose del Tratamiento Clínico:"), txtDesgloseClinico,
                lblValorTotal,
                new Separator(),
                new Label("3. Método de Pago (Efectivo, Tarjeta, Transferencia) *:"), cmbMetodoPago,
                new Label("4. Monto Recibido ($) *:"), txtMontoRecibido,
                btnRegistrarPago
        );

        // Derecha: Panel de Control de Flujo de Caja Diario + Comprobante Oficial
        VBox panelCaja = new VBox(14);
        panelCaja.setPadding(new Insets(0, 0, 0, 20));

        VBox cardResumenCaja = new VBox(8);
        cardResumenCaja.setPadding(new Insets(18));
        cardResumenCaja.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E3A8A, #2563EB); " +
                "-fx-background-radius: 14;"
        );

        Label lblTitCaja = new Label("PANEL DE CONTROL — FLUJO DE CAJA DIARIO ACTIVO");
        lblTitCaja.setStyle("-fx-text-fill: #DBEAFE; -fx-font-size: 13px; -fx-font-weight: bold;");

        lblFlujoCajaTotal = new Label("$0");
        lblFlujoCajaTotal.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        lblDetalleMetodosCaja = new Label("Efectivo: $0 | Tarjeta: $0 | Transferencia: $0");
        lblDetalleMetodosCaja.setStyle("-fx-text-fill: #E0E7FF; -fx-font-size: 13px;");

        cardResumenCaja.getChildren().addAll(lblTitCaja, lblFlujoCajaTotal, lblDetalleMetodosCaja);

        HBox inferiorCaja = new HBox(14);
        VBox.setVgrow(inferiorCaja, Priority.ALWAYS);

        VBox colHistorial = new VBox(8);
        HBox.setHgrow(colHistorial, Priority.ALWAYS);
        listaPagosCaja = new ListView<>();
        VBox.setVgrow(listaPagosCaja, Priority.ALWAYS);
        listaPagosCaja.getSelectionModel().selectedItemProperty().addListener((obs, oldV, pagoSel) -> {
            if (pagoSel != null) {
                txtComprobante.setText(pagoSel.generarTextoComprobante());
            }
        });
        colHistorial.getChildren().addAll(new Label("Pagos Registrados en Caja:"), listaPagosCaja);

        VBox colComprobante = new VBox(8);
        HBox.setHgrow(colComprobante, Priority.ALWAYS);
        txtComprobante = new TextArea();
        txtComprobante.setEditable(false);
        VBox.setVgrow(txtComprobante, Priority.ALWAYS);
        txtComprobante.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");
        colComprobante.getChildren().addAll(new Label("Comprobante Oficial para el Paciente:"), txtComprobante);

        inferiorCaja.getChildren().addAll(colHistorial, colComprobante);
        panelCaja.getChildren().addAll(cardResumenCaja, inferiorCaja);

        root.setLeft(cardCobro);
        root.setCenter(panelCaja);

        presenter.cargarEstadoFacturacion();
        return root;
    }

    public void refrescar() {
        presenter.cargarEstadoFacturacion();
    }

    @Override
    public void mostrarCitasPendientesDeCobro(List<Cita> citas) {
        cmbConsultasPorCobrar.setItems(FXCollections.observableArrayList(citas));
    }

    @Override
    public void mostrarDesgloseYValorTotal(String desglose, double valorTotal) {
        txtDesgloseClinico.setText(desglose);
        lblValorTotal.setText("VALOR TOTAL A COBRAR: $" + String.format("%,.0f", valorTotal));
        txtMontoRecibido.setText(String.format("%.0f", valorTotal));
    }

    @Override
    public void mostrarComprobanteGenerado(String textoComprobante) {
        txtComprobante.setText(textoComprobante);
    }

    @Override
    public void actualizarPanelFlujoCaja(Caja caja, List<Pago> historialPagos) {
        lblFlujoCajaTotal.setText("$" + String.format("%,.0f", caja.getFlujoCajaTotal()));
        lblDetalleMetodosCaja.setText(String.format(
                "Efectivo: $%,.0f  |  Tarjeta: $%,.0f  |  Transferencia: $%,.0f  |  Total Pagos: %d",
                caja.getTotalPorMetodo(MetodoPago.EFECTIVO),
                caja.getTotalPorMetodo(MetodoPago.TARJETA),
                caja.getTotalPorMetodo(MetodoPago.TRANSFERENCIA),
                historialPagos.size()
        ));
        listaPagosCaja.setItems(FXCollections.observableArrayList(historialPagos));
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
