package co.edu.uniquindio.clinicaodontologica.model.enums;

/**
  * Enumeración que describe el Estado de la Historia Clínica según la regla de negocio RN-003.
  */
public enum EstadoHistoriaClinica {

    /**
      * Expediente abierto tras el registro del paciente, pero sin evoluciones ni procedimientos aún.
      */
    SIN_REGISTROS("Sin Registros Previos"),

    /**
      * Expediente con al menos un procedimiento odontológico registrado.
      */
    ACTUALIZADA("Actualizada");

    private final String etiqueta;

    EstadoHistoriaClinica(String etiqueta) {
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
