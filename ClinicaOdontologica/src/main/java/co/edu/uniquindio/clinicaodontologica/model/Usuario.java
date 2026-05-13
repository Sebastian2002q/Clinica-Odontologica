package co.edu.uniquindio.clinicaodontologica.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class Usuario {
    private String username;
    private String password;

    @Override
    public String toString() {
        return username;
    }
}
