package co.edu.uniquindio.clinicaodontologica.model.entity;

import co.edu.uniquindio.clinicaodontologica.model.enums.CanalNotificacion;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
  * Entidad de dominio que representa una Notificación o Recordatorio de Cita Odontológica.
  */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Recordatorio {

    /**
      * Identificador unívoco del recordatorio emitido (ej. REC-0001).
      */
    @EqualsAndHashCode.Include
    private String idRecordatorio;

    /**
      * Identificador de la cita médica programada que se recuerda al paciente.
      */
    private String idCita;

    /**
      * Cédula de ciudadanía del paciente destinatario.
      */
    private String cedulaPaciente;

    /**
      * Nombre completo del paciente destinatario.
      */
    private String nombrePaciente;

    /**
      * Destino de contacto del mensaje (número telefónico móvil o dirección de correo).
      */
    private String destinoContacto;

    /**
      * Canal tecnológico empleado para la notificación (WHATSAPP, CORREO_ELECTRONICO).
      */
    private CanalNotificacion canal;

    /**
      * Contenido textual del mensaje parametrizado con fecha, hora y doctor.
      */
    private String mensajeParametrizado;

    /**
      * Fecha y hora en la que se llevará a cabo la cita médica programada.
      */
    private LocalDateTime fechaHoraCita;

    /**
      * Fecha y hora exacta en la cual se disparó o emitió la notificación.
      */
    private LocalDateTime fechaHoraDisparo;

    /**
      * Indicador de si el recordatorio fue generado por el barrido automático de 24 horas previas.
      */
    private boolean automatico24Horas;

    /**
      * Representación textual para tablas de auditoría de notificaciones.
      *
      * @return Cadena formateada
      */
    @Override
    public String toString() {
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "[" + canal.getEtiqueta() + " -> " + destinoContacto + "] Cita " + idCita
                + " (" + fechaHoraCita.format(formateador) + ") - Paciente: " + nombrePaciente;
    }
}
