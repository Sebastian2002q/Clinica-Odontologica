package co.edu.uniquindio.clinicaodontologica.viewcontroller;

import co.edu.uniquindio.clinicaodontologica.controller.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    private LoginController loginController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginController = new LoginController();
    }

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    public void iniciarSesion() {
        String username = txtUsuario.getText();
        String password = txtPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Debe ingresar usuario y contraseña");
            return;
        }
        if (loginController.verificarCredenciales(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/clinicaodontologica/view/agendamiento.fxml"));
                Scene scene = new Scene(loader.load());
                AgendamientoViewController controller = loader.getController();
                controller.setUsuarioLogueado(username);
                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Agendamiento de Citas");
                stage.show();
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la ventana");
                e.printStackTrace();
            }
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Usuario o contraseña incorrectos");
            txtPassword.clear();
        }
    }

    @FXML
    public void registrarse() {

    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}