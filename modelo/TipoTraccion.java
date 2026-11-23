package modelo;

/**
 * Tipo de tracción de una camioneta.
 */
public enum TipoTraccion {

    /** Tracción 4x2. */
    TRACCION_4X2("4x2"),
    /** Tracción 4x4. */
    TRACCION_4X4("4x4");

    /** Etiqueta legible para la consola. */
    private final String etiqueta;

    /**
     * Inicializa el valor del enum con su etiqueta.
     *
     * @param etiqueta texto descriptivo de la tracción
     */
    TipoTraccion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve la etiqueta legible.
     *
     * @return texto descriptivo
     */
    public String getEtiqueta() {
        return etiqueta;
    }
}
