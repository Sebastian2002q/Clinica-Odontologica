package co.edu.uniquindio.clinicaodontologica.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * Objeto de valor inmutable que representa un elemento del catálogo estandarizado de diagnósticos y tarifas.
  */
@Getter
@AllArgsConstructor
public class CatalogoDiagnostico {

    /**
      * Código oficial según la Clasificación Internacional de Enfermedades (ej. K02.1, K05.3, K04.0).
      */
    private final String codigoCie10;

    /**
      * Nombre formal y estandarizado del diagnóstico odontológico.
      */
    private final String diagnosticoEstandar;

    /**
      * Tratamiento sugerido o protocolo clínico recomendado para este diagnóstico.
      */
    private final String tratamientoSugerido;

    /**
      * Tarifa monetaria base establecida por la clínica para este tratamiento.
      */
    private final double tarifaEstandar;

    /**
      * Formato para visualización en desplegables de la interfaz gráfica.
      *
      * @return Cadena con código, diagnóstico y tarifa formateada
      */
    @Override
    public String toString() {
        return codigoCie10 + " - " + diagnosticoEstandar + " ($" + String.format("%,.0f", tarifaEstandar) + ")";
    }
}
