package co.edu.uniquindio.clinicaodontologica.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
  * Entidad de dominio que representa a un Paciente de la clínica dental.
  */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Paciente {

    /**
      * Identificador unívoco del usuario en el sistema.
      */
    private String idUsuario;

    /**
      * Cédula de ciudadanía o documento de identidad oficial del paciente (clave primaria).
      */
    @EqualsAndHashCode.Include
    private String cedula;

    /**
      * Nombres y apellidos completos del paciente.
      */
    private String nombreCompleto;

    /**
      * Número de teléfono de contacto móvil para notificaciones y WhatsApp.
      */
    private String telefono;

    /**
      * Dirección de correo electrónico válida para envío de recordatorios y comprobantes.
      */
    private String email;

    /**
      * URI o identificador permanente del expediente de historia clínica del paciente.
      */
    private String linkHistoriaClinica;

    /**
      * Fecha exacta en la cual se efectuó el alta o registro del paciente en el sistema.
      */
    private LocalDate fechaRegistro;

    /**
      * Constructor principal para el alta de un nuevo paciente en el sistema.
      * Inicializa automáticamente el identificador de usuario, la fecha actual y el enlace clínico.
      *
      * @param cedula         Documento de identidad obligatorio
      * @param nombreCompleto Nombres y apellidos completos
      * @param telefono       Número telefónico de contacto
      * @param email          Correo electrónico de contacto
      */
    public Paciente(String cedula, String nombreCompleto, String telefono, String email) {
        this.idUsuario = "PAC-" + cedula;
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.linkHistoriaClinica = "hc://dental-u/expediente/" + cedula;
        this.fechaRegistro = LocalDate.now();
    }

    /**
      * Formato de visualización amigable para listas y tablas en la interfaz.
      *
      * @return Cadena con el nombre y la cédula entre paréntesis
      */
    @Override
    public String toString() {
        return nombreCompleto + " (CC: " + cedula + ")";
    }
}
