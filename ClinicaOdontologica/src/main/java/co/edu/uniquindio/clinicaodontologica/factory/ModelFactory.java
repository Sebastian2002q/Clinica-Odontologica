package co.edu.uniquindio.clinicaodontologica.factory;

import co.edu.uniquindio.clinicaodontologica.model.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ModelFactory {

    private static ModelFactory instance;

    private ClinicaOdontologica clinica;

    public static ModelFactory getInstance() {
        if (instance == null) {
            instance = new ModelFactory();
        }
        return instance;
    }

    private ModelFactory() {
        clinica = inicializarDatos();
    }

    private static ClinicaOdontologica inicializarDatos() {
        ClinicaOdontologica clinica = new ClinicaOdontologica("DENTAL U");
        Usuario usuario1 = new Usuario("admin", "admin");
        Usuario usuario2 = new Usuario("user", "user");
        clinica.agregarUsuario(usuario1);
        clinica.agregarUsuario(usuario2);
        Map<LocalDate, List<LocalTime>> horariosJuan = new HashMap<>();
        horariosJuan.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(14, 0))));
        horariosJuan.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(8, 30), LocalTime.of(9, 30), LocalTime.of(11, 0), LocalTime.of(15, 0))));
        Map<LocalDate, List<LocalTime>> horariosMaria = new HashMap<>();
        horariosMaria.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(13, 0))));
        horariosMaria.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(14, 0), LocalTime.of(16, 0))));
        Especialista especialista1 = new Especialista("Juan Perez", "Ortodoncia", horariosJuan);
        Especialista especialista2 = new Especialista("María Gómez", "Odontología General", horariosMaria);
        clinica.agregarEspecialista(especialista1);
        clinica.agregarEspecialista(especialista2);
        return clinica;
    }

    public boolean verificarCredenciales(String username, String password) {
        return clinica.verificarCredenciales(username, password);
    }

    public List<Especialista> obtenerEspecialistas() {
        return clinica.getEspecialistaList();
    }

    public boolean agregarCita(Cita cita) {
        return clinica.agregarCita(cita);
    }

    public void eliminarHorarioEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        clinica.eliminarHorarioEspecialista(especialista, fecha, hora);
    }

    public List<Cita> obtenerCitasEspecialista(Especialista especialista) {
        return clinica.obtenerCitasEspecialista(especialista);
    }

    public void eliminarEspecialista(Especialista especialista) {
        clinica.eliminarEspecialista(especialista);
    }

    public void agregarEspecialista(Especialista especialista) {
        clinica.agregarEspecialista(especialista);
    }

    public boolean existeCitaEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        return clinica.existeCitaEspecialista(especialista, fecha, hora);
    }

    public void eliminarCita(Cita citaSeleccionada) {
        clinica.eliminarCita(citaSeleccionada);
    }
}