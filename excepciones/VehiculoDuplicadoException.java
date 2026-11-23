package excepciones;

/**
 * Excepción de dominio lanzada al intentar dar de alta un vehículo cuyo
 * identificador (patente, chasis o código de stock) ya existe en el repositorio.
 */
public class VehiculoDuplicadoException extends Exception {

    /** Versión serializable de la excepción. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción indicando el identificador repetido.
     *
     * @param identificador identificador duplicado
     */
    public VehiculoDuplicadoException(String identificador) {
        super("Ya existe un vehículo con el identificador '" + identificador + "'.");
    }

    /**
     * Crea la excepción con un mensaje y la causa original.
     *
     * @param mensaje detalle del error
     * @param causa   excepción que originó el conflicto
     */
    public VehiculoDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
