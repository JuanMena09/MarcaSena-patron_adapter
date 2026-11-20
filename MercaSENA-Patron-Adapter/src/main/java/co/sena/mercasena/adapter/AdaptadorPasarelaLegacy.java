package co.sena.mercasena.adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Adaptador: traduce ProcesadorPagos a la API de PasarelaLegacy. */
public class AdaptadorPasarelaLegacy implements ProcesadorPagos {

    private final PasarelaLegacy pasarela;

    public AdaptadorPasarelaLegacy(PasarelaLegacy pasarela) {
        this.pasarela = Objects.requireNonNull(pasarela, "La pasarela es obligatoria");
    }

    @Override
    public ResultadoPago pagar(String referencia, BigDecimal montoPesos) {
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("La referencia es obligatoria");
        }
        if (montoPesos == null || montoPesos.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que 0");
        }
        long centavos = convertirACentavos(montoPesos);
        String xml = crearOrdenXml(referencia, centavos);
        int codigo = pasarela.procesar(xml);
        return new ResultadoPago(referencia, traducir(codigo));
    }

    private static long convertirACentavos(BigDecimal montoPesos) {
        try {
            return montoPesos.movePointRight(2)
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("El monto está fuera del rango permitido", e);
        }
    }

    private static String crearOrdenXml(String referencia, long centavos) {
        return "<orden><referencia>" + escaparXml(referencia) + "</referencia>"
                + "<monto>" + centavos + "</monto></orden>";
    }

    private static String escaparXml(String valor) {
        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static EstadoPago traducir(int codigo) {
        return switch (codigo) {
            case PasarelaLegacy.OK -> EstadoPago.APROBADO;
            case PasarelaLegacy.FONDOS_INSUFICIENTES -> EstadoPago.RECHAZADO_FONDOS;
            default -> EstadoPago.ERROR;
        };
    }
}
