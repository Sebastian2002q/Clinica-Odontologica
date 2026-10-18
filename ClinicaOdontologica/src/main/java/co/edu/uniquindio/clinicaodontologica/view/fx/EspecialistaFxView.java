package co.edu.uniquindio.clinicaodontologica.view.fx;

import co.edu.uniquindio.clinicaodontologica.config.ServiceLocator;
import co.edu.uniquindio.clinicaodontologica.model.entity.BloqueHorario;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.presenter.EspecialistaPresenter;
import co.edu.uniquindio.clinicaodontologica.view.contract.IEspecialistaView;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
  * Vista Pasiva JavaFX para F-07 (Registrar y gestionar especialistas y su agenda de horarios).
  */
public class EspecialistaFxView implements IEspecialistaView {
    private final EspecialistaPresenter presenter;
    private final Runnable onDataChangedCallback;

    private TextField txtNombre;
    private TextField txtEspecialidad;
    private TextField txtTarjetaProfesional;
    private TextField txtConsultorio;
    private DatePicker dateFechaHorario;
    private TextField txtHoraHorario;
    private ListView<String> listaHorariosBorrador;
    private final Map<LocalDate, List<LocalTime>> horariosBorrador = new TreeMap<>();

    private ListView<Especialista> listaEspecialistas;
    private ListView<BloqueHorario> listaAgendaEspecialistaSeleccionado;

    public EspecialistaFxView(Runnable onDataChangedCallback) {
        this.onDataChangedCallback = onDataChangedCallback;
        this.presenter = new EspecialistaPresenter(this, ServiceLocator.getInstance().getEspecialistaService());
    }

    public Parent construirVista() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        // Izquierda: Formulario de Registro de Especialista (F-07 SWR)
        VBox cardFormulario = new VBox(10);
        cardFormulario.setPrefWidth(420);
        cardFormulario.setPadding(new Insets(20));
        cardFormulario.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblTitulo = new Label("Registro de Especialistas y Horarios");
        lblTitulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo del especialista");

        txtEspecialidad = new TextField();
        txtEspecialidad.setPromptText("Especialidad (ej: Ortodoncia, Endodoncia)");

        txtTarjetaProfesional = new TextField();
        txtTarjetaProfesional.setPromptText("Tarjeta Profesional (ej: TP-20405)");

        txtConsultorio = new TextField();
        txtConsultorio.setPromptText("Consultorio asignado (ej: Consultorio 301)");

        dateFechaHorario = new DatePicker(LocalDate.now().plusDays(1));
        dateFechaHorario.setMaxWidth(Double.MAX_VALUE);

        HBox filaHora = new HBox(8);
        txtHoraHorario = new TextField("08:00");
        txtHoraHorario.setPromptText("Formato HH:mm (ej: 08:00)");
        HBox.setHgrow(txtHoraHorario, Priority.ALWAYS);

        Button btnAgregarBorrador = new Button("+ Agregar al Borrador");
        btnAgregarBorrador.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-background-radius: 6;");
        btnAgregarBorrador.setOnAction(e -> agregarHorarioABorrador());

        filaHora.getChildren().addAll(txtHoraHorario, btnAgregarBorrador);

        listaHorariosBorrador = new ListView<>();
        listaHorariosBorrador.setPrefHeight(100);

        HBox botonesForm = new HBox(10);
        Button btnGuardar = new Button("Guardar Nuevo Especialista");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnGuardar, Priority.ALWAYS);
        btnGuardar.setPrefHeight(40);
        btnGuardar.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> {
            presenter.registrarEspecialista(
                    txtNombre.getText(),
                    txtEspecialidad.getText(),
                    txtTarjetaProfesional.getText(),
                    txtConsultorio.getText(),
                    new TreeMap<>(horariosBorrador)
            );
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });

        Button btnCancelar = new Button("Cancelar / Limpiar");
        btnCancelar.setPrefHeight(40);
        btnCancelar.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnCancelar.setOnAction(e -> limpiarFormulario());

        botonesForm.getChildren().addAll(btnGuardar, btnCancelar);

        cardFormulario.getChildren().addAll(
                lblTitulo,
                new Label("Nombre del Especialista *:"), txtNombre,
                new Label("Especialidad *:"), txtEspecialidad,
                new Label("Tarjeta Profesional:"), txtTarjetaProfesional,
                new Label("Consultorio:"), txtConsultorio,
                new Separator(),
                new Label("Fecha de Disponibilidad *:"), dateFechaHorario,
                new Label("Hora (HH:mm) *:"), filaHora,
                new Label("Horarios por Guardar:"), listaHorariosBorrador,
                botonesForm
        );

        // Derecha: Lista de Especialistas Registrados + Su Agenda Configurada
        HBox panelDerecho = new HBox(16);
        panelDerecho.setPadding(new Insets(0, 0, 0, 18));

        VBox colListaEsp = new VBox(10);
        HBox.setHgrow(colListaEsp, Priority.ALWAYS);

        Label lblEsp = new Label("Especialistas Registrados en el Sistema:");
        lblEsp.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        listaEspecialistas = new ListView<>();
        VBox.setVgrow(listaEspecialistas, Priority.ALWAYS);
        listaEspecialistas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nuevo) -> {
            if (nuevo != null) {
                presenter.seleccionarEspecialista(nuevo);
            }
        });

        HBox accionesExistente = new HBox(8);
        Button btnAnadirHorarioExistente = new Button("+ Añadir Fecha/Hora al Especialista Seleccionado");
        btnAnadirHorarioExistente.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnAnadirHorarioExistente.setOnAction(e -> {
            presenter.agregarHorarioAEspecialistaExistente(
                    listaEspecialistas.getSelectionModel().getSelectedItem(),
                    dateFechaHorario.getValue(),
                    txtHoraHorario.getText()
            );
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
        });

        Button btnEliminarEsp = new Button("Eliminar Especialista");
        btnEliminarEsp.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnEliminarEsp.setOnAction(e -> {
            Especialista sel = listaEspecialistas.getSelectionModel().getSelectedItem();
            if (sel != null && UiAlerts.confirmar("Eliminar Especialista", "¿Desea eliminar al especialista " + sel.getNombre() + "?")) {
                presenter.eliminarEspecialista(sel);
                if (onDataChangedCallback != null) {
                    onDataChangedCallback.run();
                }
            }
        });
        accionesExistente.getChildren().addAll(btnAnadirHorarioExistente, btnEliminarEsp);

        colListaEsp.getChildren().addAll(lblEsp, listaEspecialistas, accionesExistente);

        VBox colAgenda = new VBox(10);
        HBox.setHgrow(colAgenda, Priority.ALWAYS);
        Label lblAg = new Label("Agenda y Disponibilidad del Especialista:");
        lblAg.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        listaAgendaEspecialistaSeleccionado = new ListView<>();
        VBox.setVgrow(listaAgendaEspecialistaSeleccionado, Priority.ALWAYS);

        colAgenda.getChildren().addAll(lblAg, listaAgendaEspecialistaSeleccionado);

        panelDerecho.getChildren().addAll(colListaEsp, colAgenda);

        root.setLeft(cardFormulario);
        root.setCenter(panelDerecho);

        presenter.cargarEspecialistas();
        return root;
    }

    private void agregarHorarioABorrador() {
        LocalDate fecha = dateFechaHorario.getValue();
        String horaTxt = txtHoraHorario.getText();
        if (fecha == null || horaTxt == null || horaTxt.isBlank()) {
            mostrarError("Campos incompletos", "Seleccione una fecha e ingrese la hora en formato HH:mm.");
            return;
        }
        try {
            LocalTime hora = LocalTime.parse(horaTxt.trim());
            List<LocalTime> listaDia = horariosBorrador.computeIfAbsent(fecha, f -> new ArrayList<>());
            if (!listaDia.contains(hora)) {
                listaDia.add(hora);
                listaDia.sort(LocalTime::compareTo);
            }
            refrescarListaBorrador();
        } catch (Exception e) {
            mostrarError("Hora inválida", "La hora debe tener el formato HH:mm (ejemplo: 08:00 o 14:30).");
        }
    }

    private void refrescarListaBorrador() {
        listaHorariosBorrador.getItems().clear();
        for (Map.Entry<LocalDate, List<LocalTime>> entry : horariosBorrador.entrySet()) {
            for (LocalTime h : entry.getValue()) {
                listaHorariosBorrador.getItems().add(entry.getKey() + " — " + h);
            }
        }
    }

    public void refrescar() {
        presenter.cargarEspecialistas();
    }

    @Override
    public void mostrarEspecialistas(List<Especialista> especialistas) {
        listaEspecialistas.setItems(FXCollections.observableArrayList(especialistas));
    }

    @Override
    public void mostrarHorariosEspecialista(List<BloqueHorario> bloques) {
        listaAgendaEspecialistaSeleccionado.setItems(FXCollections.observableArrayList(bloques));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.clear();
        txtEspecialidad.clear();
        txtTarjetaProfesional.clear();
        txtConsultorio.clear();
        horariosBorrador.clear();
        listaHorariosBorrador.getItems().clear();
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
