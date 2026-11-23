package excepciones;

/**
 * Excepción de dominio lanzada cuando no se encuentra un vehículo para el
 * identificador indicado en una operación de búsqueda, modificación o baja.
 */
public class VehiculoNoEncontradoException extends Exception {

    /** Versión serializable de la excepción. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción indicando el identificador buscado.
     *
     * @param identificador identificador no encontrado
     */
    public VehiculoNoEncontradoException(String identificador) {
        super("No se encontró ningún vehículo con el identificador '" + identificador + "'.");
    }
}
