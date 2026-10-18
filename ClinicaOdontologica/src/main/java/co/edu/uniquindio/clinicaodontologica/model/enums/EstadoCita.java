package co.edu.uniquindio.clinicaodontologica.model.enums;

/**
  * Enumeración que representa los Estados del Ciclo de Vida de una Cita Odontológica.
  */
public enum EstadoCita {

    /**
      * Cita reservada exitosamente en la agenda del especialista (bloque horario en Rojo).
      */
    AGENDADA("Agendada"),

    /**
      * Cita en la cual el especialista ya registró el procedimiento clínico en la historia clínica.
      */
    ATENDIDA("Atendida"),

    /**
      * Cita en la cual ya se liquidó el cobro correspondiente en caja.
      */
    PAGADA("Pagada"),

    /**
      * Cita cancelada voluntariamente o reprogramada; libera el bloque horario a Verde en la agenda.
      */
    CANCELADA("Cancelada"),

    /**
      * El paciente no se presentó en la fecha y hora acordadas.
      */
    NO_ASISTIDA("No Asistida");

    private final String etiqueta;

    EstadoCita(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
