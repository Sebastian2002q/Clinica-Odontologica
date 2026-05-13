package co.edu.uniquindio.clinicaodontologica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClinicaOdontologicaApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/co/edu/uniquindio/clinicaodontologica/view/login.fxml"
                )
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("Clínica Odontológica");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}