package co.edu.uniquindio.clinicaodontologica.model;

import lombok.*;

@Getter
@Setter
public class Recepcionista extends Usuario {

    private String nombre;

    public Recepcionista(String username, String password, String nombre) {
        super(username, password);
        this.nombre = nombre;
    }
}