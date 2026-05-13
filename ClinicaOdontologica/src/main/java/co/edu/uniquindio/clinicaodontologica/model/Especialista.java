package co.edu.uniquindio.clinicaodontologica.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Especialista {
    private String nombre;
    private String especialidad;
    private Map<LocalDate, List<LocalTime>> horariosDisponibles;

    @Override
    public String toString() {
        return nombre + " - " + especialidad;
    }

    public void eliminarHorario(LocalDate fecha, LocalTime hora) {

        if (horariosDisponibles.containsKey(fecha)) {
            horariosDisponibles.get(fecha).remove(hora);

            if (horariosDisponibles.get(fecha).isEmpty()) {
                horariosDisponibles.remove(fecha);
            }
        }
    }
}