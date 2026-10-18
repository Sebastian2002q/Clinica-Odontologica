package co.edu.uniquindio.clinicaodontologica.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
  * Entidad de dominio que representa a un Odontólogo Especialista de la clínica.
  */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Especialista {

    /**
      * Código de identificación único del doctor especialista (ej. DOC-1001).
      */
    @EqualsAndHashCode.Include
    private String idDoctor;

    /**
      * Nombres y apellidos completos del doctor especialista.
      */
    private String nombre;

    /**
      * Rama o especialidad odontológica (ej. Ortodoncia, Endodoncia, Periodoncia, Odontopediatría).
      */
    private String especialidad;

    /**
      * Número de tarjeta profesional acreditada ante el Ministerio de Salud / Colegio Odontológico.
      */
    private String tarjetaProfesional;

    /**
      * Consultorio o unidad odontológica física asignada para la atención de pacientes.
      */
    private String consultorio;

    /**
      * Agenda médica propia del especialista que administra los bloques horarios de atención.
      */
    private Agenda agenda;

    /**
      * Constructor completo para registrar un especialista formalmente en la clínica.
      *
      * @param idDoctor           Identificador del doctor
      * @param nombre             Nombre completo del doctor
      * @param especialidad       Especialidad odontológica
      * @param tarjetaProfesional Tarjeta profesional médica
      * @param consultorio        Consultorio físico asignado
      */
    public Especialista(String idDoctor, String nombre, String especialidad, String tarjetaProfesional, String consultorio) {
        this.idDoctor = idDoctor;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tarjetaProfesional = tarjetaProfesional;
        this.consultorio = consultorio;
        this.agenda = new Agenda("AG-" + idDoctor, idDoctor);
    }

    /**
      * Constructor de conveniencia que permite inicializar un especialista con un mapa de horarios.
      *
      * @param nombre            Nombre del especialista
      * @param especialidad      Especialidad médica
      * @param horariosIniciales Mapa de fechas asociadas a listas de horas disponibles
      */
    public Especialista(String nombre, String especialidad, Map<LocalDate, List<LocalTime>> horariosIniciales) {
        this("DOC-" + Math.abs(nombre.hashCode() % 10000), nombre, especialidad, "TP-" + Math.abs(nombre.hashCode() % 90000 + 10000), "Consultorio 101");
        if (horariosIniciales != null) {
            for (Map.Entry<LocalDate, List<LocalTime>> entry : horariosIniciales.entrySet()) {
                for (LocalTime hora : entry.getValue()) {
                    this.agenda.agregarHorarioDisponible(entry.getKey(), hora);
                }
            }
        }
    }

    /**
      * Obtiene y consulta la agenda médica del especialista.
      *
      * @return Objeto Agenda asociado al especialista
      */
    public Agenda consultarAgenda() {
        return agenda;
    }

    /**
      * Representación legible para desplegables y tablas de selección.
      *
      * @return Cadena con el nombre, especialidad y consultorio
      */
    @Override
    public String toString() {
        return nombre + " - " + especialidad + " (" + consultorio + ")";
    }
}
