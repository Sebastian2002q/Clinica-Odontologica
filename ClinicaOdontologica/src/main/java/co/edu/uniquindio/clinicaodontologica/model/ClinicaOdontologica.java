package co.edu.uniquindio.clinicaodontologica.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
@Getter
public class ClinicaOdontologica {
    private String name;
    private List<Usuario> usuarioList = new ArrayList<>();
    private List<Cita> citaList = new ArrayList<>();
    private List<Especialista> especialistaList = new ArrayList<>();

    public ClinicaOdontologica(String name) {
        this.name = name;
    }

    public boolean verificarCredenciales(String username, String password) {
        for (Usuario u : usuarioList) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void agregarUsuario(Usuario usuario) {
        usuarioList.add(usuario);
    }

    public void agregarCita(Cita cita) {
        citaList.add(cita);
    }

    public void agregarEspecialista(Especialista especialista) {
        especialistaList.add(especialista);
    }

    public void eliminarHorarioEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        for (Especialista especialista1 : especialistaList) {
            if (especialista1.equals(especialista)) {
                especialista1.eliminarHorario(fecha, hora);
            }
        }
    }

    public List<Cita> obtenerCitasEspecialista(Especialista especialista) {
        List<Cita> citasEspecialista = new ArrayList<>();
        for (Cita cita : citaList) {

            if (cita.getEspecialista().equals(especialista)) {
                citasEspecialista.add(cita);
            }
        }
        return citasEspecialista;
    }
}
