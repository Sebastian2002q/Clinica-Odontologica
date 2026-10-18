package co.edu.uniquindio.clinicaodontologica.repository;

import co.edu.uniquindio.clinicaodontologica.model.entity.Caja;
import co.edu.uniquindio.clinicaodontologica.model.entity.Pago;

import java.util.List;
import java.util.Optional;

/**
  * Repositorio para la gestión de Caja y Transacciones de Pago.
  */
public class CajaRepository {

    /**
      * Instancia de la caja principal activa que consolida el flujo diario de la clínica.
      */
    private final Caja cajaActiva = new Caja("CAJA-PRINCIPAL-01", 0.0);

    /**
      * Secuencia numérica para la generación correlativa de recibos y pagos.
      */
    private int secuenciaPago = 1;

    /**
      * Genera el siguiente identificador correlativo para un pago (ej. PAG-0001).
      *
      * @return Identificador en formato PAG-XXXX
      */
    public String generarSiguienteIdPago() {
        return String.format("PAG-%04d", secuenciaPago);
    }

    /**
      * Genera el siguiente número consecutivo oficial para un comprobante de caja (ej. FAC-2026-0001).
      *
      * @return Consecutivo oficial
      */
    public String generarSiguienteComprobante() {
        return String.format("FAC-2026-%04d", secuenciaPago++);
    }

    /**
      * Obtiene la instancia activa de la caja de la clínica.
      *
      * @return Objeto Caja activo
      */
    public Caja obtenerCajaActiva() {
        return cajaActiva;
    }

    /**
      * Registra un pago en la caja activa actualizando el flujo de caja diario.
      *
      * @param pago Pago formal a persistir
      */
    public void guardarPago(Pago pago) {
        cajaActiva.registrarIngresoPago(pago);
    }

    /**
      * Busca si una cita médica ya cuenta con un pago registrado en caja.
      *
      * @param idCita Identificador de la cita médica
      * @return Optional con el pago si la cita ya fue liquidada
      */
    public Optional<Pago> buscarPagoPorCita(String idCita) {
        return cajaActiva.getPagosRegistrados().stream()
                .filter(p -> p.getIdCita().equals(idCita))
                .findFirst();
    }

    /**
      * Retorna la lista de todos los pagos registrados cronológicamente en caja.
      *
      * @return Lista no modificable de pagos
      */
    public List<Pago> listarPagos() {
        return cajaActiva.getPagosRegistrados();
    }
}
