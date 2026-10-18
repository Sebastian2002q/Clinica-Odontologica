package co.edu.uniquindio.clinicaodontologica.presenter;

import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.service.EspecialistaService;
import co.edu.uniquindio.clinicaodontologica.view.contract.IEspecialistaView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
  * Presenter MVP para el cumplimiento de:
  */
public class EspecialistaPresenter {

    /**
      * Contrato de la vista pasiva de especialistas.
      */
    private final IEspecialistaView view;

    /**
      * Servicio de dominio de especialistas.
      */
    private final EspecialistaService especialistaService;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param view                Vista pasiva que implementa IEspecialistaView
      * @param especialistaService Servicio de doctores y agendas
      */
    public EspecialistaPresenter(IEspecialistaView view, EspecialistaService especialistaService) {
        this.view = view;
        this.especialistaService = especialistaService;
    }

    /**
      * Consulta el catálogo de especialistas registrados y los envía a la vista.
      */
    public void cargarEspecialistas() {
        view.mostrarEspecialistas(especialistaService.listarEspecialistas());
    }

    /**
      * Maneja el evento de selección de un especialista en la tabla, cargando los bloques de su agenda médica.
      *
      * @param especialista Especialista seleccionado
      */
    public void seleccionarEspecialista(Especialista especialista) {
        if (especialista == null) {
            return;
        }
        view.mostrarHorariosEspecialista(especialista.getAgenda().obtenerTodosLosBloques());
    }

    /**
      * Procesa la captura del formulario para dar de alta un nuevo especialista médico con su agenda inicial.
      *
      * @param nombre             Nombre completo del doctor
      * @param especialidad       Especialidad médica
      * @param tarjetaProfesional Tarjeta profesional
      * @param consultorio        Consultorio físico
      * @param horarios           Mapa de fechas y horas disponibles
      */
    public void registrarEspecialista(String nombre, String especialidad, String tarjetaProfesional,
                                      String consultorio, Map<LocalDate, List<LocalTime>> horarios) {
        try {
            Especialista creado = especialistaService.registrarEspecialista(
                    nombre, especialidad, tarjetaProfesional, consultorio, horarios
            );
            view.limpiarFormulario();
            cargarEspecialistas();
            view.mostrarHorariosEspecialista(creado.getAgenda().obtenerTodosLosBloques());
            view.mostrarExito("Especialista Registrado (F-07)",
                    "El especialista " + creado.getNombre() + " (" + creado.getEspecialidad()
                            + ") fue registrado correctamente con su agenda de horarios.");
        } catch (RuntimeException e) {
            view.mostrarError("Error al Registrar Especialista (F-07)", e.getMessage());
        }
    }

    /**
      * Agrega un nuevo bloque de atención a la agenda médica de un especialista existente.
      *
      * @param especialista Especialista seleccionado
      * @param fecha        Fecha del nuevo bloque
      * @param horaTexto    Hora digitada en formato HH:mm
      */
    public void agregarHorarioAEspecialistaExistente(Especialista especialista, LocalDate fecha, String horaTexto) {
        if (especialista == null) {
            view.mostrarError("Selección requerida", "Seleccione un especialista de la lista.");
            return;
        }
        try {
            LocalTime hora = LocalTime.parse(horaTexto.trim());
            especialistaService.agregarHorarioEspecialista(especialista, fecha, hora);
            view.mostrarHorariosEspecialista(especialista.getAgenda().obtenerTodosLosBloques());
            view.mostrarExito("Horario Agregado", "Se agregó el bloque " + fecha + " " + hora + " a la agenda de " + especialista.getNombre() + ".");
        } catch (Exception e) {
            view.mostrarError("Error en Horario", "Formato de hora inválido (use HH:mm, ej. 08:00) o conflicto: " + e.getMessage());
        }
    }

    /**
      * Da de baja a un especialista y elimina sus citas en cascada.
      *
      * @param especialista Especialista seleccionado para eliminación
      */
    public void eliminarEspecialista(Especialista especialista) {
        if (especialista == null) {
            view.mostrarError("Selección requerida", "Seleccione un especialista para eliminar.");
            return;
        }
        try {
            especialistaService.eliminarEspecialista(especialista);
            cargarEspecialistas();
            view.mostrarHorariosEspecialista(List.of());
            view.mostrarExito("Especialista Eliminado", "Se eliminó el especialista " + especialista.getNombre() + ".");
        } catch (RuntimeException e) {
            view.mostrarError("Error al Eliminar", e.getMessage());
        }
    }
}
