package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.Usuario;
import co.edu.uniquindio.clinicaodontologica.model.enums.CanalNotificacion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
  * Contenedor Principal de Navegación con Menú Lateral (Sidebar) moderno,
  * títulos limpios en español y sincronización reactiva entre todos los módulos.
  */
public class MainDashboardFxView {
    private final Stage stage;
    private final Usuario usuarioAutenticado;

    private Label lblKpiCajaTop;
    private Label lblTituloModuloActual;
    private StackPane contenedorCentral;

    private DashboardHomeFxView dashboardHomeFxView;
    private PacienteFxView pacienteFxView;
    private AgendamientoFxView agendamientoFxView;
    private AtencionClinicaFxView atencionClinicaFxView;
    private FacturacionFxView facturacionFxView;
    private InventarioFxView inventarioFxView;
    private EspecialistaFxView especialistaFxView;
    private RecordatorioFxView recordatorioFxView;

    private final List<Node> vistasModulos = new ArrayList<>();
    private final List<String> titulosModulos = new ArrayList<>();
    private final List<Button> botonesMenuSidebar = new ArrayList<>();
    private int moduloActualIndex = 0;

    public MainDashboardFxView(Stage stage, Usuario usuarioAutenticado) {
        this.stage = stage;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    public Parent construirVista() {
        // Disparar barrido automático de recordatorios 24h al iniciar sesión
        ServiceLocator.getInstance()
                .getRecordatorioService()
                .dispararRecordatoriosAutomaticos24Horas(LocalDateTime.now(), CanalNotificacion.WHATSAPP);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F8FAFC;");

        // Inicializar vistas con callbacks
        Runnable callbackSincronizacion = this::refrescarTodasLasVistas;
        dashboardHomeFxView = new DashboardHomeFxView(this::seleccionarModulo);
        pacienteFxView = new PacienteFxView(callbackSincronizacion);
        agendamientoFxView = new AgendamientoFxView(callbackSincronizacion);
        atencionClinicaFxView = new AtencionClinicaFxView(callbackSincronizacion);
        facturacionFxView = new FacturacionFxView(usuarioAutenticado.getNombreCompleto(), callbackSincronizacion);
        inventarioFxView = new InventarioFxView(callbackSincronizacion);
        especialistaFxView = new EspecialistaFxView(callbackSincronizacion);
        recordatorioFxView = new RecordatorioFxView();

        // Registrar módulos y títulos limpios sin códigos técnicos
        vistasModulos.clear();
        titulosModulos.clear();

        vistasModulos.add(dashboardHomeFxView.construirVista());
        titulosModulos.add("Panel Principal — Flujo de Atención Odontológica");

        vistasModulos.add(pacienteFxView.construirVista());
        titulosModulos.add("Gestión y Registro de Pacientes");

        vistasModulos.add(agendamientoFxView.construirVista());
        titulosModulos.add("Agenda Médica y Citas Odontológicas");

        vistasModulos.add(atencionClinicaFxView.construirVista());
        titulosModulos.add("Atención Odontológica y Evolución Clínica");

        vistasModulos.add(facturacionFxView.construirVista());
        titulosModulos.add("Facturación, Cobros y Caja Diaria");

        vistasModulos.add(inventarioFxView.construirVista());
        titulosModulos.add("Control de Inventario y Almacén");

        vistasModulos.add(especialistaFxView.construirVista());
        titulosModulos.add("Doctores Especialistas y Consultorios");

        vistasModulos.add(recordatorioFxView.construirVista());
        titulosModulos.add("Recordatorios Preventivos de Citas (24h)");

        // ==========================================
        // 1. PANEL LATERAL IZQUIERDO (SIDEBAR)
        // ==========================================
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(260);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color: #0F172A;");

        // Cabecera del Sidebar con Logo y Título
        HBox brandHeader = new HBox(12);
        brandHeader.setAlignment(Pos.CENTER_LEFT);
        brandHeader.setPadding(new Insets(0, 4, 10, 4));

        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView imgLogo = new ImageView(new Image(logoStream));
            imgLogo.setFitWidth(38);
            imgLogo.setFitHeight(38);
            imgLogo.setPreserveRatio(true);
            brandHeader.getChildren().add(imgLogo);
        }

        VBox brandTexts = new VBox(2);
        Label lblBrandName = new Label("DENTAL U");
        lblBrandName.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblBrandSub = new Label("Sistema Odontológico");
        lblBrandSub.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        brandTexts.getChildren().addAll(lblBrandName, lblBrandSub);
        brandHeader.getChildren().add(brandTexts);

        // Tarjeta de Usuario en Sesión
        VBox userCard = new VBox(3);
        userCard.setPadding(new Insets(10, 12, 10, 12));
        userCard.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 8;");
        Label lblNombreUsuario = new Label(usuarioAutenticado.getNombreCompleto());
        lblNombreUsuario.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label lblRolUsuario = new Label("● " + usuarioAutenticado.getRol().getEtiqueta());
        lblRolUsuario.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 11px; -fx-font-weight: bold;");
        userCard.getChildren().addAll(lblNombreUsuario, lblRolUsuario);

        Label lblSeccionMenu = new Label("MENÚ PRINCIPAL");
        lblSeccionMenu.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 4 2 4;");

        // Botones de Navegación del Sidebar
        botonesMenuSidebar.clear();
        String[] menuLabels = {
                "🏠  Inicio",
                "👥  Pacientes",
                "📅  Agenda y Citas",
                "🩺  Atención Clínica",
                "💳  Caja y Pagos",
                "📦  Inventario",
                "👨‍⚕️  Especialistas",
                "🔔  Recordatorios"
        };

        VBox contenedorBotonesNav = new VBox(5);
        for (int i = 0; i < menuLabels.length; i++) {
            final int index = i;
            Button btnNav = new Button(menuLabels[i]);
            btnNav.setMaxWidth(Double.MAX_VALUE);
            btnNav.setPrefHeight(40);
            btnNav.setAlignment(Pos.CENTER_LEFT);
            btnNav.setPadding(new Insets(8, 14, 8, 14));
            btnNav.setOnAction(e -> seleccionarModulo(index));
            botonesMenuSidebar.add(btnNav);
            contenedorBotonesNav.getChildren().add(btnNav);
        }

        Region spacerSidebar = new Region();
        VBox.setVgrow(spacerSidebar, Priority.ALWAYS);

        Button btnCerrarSesion = new Button("🚪  Cerrar Sesión");
        btnCerrarSesion.setMaxWidth(Double.MAX_VALUE);
        btnCerrarSesion.setPrefHeight(38);
        btnCerrarSesion.setAlignment(Pos.CENTER_LEFT);
        btnCerrarSesion.setStyle(
                "-fx-background-color: rgba(220, 38, 38, 0.15); -fx-text-fill: #FCA5A5; " +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        btnCerrarSesion.setOnAction(e -> cerrarSesion());

        sidebar.getChildren().addAll(
                brandHeader,
                userCard,
                new Separator(),
                lblSeccionMenu,
                contenedorBotonesNav,
                spacerSidebar,
                btnCerrarSesion
        );

        ScrollPane scrollSidebar = new ScrollPane(sidebar);
        scrollSidebar.setFitToWidth(true);
        scrollSidebar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollSidebar.setStyle("-fx-background: #0F172A; -fx-background-color: #0F172A;");

        // ==========================================
        // 2. PANEL CONTENIDO CENTRAL
        // ==========================================
        BorderPane panelContenido = new BorderPane();

        // Barra Superior del Contenido
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14, 26, 14, 26));
        topBar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        lblTituloModuloActual = new Label("Panel Principal");
        lblTituloModuloActual.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        lblKpiCajaTop = new Label();
        lblKpiCajaTop.setStyle(
                "-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; " +
                "-fx-padding: 6 16 6 16; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 13px;"
        );

        topBar.getChildren().addAll(lblTituloModuloActual, topSpacer, lblKpiCajaTop);

        // Contenedor dinámico de vistas
        contenedorCentral = new StackPane();
        contenedorCentral.setStyle("-fx-background-color: #F8FAFC;");

        ScrollPane scrollContenido = new ScrollPane(contenedorCentral);
        scrollContenido.setFitToWidth(true);
        scrollContenido.setFitToHeight(true);
        scrollContenido.setStyle("-fx-background-color: transparent; -fx-background: #F8FAFC;");

        panelContenido.setTop(topBar);
        panelContenido.setCenter(scrollContenido);

        root.setLeft(scrollSidebar);
        root.setCenter(panelContenido);

        // Seleccionar por defecto el primer módulo (Inicio)
        seleccionarModulo(0);
        actualizarKpiCajaHeader();

        return root;
    }

    public void seleccionarModulo(int indice) {
        if (indice < 0 || indice >= vistasModulos.size()) {
            return;
        }
        moduloActualIndex = indice;

        // Actualizar vista activa en el contenedor central
        contenedorCentral.getChildren().clear();
        contenedorCentral.getChildren().add(vistasModulos.get(indice));

        // Actualizar título superior
        lblTituloModuloActual.setText(titulosModulos.get(indice));

        // Actualizar estilos activos de los botones del sidebar
        for (int i = 0; i < botonesMenuSidebar.size(); i++) {
            Button btn = botonesMenuSidebar.get(i);
            if (i == indice) {
                btn.setStyle(
                        "-fx-background-color: #2563EB; -fx-text-fill: #FFFFFF; " +
                        "-fx-font-weight: bold; -fx-font-size: 13.5px; -fx-background-radius: 8; -fx-cursor: hand;"
                );
            } else {
                btn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #94A3B8; " +
                        "-fx-font-weight: normal; -fx-font-size: 13.5px; -fx-background-radius: 8; -fx-cursor: hand;"
                );
            }
        }

        refrescarTodasLasVistas();
    }

    private void refrescarTodasLasVistas() {
        actualizarKpiCajaHeader();
        dashboardHomeFxView.refrescar();
        pacienteFxView.refrescar();
        agendamientoFxView.refrescar();
        atencionClinicaFxView.refrescar();
        facturacionFxView.refrescar();
        inventarioFxView.refrescar();
        especialistaFxView.refrescar();
        recordatorioFxView.refrescar();
    }

    private void actualizarKpiCajaHeader() {
        double totalCaja = ServiceLocator.getInstance().getFacturacionService().obtenerCajaDiaria().getFlujoCajaTotal();
        lblKpiCajaTop.setText("💰 Flujo Caja Diario: $" + String.format("%,.0f", totalCaja));
    }

    private void cerrarSesion() {
        if (UiAlerts.confirmar("Cerrar Sesión", "¿Está seguro de cerrar la sesión actual?")) {
            ServiceLocator.getInstance().getAuthService().cerrarSesion();
            LoginFxView loginView = new LoginFxView(stage);
            Scene scene = new Scene(loginView.construirVista(), 1120, 700);
            UiAlerts.aplicarEstilos(scene);
            stage.setTitle("Clínica Odontológica DENTAL U — Inicio de Sesión");
            stage.setScene(scene);
            stage.show();
        }
    }
}
