package co.edu.uniquindio.clinicaodontologica.controller;

import co.edu.uniquindio.clinicaodontologica.factory.ModelFactory;

public class LoginController {
    private ModelFactory modelFactory;

    public LoginController() {
        modelFactory = ModelFactory.getInstance();
    }

    public boolean verificarCredenciales(String username, String password) {
        return modelFactory.verificarCredenciales(username, password);
    }
}
