package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.HistoriaClinica;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import co.edu.uniquindio.clinicaodontologica.repository.HistoriaClinicaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.PacienteRepository;

import java.util.List;
import java.util.Optional;

/**
  * Servicio encargado del registro, actualización y consulta de pacientes junto con su historia clínica.
  */
public class PacienteService {

    /**
      * Repositorio para persistir y consultar datos de pacientes.
      */
    private final PacienteRepository pacienteRepository;

    /**
      * Repositorio para la apertura y persistencia de historias clínicas.
      */
    private final HistoriaClinicaRepository historiaClinicaRepository;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param pacienteRepository        Repositorio de pacientes
      * @param historiaClinicaRepository Repositorio de historias clínicas
      */
    public PacienteService(PacienteRepository pacienteRepository, HistoriaClinicaRepository historiaClinicaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
    }

    /**
      * Registra un nuevo paciente en la clínica cumpliendo con F-01 y RN-001.
      *
      * @param cedula         Documento de identidad oficial obligatorio
      * @param nombreCompleto Nombres y apellidos completos del paciente
      * @param telefono       Teléfono de contacto
      * @param email          Correo electrónico de contacto
      * @return Paciente registrado exitosamente
      * @throws IllegalArgumentException si falta algún dato obligatorio o el correo es inválido
      * @throws IllegalStateException    si ya existe un paciente registrado con la misma cédula
      */
    public Paciente registrarPaciente(String cedula, String nombreCompleto, String telefono, String email) {
        if (cedula == null || cedula.isBlank()
                || nombreCompleto == null || nombreCompleto.isBlank()
                || telefono == null || telefono.isBlank()
                || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Todos los campos (Cédula, Nombre Completo, Teléfono y Correo Electrónico) son obligatorios.");
        }
        String cedulaLimpia = cedula.trim();
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("El correo electrónico ingresado no tiene un formato válido.");
        }
        // bloquear guardado si la cédula ya existe previamente
        if (pacienteRepository.existePorCedula(cedulaLimpia)) {
            throw new IllegalStateException("Ya existe un paciente registrado con la cédula " + cedulaLimpia + ".");
        }

        Paciente paciente = new Paciente(cedulaLimpia, nombreCompleto.trim(), telefono.trim(), email.trim());
        pacienteRepository.guardar(paciente);

        // Apertura automática de su expediente de Historia Clínica (F-01 y RN-003)
        HistoriaClinica historiaClinica = new HistoriaClinica(paciente);
        historiaClinicaRepository.guardar(historiaClinica);

        return paciente;
    }

    /**
      * Busca un paciente registrado por su número de cédula.
      *
      * @param cedula Cédula del paciente
      * @return Optional con el paciente si existe
      */
    public Optional<Paciente> buscarPorCedula(String cedula) {
        return pacienteRepository.buscarPorCedula(cedula);
    }

    /**
      * Valida si un paciente está debidamente registrado en el sistema (requisito previo para RN-001).
      *
      * @param cedula Cédula a comprobar
      * @return true si el paciente ya está registrado
      */
    public boolean estaRegistrado(String cedula) {
        return pacienteRepository.existePorCedula(cedula);
    }

    /**
      * Retorna la lista completa de pacientes registrados en la clínica.
      *
      * @return Lista de pacientes
      */
    public List<Paciente> listarPacientes() {
        return pacienteRepository.listarTodos();
    }

    /**
      * Obtiene el expediente de historia clínica vinculado a la cédula de un paciente.
      *
      * @param cedulaPaciente Cédula del paciente
      * @return Optional con la historia clínica si existe
      */
    public Optional<HistoriaClinica> obtenerHistoriaClinica(String cedulaPaciente) {
        return historiaClinicaRepository.buscarPorCedulaPaciente(cedulaPaciente);
    }
}
