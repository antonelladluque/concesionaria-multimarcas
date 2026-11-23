package modelo;

/**
 * Interfaz de comportamiento para vehículos sujetos a tasación e impuestos.
 */
public interface Tasable {

    /**
     * Calcula el impuesto anual de patente según las reglas del tipo de vehículo.
     *
     * @return monto del impuesto, mayor o igual que cero
     */
    Double calcularImpuestoPatente();

    /**
     * Calcula el valor tasado del vehículo (valor comercial de referencia).
     *
     * @return valor tasado estimado
     */
    Double calcularValorTasado();
}
