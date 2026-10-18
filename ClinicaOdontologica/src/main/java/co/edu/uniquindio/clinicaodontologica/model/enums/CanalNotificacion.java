package co.edu.uniquindio.clinicaodontologica.model.enums;

/**
  * Enumeración que representa los Canales de Comunicación Digital para Recordatorios.
  */
public enum CanalNotificacion {

    /**
      * Notificación instantánea vía WhatsApp API al número móvil del paciente.
      */
    WHATSAPP("WhatsApp"),

    /**
      * Mensaje de correo electrónico formal con los detalles de la cita odontológica.
      */
    CORREO_ELECTRONICO("Correo Electrónico");

    private final String etiqueta;

    CanalNotificacion(String etiqueta) {
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
