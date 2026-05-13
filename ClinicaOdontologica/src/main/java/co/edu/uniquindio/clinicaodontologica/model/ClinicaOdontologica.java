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

    public boolean agregarCita(Cita cita) {
        for (Cita cita1 : citaList) {
            if (cita1.getPaciente().getCedula().equals(cita.getPaciente().getCedula()) && cita1.getFecha().equals(cita.getFecha()) && cita1.getHora().equals(cita.getHora())) {
                return false;
            }
        }
        citaList.add(cita);
        return true;
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

    public void eliminarEspecialista(Especialista especialista) {
        citaList.removeIf(cita -> cita.getEspecialista().equals(especialista));
        especialistaList.remove(especialista);
    }

    public boolean existeCitaEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        for (Cita cita : citaList) {
            if (cita.getEspecialista().equals(especialista) && cita.getFecha().equals(fecha) && cita.getHora().equals(hora)) {
                return true;
            }
        }
        return false;
    }

    public void eliminarCita(Cita cita) {
        citaList.remove(cita);
        Especialista especialista = cita.getEspecialista();
        especialista.getHorariosDisponibles().computeIfAbsent(cita.getFecha(), f -> new ArrayList<>()).add(cita.getHora());
        especialista.getHorariosDisponibles().get(cita.getFecha()).sort(LocalTime::compareTo);
    }
}
