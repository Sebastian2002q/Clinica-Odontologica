package co.edu.uniquindio.clinicaodontologica.controller;

import co.edu.uniquindio.clinicaodontologica.factory.ModelFactory;
import co.edu.uniquindio.clinicaodontologica.model.Especialista;

import java.time.LocalDate;
import java.time.LocalTime;

public class EspecialistaController {

    private ModelFactory modelFactory;

    public EspecialistaController() {
        modelFactory = ModelFactory.getInstance();
    }

    public void agregarEspecialista(Especialista especialista) {
        modelFactory.agregarEspecialista(especialista);
    }

    public boolean existeCitaEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        return modelFactory.existeCitaEspecialista(especialista, fecha, hora);
    }
}
