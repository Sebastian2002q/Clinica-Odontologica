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
 * Vista Pasiva JavaFX para el cobro y control de flujo de caja.
 * Permite seleccionar el método de pago e ingresar los datos requeridos por la estrategia correspondiente.
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
    private Label lblReferencia;
    private TextField txtReferenciaTransaccion;

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

        // Izquierda: Pantalla de Cobro Rápido
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
        cmbMetodoPago.setOnAction(e -> actualizarCamposSegunEstrategia(cmbMetodoPago.getValue()));

        txtMontoRecibido = new TextField();
        txtMontoRecibido.setPromptText("Monto recibido del paciente ($)");

        lblReferencia = new Label("5. Referencia / Observación (Opcional en efectivo):");
        txtReferenciaTransaccion = new TextField();
        txtReferenciaTransaccion.setPromptText("Opcional: Detalle de pago...");

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
                    txtReferenciaTransaccion.getText(),
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
                new Label("3. Método de Pago (Patrón Strategy) *:"), cmbMetodoPago,
                new Label("4. Monto Recibido ($) *:"), txtMontoRecibido,
                lblReferencia, txtReferenciaTransaccion,
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

        lblDetalleMetodosCaja = new Label("Efectivo: $0 | Tarjeta: $0 | Transferencia: $0 | Convenio: $0");
        lblDetalleMetodosCaja.setStyle("-fx-text-fill: #E0E7FF; -fx-font-size: 12px;");

        cardResumenCaja.getChildren().addAll(lblTitCaja, lblFlujoCajaTotal, lblDetalleMetodosCaja);

        HBox inferiorCaja = new HBox(14);
        VBox.setVgrow(inferiorCaja, Priority.ALWAYS);

        // Columna Historial de Pagos
        VBox colHistorial = new VBox(8);
        colHistorial.setPrefWidth(340);
        Label lblHist = new Label("Comprobantes Emitidos en Jornada");
        lblHist.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        listaPagosCaja = new ListView<>();
        VBox.setVgrow(listaPagosCaja, Priority.ALWAYS);
        listaPagosCaja.getSelectionModel().selectedItemProperty().addListener((obs, oldV, pagoSel) -> {
            if (pagoSel != null) {
                mostrarComprobanteGenerado(pagoSel.generarTextoComprobante());
            }
        });

        colHistorial.getChildren().addAll(lblHist, listaPagosCaja);

        // Columna Comprobante Oficial Formateado
        VBox colComprobante = new VBox(8);
        HBox.setHgrow(colComprobante, Priority.ALWAYS);
        Label lblComp = new Label("Vista Previa del Comprobante Oficial");
        lblComp.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        txtComprobante = new TextArea();
        txtComprobante.setEditable(false);
        txtComprobante.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");
        VBox.setVgrow(txtComprobante, Priority.ALWAYS);
        txtComprobante.setPromptText("El comprobante oficial se emitirá al registrar el cobro...");

        colComprobante.getChildren().addAll(lblComp, txtComprobante);

        inferiorCaja.getChildren().addAll(colHistorial, colComprobante);
        panelCaja.getChildren().addAll(cardResumenCaja, inferiorCaja);

        root.setLeft(cardCobro);
        root.setCenter(panelCaja);

        presenter.cargarEstadoFacturacion();
        return root;
    }

    private void actualizarCamposSegunEstrategia(MetodoPago metodo) {
        if (metodo == null) return;
        switch (metodo) {
            case EFECTIVO -> {
                txtMontoRecibido.setDisable(false);
                lblReferencia.setText("5. Observación / Referencia (Opcional):");
                txtReferenciaTransaccion.setPromptText("Opcional: Detalle de billetes o recibido...");
            }
            case TARJETA -> {
                txtMontoRecibido.setDisable(true);
                lblReferencia.setText("5. No. de Autorización / Voucher Datáfono *:");
                txtReferenciaTransaccion.setPromptText("Ej: AUT-847291 / Código de voucher...");
            }
            case TRANSFERENCIA -> {
                txtMontoRecibido.setDisable(true);
                lblReferencia.setText("5. No. Comprobante / Referencia Digital *:");
                txtReferenciaTransaccion.setPromptText("Ej: Ref Nequi/Daviplata/PSE M-1829471...");
            }
            case CONVENIO -> {
                txtMontoRecibido.setDisable(true);
                lblReferencia.setText("5. No. de Póliza / Carnet / Convenio Dental *:");
                txtReferenciaTransaccion.setPromptText("Ej: POL-SURA-992148 / Carnet institucional...");
            }
        }
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
                "Efectivo: $%,.0f  |  Tarjeta: $%,.0f  |  Transferencia: $%,.0f  |  Convenio: $%,.0f  |  Total Pagos: %d",
                caja.getTotalPorMetodo(MetodoPago.EFECTIVO),
                caja.getTotalPorMetodo(MetodoPago.TARJETA),
                caja.getTotalPorMetodo(MetodoPago.TRANSFERENCIA),
                caja.getTotalPorMetodo(MetodoPago.CONVENIO),
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
