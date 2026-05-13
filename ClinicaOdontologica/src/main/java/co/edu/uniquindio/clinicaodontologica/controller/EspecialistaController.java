package co.edu.uniquindio.clinicaodontologica.controller;

import co.edu.uniquindio.clinicaodontologica.factory.ModelFactory;
import co.edu.uniquindio.clinicaodontologica.model.Especialista;

public class EspecialistaController {

    private ModelFactory modelFactory;

    public EspecialistaController() {
        modelFactory = ModelFactory.getInstance();
    }

    public void agregarEspecialista(Especialista especialista) {
        modelFactory.agregarEspecialista(especialista);
    }
}
