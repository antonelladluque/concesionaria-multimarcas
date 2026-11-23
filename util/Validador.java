package util;

import excepciones.DatoInvalidoException;
import modelo.Vehiculo;

/**
 * Utilidades estáticas de validación y conversión de datos de entrada.
 *
 * <p>El servicio y la consola comparten estas reglas para que un mismo dato
 * se valide siempre de idéntica manera. Las conversiones usan los
 * <i>wrappers</i> {@link Integer} y {@link Double} y nunca tipos primitivos.</p>
 */
public final class Validador {

    /** Constante para evitar instanciación de la clase utilitaria. */
    private Validador() {
    }

    /**
     * Valida que un texto no sea nulo ni esté vacío.
     *
     * @param texto       texto a validar
     * @param nombreCampo nombre del campo para el mensaje de error
     * @return texto recortado (sin espacios laterales)
     * @throws DatoInvalidoException si el texto es nulo o vacío
     */
    public static String validarTextoNoVacio(String texto, String nombreCampo)
            throws DatoInvalidoException {
        if (texto == null || texto.isBlank()) {
            throw new DatoInvalidoException("El campo '" + nombreCampo + "' no puede estar vacío.");
        }
        return texto.trim();
    }

    /**
     * Convierte un texto a {@link Integer} usando {@link Integer#valueOf(String)}.
     *
     * @param texto       texto a convertir
     * @param nombreCampo nombre del campo para el mensaje de error
     * @return valor entero parseado
     * @throws DatoInvalidoException si el texto no es un número entero válido
     */
    public static Integer parsearEntero(String texto, String nombreCampo)
            throws DatoInvalidoException {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException causa) {
            throw new DatoInvalidoException(
                    "El campo '" + nombreCampo + "' debe ser un número entero.", causa);
        }
    }

    /**
     * Convierte un texto a {@link Double}, aceptando coma o punto decimal.
     *
     * @param texto       texto a convertir
     * @param nombreCampo nombre del campo para el mensaje de error
     * @return valor decimal parseado
     * @throws DatoInvalidoException si el texto no es un número válido
     */
    public static Double parsearDouble(String texto, String nombreCampo)
            throws DatoInvalidoException {
        try {
            return Double.valueOf(texto.trim().replace(',', '.'));
        } catch (NumberFormatException causa) {
            throw new DatoInvalidoException(
                    "El campo '" + nombreCampo + "' debe ser un número decimal.", causa);
        }
    }

    /**
     * Valida el rango del año de fabricación.
     *
     * @param anio año a validar
     * @return el año validado
     * @throws DatoInvalidoException si es nulo o está fuera de rango
     */
    public static Integer validarAnio(Integer anio) throws DatoInvalidoException {
        if (anio == null || anio < Vehiculo.ANIO_MINIMO || anio > Vehiculo.ANIO_MAXIMO) {
            throw new DatoInvalidoException("El año debe estar entre "
                    + Vehiculo.ANIO_MINIMO + " y " + Vehiculo.ANIO_MAXIMO + ".");
        }
        return anio;
    }

    /**
     * Valida que el precio sea mayor que cero.
     *
     * @param precio precio a validar
     * @return el precio validado
     * @throws DatoInvalidoException si es nulo o no es positivo
     */
    public static Double validarPrecio(Double precio) throws DatoInvalidoException {
        if (precio == null || precio <= 0) {
            throw new DatoInvalidoException("El precio debe ser mayor que cero.");
        }
        return precio;
    }

    /**
     * Valida que el kilometraje no sea negativo. La coherencia con el estado
     * se comprueba en {@link Vehiculo#validar()}.
     *
     * @param kilometraje kilometraje a validar
     * @return el kilometraje validado
     * @throws DatoInvalidoException si es nulo o negativo
     */
    public static Double validarKilometraje(Double kilometraje) throws DatoInvalidoException {
        if (kilometraje == null || kilometraje < 0) {
            throw new DatoInvalidoException("El kilometraje no puede ser negativo ni nulo.");
        }
        return kilometraje;
    }

    /**
     * Valida un valor entero positivo usado en atributos específicos.
     *
     * @param valor       valor a validar
     * @param nombreCampo nombre del campo para el mensaje de error
     * @return el valor validado
     * @throws DatoInvalidoException si es nulo o no es positivo
     */
    public static Integer validarEnteroPositivo(Integer valor, String nombreCampo)
            throws DatoInvalidoException {
        if (valor == null || valor <= 0) {
            throw new DatoInvalidoException("El campo '" + nombreCampo + "' debe ser mayor que cero.");
        }
        return valor;
    }

    /**
     * Valida un porcentaje de descuento.
     *
     * @param porcentaje porcentaje a validar
     * @return el porcentaje validado
     * @throws DatoInvalidoException si es nulo o está fuera de [0, 100]
     */
    public static Double validarPorcentaje(Double porcentaje) throws DatoInvalidoException {
        if (porcentaje == null || porcentaje < 0 || porcentaje > 100) {
            throw new DatoInvalidoException("El descuento debe estar entre 0 y 100.");
        }
        return porcentaje;
    }

    /**
     * Convierte un texto al constante de enum indicado.
     *
     * @param texto       nombre de la constante
     * @param tipoEnum    clase del enum
     * @param <E>         tipo del enum
     * @return constante del enum
     * @throws DatoInvalidoException si el texto no corresponde a una constante
     */
    public static <E extends Enum<E>> E parsearEnum(String texto, Class<E> tipoEnum)
            throws DatoInvalidoException {
        try {
            return Enum.valueOf(tipoEnum, texto.trim());
        } catch (IllegalArgumentException | NullPointerException causa) {
            throw new DatoInvalidoException(
                    "Valor '" + texto + "' inválido para " + tipoEnum.getSimpleName() + ".", causa);
        }
    }
}
