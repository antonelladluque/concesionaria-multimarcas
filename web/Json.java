package web;

import modelo.Automovil;
import modelo.Camioneta;
import modelo.EstadoVehiculo;
import modelo.EstiloMotocicleta;
import modelo.Motocicleta;
import modelo.SimulacionVenta;
import modelo.TipoCabina;
import modelo.TipoCarroceria;
import modelo.TipoTraccion;
import modelo.TipoVehiculo;
import modelo.Vehiculo;

import java.util.List;

/**
 * Serializador JSON mínimo implementado con Java estándar.
 *
 * <p>Se evita cualquier librería externa: solo se producen los tipos que la
 * aplicación necesita (objetos, arreglos, cadenas, números y booleanos).
 * Las cadenas se escapan correctamente para soportar acentos y comillas.</p>
 */
public final class Json {

    /** Constructor privado de clase utilitaria. */
    private Json() {
    }

    /**
     * Escapa una cadena para usarla dentro de un literal JSON.
     *
     * @param texto cadena original
     * @return cadena escapada, sin las comillas envolventes
     */
    public static String escape(String texto) {
        StringBuilder salida = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            switch (caracter) {
                case '"' -> salida.append("\\\"");
                case '\\' -> salida.append("\\\\");
                case '\b' -> salida.append("\\b");
                case '\f' -> salida.append("\\f");
                case '\n' -> salida.append("\\n");
                case '\r' -> salida.append("\\r");
                case '\t' -> salida.append("\\t");
                default -> {
                    if (caracter < 0x20) {
                        salida.append(String.format("\\u%04x", (int) caracter));
                    } else {
                        salida.append(caracter);
                    }
                }
            }
        }
        return salida.toString();
    }

    /**
     * Serializa un vehículo con sus atributos comunes y específicos.
     *
     * @param vehiculo vehículo a serializar
     * @return objeto JSON
     */
    public static String vehiculo(Vehiculo vehiculo) {
        StringBuilder salida = new StringBuilder("{");
        cadena(salida, "id", vehiculo.getIdentificador(), true);
        cadena(salida, "marca", vehiculo.getMarca(), false);
        cadena(salida, "modelo", vehiculo.getModelo(), false);
        numero(salida, "anio", vehiculo.getAnio().doubleValue(), false);
        cadena(salida, "color", vehiculo.getColor(), false);
        cadena(salida, "estado", vehiculo.getEstado().name(), false);
        cadena(salida, "estadoEtiqueta", vehiculo.getEstado().getEtiqueta(), false);
        numero(salida, "kilometraje", vehiculo.getKilometraje(), false);
        numero(salida, "precio", vehiculo.getPrecio(), false);
        cadena(salida, "tipo", vehiculo.obtenerTipo().name(), false);
        cadena(salida, "tipoEtiqueta", vehiculo.obtenerTipo().getEtiqueta(), false);
        numero(salida, "impuesto", vehiculo.calcularImpuestoPatente(), false);
        numero(salida, "tasado", vehiculo.calcularValorTasado(), false);

        if (vehiculo instanceof Automovil) {
            Automovil auto = (Automovil) vehiculo;
            cadena(salida, "carroceria", auto.getCarroceria().name(), false);
            cadena(salida, "carroceriaEtiqueta", auto.getCarroceria().getEtiqueta(), false);
            numero(salida, "puertas", auto.getPuertas().doubleValue(), false);
        } else if (vehiculo instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) vehiculo;
            cadena(salida, "cabina", camioneta.getCabina().name(), false);
            cadena(salida, "cabinaEtiqueta", camioneta.getCabina().getEtiqueta(), false);
            cadena(salida, "traccion", camioneta.getTraccion().name(), false);
            numero(salida, "capacidadCarga", camioneta.getCapacidadCarga(), false);
        } else if (vehiculo instanceof Motocicleta) {
            Motocicleta moto = (Motocicleta) vehiculo;
            cadena(salida, "estilo", moto.getEstilo().name(), false);
            cadena(salida, "estiloEtiqueta", moto.getEstilo().getEtiqueta(), false);
            numero(salida, "cilindrada", moto.getCilindrada().doubleValue(), false);
        }
        cadena(salida, "ficha", vehiculo.obtenerFichaTecnicaDetallada(), false);
        return salida.append('}').toString();
    }

    /**
     * Serializa una lista de vehículos.
     *
     * @param vehiculos vehículos a serializar
     * @return arreglo JSON
     */
    public static String lista(List<Vehiculo> vehiculos) {
        StringBuilder salida = new StringBuilder("[");
        for (int i = 0; i < vehiculos.size(); i++) {
            if (i > 0) {
                salida.append(',');
            }
            salida.append(vehiculo(vehiculos.get(i)));
        }
        return salida.append(']').toString();
    }

    /**
     * Serializa el resultado de una simulación de venta.
     *
     * @param simulacion simulación a serializar
     * @return objeto JSON
     */
    public static String simulacion(SimulacionVenta simulacion) {
        StringBuilder salida = new StringBuilder("{");
        cadena(salida, "id", simulacion.getVehiculo().getIdentificador(), true);
        numero(salida, "descuento", simulacion.getPorcentajeDescuento(), false);
        numero(salida, "precioBase", simulacion.getVehiculo().getPrecio(), false);
        numero(salida, "precioFinal", simulacion.getPrecioFinal(), false);
        numero(salida, "impuesto", simulacion.getImpuestoPatente(), false);
        numero(salida, "tasado", simulacion.getValorTasado(), false);
        numero(salida, "total", simulacion.getTotal(), false);
        return salida.append('}').toString();
    }

    /**
     * Devuelve todas las constantes del dominio para poblar los formularios.
     *
     * @return objeto JSON con los enums del dominio
     */
    public static String opciones() {
        StringBuilder salida = new StringBuilder("{");
        salida.append("\"tipos\":").append(opcionesTipos()).append(',');
        salida.append("\"estados\":").append(opciones(EstadoVehiculo.values(),
                e -> e.getEtiqueta())).append(',');
        salida.append("\"carrocerias\":").append(opciones(TipoCarroceria.values(),
                e -> e.getEtiqueta())).append(',');
        salida.append("\"cabinas\":").append(opciones(TipoCabina.values(),
                e -> e.getEtiqueta())).append(',');
        salida.append("\"tracciones\":").append(opciones(TipoTraccion.values(),
                e -> e.getEtiqueta())).append(',');
        salida.append("\"estilos\":").append(opciones(EstiloMotocicleta.values(),
                e -> e.getEtiqueta()));
        return salida.append('}').toString();
    }

    /**
     * Serializa los tipos de vehículo (la etiqueta se obtiene del enum).
     *
     * @return arreglo JSON
     */
    private static String opcionesTipos() {
        StringBuilder salida = new StringBuilder("[");
        for (int i = 0; i < TipoVehiculo.values().length; i++) {
            if (i > 0) {
                salida.append(',');
            }
            TipoVehiculo tipo = TipoVehiculo.values()[i];
            salida.append("{\"valor\":\"").append(tipo.name())
                    .append("\",\"etiqueta\":\"").append(escape(tipo.getEtiqueta())).append("\"}");
        }
        return salida.append(']').toString();
    }

    /**
     * Serializa genéricamente un arreglo de constantes de enum.
     *
     * @param valores   constantes del enum
     * @param etiqueta función que devuelve la etiqueta legible
     * @param <E>       tipo del enum
     * @return arreglo JSON
     */
    private static <E extends Enum<E>> String opciones(E[] valores,
                                                       java.util.function.Function<E, String> etiqueta) {
        StringBuilder salida = new StringBuilder("[");
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                salida.append(',');
            }
            salida.append("{\"valor\":\"").append(valores[i].name())
                    .append("\",\"etiqueta\":\"").append(escape(etiqueta.apply(valores[i])))
                    .append("\"}");
        }
        return salida.append(']').toString();
    }

    /**
     * Genera un objeto JSON de error.
     *
     * @param mensaje mensaje de error
     * @return objeto JSON
     */
    public static String error(String mensaje) {
        StringBuilder salida = new StringBuilder("{\"ok\":false,\"mensaje\":\"");
        return salida.append(escape(mensaje)).append("\"}").toString();
    }

    /**
     * Genera un objeto JSON de confirmación.
     *
     * @param mensaje mensaje de confirmación
     * @return objeto JSON
     */
    public static String mensaje(String mensaje) {
        StringBuilder salida = new StringBuilder("{\"ok\":true,\"mensaje\":\"");
        return salida.append(escape(mensaje)).append("\"}").toString();
    }

    /**
     * Agrega una propiedad de texto al objeto en construcción.
     *
     * @param objeto  constructor del objeto
     * @param clave   nombre de la propiedad
     * @param valor   valor de la propiedad
     * @param primera indica si es la primera propiedad
     */
    private static void cadena(StringBuilder objeto, String clave, String valor, boolean primera) {
        if (!primera) {
            objeto.append(',');
        }
        objeto.append('"').append(clave).append("\":\"").append(escape(valor)).append('"');
    }

    /**
     * Agrega una propiedad numérica al objeto en construcción.
     *
     * @param objeto  constructor del objeto
     * @param clave   nombre de la propiedad
     * @param valor   valor numérico
     * @param primera indica si es la primera propiedad
     */
    private static void numero(StringBuilder objeto, String clave, Double valor, boolean primera) {
        if (!primera) {
            objeto.append(',');
        }
        objeto.append('"').append(clave).append("\":").append(valor);
    }
}
