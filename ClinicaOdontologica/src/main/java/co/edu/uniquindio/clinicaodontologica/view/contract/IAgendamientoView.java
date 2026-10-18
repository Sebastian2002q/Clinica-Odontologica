package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.BloqueHorario;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;

import java.time.LocalDate;
import java.util.List;

/**
  * Contrato de Vista Pasiva (MVP) para la Agenda Médica y Citas.
  */
public interface IAgendamientoView {

    /**
      * Muestra la lista de especialistas disponibles en el selector desplegable.
      *
      * @param especialistas Lista de especialistas
      */
    void mostrarEspecialistas(List<Especialista> especialistas);

    /**
      * Muestra la lista de pacientes registrados para autocompletar el formulario de cita.
      *
      * @param pacientes Lista de pacientes
      */
    void mostrarPacientesRegistrados(List<Paciente> pacientes);

    /**
      * Actualiza las fechas configuradas en la agenda del especialista seleccionado.
      *
      * @param fechas Lista de fechas
      */
    void mostrarFechasAgenda(List<LocalDate> fechas);

    /**
      * Selecciona una fecha programática en el selector de fechas.
      *
      * @param fecha Fecha a seleccionar
      */
    void seleccionarFechaEnCombo(LocalDate fecha);

    /**
      * Renderiza la cuadrícula de bloques horarios mostrando libres (Verde) y ocupados (Rojo).
      *
      * @param especialista Especialista consultado
      * @param fecha        Fecha consultada
      * @param bloques      Lista de bloques con su estado de disponibilidad
      */
    void mostrarCalendarioBloques(Especialista especialista, LocalDate fecha, List<BloqueHorario> bloques);

    /**
      * Muestra las citas agendadas correspondientes al doctor seleccionado.
      *
      * @param citas Lista de citas
      */
    void mostrarCitasEspecialista(List<Cita> citas);

    /**
      * Limpia los campos del formulario de agendamiento.
      */
    void limpiarFormularioReserva();

    /**
      * Despliega una alerta de éxito en la interfaz.
      *
      * @param titulo  Título del mensaje
      * @param mensaje Detalle del mensaje
      */
    void mostrarExito(String titulo, String mensaje);

    /**
      * Despliega una alerta de error o bloqueo de regla de negocio.
      *
      * @param titulo  Título del error
      * @param mensaje Detalle del error
      */
    void mostrarError(String titulo, String mensaje);
}
