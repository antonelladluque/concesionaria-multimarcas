package modelo;

/**
 * Tipos de vehículos comercializados. Se usa en los filtros y en la
 * reconstrucción polimórfica de los registros leídos desde el archivo CSV.
 */
public enum TipoVehiculo {

    /** Automóvil de pasajeros. */
    AUTOMOVIL("Automóvil"),
    /** Camioneta / utilitario. */
    CAMIONETA("Camioneta"),
    /** Motocicleta. */
    MOTOCICLETA("Motocicleta");

    /** Etiqueta legible para la consola. */
    private final String etiqueta;

    /**
     * Inicializa el valor del enum con su etiqueta.
     *
     * @param etiqueta texto descriptivo del tipo
     */
    TipoVehiculo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve la etiqueta legible del tipo.
     *
     * @return texto descriptivo
     */
    public String getEtiqueta() {
        return etiqueta;
    }
}
