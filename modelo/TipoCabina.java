package modelo;

/**
 * Configuración de cabina/caja de una camioneta.
 */
public enum TipoCabina {

    /** Pick-up con cabina simple. */
    CABINA_SIMPLE("Pick-up cabina simple"),
    /** Pick-up con doble cabina. */
    DOBLE_CABINA("Pick-up doble cabina"),
    /** Utilitario cerrado. */
    UTILITARIO("Utilitario");

    /** Etiqueta legible para la consola. */
    private final String etiqueta;

    /**
     * Inicializa el valor del enum con su etiqueta.
     *
     * @param etiqueta texto descriptivo de la cabina
     */
    TipoCabina(String etiqueta) {
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
