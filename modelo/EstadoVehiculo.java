package modelo;

/**
 * Estado comercial de un vehículo dentro del stock de la concesionaria.
 */
public enum EstadoVehiculo {

    /** Vehículo de 0 kilómetros. */
    NUEVO("Nuevo (0 km)"),
    /** Vehículo con uso previo y kilometraje mayor que cero. */
    USADO("Usado");

    /** Etiqueta legible para la consola. */
    private final String etiqueta;

    /**
     * Inicializa el valor del enum con su etiqueta.
     *
     * @param etiqueta texto descriptivo del estado
     */
    EstadoVehiculo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve la etiqueta legible del estado.
     *
     * @return texto descriptivo
     */
    public String getEtiqueta() {
        return etiqueta;
    }
}
