package modelo;

import java.io.Serializable;

/**
 * Objeto de transferencia inmutable con el resultado de una simulación de venta.
 *
 * <p><b>Remarks:</b> se separa el resultado de la operación del vehículo para
 * que la vista pueda formatearlo sin acceder a reglas de negocio.</p>
 */
public class SimulacionVenta implements Serializable {

    /** Versión serializable de la clase. */
    private static final long serialVersionUID = 1L;

    /** Vehículo simulado. */
    private final Vehiculo vehiculo;

    /** Porcentaje de descuento aplicado. */
    private final Double porcentajeDescuento;

    /** Precio final luego del descuento. */
    private final Double precioFinal;

    /** Impuesto de patente del vehículo. */
    private final Double impuestoPatente;

    /** Valor tasado del vehículo. */
    private final Double valorTasado;

    /** Total a cobrar incluyendo el impuesto. */
    private final Double total;

    /**
     * Crea una simulación con todos sus importes ya calculados.
     *
     * @param vehiculo             vehículo simulado
     * @param porcentajeDescuento  descuento aplicado
     * @param precioFinal          precio con descuento
     * @param impuestoPatente      impuesto de patente
     * @param valorTasado          valor tasado
     * @param total                precio final más impuesto
     */
    public SimulacionVenta(Vehiculo vehiculo, Double porcentajeDescuento, Double precioFinal,
                           Double impuestoPatente, Double valorTasado, Double total) {
        this.vehiculo = vehiculo;
        this.porcentajeDescuento = porcentajeDescuento;
        this.precioFinal = precioFinal;
        this.impuestoPatente = impuestoPatente;
        this.valorTasado = valorTasado;
        this.total = total;
    }

    /**
     * Devuelve el vehículo simulado.
     *
     * @return vehículo simulado
     */
    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    /**
     * Devuelve el descuento aplicado.
     *
     * @return porcentaje de descuento
     */
    public Double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    /**
     * Devuelve el precio final con descuento.
     *
     * @return precio final
     */
    public Double getPrecioFinal() {
        return precioFinal;
    }

    /**
     * Devuelve el impuesto de patente.
     *
     * @return impuesto calculado
     */
    public Double getImpuestoPatente() {
        return impuestoPatente;
    }

    /**
     * Devuelve el valor tasado.
     *
     * @return valor tasado
     */
    public Double getValorTasado() {
        return valorTasado;
    }

    /**
     * Devuelve el total a cobrar.
     *
     * @return total con impuesto
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Devuelve una representación textual de la simulación.
     *
     * @return resumen legible de la simulación
     */
    @Override
    public String toString() {
        return "SimulacionVenta{vehiculo=" + vehiculo.getIdentificador()
                + ", descuento=" + porcentajeDescuento + "%"
                + ", precioFinal=" + precioFinal
                + ", impuestoPatente=" + impuestoPatente
                + ", valorTasado=" + valorTasado
                + ", total=" + total + '}';
    }
}
