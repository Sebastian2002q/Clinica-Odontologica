package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.repository.CitaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.EspecialistaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
  * Servicio encargado del registro, consulta y gestión de los especialistas y sus horarios.
  */
public class EspecialistaService {

    /**
      * Repositorio para persistir y consultar datos de especialistas.
      */
    private final EspecialistaRepository especialistaRepository;

    /**
      * Repositorio de citas para eliminar en cascada citas asociadas en caso de baja médica.
      */
    private final CitaRepository citaRepository;

    /**
      * Secuencia numérica correlativa para generar códigos de doctor (DOC-001, DOC-002, etc.).
      */
    private int secuenciaDoctor = 1;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param especialistaRepository Repositorio de especialistas
      * @param citaRepository         Repositorio de citas
      */
    public EspecialistaService(EspecialistaRepository especialistaRepository, CitaRepository citaRepository) {
        this.especialistaRepository = especialistaRepository;
        this.citaRepository = citaRepository;
    }

    /**
      * Registra un nuevo especialista médico con su agenda horaria inicial.
      *
      * @param nombre              Nombres y apellidos completos del doctor
      * @param especialidad        Especialidad odontológica
      * @param tarjetaProfesional  Número de tarjeta profesional acreditada
      * @param consultorio         Consultorio físico asignado
      * @param horariosDisponibles Mapa de fechas con listas de horas disponibles a programar
      * @return Especialista creado y guardado
      * @throws IllegalArgumentException si los campos obligatorios están vacíos o no se asigna al menos un horario
      */
    public Especialista registrarEspecialista(String nombre, String especialidad, String tarjetaProfesional,
                                              String consultorio, Map<LocalDate, List<LocalTime>> horariosDisponibles) {
        if (nombre == null || nombre.isBlank() || especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("El nombre y la especialidad del especialista son obligatorios.");
        }
        if (horariosDisponibles == null || horariosDisponibles.isEmpty()
                || horariosDisponibles.values().stream().allMatch(List::isEmpty)) {
            throw new IllegalArgumentException("Debe registrar al menos un horario disponible para el especialista.");
        }
        String idDoctor = String.format("DOC-%03d", secuenciaDoctor++);
        String tarjeta = (tarjetaProfesional == null || tarjetaProfesional.isBlank())
                ? "TP-" + (10000 + secuenciaDoctor)
                : tarjetaProfesional.trim();
        String consultorioAsignado = (consultorio == null || consultorio.isBlank())
                ? "Consultorio " + (100 + (secuenciaDoctor % 10))
                : consultorio.trim();

        Especialista especialista = new Especialista(idDoctor, nombre.trim(), especialidad.trim(), tarjeta, consultorioAsignado);
        for (Map.Entry<LocalDate, List<LocalTime>> entry : horariosDisponibles.entrySet()) {
            for (LocalTime hora : entry.getValue()) {
                especialista.getAgenda().agregarHorarioDisponible(entry.getKey(), hora);
            }
        }
        especialistaRepository.guardar(especialista);
        return especialista;
    }

    /**
      * Actualiza los datos informativos y amplía la disponibilidad horaria de un especialista registrado.
      *
      * @param idDoctor             Identificador del doctor
      * @param nombre               Nuevo nombre
      * @param especialidad         Nueva especialidad
      * @param tarjetaProfesional   Nueva tarjeta profesional
      * @param consultorio          Nuevo consultorio
      * @param nuevosHorariosLibres Nuevos horarios a agregar
      * @return Especialista actualizado
      * @throws IllegalArgumentException si los datos requeridos son inválidos
      */
    public Especialista actualizarEspecialista(String idDoctor, String nombre, String especialidad,
                                               String tarjetaProfesional, String consultorio,
                                               Map<LocalDate, List<LocalTime>> nuevosHorariosLibres) {
        Especialista especialista = especialistaRepository.buscarPorId(idDoctor)
                .orElseThrow(() -> new IllegalArgumentException("Especialista no encontrado: " + idDoctor));
        if (nombre == null || nombre.isBlank() || especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("El nombre y la especialidad son obligatorios.");
        }
        if (nuevosHorariosLibres == null || nuevosHorariosLibres.isEmpty()) {
            throw new IllegalArgumentException("El especialista debe mantener al menos un horario configurado.");
        }

        especialista.setNombre(nombre.trim());
        especialista.setEspecialidad(especialidad.trim());
        if (tarjetaProfesional != null && !tarjetaProfesional.isBlank()) {
            especialista.setTarjetaProfesional(tarjetaProfesional.trim());
        }
        if (consultorio != null && !consultorio.isBlank()) {
            especialista.setConsultorio(consultorio.trim());
        }

        for (Map.Entry<LocalDate, List<LocalTime>> entry : nuevosHorariosLibres.entrySet()) {
            LocalDate fecha = entry.getKey();
            for (LocalTime hora : entry.getValue()) {
                if (!especialista.getAgenda().existeBloqueConfigurado(fecha, hora)) {
                    especialista.getAgenda().agregarHorarioDisponible(fecha, hora);
                }
            }
        }
        especialistaRepository.guardar(especialista);
        return especialista;
    }

    /**
      * Agrega un nuevo horario libre a la agenda médica del especialista si no se encuentra ocupado por una cita.
      *
      * @param especialista Especialista médico
      * @param fecha        Fecha del bloque
      * @param hora         Hora del bloque
      * @throws IllegalArgumentException si la fecha u hora son nulas
      * @throws IllegalStateException    si el horario ya se encuentra reservado por una cita activa
      */
    public void agregarHorarioEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Debe indicar fecha y hora válidas.");
        }
        if (citaRepository.buscarActivaPorEspecialistaFechaHora(especialista, fecha, hora).isPresent()) {
            throw new IllegalStateException("No se puede agregar como libre un horario que ya tiene una cita agendada.");
        }
        especialista.getAgenda().agregarHorarioDisponible(fecha, hora);
    }

    /**
      * Da de baja a un especialista y elimina en cascada las citas asignadas para mantener la integridad de la base de datos.
      *
      * @param especialista Especialista a dar de baja
      * @throws IllegalArgumentException si el parámetro es nulo
      */
    public void eliminarEspecialista(Especialista especialista) {
        if (especialista == null) {
            throw new IllegalArgumentException("Debe seleccionar un especialista para eliminar.");
        }
        citaRepository.eliminarPorEspecialista(especialista);
        especialistaRepository.eliminar(especialista.getIdDoctor());
    }

    /**
      * Retorna la lista con todos los especialistas registrados en la clínica.
      *
      * @return Lista de especialistas
      */
    public List<Especialista> listarEspecialistas() {
        return especialistaRepository.listarTodos();
    }

    /**
      * Busca un especialista a partir de su identificador único.
      *
      * @param idDoctor Identificador único del doctor
      * @return Optional con el especialista si existe
      */
    public Optional<Especialista> buscarPorId(String idDoctor) {
        return especialistaRepository.buscarPorId(idDoctor);
    }
}
