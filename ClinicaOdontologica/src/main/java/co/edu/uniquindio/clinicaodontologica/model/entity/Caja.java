package co.edu.uniquindio.clinicaodontologica.model.entity;

import co.edu.uniquindio.clinicaodontologica.model.enums.MetodoPago;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
  * Entidad de dominio que administra el Flujo de Caja Diario de la clínica dental.
  */
@Getter
public class Caja {

    /**
      * Identificador unívoco del registro de caja diaria (ej. CAJA-2026-10-16).
      */
    private final String idCaja;

    /**
      * Monto base o fondo inicial con el que abre la caja en la jornada diaria.
      */
    private double saldoInicial;

    /**
      * Total acumulado de ingresos recaudados exclusivamente por conceptos de pagos odontológicos.
      */
    private double totalIngresosConsultas;

    /**
      * Lista cronológica de todos los pagos registrados durante la operación de la caja.
      */
    private final List<Pago> pagosRegistrados = new ArrayList<>();

    /**
      * Constructor principal para inicializar la caja diaria.
      *
      * @param idCaja       Identificador de la caja
      * @param saldoInicial Saldo base de apertura
      */
    public Caja(String idCaja, double saldoInicial) {
        this.idCaja = idCaja;
        this.saldoInicial = saldoInicial;
        this.totalIngresosConsultas = 0.0;
    }

    /**
      * Registra un nuevo ingreso por pago de consulta, sumando al acumulado de la clínica.
      *
      * @param pago Transacción de pago procesada
      */
    public void registrarIngresoPago(Pago pago) {
        pagosRegistrados.add(pago);
        totalIngresosConsultas += pago.getValorTotal();
    }

    /**
      * Calcula el flujo de caja total actual sumando el saldo inicial más todos los ingresos recaudados.
      *
      * @return Valor monetario total disponible en caja
      */
    public double getFlujoCajaTotal() {
        return saldoInicial + totalIngresosConsultas;
    }

    /**
      * Calcula la suma de ingresos recaudados discriminados por un método de pago específico.
      *
      * @param metodo Método de pago a filtrar (EFECTIVO, TARJETA, TRANSFERENCIA)
      * @return Suma monetaria cobrada mediante dicho medio
      */
    public double getTotalPorMetodo(MetodoPago metodo) {
        return pagosRegistrados.stream()
                .filter(p -> p.getMetodoPago() == metodo)
                .mapToDouble(Pago::getValorTotal)
                .sum();
    }

    /**
      * Retorna una vista inmutable de la lista de pagos para proteger la integridad contable.
      *
      * @return Lista no modificable de pagos
      */
    public List<Pago> getPagosRegistrados() {
        return Collections.unmodifiableList(pagosRegistrados);
    }
}
