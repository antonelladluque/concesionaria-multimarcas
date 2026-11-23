package excepciones;

/**
 * Excepción de dominio lanzada cuando un dato de entrada no cumple las reglas
 * de validación (rango de año, texto vacío, precio negativo, etc.).
 *
 * <p><b>Decisión de diseño:</b> es una excepción <i>checked</i> para obligar a
 * la capa de vista a informar el error al usuario y a la capa de servicio a
 * decidir si la operación se aborta.</p>
 */
public class DatoInvalidoException extends Exception {

    /** Versión serializable de la excepción. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param mensaje detalle del dato inválido
     */
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepción con un mensaje y la causa original.
     *
     * @param mensaje detalle del dato inválido
     * @param causa   excepción de bajo nivel que originó el error
     */
    public DatoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
