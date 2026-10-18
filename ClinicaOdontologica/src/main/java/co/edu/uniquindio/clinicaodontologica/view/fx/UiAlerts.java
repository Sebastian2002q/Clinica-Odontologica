package co.edu.uniquindio.clinicaodontologica.view.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
  * Utilidad transversal de alertas visuales para las vistas pasivas JavaFX.
  */
public final class UiAlerts {

    private UiAlerts() {
    }

    public static void aplicarEstilos(javafx.scene.Scene scene) {
        if (scene != null) {
            var resource = UiAlerts.class.getResource("/styles/app.css");
            if (resource != null) {
                String css = resource.toExternalForm();
                if (!scene.getStylesheets().contains(css)) {
                    scene.getStylesheets().add(css);
                }
            }
        }
    }

    public static void mostrarInformacion(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void mostrarError(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        var resource = UiAlerts.class.getResource("/styles/app.css");
        if (resource != null) {
            alert.getDialogPane().getStylesheets().add(resource.toExternalForm());
        }
        Optional<ButtonType> res = alert.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        var resource = UiAlerts.class.getResource("/styles/app.css");
        if (resource != null) {
            alert.getDialogPane().getStylesheets().add(resource.toExternalForm());
        }
        alert.showAndWait();
    }
}
