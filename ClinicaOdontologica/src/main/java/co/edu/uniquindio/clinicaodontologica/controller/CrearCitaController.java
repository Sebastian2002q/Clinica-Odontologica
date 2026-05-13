package co.edu.uniquindio.clinicaodontologica.controller;

import co.edu.uniquindio.clinicaodontologica.factory.ModelFactory;
import co.edu.uniquindio.clinicaodontologica.model.Cita;
import co.edu.uniquindio.clinicaodontologica.model.Especialista;

import java.time.LocalDate;
import java.time.LocalTime;

public class CrearCitaController {
    private ModelFactory modelFactory;

    public CrearCitaController() {
        modelFactory = ModelFactory.getInstance();
    }
    public void agregarCita(Cita cita) {
        modelFactory.agregarCita(cita);
    }

    public void eliminarHorarioEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora){
        modelFactory.eliminarHorarioEspecialista(especialista, fecha, hora);
    }

}
