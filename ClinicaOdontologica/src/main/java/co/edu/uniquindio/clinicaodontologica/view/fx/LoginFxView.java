package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.Usuario;
import co.edu.uniquindio.clinicaodontologica.model.enums.RolUsuario;
import co.edu.uniquindio.clinicaodontologica.presenter.LoginPresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.ILoginView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;

/**
  * Vista Pasiva JavaFX para Inicio de Sesión y Registro de Usuarios (implementa ILoginView).
  */
public class LoginFxView implements ILoginView {
    private final Stage stage;
    private final LoginPresenter presenter;

    private TextField txtUsuario;
    private PasswordField txtPassword;
    private ComboBox<RolUsuario> cmbRolRegistro;
    private TextField txtNombreRegistro;

    public LoginFxView(Stage stage) {
        this.stage = stage;
        this.presenter = new LoginPresenter(this, ServiceLocator.getInstance().getAuthService());
    }

    public Parent construirVista() {
        HBox root = new HBox();
        root.setPrefSize(1120, 700);
        root.setStyle("-fx-background-color: #F8FAFC;");

        // Panel izquierdo de marca
        VBox panelMarca = new VBox(22);
        panelMarca.setAlignment(Pos.CENTER);
        panelMarca.setPadding(new Insets(45));
        panelMarca.setPrefWidth(450);
        panelMarca.setStyle("-fx-background-color: linear-gradient(to bottom right, #1E40AF, #1E3A8A);");
        HBox.setHgrow(panelMarca, Priority.ALWAYS);

        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView imgLogo = new ImageView(new Image(logoStream));
            imgLogo.setFitWidth(140);
            imgLogo.setFitHeight(140);
            imgLogo.setPreserveRatio(true);
            panelMarca.getChildren().add(imgLogo);
        }

        Label lblTituloMarca = new Label("DENTAL U — SIGO");
        lblTituloMarca.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");

        Label lblSubtituloMarca = new Label("Sistema Integral de Gestión Odontológica\nArquitectura Modular MVP (F-01 a F-07)");
        lblSubtituloMarca.setWrapText(true);
        lblSubtituloMarca.setAlignment(Pos.CENTER);
        lblSubtituloMarca.setStyle("-fx-text-fill: #DBEAFE; -fx-font-size: 14px; -fx-text-alignment: center;");

        Label lblCredencialesDemo = new Label(
                "Credenciales de prueba disponibles:\n\n" +
                "• admin / admin  ->  Administrador General\n" +
                "• user / user    ->  Auxiliar Administrativo\n" +
                "• doctor / doctor ->  Doctor Especialista"
        );
        lblCredencialesDemo.setStyle(
                "-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; " +
                "-fx-padding: 16; -fx-background-radius: 10; -fx-font-size: 12px; -fx-line-spacing: 4px;"
        );

        panelMarca.getChildren().addAll(lblTituloMarca, lblSubtituloMarca, lblCredencialesDemo);

        // Panel derecho de formulario
        StackPane contenedorDerecho = new StackPane();
        contenedorDerecho.setPadding(new Insets(40));
        HBox.setHgrow(contenedorDerecho, Priority.ALWAYS);

        VBox cardLogin = new VBox(16);
        cardLogin.setAlignment(Pos.CENTER_LEFT);
        cardLogin.setMaxWidth(460);
        cardLogin.setPadding(new Insets(36));
        cardLogin.getStyleClass().add("card-panel");

        Label lblBienvenido = new Label("Iniciar Sesión");
        lblBienvenido.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label lblInstruccion = new Label("Ingrese sus credenciales para acceder a los módulos clínicos y administrativos");
        lblInstruccion.setWrapText(true);
        lblInstruccion.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");

        txtUsuario = new TextField();
        txtUsuario.setPromptText("Usuario (ej: admin, user, doctor)");
        txtUsuario.setPrefHeight(42);

        txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");
        txtPassword.setPrefHeight(42);

        Button btnLogin = new Button("Ingresar al Sistema");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(44);
        btnLogin.setDefaultButton(true);
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setOnAction(e -> presenter.iniciarSesion(txtUsuario.getText(), txtPassword.getText()));

        Separator sep = new Separator();

        Label lblRegistroRapido = new Label("¿Nuevo funcionario? Registro rápido de usuario:");
        lblRegistroRapido.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        txtNombreRegistro = new TextField();
        txtNombreRegistro.setPromptText("Nombre completo del funcionario");
        txtNombreRegistro.setPrefHeight(38);

        cmbRolRegistro = new ComboBox<>();
        cmbRolRegistro.getItems().addAll(RolUsuario.values());
        cmbRolRegistro.setValue(RolUsuario.AUXILIAR_ADMINISTRATIVO);
        cmbRolRegistro.setMaxWidth(Double.MAX_VALUE);
        cmbRolRegistro.setPrefHeight(38);

        Button btnRegistrarse = new Button("Registrar Funcionario y Crear Usuario");
        btnRegistrarse.setMaxWidth(Double.MAX_VALUE);
        btnRegistrarse.setPrefHeight(40);
        btnRegistrarse.getStyleClass().add("btn-secondary");
        btnRegistrarse.setOnAction(e -> presenter.registrarNuevoUsuario(
                txtUsuario.getText(),
                txtPassword.getText(),
                txtNombreRegistro.getText(),
                cmbRolRegistro.getValue()
        ));

        cardLogin.getChildren().addAll(
                lblBienvenido,
                lblInstruccion,
                new Label("Usuario *:"),
                txtUsuario,
                new Label("Contraseña *:"),
                txtPassword,
                btnLogin,
                sep,
                lblRegistroRapido,
                txtNombreRegistro,
                cmbRolRegistro,
                btnRegistrarse
        );

        contenedorDerecho.getChildren().add(cardLogin);
        root.getChildren().addAll(panelMarca, contenedorDerecho);
        return root;
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
        UiAlerts.mostrarError(titulo, mensaje);
    }

    @Override
    public void mostrarInformacion(String titulo, String mensaje) {
        UiAlerts.mostrarInformacion(titulo, mensaje);
    }

    @Override
    public void limpiarCampos() {
        txtUsuario.clear();
        txtPassword.clear();
        txtNombreRegistro.clear();
    }

    @Override
    public void navegarAlPanelPrincipal(Usuario usuarioAutenticado) {
        MainDashboardFxView dashboard = new MainDashboardFxView(stage, usuarioAutenticado);
        Scene scene = new Scene(dashboard.construirVista(), 1280, 780);
        UiAlerts.aplicarEstilos(scene);
        stage.setTitle("DENTAL U - SIGO | Sesión: " + usuarioAutenticado.getNombreCompleto());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
