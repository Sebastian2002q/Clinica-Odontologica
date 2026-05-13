package co.edu.uniquindio.clinicaodontologica.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class Cita {
    private Paciente paciente;
    private Especialista especialista;
    private String motivo;
    private LocalDate fecha;
    private LocalTime hora;

    @Override
    public String toString() {

        return paciente.getNombre()
                + " - "
                + fecha
                + " "
                + hora;
    }
}
