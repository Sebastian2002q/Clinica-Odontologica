package co.edu.uniquindio.clinicaodontologica;

import co.edu.uniquindio.clinicaodontologica.view.fx.LoginFxView;
import co.edu.uniquindio.clinicaodontologica.view.fx.UiAlerts;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
  * Punto de entrada de la aplicación JavaFX para Clínica Odontológica DENTAL U (SIGO).
  * Reestructurado desde cero bajo arquitectura modular MVP sin clases Dios.
  */
public class ClinicaOdontologicaApplication extends Application {

    @Override
    public void start(Stage stage) {
        LoginFxView loginView = new LoginFxView(stage);
        Scene scene = new Scene(loginView.construirVista(), 1120, 700);
        UiAlerts.aplicarEstilos(scene);

        stage.setTitle("Clínica Odontológica DENTAL U — SIGO (Arquitectura MVP)");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}