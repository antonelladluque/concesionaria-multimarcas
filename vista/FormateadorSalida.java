package vista;

import modelo.SimulacionVenta;
import modelo.Vehiculo;

import java.util.List;
import java.util.Locale;

/**
 * Clase utilitaria de presentación: convierte datos del dominio en texto para
 * la consola. No contiene reglas de negocio ni acceso a archivos.
 */
public final class FormateadorSalida {

    /** Constructor privado de clase utilitaria. */
    private FormateadorSalida() {
    }

    /**
     * Formatea una lista de vehículos como una tabla alineada.
     *
     * @param vehiculos vehículos a mostrar
     * @return tabla lista para imprimir
     */
    public static String tabla(List<Vehiculo> vehiculos) {
        if (vehiculos.isEmpty()) {
            return "  No hay vehículos que coincidan con el criterio.";
        }
        StringBuilder salida = new StringBuilder();
        salida.append(String.format(Locale.US,"  %-16s %-14s %-14s %-6s %-13s %-12s %12s%n",
                "ID", "Marca", "Modelo", "Año", "Estado", "Tipo", "Precio"));
        salida.append("  ").append("-".repeat(94)).append(System.lineSeparator());
        for (Vehiculo vehiculo : vehiculos) {
            salida.append(String.format(Locale.US,"  %-16s %-14s %-14s %-6d %-13s %-12s %12.2f%n",
                    cortar(vehiculo.getIdentificador(), 16),
                    cortar(vehiculo.getMarca(), 14),
                    cortar(vehiculo.getModelo(), 14),
                    vehiculo.getAnio(),
                    vehiculo.getEstado().getEtiqueta(),
                    vehiculo.obtenerTipo().getEtiqueta(),
                    vehiculo.getPrecio()));
        }
        salida.append("  Total de registros: ").append(vehiculos.size());
        return salida.toString();
    }

    /**
     * Muestra la ficha técnica detallada de un vehículo.
     *
     * @param vehiculo vehículo a mostrar
     * @return ficha técnica formateada
     */
    public static String ficha(Vehiculo vehiculo) {
        return System.lineSeparator() + "  " + vehiculo.obtenerFichaTecnicaDetallada();
    }

    /**
     * Muestra el resultado de una simulación de venta.
     *
     * @param simulacion simulación a mostrar
     * @return resumen formateado
     */
    public static String simulacion(SimulacionVenta simulacion) {
        return System.lineSeparator()
                + "  Vehículo:       " + simulacion.getVehiculo().obtenerFichaTecnicaDetallada() + System.lineSeparator()
                + "  Descuento:      " + simulacion.getPorcentajeDescuento() + " %" + System.lineSeparator()
                + "  Precio final:   $" + String.format(Locale.US,"%.2f", simulacion.getPrecioFinal()) + System.lineSeparator()
                + "  Patente:        $" + String.format(Locale.US,"%.2f", simulacion.getImpuestoPatente()) + System.lineSeparator()
                + "  Valor tasado:   $" + String.format(Locale.US,"%.2f", simulacion.getValorTasado()) + System.lineSeparator()
                + "  TOTAL A COBRAR: $" + String.format(Locale.US,"%.2f", simulacion.getTotal());
    }

    /**
     * Recorta un texto para que no rompa el ancho de la columna.
     *
     * @param texto texto original
     * @param ancho ancho máximo
     * @return texto recortado
     */
    private static String cortar(String texto, int ancho) {
        return texto.length() <= ancho ? texto : texto.substring(0, ancho - 1) + "…";
    }
}
