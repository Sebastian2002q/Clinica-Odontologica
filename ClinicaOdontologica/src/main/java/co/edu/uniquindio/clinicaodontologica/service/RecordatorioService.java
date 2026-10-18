package co.edu.uniquindio.clinicaodontologica.service;

import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Paciente;
import co.edu.uniquindio.clinicaodontologica.model.entity.Recordatorio;
import co.edu.uniquindio.clinicaodontologica.model.enums.CanalNotificacion;
import co.edu.uniquindio.clinicaodontologica.model.enums.EstadoCita;
import co.edu.uniquindio.clinicaodontologica.repository.CitaRepository;
import co.edu.uniquindio.clinicaodontologica.repository.RecordatorioRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
  * Servicio encargado de generar y registrar el envío de recordatorios para las citas próximas.
  */
public class RecordatorioService {

    /**
      * Repositorio de citas para consultar la agenda programada.
      */
    private final CitaRepository citaRepository;

    /**
      * Repositorio de recordatorios para auditar y persistir las notificaciones emitidas.
      */
    private final RecordatorioRepository recordatorioRepository;

    /**
      * Constructor con inyección de dependencias.
      *
      * @param citaRepository         Repositorio de citas
      * @param recordatorioRepository Repositorio de recordatorios
      */
    public RecordatorioService(CitaRepository citaRepository, RecordatorioRepository recordatorioRepository) {
        this.citaRepository = citaRepository;
        this.recordatorioRepository = recordatorioRepository;
    }

    /**
      * Ejecuta el barrido automatizado de citas activas programadas en las próximas 24 horas
      * (o para el día inmediatamente posterior), emitiendo notificaciones parametrizadas.
      *
      * @param momentoActual Fecha y hora de referencia para evaluar la ventana (o null para ahora)
      * @param canal         Canal digital preferente (WhatsApp o Correo)
      * @return Lista de nuevos recordatorios generados y persistidos
      */
    public List<Recordatorio> dispararRecordatoriosAutomaticos24Horas(LocalDateTime momentoActual, CanalNotificacion canal) {
        if (momentoActual == null) {
            momentoActual = LocalDateTime.now();
        }
        CanalNotificacion canalEfectivo = (canal == null) ? CanalNotificacion.WHATSAPP : canal;
        LocalDate fechaSiguiente = momentoActual.toLocalDate().plusDays(1);

        List<Recordatorio> generados = new ArrayList<>();
        for (Cita cita : citaRepository.listarActivas()) {
            if (cita.getEstado() != EstadoCita.AGENDADA) {
                continue;
            }
            if (recordatorioRepository.existeRecordatorioParaCita(cita.getIdCita())) {
                continue;
            }
            LocalDateTime fechaHoraCita = LocalDateTime.of(cita.getFecha(), cita.getHora());
            long horasFaltantes = Duration.between(momentoActual, fechaHoraCita).toHours();

            boolean esParaMananaOVentana24h = cita.getFecha().equals(fechaSiguiente)
                    || (horasFaltantes >= 0 && horasFaltantes <= 24);

            if (esParaMananaOVentana24h) {
                Recordatorio recordatorio = crearYGuardarRecordatorio(cita, canalEfectivo, momentoActual, true);
                generados.add(recordatorio);
            }
        }
        return generados;
    }

    /**
      * Emite un recordatorio manual e individual para una cita odontológica específica.
      *
      * @param idCita Identificador de la cita médica
      * @param canal  Canal digital a utilizar
      * @return Recordatorio generado y guardado
      * @throws IllegalArgumentException si la cita no existe
      */
    public Recordatorio enviarRecordatorioIndividual(String idCita, CanalNotificacion canal) {
        Cita cita = citaRepository.buscarPorId(idCita)
                .orElseThrow(() -> new IllegalArgumentException("No existe la cita con ID: " + idCita));
        CanalNotificacion canalEfectivo = (canal == null) ? CanalNotificacion.WHATSAPP : canal;
        return crearYGuardarRecordatorio(cita, canalEfectivo, LocalDateTime.now(), false);
    }

    /**
      * Construye y guarda la entidad del recordatorio asignando destinatario y plantilla parametrizada.
      */
    private Recordatorio crearYGuardarRecordatorio(Cita cita, CanalNotificacion canal, LocalDateTime momentoDisparo, boolean automatico24h) {
        Paciente paciente = cita.getPaciente();
        String destino = (canal == CanalNotificacion.WHATSAPP) ? paciente.getTelefono() : paciente.getEmail();
        String mensaje = formatearMensaje(cita, canal);
        Recordatorio recordatorio = new Recordatorio(
                recordatorioRepository.generarSiguienteId(),
                cita.getIdCita(),
                paciente.getCedula(),
                paciente.getNombreCompleto(),
                destino,
                canal,
                mensaje,
                LocalDateTime.of(cita.getFecha(), cita.getHora()),
                momentoDisparo,
                automatico24h
        );
        recordatorioRepository.guardar(recordatorio);
        return recordatorio;
    }

    /**
      * Da formato a la plantilla del mensaje de notificación con los datos de la cita, doctor y consultorio.
      *
      * @param cita  Cita odontológica agendada
      * @param canal Canal de notificación empleado
      * @return Texto formateado del mensaje
      */
    public String formatearMensaje(Cita cita, CanalNotificacion canal) {
        return String.format(
                "Hola %s, DENTAL U te recuerda tu cita odontológica [%s] programada para el día %s a las %s "
                        + "con el especialista Dr(a). %s (%s) en %s. Motivo: %s. Canal: %s (Recordatorio 24h).",
                cita.getPaciente().getNombreCompleto(),
                cita.getIdCita(),
                cita.getFecha(),
                cita.getHora(),
                cita.getEspecialista().getNombre(),
                cita.getEspecialista().getEspecialidad(),
                cita.getEspecialista().getConsultorio(),
                cita.getMotivo(),
                canal.getEtiqueta()
        );
    }

    /**
      * Retorna la lista con todos los recordatorios emitidos históricamente.
      *
      * @return Lista de recordatorios
      */
    public List<Recordatorio> listarRecordatoriosEnviados() {
        return recordatorioRepository.listarTodos();
    }
}
