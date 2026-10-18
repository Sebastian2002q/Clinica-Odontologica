package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.*;
import co.edu.uniquindio.clinicaodontologica.presenter.AtencionClinicaPresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.IAtencionClinicaView;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
  * Vista Pasiva JavaFX para F-03 (Registrar procedimiento en Historia Clínica), RN-003 y RN-004.
  * Incluye menú desplegable de diagnósticos estándar CIE-10, tratamiento, evolución médica,
  * consumo de insumos en tiempo real y actualización de la Historia Clínica a "Actualizada".
  */
public class AtencionClinicaFxView implements IAtencionClinicaView {
    private final AtencionClinicaPresenter presenter;
    private final Runnable onDataChangedCallback;

    private ComboBox<Cita> cmbCitasActivas;
    private ComboBox<CatalogoDiagnostico> cmbDiagnosticosEstandar;
    private TextField txtTratamientoAplicado;
    private TextField txtValorProcedimiento;
    private TextArea txtEvolucionMedica;

    private ComboBox<Insumo> cmbInsumos;
    private TextField txtCantidadInsumo;
    private ListView<ConsumoInsumo> listaInsumosDeclarados;
    private final List<ConsumoInsumo> consumosActuales = new ArrayList<>();

    private TextArea txtVisorHistoriaClinica;

    public AtencionClinicaFxView(Runnable onDataChangedCallback) {
        this.onDataChangedCallback = onDataChangedCallback;
        ServiceLocator loc = ServiceLocator.getInstance();
        this.presenter = new AtencionClinicaPresenter(
                this,
                loc.getAtencionClinicaService(),
                loc.getAgendamientoService(),
                loc.getInventarioService()
        );
    }

    public Parent construirVista() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        // Izquierda: Formulario Clínico Digital (F-03 SWR)
        VBox cardFormulario = new VBox(10);
        cardFormulario.setPrefWidth(520);
        cardFormulario.setPadding(new Insets(20));
        cardFormulario.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblTitulo = new Label("Registro de Procedimiento Clínico");
        lblTitulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        cmbCitasActivas = new ComboBox<>();
        cmbCitasActivas.setMaxWidth(Double.MAX_VALUE);
        cmbCitasActivas.setPromptText("Seleccione la cita en atención...");
        cmbCitasActivas.setOnAction(e -> presenter.seleccionarCita(cmbCitasActivas.getValue()));

        cmbDiagnosticosEstandar = new ComboBox<>();
        cmbDiagnosticosEstandar.setMaxWidth(Double.MAX_VALUE);
        cmbDiagnosticosEstandar.setPromptText("Seleccione Diagnóstico Estándar (CIE-10)...");
        cmbDiagnosticosEstandar.setOnAction(e -> {
            CatalogoDiagnostico cat = cmbDiagnosticosEstandar.getValue();
            if (cat != null) {
                txtTratamientoAplicado.setText(cat.getTratamientoSugerido());
                txtValorProcedimiento.setText(String.format("%.0f", cat.getTarifaEstandar()));
            }
        });

        txtTratamientoAplicado = new TextField();
        txtTratamientoAplicado.setPromptText("Tratamiento odontológico aplicado");

        txtValorProcedimiento = new TextField();
        txtValorProcedimiento.setPromptText("Valor del procedimiento ($)");

        txtEvolucionMedica = new TextArea();
        txtEvolucionMedica.setPromptText("Describa la evolución médica, hallazgos clínicos y procedimiento realizado...");
        txtEvolucionMedica.setPrefRowCount(3);
        txtEvolucionMedica.setWrapText(true);

        // Subsección de insumos consumidos (RN-004 y F-06)
        Label lblInsumos = new Label("Insumos Consumidos en la Consulta (Descuento automático F-06):");
        lblInsumos.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        HBox filaInsumo = new HBox(8);
        cmbInsumos = new ComboBox<>();
        cmbInsumos.setPromptText("Seleccione insumo (ej. Resina)...");
        cmbInsumos.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cmbInsumos, Priority.ALWAYS);

        txtCantidadInsumo = new TextField("1");
        txtCantidadInsumo.setPrefWidth(70);
        txtCantidadInsumo.setPromptText("Cant.");

        Button btnAgregarInsumo = new Button("+ Agregar Insumo");
        btnAgregarInsumo.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-background-radius: 6;");
        btnAgregarInsumo.setOnAction(e -> agregarInsumoALista());

        filaInsumo.getChildren().addAll(cmbInsumos, txtCantidadInsumo, btnAgregarInsumo);

        listaInsumosDeclarados = new ListView<>();
        listaInsumosDeclarados.setPrefHeight(90);

        HBox botonesAccion = new HBox(10);
        Button btnGuardarProc = new Button("Guardar Procedimiento y Actualizar Historia Clínica");
        btnGuardarProc.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnGuardarProc, Priority.ALWAYS);
        btnGuardarProc.setPrefHeight(40);
        btnGuardarProc.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnGuardarProc.setOnAction(e -> {
            try {
                double valor = Double.parseDouble(txtValorProcedimiento.getText().trim());
                CatalogoDiagnostico diag = cmbDiagnosticosEstandar.getValue();
                String textoDiag = (diag != null) ? diag.getCodigoCie10() + " - " + diag.getDiagnosticoEstandar() : "";
                presenter.registrarProcedimiento(
                        cmbCitasActivas.getValue(),
                        textoDiag,
                        txtTratamientoAplicado.getText(),
                        txtEvolucionMedica.getText(),
                        valor,
                        new ArrayList<>(consumosActuales)
                );
                if (onDataChangedCallback != null) {
                    onDataChangedCallback.run();
                }
            } catch (NumberFormatException ex) {
                mostrarError("Valor inválido", "Ingrese un valor numérico válido para la tarifa del procedimiento.");
            }
        });

        Button btnNoAsistio = new Button("Marcar Inasistencia");
        btnNoAsistio.setPrefHeight(40);
        btnNoAsistio.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnNoAsistio.setOnAction(e -> {
            presenter.marcarInasistencia(cmbCitasActivas.getValue());
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });

        botonesAccion.getChildren().addAll(btnGuardarProc, btnNoAsistio);

        cardFormulario.getChildren().addAll(
                lblTitulo,
                new Label("Cita / Paciente en Atención *:"), cmbCitasActivas,
                new Label("Diagnóstico Estándar (Menú Desplegable) *:"), cmbDiagnosticosEstandar,
                new Label("Tratamiento Aplicado *:"), txtTratamientoAplicado,
                new Label("Valor del Procedimiento ($) *:"), txtValorProcedimiento,
                new Label("Evolución Médica Estructurada *:"), txtEvolucionMedica,
                lblInsumos, filaInsumo, listaInsumosDeclarados,
                botonesAccion
        );

        // Derecha: Expediente de Historia Clínica en tiempo real
        VBox panelDerecho = new VBox(12);
        panelDerecho.setPadding(new Insets(0, 0, 0, 18));

        Label lblHc = new Label("Ficha de Evolución e Historia Clínica Digital (Indexación Permanente)");
        lblHc.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        txtVisorHistoriaClinica = new TextArea();
        txtVisorHistoriaClinica.setEditable(false);
        VBox.setVgrow(txtVisorHistoriaClinica, Priority.ALWAYS);
        txtVisorHistoriaClinica.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 13px;");

        panelDerecho.getChildren().addAll(lblHc, txtVisorHistoriaClinica);

        root.setLeft(cardFormulario);
        root.setCenter(panelDerecho);

        presenter.cargarDatosIniciales();
        return root;
    }

    private void agregarInsumoALista() {
        Insumo insumo = cmbInsumos.getValue();
        if (insumo == null) {
            mostrarError("Seleccione insumo", "Debe elegir un insumo odontológico de la lista.");
            return;
        }
        try {
            int cant = Integer.parseInt(txtCantidadInsumo.getText().trim());
            if (cant <= 0) {
                mostrarError("Cantidad inválida", "La cantidad debe ser mayor a cero.");
                return;
            }
            ConsumoInsumo consumo = new ConsumoInsumo(insumo.getCodigo(), insumo.getNombre(), cant, insumo.getUnidadMedida());
            consumosActuales.add(consumo);
            listaInsumosDeclarados.setItems(FXCollections.observableArrayList(consumosActuales));
        } catch (NumberFormatException e) {
            mostrarError("Cantidad inválida", "Ingrese un número entero válido.");
        }
    }

    public void refrescar() {
        presenter.cargarDatosIniciales();
    }

    @Override
    public void mostrarCitasActivas(List<Cita> citas) {
        cmbCitasActivas.setItems(FXCollections.observableArrayList(citas));
    }

    @Override
    public void mostrarCatalogoDiagnosticos(List<CatalogoDiagnostico> diagnosticos) {
        cmbDiagnosticosEstandar.setItems(FXCollections.observableArrayList(diagnosticos));
    }

    @Override
    public void mostrarInsumosDisponibles(List<Insumo> insumos) {
        cmbInsumos.setItems(FXCollections.observableArrayList(insumos));
    }

    @Override
    public void mostrarHistoriaClinicaActualizada(HistoriaClinica hc) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================\n");
        sb.append("  HISTORIA CLÍNICA ODONTOLÓGICA — ").append(hc.getIdHistoria()).append("\n");
        sb.append("====================================================================\n");
        sb.append("Link Permanente : ").append(hc.getLinkHistoriaClinica()).append("\n");
        sb.append("Paciente        : ").append(hc.getNombrePaciente()).append(" (CC: ").append(hc.getCedulaPaciente()).append(")\n");
        sb.append("ESTADO HISTORIAL: ").append(hc.getEstado().getEtiqueta().toUpperCase()).append("\n");
        sb.append("--------------------------------------------------------------------\n");
        if (hc.getProcedimientos().isEmpty()) {
            sb.append("El paciente aún no registra procedimientos clínicos.\n");
        } else {
            for (ProcedimientoClinico p : hc.getProcedimientos()) {
                sb.append("Procedimiento   : ").append(p.getIdProcedimiento()).append(" (Cita ").append(p.getIdCita()).append(")\n");
                sb.append("Fecha / Hora    : ").append(p.getFechaHora()).append("\n");
                sb.append("Especialista    : ").append(p.getNombreEspecialista()).append("\n");
                sb.append("Diagnóstico     : ").append(p.getDiagnosticoEstandar()).append("\n");
                sb.append("Tratamiento     : ").append(p.getTratamientoAplicado()).append("\n");
                sb.append("Evolución Médica: ").append(p.getEvolucionMedica()).append("\n");
                sb.append("Valor Tarifa    : $").append(String.format("%,.0f", p.getValorProcedimiento())).append("\n");
                sb.append("Insumos Usados  : ").append(p.getInsumosConsumidos()).append("\n");
                sb.append("--------------------------------------------------------------------\n");
            }
        }
        txtVisorHistoriaClinica.setText(sb.toString());
    }

    @Override
    public void limpiarFormularioClinico() {
        txtTratamientoAplicado.clear();
        txtValorProcedimiento.clear();
        txtEvolucionMedica.clear();
        consumosActuales.clear();
        listaInsumosDeclarados.getItems().clear();
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
