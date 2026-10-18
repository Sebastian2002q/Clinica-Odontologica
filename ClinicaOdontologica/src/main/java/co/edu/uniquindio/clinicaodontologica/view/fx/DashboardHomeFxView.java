package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.Insumo;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

/**
  * Tablero de Control de Flujo Intuitivo para Guía Operacional del Sistema.
  * Presenta la lógica clínica secuencial (Paso 1 -> Paso 2 -> Paso 3 -> Paso 4)
  * e indicadores en vivo para que cualquier usuario o evaluador comprenda
  * exactamente el ciclo de vida de la atención odontológica.
  */
public class DashboardHomeFxView {
    private final Consumer<Integer> onNavegarTabCallback;

    private Label lblTotalPacientes;
    private Label lblTotalCitas;
    private Label lblTotalCaja;
    private Label lblAlertasInsumos;
    private Label lblEstadoInventarioTexto;

    public DashboardHomeFxView(Consumer<Integer> onNavegarTabCallback) {
        this.onNavegarTabCallback = onNavegarTabCallback;
    }

    public Parent construirVista() {
        VBox contenedorPrincipal = new VBox(22);
        contenedorPrincipal.setPadding(new Insets(24, 28, 28, 28));

        // 1. Banner de Bienvenida y Guía Metodológica
        VBox bannerBienvenida = new VBox(8);
        bannerBienvenida.setPadding(new Insets(20, 24, 20, 24));
        bannerBienvenida.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E3A8A, #2563EB); " +
                "-fx-background-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label lblTituloBanner = new Label("Bienvenido a DENTAL U — Flujo Operacional de Atención");
        lblTituloBanner.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label lblSubtituloBanner = new Label(
                "La atención odontológica se ejecuta siguiendo una lógica secuencial de 4 pasos integrados.\n" +
                "Haga clic en cualquiera de los pasos para ingresar directamente al módulo correspondiente."
        );
        lblSubtituloBanner.setStyle("-fx-text-fill: #DBEAFE; -fx-font-size: 13px; -fx-line-spacing: 3px;");

        bannerBienvenida.getChildren().addAll(lblTituloBanner, lblSubtituloBanner);

        // 2. Sección del Flujo de Trabajo Secuencial (4 Pasos)
        Label lblTituloFlujo = new Label("Flujo de Atención Clínica (Secuencia Lógica)");
        lblTituloFlujo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        HBox gridFlujo = new HBox(16);
        gridFlujo.setAlignment(Pos.CENTER);

        // Paso 1: Paciente
        VBox cardPaso1 = crearCardPaso(
                "PASO 1", "#2563EB", "#DBEAFE",
                "1. Registro de Pacientes",
                "Todo paciente debe registrarse con cédula única antes de solicitar citas. El sistema crea su Historia Clínica digital inmediatamente.",
                lblTotalPacientes = new Label("Cargando..."),
                "Ir a Pacientes",
                1
        );

        // Paso 2: Cita
        VBox cardPaso2 = crearCardPaso(
                "PASO 2", "#059669", "#DCFCE7",
                "2. Agenda y Citas Médicas",
                "Verifique la agenda médica en el calendario interactivo. Bloques verdes están libres y rojos ocupados, impidiendo doble asignación.",
                lblTotalCitas = new Label("Cargando..."),
                "Ir al Calendario",
                2
        );

        // Paso 3: Atención Clínica
        VBox cardPaso3 = crearCardPaso(
                "PASO 3", "#7C3AED", "#EDE9FE",
                "3. Atención Odontológica",
                "El especialista selecciona el diagnóstico CIE-10, asienta el tratamiento y el sistema descuenta los insumos del stock.",
                new Label("Diagnóstico CIE-10 y Stock"),
                "Ir a Consulta Odontológica",
                3
        );

        // Paso 4: Facturación y Caja
        VBox cardPaso4 = crearCardPaso(
                "PASO 4", "#0D9488", "#CCFBF1",
                "4. Facturación y Caja",
                "Una vez atendido el paciente, caja recibe el pago (efectivo, tarjeta o transferencia), emite recibo y cuadra la caja diaria.",
                lblTotalCaja = new Label("Cargando..."),
                "Ir a Caja y Facturación",
                4
        );

        HBox.setHgrow(cardPaso1, Priority.ALWAYS);
        HBox.setHgrow(cardPaso2, Priority.ALWAYS);
        HBox.setHgrow(cardPaso3, Priority.ALWAYS);
        HBox.setHgrow(cardPaso4, Priority.ALWAYS);
        gridFlujo.getChildren().addAll(cardPaso1, cardPaso2, cardPaso3, cardPaso4);

        // 3. Módulos Logísticos y de Soporte
        Label lblTituloSoporte = new Label("Módulos Logísticos y de Soporte");
        lblTituloSoporte.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        HBox gridSoporte = new HBox(16);

        // Soporte: Inventario
        VBox cardInventario = crearCardSoporte(
                "📦 Gestión de Inventario",
                "Monitorea existencias de insumos odontológicos, puntos de reorden y niveles críticos de abastecimiento.",
                lblAlertasInsumos = new Label("0 Alertas"),
                lblEstadoInventarioTexto = new Label("Verificando insumos..."),
                "Abrir Inventario",
                5
        );

        // Soporte: Especialistas
        VBox cardEspecialistas = crearCardSoporte(
                "👨‍⚕️ Plantel de Especialistas",
                "Administra los doctores, especialidades, consultorios asignados y definición de bloques de disponibilidad semanal.",
                new Label("Doctores y Consultorios"),
                new Label("Horarios 8:00 AM a 5:00 PM"),
                "Abrir Especialistas",
                6
        );

        // Soporte: Recordatorios
        VBox cardRecordatorios = crearCardSoporte(
                "🔔 Recordatorios Preventivos",
                "Envío automático de notificaciones a pacientes 24 horas antes de su consulta para mitigar inasistencias.",
                new Label("Canales Digitales"),
                new Label("Notificaciones WhatsApp y Email"),
                "Abrir Recordatorios",
                7
        );

        HBox.setHgrow(cardInventario, Priority.ALWAYS);
        HBox.setHgrow(cardEspecialistas, Priority.ALWAYS);
        HBox.setHgrow(cardRecordatorios, Priority.ALWAYS);
        gridSoporte.getChildren().addAll(cardInventario, cardEspecialistas, cardRecordatorios);

        contenedorPrincipal.getChildren().addAll(
                bannerBienvenida,
                lblTituloFlujo,
                gridFlujo,
                new Separator(),
                lblTituloSoporte,
                gridSoporte
        );

        ScrollPane scroll = new ScrollPane(contenedorPrincipal);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        refrescar();
        return scroll;
    }

    private VBox crearCardPaso(String badgeTexto, String badgeColor, String badgeBg,
                               String titulo, String descripcion, Label lblKpi,
                               String textoBoton, int tabIndexDestino) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefHeight(290);
        card.getStyleClass().add("card-panel");

        Label badge = new Label(badgeTexto);
        badge.setStyle(
                "-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeColor + "; " +
                "-fx-padding: 3 10 3 10; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 11px;"
        );

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        lblTitulo.setWrapText(true);

        Label lblDesc = new Label(descripcion);
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        VBox.setVgrow(lblDesc, Priority.ALWAYS);

        lblKpi.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + badgeColor + ";");

        Button btnAccion = new Button(textoBoton);
        btnAccion.setMaxWidth(Double.MAX_VALUE);
        btnAccion.getStyleClass().add("btn-secondary");
        btnAccion.setOnAction(e -> {
            if (onNavegarTabCallback != null) {
                onNavegarTabCallback.accept(tabIndexDestino);
            }
        });

        card.getChildren().addAll(badge, lblTitulo, lblDesc, lblKpi, btnAccion);
        return card;
    }

    private VBox crearCardSoporte(String titulo, String descripcion, Label lblHeaderDato, Label lblDetalleDato,
                                  String textoBoton, int tabIndexDestino) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefHeight(220);
        card.getStyleClass().add("card-panel");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label lblDesc = new Label(descripcion);
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        VBox.setVgrow(lblDesc, Priority.ALWAYS);

        lblHeaderDato.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        lblDetalleDato.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        Button btnAccion = new Button(textoBoton);
        btnAccion.setMaxWidth(Double.MAX_VALUE);
        btnAccion.getStyleClass().add("btn-secondary");
        btnAccion.setOnAction(e -> {
            if (onNavegarTabCallback != null) {
                onNavegarTabCallback.accept(tabIndexDestino);
            }
        });

        card.getChildren().addAll(lblTitulo, lblDesc, lblHeaderDato, lblDetalleDato, btnAccion);
        return card;
    }

    public void refrescar() {
        ServiceLocator loc = ServiceLocator.getInstance();
        try {
            int totalPacientes = loc.getPacienteService().listarPacientes().size();
            lblTotalPacientes.setText("● " + totalPacientes + " Pacientes Registrados");

            int totalCitas = loc.getAgendamientoService().listarTodasLasCitasActivas().size();
            lblTotalCitas.setText("● " + totalCitas + " Citas Activas Programadas");

            double totalCaja = loc.getFacturacionService().obtenerCajaDiaria().getFlujoCajaTotal();
            lblTotalCaja.setText("● Caja Hoy: $" + String.format("%,.0f", totalCaja));

            List<Insumo> insumosAlerta = loc.getInventarioService().listarInsumosConAlerta();
            int cantAlertas = insumosAlerta.size();
            if (cantAlertas > 0) {
                lblAlertasInsumos.setText("⚠️ " + cantAlertas + " Insumos con Stock Bajo/Crítico");
                lblAlertasInsumos.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #DC2626;");
                lblEstadoInventarioTexto.setText("Requiere reabastecimiento");
            } else {
                lblAlertasInsumos.setText("✓ Todos los insumos en stock normal");
                lblAlertasInsumos.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #059669;");
                lblEstadoInventarioTexto.setText("Niveles de inventario óptimos");
            }
        } catch (Exception ignored) {
        }
    }
}
