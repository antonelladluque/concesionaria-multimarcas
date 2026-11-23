package modelo;

/**
 * Carrocerías posibles de un automóvil.
 */
public enum TipoCarroceria {

    /** Sedán. */
    SEDAN("Sedán"),
    /** Hatchback. */
    HATCHBACK("Hatchback"),
    /** Coupé. */
    COUPE("Coupé"),
    /** SUV. */
    SUV("SUV"),
    /** Familiar / station wagon. */
    FAMILIAR("Familiar"),
    /** Convertible. */
    CONVERTIBLE("Convertible");

    /** Etiqueta legible para la consola. */
    private final String etiqueta;

    /**
     * Inicializa el valor del enum con su etiqueta.
     *
     * @param etiqueta texto descriptivo de la carrocería
     */
    TipoCarroceria(String etiqueta) {
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
