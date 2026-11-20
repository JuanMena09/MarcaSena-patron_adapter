package co.sena.mercasena.adapter;

/**
 * Librería de un tercero (adaptee). NO SE PUEDE MODIFICAR.
 * Recibe una orden XML con el monto en centavos y devuelve un código.
 */
public class PasarelaLegacy {

    public static final int OK = 0;
    public static final int FONDOS_INSUFICIENTES = 1;
    public static final int ERROR = 2;

    private String ultimoXml;

    public int procesar(String xmlOrden) {
        this.ultimoXml = xmlOrden;
        long centavos;
        try {
            centavos = Long.parseLong(extraer(xmlOrden, "monto"));
        } catch (RuntimeException e) {
            return ERROR;
        }
        if (centavos <= 0) {
            return ERROR;
        }
        if (centavos > 500_000_000L) {
            return FONDOS_INSUFICIENTES;
        }
        return OK;
    }

    public String getUltimoXml() {
        return ultimoXml;
    }

    private static String extraer(String xml, String etiqueta) {
        String abre = "<" + etiqueta + ">";
        int inicio = xml.indexOf(abre) + abre.length();
        int fin = xml.indexOf("</" + etiqueta + ">", inicio);
        return xml.substring(inicio, fin);
    }
}
