package co.sena.mercasena.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AdaptadorPasarelaLegacyTest {

    private PasarelaLegacy pasarela;
    private ProcesadorPagos procesador;

    @BeforeEach
    void preparar() {
        pasarela = new PasarelaLegacy();
        procesador = new AdaptadorPasarelaLegacy(pasarela);
    }

    @Test
    void pagoNormalEsAprobado() {
        ResultadoPago r = procesador.pagar("PED-1001", new BigDecimal("50000"));
        assertEquals(EstadoPago.APROBADO, r.estado());
        assertTrue(pasarela.getUltimoXml().contains("<monto>5000000</monto>"));
    }

    @Test
    void pagoMuyAltoEsRechazadoPorFondos() {
        ResultadoPago r = procesador.pagar("PED-1002", new BigDecimal("6000000"));
        assertEquals(EstadoPago.RECHAZADO_FONDOS, r.estado());
    }

    @Test
    void codigoOkSeTraduceComoAprobado() {
        ResultadoPago r = procesador.pagar("PED-ESTADO-OK", new BigDecimal("100"));
        assertEquals(EstadoPago.APROBADO, r.estado());
    }

    @Test
    void codigoFondosInsuficientesSeTraduceComoRechazado() {
        ResultadoPago r = procesador.pagar("PED-ESTADO-FONDOS", new BigDecimal("6000000"));
        assertEquals(EstadoPago.RECHAZADO_FONDOS, r.estado());
    }

    @Test
    void montoConDecimalesSeConvierteSinPerderCentavos() {
        procesador.pagar("PED-DEC-1", new BigDecimal("1999.99"));
        assertTrue(pasarela.getUltimoXml().contains("<monto>199999</monto>"));
    }

    @Test
    void montoConMasDeDosDecimalesRedondeaHalfUp() {
        procesador.pagar("PED-DEC-2", new BigDecimal("1.995"));
        assertTrue(pasarela.getUltimoXml().contains("<monto>200</monto>"));
    }

    @Test
    void referenciaEscapaCaracteresEspecialesDeXml() {
        String referencia = "PED<&>\"'";
        procesador.pagar(referencia, BigDecimal.ONE);
        assertTrue(pasarela.getUltimoXml().contains("PED&lt;&amp;&gt;&quot;&apos;"));
    }

    @Test
    void referenciaNoPuedeInyectarEtiquetaMonto() {
        procesador.pagar("PED</referencia><monto>999999999</monto><referencia>", BigDecimal.ONE);
        assertTrue(pasarela.getUltimoXml().contains("&lt;monto&gt;999999999&lt;/monto&gt;"));
        assertTrue(pasarela.getUltimoXml().contains("<monto>100</monto>"));
    }

    @Test
    void codigoDesconocidoSeTraduceComoError() {
        PasarelaLegacy pasarelaCodigoDesconocido = new PasarelaLegacy() {
            @Override
            public int procesar(String xmlOrden) {
                return 99;
            }
        };
        ProcesadorPagos procesadorConCodigoDesconocido = new AdaptadorPasarelaLegacy(pasarelaCodigoDesconocido);
        assertEquals(EstadoPago.ERROR, procesadorConCodigoDesconocido.pagar("PED-ERROR", BigDecimal.ONE).estado());
    }

    @Test
    void limiteExactoDeCincoMillonesSeAcepta() {
        ResultadoPago r = procesador.pagar("PED-LIMITE", new BigDecimal("5000000"));
        assertEquals(EstadoPago.APROBADO, r.estado());
        assertTrue(pasarela.getUltimoXml().contains("<monto>500000000</monto>"));
    }

    @Test
    void montoUnCentavoPorEncimaDelLimiteSeRechaza() {
        ResultadoPago r = procesador.pagar("PED-LIMITE-PLUS", new BigDecimal("5000000.01"));
        assertEquals(EstadoPago.RECHAZADO_FONDOS, r.estado());
    }

    @Test
    void montoCeroNoSePermite() {
        assertThrows(IllegalArgumentException.class, () -> procesador.pagar("PED-0", BigDecimal.ZERO));
    }

    @Test
    void montoNuloNoSePermite() {
        assertThrows(IllegalArgumentException.class, () -> procesador.pagar("PED-NULO", null));
    }

    @Test
    void referenciaNulaNoSePermite() {
        assertThrows(IllegalArgumentException.class, () -> procesador.pagar(null, BigDecimal.ONE));
    }

    @Test
    void pasarelaNulaNoSePermite() {
        assertThrows(NullPointerException.class, () -> new AdaptadorPasarelaLegacy(null));
    }

    @Test
    void montoNegativoNoSePermite() {
        assertThrows(IllegalArgumentException.class, () -> procesador.pagar("PED-1", new BigDecimal("-1")));
    }

    @Test
    void referenciaVaciaNoSePermite() {
        assertThrows(IllegalArgumentException.class, () -> procesador.pagar(" ", BigDecimal.TEN));
    }
}
