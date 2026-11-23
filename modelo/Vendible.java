package modelo;

/**
 * Interfaz de comportamiento para vehículos que pueden venderse.
 *
 * <p><b>Decisión de diseño:</b> el descuento <b>no modifica</b> el precio del
 * vehículo; devuelve el precio resultante para permitir simulaciones sin
 * alterar el stock.</p>
 */
public interface Vendible {

    /**
     * Calcula el precio final de venta del vehículo.
     *
     * @return precio final de venta, mayor que cero
     */
    Double calcularPrecioFinalVenta();

    /**
     * Simula un descuento sobre el precio de venta.
     *
     * @param porcentaje porcentaje de descuento entre 0 y 100
     * @return precio luego de aplicar el descuento
     */
    Double aplicarDescuento(Double porcentaje);
}
