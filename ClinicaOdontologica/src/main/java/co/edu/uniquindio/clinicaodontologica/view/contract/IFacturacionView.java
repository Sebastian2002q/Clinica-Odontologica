package co.edu.uniquindio.clinicaodontologica.view.contract;

import co.edu.uniquindio.clinicaodontologica.model.entity.Caja;
import co.edu.uniquindio.clinicaodontologica.model.entity.Cita;
import co.edu.uniquindio.clinicaodontologica.model.entity.Pago;

import java.util.List;

/**
  * Contrato de Vista Pasiva (MVP) para la Facturación, Cobros y Flujo de Caja.
  */
public interface IFacturacionView {

    /**
      * Muestra la lista de consultas o citas pendientes por liquidación de pago.
      *
      * @param citas Lista de citas pendientes
      */
    void mostrarCitasPendientesDeCobro(List<Cita> citas);

    /**
      * Muestra el desglose de conceptos clínicos y el valor total a cobrar en la interfaz.
      *
      * @param desglose   Texto con el detalle de tratamientos y tarifas
      * @param valorTotal Monto total a cobrar
      */
    void mostrarDesgloseYValorTotal(String desglose, double valorTotal);

    /**
      * Despliega en pantalla el comprobante oficial de pago generado con su numeración consecutiva.
      *
      * @param textoComprobante Texto del recibo oficial
      */
    void mostrarComprobanteGenerado(String textoComprobante);

    /**
      * Actualiza el panel de métricas financieras de caja diaria y la tabla de pagos emitidos.
      *
      * @param caja           Entidad caja activa
      * @param historialPagos Histórico de pagos
      */
    void actualizarPanelFlujoCaja(Caja caja, List<Pago> historialPagos);

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
