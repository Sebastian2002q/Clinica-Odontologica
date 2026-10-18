package co.edu.uniquindio.clinicaodontologica.model.entity;

import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
  * Entidad de dominio que modela una Cita Odontológica agendada en el sistema.
  */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cita {

    /**
      * Identificador unívoco del registro de la cita médica (ej. CIT-0001).
      */
    @EqualsAndHashCode.Include
    private String idCita;

    /**
      * Paciente titular debidamente registrado en el sistema.
      */
    private Paciente paciente;

    /**
      * Doctor especialista asignado para la atención.
      */
    private Especialista especialista;

    /**
      * Fecha en la cual está programada la cita médica.
      */
    private LocalDate fecha;

    /**
      * Hora exacta de inicio de la atención odontológica.
      */
    private LocalTime hora;

    /**
      * Motivo o sintomatología descrita por el paciente al momento del agendamiento.
      */
    private String motivo;

    /**
      * Estado del ciclo de vida de la cita (AGENDADA, ATENDIDA, PAGADA, CANCELADA, NO_ASISTIDA).
      */
    private EstadoCita estado;

    /**
      * Constructor principal para crear una nueva cita médica en estado inicial EstadoCita#AGENDADA.
      *
      * @param idCita       Identificador unívoco
      * @param paciente     Paciente registrado
      * @param especialista Especialista con bloque disponible
      * @param motivo       Motivo de la consulta
      * @param fecha        Fecha programada
      * @param hora         Hora programada
      */
    public Cita(String idCita, Paciente paciente, Especialista especialista, String motivo, LocalDate fecha, LocalTime hora) {
        this.idCita = idCita;
        this.paciente = paciente;
        this.especialista = especialista;
        this.motivo = motivo;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = EstadoCita.AGENDADA;
    }

    /**
      * Confirma la cita manteniendo su estado en EstadoCita#AGENDADA.
      */
    public void confirmarCita() {
        this.estado = EstadoCita.AGENDADA;
    }

    /**
      * Cancela la cita médica, permitiendo la posterior liberación del bloque en la agenda.
      */
    public void cancelar() {
        this.estado = EstadoCita.CANCELADA;
    }

    /**
      * Cambia el estado de la cita a EstadoCita#ATENDIDA tras registrarse el procedimiento clínico.
      */
    public void marcarAtendida() {
        this.estado = EstadoCita.ATENDIDA;
    }

    /**
      * Marca la inasistencia del paciente si no se presentó a su consulta programada.
      */
    public void marcarNoAsistida() {
        this.estado = EstadoCita.NO_ASISTIDA;
    }

    /**
      * Cambia el estado de la cita a EstadoCita#PAGADA una vez liquidado el pago en caja.
      */
    public void marcarPagada() {
        this.estado = EstadoCita.PAGADA;
    }

    /**
      * Representación textual para tablas y resúmenes de citas.
      *
      * @return Cadena con el id, paciente, fecha, hora y estado
      */
    @Override
    public String toString() {
        return idCita + " | " + paciente.getNombreCompleto() + " | " + fecha + " " + hora + " | " + estado.getEtiqueta();
    }
}
