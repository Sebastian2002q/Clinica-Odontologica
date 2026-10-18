package co.edu.uniquindio.clinicaodontologica.model.enums;

/**
  * Enumeración que define los Roles de Usuario para el control de acceso en el sistema (RBAC).
  */
public enum RolUsuario {

    /**
      * Acceso total a todos los módulos: configuración, médicos, finanzas, inventario y reportes.
      */
    ADMINISTRADOR("Administrador General"),

    /**
      * Operador principal de recepción: registro de pacientes, agenda de citas, cobros y recordatorios.
      */
    AUXILIAR_ADMINISTRATIVO("Auxiliar Administrativo"),

    /**
      * Personal de salud: consulta de agenda médica, diligenciamiento de atención e historias clínicas.
      */
    DOCTOR_ESPECIALISTA("Doctor Especialista");

    private final String etiqueta;

    RolUsuario(String etiqueta) {
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
