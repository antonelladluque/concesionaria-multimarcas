package excepciones;

/**
 * Excepción de dominio lanzada cuando el kilometraje no es coherente con el
 * estado del vehículo: un vehículo {@code NUEVO} debe tener 0 km y uno
 * {@code USADO} debe tener kilometraje estrictamente mayor que 0.
 */
public class KilometrajeInconsistenteException extends DatoInvalidoException {

    /** Versión serializable de la excepción. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con el mensaje del conflicto detectado.
     *
     * @param mensaje detalle de la incoherencia
     */
    public KilometrajeInconsistenteException(String mensaje) {
        super(mensaje);
    }
}
