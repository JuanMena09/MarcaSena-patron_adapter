package co.sena.mercasena.adapter;

import java.math.BigDecimal;

/** Interfaz objetivo (target) que usa MercaSENA. */
public interface ProcesadorPagos {

    ResultadoPago pagar(String referencia, BigDecimal montoPesos);
}
