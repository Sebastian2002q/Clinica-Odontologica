package co.edu.uniquindio.clinicaodontologica.controller;

import co.edu.uniquindio.clinicaodontologica.factory.ModelFactory;
import co.edu.uniquindio.clinicaodontologica.model.Cita;
import co.edu.uniquindio.clinicaodontologica.model.Especialista;

import java.util.List;

public class AgendamientoController {
    private ModelFactory modelFactory;

    public AgendamientoController() {
        modelFactory = ModelFactory.getInstance();
    }

    public List<Especialista> obtenerEspecialistas() {
        return modelFactory.obtenerEspecialistas();
    }

    public List<Cita> obtenerCitasEspecialista(Especialista especialista) {
        return modelFactory.obtenerCitasEspecialista(especialista);
    }

    public void eliminarEspecialista(Especialista especialista) {
        modelFactory.eliminarEspecialista(especialista);
    }
}
