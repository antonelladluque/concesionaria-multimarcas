package modelo;

import excepciones.DatoInvalidoException;
import excepciones.KilometrajeInconsistenteException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase abstracta base de todos los vehículos de la concesionaria.
 *
 * <p>Define la identidad de negocio (identificador único), los atributos
 * comunes, las reglas de validación compartidas y el contrato polimórfico que
 * cada subclase debe implementar. Se declara {@link Serializable} para
 * permitir, si se desea, persistencia binaria además del CSV implementado.</p>
 *
 * <p><b>Remarks:</b> se usan exclusivamente clases <i>wrapper</i> ({@link Integer},
 * {@link Double}) para que un dato ausente sea distinguible de un cero y para
 * centralizar las conversiones desde la entrada de consola y desde el archivo.</p>
 */
public abstract class Vehiculo implements Serializable, Vendible, Tasable {

    /** Versión serializable de la clase. */
    private static final long serialVersionUID = 1L;

    /** Año mínimo aceptado para un vehículo. */
    public static final Integer ANIO_MINIMO = 1950;

    /** Año máximo aceptado para un vehículo. */
    public static final Integer ANIO_MAXIMO = 2026;

    /** Porcentaje aplicado al calcular el valor tasado. */
    private static final Double FACTOR_TASACION = 0.95;

    /** Identificador único de negocio: patente, chasis o código de stock. */
    private String identificador;

    /** Marca del vehículo. */
    private String marca;

    /** Modelo del vehículo. */
    private String modelo;

    /** Año de fabricación. */
    private Integer anio;

    /** Color del vehículo. */
    private String color;

    /** Estado comercial (nuevo o usado). */
    private EstadoVehiculo estado;

    /** Kilometraje registrado. */
    private Double kilometraje;

    /** Precio de venta del vehículo. */
    private Double precio;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Vehiculo() {
    }

    /**
     * Constructor parametrizado con los atributos comunes.
     *
     * @param identificador identificador único
     * @param marca         marca del vehículo
     * @param modelo        modelo del vehículo
     * @param anio          año de fabricación
     * @param color         color del vehículo
     * @param estado        estado comercial
     * @param kilometraje   kilometraje registrado
     * @param precio        precio de venta
     */
    public Vehiculo(String identificador, String marca, String modelo, Integer anio,
                    String color, EstadoVehiculo estado, Double kilometraje, Double precio) {
        this.identificador = identificador;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.estado = estado;
        this.kilometraje = kilometraje;
        this.precio = precio;
    }

    /**
     * Devuelve el identificador único.
     *
     * @return identificador de negocio
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Establece el identificador único.
     *
     * @param identificador identificador de negocio
     */
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    /**
     * Devuelve la marca.
     *
     * @return marca del vehículo
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca.
     *
     * @param marca marca del vehículo
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Devuelve el modelo.
     *
     * @return modelo del vehículo
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Establece el modelo.
     *
     * @param modelo modelo del vehículo
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Devuelve el año de fabricación.
     *
     * @return año de fabricación
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * Establece el año de fabricación.
     *
     * @param anio año de fabricación
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * Devuelve el color.
     *
     * @return color del vehículo
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color.
     *
     * @param color color del vehículo
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Devuelve el estado comercial.
     *
     * @return estado del vehículo
     */
    public EstadoVehiculo getEstado() {
        return estado;
    }

    /**
     * Establece el estado comercial.
     *
     * @param estado estado del vehículo
     */
    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    /**
     * Devuelve el kilometraje.
     *
     * @return kilometraje registrado
     */
    public Double getKilometraje() {
        return kilometraje;
    }

    /**
     * Establece el kilometraje.
     *
     * @param kilometraje kilometraje registrado
     */
    public void setKilometraje(Double kilometraje) {
        this.kilometraje = kilometraje;
    }

    /**
     * Devuelve el precio.
     *
     * @return precio de venta
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio.
     *
     * @param precio precio de venta
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Devuelve el tipo concreto del vehículo.
     *
     * @return tipo del vehículo
     */
    public abstract TipoVehiculo obtenerTipo();

    /**
     * Devuelve una ficha técnica detallada con los atributos específicos del tipo.
     *
     * @return ficha técnica legible
     */
    public abstract String obtenerFichaTecnicaDetallada();

    /**
     * Valida las reglas comunes y delega las específicas a la subclase.
     *
     * @throws DatoInvalidoException si algún dato no cumple las reglas de dominio
     */
    public void validar() throws DatoInvalidoException {
        validarDatosComunes();
        validarReglasEspecificas();
    }

    /**
     * Valida los atributos comunes, incluida la coherencia estado/kilometraje.
     *
     * @throws DatoInvalidoException si falta un dato obligatorio o está fuera de rango
     */
    private void validarDatosComunes() throws DatoInvalidoException {
        if (identificador == null || identificador.isBlank()) {
            throw new DatoInvalidoException("El identificador es obligatorio.");
        }
        if (marca == null || marca.isBlank()) {
            throw new DatoInvalidoException("La marca es obligatoria.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new DatoInvalidoException("El modelo es obligatorio.");
        }
        if (color == null || color.isBlank()) {
            throw new DatoInvalidoException("El color es obligatorio.");
        }
        if (anio == null || anio < ANIO_MINIMO || anio > ANIO_MAXIMO) {
            throw new DatoInvalidoException("El año debe estar entre " + ANIO_MINIMO + " y " + ANIO_MAXIMO + ".");
        }
        if (precio == null || precio <= 0) {
            throw new DatoInvalidoException("El precio debe ser mayor que cero.");
        }
        if (estado == null) {
            throw new DatoInvalidoException("El estado es obligatorio.");
        }
        if (kilometraje == null || kilometraje < 0) {
            throw new DatoInvalidoException("El kilometraje no puede ser negativo ni nulo.");
        }
        if (estado == EstadoVehiculo.NUEVO && kilometraje != 0) {
            throw new KilometrajeInconsistenteException(
                    "Un vehículo NUEVO debe tener 0 km; se informaron " + kilometraje + " km.");
        }
        if (estado == EstadoVehiculo.USADO && kilometraje <= 0) {
            throw new KilometrajeInconsistenteException(
                    "Un vehículo USADO debe tener más de 0 km; se informaron " + kilometraje + " km.");
        }
    }

    /**
     * Valida los atributos propios de cada tipo de vehículo.
     *
     * @throws DatoInvalidoException si algún atributo específico es inválido
     */
    protected abstract void validarReglasEspecificas() throws DatoInvalidoException;

    /**
     * Redondea un importe a dos decimales.
     *
     * @param valor importe a redondear
     * @return importe redondeado
     */
    protected static Double redondear(Double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>El precio final de venta es el precio comercial. Las subclases pueden
     * enriquecerlo con ajustes propios mediante polimorfismo.</p>
     */
    @Override
    public Double calcularPrecioFinalVenta() {
        return redondear(precio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double aplicarDescuento(Double porcentaje) {
        if (porcentaje == null || porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        return redondear(calcularPrecioFinalVenta() * (1 - porcentaje / 100.0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double calcularValorTasado() {
        return redondear(precio * FACTOR_TASACION);
    }

    /**
     * Compara vehículos por su identificador único de negocio.
     *
     * @param objeto objeto a comparar
     * @return {@code true} si ambos son vehículos con el mismo identificador
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Vehiculo)) {
            return false;
        }
        Vehiculo otro = (Vehiculo) objeto;
        return Objects.equals(identificador, otro.identificador);
    }

    /**
     * Genera el código de hash a partir del identificador único.
     *
     * @return código de hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }

    /**
     * Devuelve una representación ordenada de los atributos comunes.
     *
     * @return representación textual del vehículo
     */
    @Override
    public String toString() {
        return "Vehiculo{identificador='" + identificador + '\''
                + ", marca='" + marca + '\''
                + ", modelo='" + modelo + '\''
                + ", anio=" + anio
                + ", color='" + color + '\''
                + ", estado=" + estado
                + ", kilometraje=" + kilometraje
                + ", precio=" + precio + '}';
    }
}
