package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.Insumo;
import co.edu.uniquindio.clinicaodontologica.model.enums.NivelAlertaStock;
import co.edu.uniquindio.clinicaodontologica.presenter.InventarioPresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.IInventarioView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
  * Vista Pasiva JavaFX para Gestión de Inventario y Almacén Odontológico.
  * Categorías y unidades de medida estandarizadas mediante ComboBox para evitar desorden.
  * Envuelto en ScrollPane para visualización completa y fluida en cualquier resolución.
  */
public class InventarioFxView implements IInventarioView {
    private final InventarioPresenter presenter;
    private final Runnable onDataChangedCallback;

    private TextField txtCodigo;
    private TextField txtNombre;
    private ComboBox<String> cmbCategoria;
    private ComboBox<String> cmbUnidad;
    private TextField txtStockActual;
    private TextField txtStockMinimo;
    private TextField txtCosto;
    private TextField txtCantidadReabastecer;

    private TableView<Insumo> tablaInsumos;
    private ListView<Insumo> listaAlertasCriticas;

    public InventarioFxView(Runnable onDataChangedCallback) {
        this.onDataChangedCallback = onDataChangedCallback;
        this.presenter = new InventarioPresenter(this, ServiceLocator.getInstance().getInventarioService());
    }

    public Parent construirVista() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        // 1. Panel Izquierdo: Formulario de Registro / Edición y Reabastecimiento
        VBox cardFormulario = new VBox(8);
        cardFormulario.setPrefWidth(370);
        cardFormulario.setPadding(new Insets(20));
        cardFormulario.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblTitulo = new Label("Control de Inventario y Almacén");
        lblTitulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        txtCodigo = new TextField();
        txtCodigo.setPromptText("Código (ej: INS-008)");

        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del insumo (ej: Resina Fotocurada)");

        // Categorías predeterminadas odontológicas para evitar inconsistencias
        cmbCategoria = new ComboBox<>();
        cmbCategoria.setMaxWidth(Double.MAX_VALUE);
        cmbCategoria.getItems().addAll(
                "Restauración y Operatoria",
                "Anestésicos y Farmacología",
                "Bioseguridad y Esterilización",
                "Endodoncia",
                "Ortodoncia",
                "Periodoncia",
                "Cirugía y Exodoncia",
                "Descartables y Consumibles",
                "Impresión y Modelos",
                "Estética y Blanqueamiento",
                "Cementos y Adhesivos",
                "Odontopediatría"
        );
        cmbCategoria.setValue("Restauración y Operatoria");

        // Unidades de medida estandarizadas
        cmbUnidad = new ComboBox<>();
        cmbUnidad.setMaxWidth(Double.MAX_VALUE);
        cmbUnidad.getItems().addAll(
                "Jeringa/Porción",
                "Carpule",
                "Par",
                "Unidad",
                "Aplicador",
                "Dosis",
                "Frasco",
                "Caja",
                "Tubo",
                "Rollo"
        );
        cmbUnidad.setValue("Unidad");

        txtStockActual = new TextField();
        txtStockActual.setPromptText("Cantidad inicial disponible (ej: 25)");

        txtStockMinimo = new TextField();
        txtStockMinimo.setPromptText("Stock mínimo crítico para alerta (ej: 10)");

        txtCosto = new TextField();
        txtCosto.setPromptText("Costo unitario ($) (ej: 18000)");

        Button btnRegistrar = new Button("Registrar / Guardar Insumo");
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setPrefHeight(38);
        btnRegistrar.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnRegistrar.setOnAction(e -> {
            presenter.registrarInsumo(
                    txtCodigo.getText(),
                    txtNombre.getText(),
                    cmbCategoria.getValue(),
                    cmbUnidad.getValue(),
                    txtStockActual.getText(),
                    txtStockMinimo.getText(),
                    txtCosto.getText()
            );
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });

        Separator sep = new Separator();
        Label lblReab = new Label("Reabastecer Insumo Seleccionado:");
        lblReab.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E3A8A; -fx-font-size: 13px;");

        HBox filaReab = new HBox(8);
        txtCantidadReabastecer = new TextField("10");
        txtCantidadReabastecer.setPrefWidth(90);

        Button btnReabastecer = new Button("+ Ingresar Stock");
        btnReabastecer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnReabastecer, Priority.ALWAYS);
        btnReabastecer.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnReabastecer.setOnAction(e -> {
            presenter.reabastecerInsumo(
                    tablaInsumos.getSelectionModel().getSelectedItem(),
                    txtCantidadReabastecer.getText()
            );
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });
        filaReab.getChildren().addAll(txtCantidadReabastecer, btnReabastecer);

        cardFormulario.getChildren().addAll(
                lblTitulo,
                new Label("Código Insumo *:"), txtCodigo,
                new Label("Nombre del Material *:"), txtNombre,
                new Label("Categoría Predeterminada *:"), cmbCategoria,
                new Label("Unidad de Medida *:"), cmbUnidad,
                new Label("Stock Actual *:"), txtStockActual,
                new Label("Stock Mínimo Crítico (Alerta) *:"), txtStockMinimo,
                new Label("Costo Unitario ($) *:"), txtCosto,
                btnRegistrar,
                sep,
                lblReab,
                filaReab
        );

        // 2. Panel Derecho: Tabla de Insumos + Alertas Activas
        VBox panelDerecho = new VBox(12);
        panelDerecho.setPadding(new Insets(0, 0, 0, 18));
        VBox.setVgrow(panelDerecho, Priority.ALWAYS);

        Label lblTabla = new Label("Stock General de Materiales Odontológicos (Semáforo de Disponibilidad)");
        lblTabla.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        tablaInsumos = new TableView<>();
        tablaInsumos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaInsumos.setPrefHeight(270);
        VBox.setVgrow(tablaInsumos, Priority.ALWAYS);

        TableColumn<Insumo, String> colCod = new TableColumn<>("Código");
        colCod.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodigo()));

        TableColumn<Insumo, String> colNom = new TableColumn<>("Insumo Odontológico");
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));

        TableColumn<Insumo, String> colCat = new TableColumn<>("Categoría");
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategoria()));

        TableColumn<Insumo, String> colStock = new TableColumn<>("Stock Actual");
        colStock.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStockActual() + " " + d.getValue().getUnidadMedida()
        ));

        TableColumn<Insumo, String> colMin = new TableColumn<>("Mínimo Crítico");
        colMin.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStockMinimoCritico())));

        TableColumn<Insumo, String> colAlerta = new TableColumn<>("Alerta de Stock");
        colAlerta.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNivelAlerta().getEtiqueta()));
        colAlerta.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Insumo ins = getTableRow().getItem();
                    NivelAlertaStock nivel = ins.getNivelAlerta();
                    setText(item);
                    if (nivel == NivelAlertaStock.CRITICO) {
                        setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-font-weight: bold;");
                    } else if (nivel == NivelAlertaStock.ADVERTENCIA) {
                        setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tablaInsumos.getColumns().addAll(colCod, colNom, colCat, colStock, colMin, colAlerta);

        // Al seleccionar un insumo en la tabla, se precargan sus datos en el formulario
        tablaInsumos.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                txtCodigo.setText(sel.getCodigo());
                txtNombre.setText(sel.getNombre());
                if (cmbCategoria.getItems().contains(sel.getCategoria())) {
                    cmbCategoria.setValue(sel.getCategoria());
                }
                if (cmbUnidad.getItems().contains(sel.getUnidadMedida())) {
                    cmbUnidad.setValue(sel.getUnidadMedida());
                }
                txtStockActual.setText(String.valueOf(sel.getStockActual()));
                txtStockMinimo.setText(String.valueOf(sel.getStockMinimoCritico()));
                txtCosto.setText(String.format("%.0f", sel.getCostoUnitario()));
            }
        });

        Label lblAlertas = new Label("Alertas Activas de Escasez / Stock Bajo o Crítico (Amarillo / Rojo):");
        lblAlertas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #DC2626;");

        listaAlertasCriticas = new ListView<>();
        listaAlertasCriticas.setPrefHeight(100);

        panelDerecho.getChildren().addAll(lblTabla, tablaInsumos, lblAlertas, listaAlertasCriticas);

        root.setLeft(cardFormulario);
        root.setCenter(panelDerecho);

        presenter.cargarInventario();

        // Envolver en ScrollPane para que el usuario pueda desplazarse y ver todo sin que se corte nada abajo
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        return scroll;
    }

    public void refrescar() {
        presenter.cargarInventario();
    }

    @Override
    public void mostrarInventarioCompleto(List<Insumo> insumos) {
        tablaInsumos.setItems(FXCollections.observableArrayList(insumos));
        tablaInsumos.refresh();
    }

    @Override
    public void mostrarAlertasCriticas(List<Insumo> insumosEnAlerta) {
        listaAlertasCriticas.setItems(FXCollections.observableArrayList(insumosEnAlerta));
    }

    @Override
    public void limpiarFormularioInsumo() {
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue("Restauración y Operatoria");
        cmbUnidad.setValue("Unidad");
        txtStockActual.clear();
        txtStockMinimo.clear();
        txtCosto.clear();
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
