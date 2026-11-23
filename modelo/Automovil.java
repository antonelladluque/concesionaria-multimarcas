package modelo;

import excepciones.DatoInvalidoException;

import java.io.Serializable;
import java.util.Locale;

/**
 * Automóvil de pasajeros. Especifica carrocería y cantidad de puertas, y
 * define su propia fórmula de impuesto de patente.
 */
public class Automovil extends Vehiculo implements Serializable {

    /** Versión serializable de la clase. */
    private static final long serialVersionUID = 1L;

    /** Impuesto fijo base. */
    private static final Double IMPUESTO_BASE = 60.0;

    /** Alícuota aplicada al precio. */
    private static final Double ALICUOTA = 0.015;

    /** Recargo para vehículos fabricados a partir de 2020. */
    private static final Double RECARGO_MODERNO = 40.0;

    /** Año a partir del cual se aplica el recargo. */
    private static final Integer ANIO_RECARGO = 2020;

    /** Tipo de carrocería. */
    private TipoCarroceria carroceria;

    /** Cantidad de puertas. */
    private Integer puertas;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Automovil() {
        super();
    }

    /**
     * Constructor parametrizado completo.
     *
     * @param identificador identificador único
     * @param marca         marca
     * @param modelo        modelo
     * @param anio          año de fabricación
     * @param color         color
     * @param estado        estado comercial
     * @param kilometraje   kilometraje
     * @param precio        precio de venta
     * @param carroceria    tipo de carrocería
     * @param puertas       cantidad de puertas
     */
    public Automovil(String identificador, String marca, String modelo, Integer anio,
                     String color, EstadoVehiculo estado, Double kilometraje, Double precio,
                     TipoCarroceria carroceria, Integer puertas) {
        super(identificador, marca, modelo, anio, color, estado, kilometraje, precio);
        this.carroceria = carroceria;
        this.puertas = puertas;
    }

    /**
     * Devuelve la carrocería.
     *
     * @return tipo de carrocería
     */
    public TipoCarroceria getCarroceria() {
        return carroceria;
    }

    /**
     * Establece la carrocería.
     *
     * @param carroceria tipo de carrocería
     */
    public void setCarroceria(TipoCarroceria carroceria) {
        this.carroceria = carroceria;
    }

    /**
     * Devuelve la cantidad de puertas.
     *
     * @return cantidad de puertas
     */
    public Integer getPuertas() {
        return puertas;
    }

    /**
     * Establece la cantidad de puertas.
     *
     * @param puertas cantidad de puertas
     */
    public void setPuertas(Integer puertas) {
        this.puertas = puertas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipoVehiculo obtenerTipo() {
        return TipoVehiculo.AUTOMOVIL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validarReglasEspecificas() throws DatoInvalidoException {
        if (carroceria == null) {
            throw new DatoInvalidoException("La carrocería del automóvil es obligatoria.");
        }
        if (puertas == null || puertas < 2 || puertas > 5) {
            throw new DatoInvalidoException("La cantidad de puertas debe estar entre 2 y 5.");
        }
    }

    /**
     * Calcula el impuesto: 60 + 1,5% del precio + 40 si es del año 2020 o posterior.
     *
     * @return monto del impuesto anual de patente
     */
    @Override
    public Double calcularImpuestoPatente() {
        Double recargo = getAnio() >= ANIO_RECARGO ? RECARGO_MODERNO : 0.0;
        return redondear(IMPUESTO_BASE + getPrecio() * ALICUOTA + recargo);
    }

    /**
     * Devuelve la ficha técnica extendida del automóvil.
     *
     * @return ficha técnica legible
     */
    @Override
    public String obtenerFichaTecnicaDetallada() {
        return "AUTOMÓVIL | " + getMarca() + " " + getModelo() + " " + getAnio()
                + " | Carrocería: " + carroceria.getEtiqueta()
                + " | Puertas: " + puertas
                + " | " + getColor()
                + " | " + getEstado().getEtiqueta()
                + " | " + String.format(Locale.US, "%.0f", getKilometraje()) + " km"
                + " | Precio: $" + String.format(Locale.US, "%.2f", getPrecio());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Automovil{" + super.toString()
                + ", carroceria=" + carroceria
                + ", puertas=" + puertas + '}';
    }
}
