package modelo;

/**
 * Estilos o tipos de motocicleta.
 */
public enum EstiloMotocicleta {

    /** Naked. */
    NAKED("Naked"),
    /** Enduro. */
    ENDURO("Enduro"),
    /** Touring. */
    TOURING("Touring"),
    /** Scooter. */
    SCOOTER("Scooter"),
    /** Deportiva. */
    DEPORTIVA("Deportiva"),
    /** Custom. */
    CUSTOM("Custom");

    /** Etiqueta legible para la consola. */
    private final String etiqueta;

    /**
     * Inicializa el valor del enum con su etiqueta.
     *
     * @param etiqueta texto descriptivo del estilo
     */
    EstiloMotocicleta(String etiqueta) {
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
