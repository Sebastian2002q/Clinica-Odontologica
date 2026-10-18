package co.edu.uniquindio.clinicaodontologica.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
  * Objeto que representa un slot o bloque temporal en el calendario de la agenda médica.
  */
@Getter
@Setter
@AllArgsConstructor
public class BloqueHorario {

    /**
      * Fecha correspondiente al bloque de atención.
      */
    private LocalDate fecha;

    /**
      * Hora exacta de inicio de la cita o bloque de atención.
      */
    private LocalTime hora;

    /**
      * Indicador de si el bloque está libre (true) u ocupado (false).
      */
    private boolean disponible;

    /**
      * Representación textual con indicador de estado para depuración y registros.
      *
      * @return Cadena con la hora y el texto LIBRE u OCUPADO
      */
    @Override
    public String toString() {
        return hora + " [" + (disponible ? "LIBRE (Verde)" : "OCUPADO (Rojo)") + "]";
    }
}
