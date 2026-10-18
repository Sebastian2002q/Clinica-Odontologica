package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Recordatorio;

import java.util.List;

/**
  * Contrato de Vista Pasiva (MVP) para el módulo de Notificaciones y Recordatorios.
  */
public interface IRecordatorioView {

    /**
      * Muestra la lista de citas agendadas disponibles para notificar.
      *
      * @param citas Lista de citas
      */
    void mostrarCitasProgramadas(List<Cita> citas);

    /**
      * Muestra la bitácora histórica de recordatorios enviados por WhatsApp o Correo.
      *
      * @param recordatorios Histórico de recordatorios
      */
    void mostrarHistorialRecordatorios(List<Recordatorio> recordatorios);

    /**
      * Muestra en tiempo real la previsualización del mensaje parametrizado.
      *
      * @param mensaje Texto del mensaje
      */
    void mostrarVistaPreviaMensaje(String mensaje);

    /**
      * Muestra una notificación de éxito o confirmación al usuario.
      *
      * @param titulo  Título del mensaje
      * @param mensaje Detalle del mensaje
      */
    void mostrarExito(String titulo, String mensaje);

    /**
      * Muestra una notificación de error en la interfaz.
      *
      * @param titulo  Título del error
      * @param mensaje Detalle del error
      */
    void mostrarError(String titulo, String mensaje);
}
