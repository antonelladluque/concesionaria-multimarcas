package modelo;

import excepciones.DatoInvalidoException;

import java.io.Serializable;
import java.util.Locale;

/**
 * Camioneta o utilitario. Especifica cabina, tracción y capacidad de carga.
 */
public class Camioneta extends Vehiculo implements Serializable {

    /** Versión serializable de la clase. */
    private static final long serialVersionUID = 1L;

    /** Impuesto fijo base. */
    private static final Double IMPUESTO_BASE = 90.0;

    /** Alícuota aplicada al precio. */
    private static final Double ALICUOTA = 0.02;

    /** Cargo por kilogramo de capacidad de carga. */
    private static final Double CARGO_POR_KG = 0.5;

    /** Recargo por tracción 4x4. */
    private static final Double RECARGO_4X4 = 50.0;

    /** Configuración de cabina/caja. */
    private TipoCabina cabina;

    /** Tipo de tracción. */
    private TipoTraccion traccion;

    /** Capacidad de carga en kilogramos. */
    private Double capacidadCarga;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Camioneta() {
        super();
    }

    /**
     * Constructor parametrizado completo.
     *
     * @param identificador  identificador único
     * @param marca          marca
     * @param modelo         modelo
     * @param anio           año de fabricación
     * @param color          color
     * @param estado         estado comercial
     * @param kilometraje    kilometraje
     * @param precio         precio de venta
     * @param cabina         configuración de cabina/caja
     * @param traccion       tipo de tracción
     * @param capacidadCarga capacidad de carga en kg
     */
    public Camioneta(String identificador, String marca, String modelo, Integer anio,
                     String color, EstadoVehiculo estado, Double kilometraje, Double precio,
                     TipoCabina cabina, TipoTraccion traccion, Double capacidadCarga) {
        super(identificador, marca, modelo, anio, color, estado, kilometraje, precio);
        this.cabina = cabina;
        this.traccion = traccion;
        this.capacidadCarga = capacidadCarga;
    }

    /**
     * Devuelve la configuración de cabina.
     *
     * @return cabina/caja de la camioneta
     */
    public TipoCabina getCabina() {
        return cabina;
    }

    /**
     * Establece la configuración de cabina.
     *
     * @param cabina cabina/caja de la camioneta
     */
    public void setCabina(TipoCabina cabina) {
        this.cabina = cabina;
    }

    /**
     * Devuelve el tipo de tracción.
     *
     * @return tracción de la camioneta
     */
    public TipoTraccion getTraccion() {
        return traccion;
    }

    /**
     * Establece el tipo de tracción.
     *
     * @param traccion tracción de la camioneta
     */
    public void setTraccion(TipoTraccion traccion) {
        this.traccion = traccion;
    }

    /**
     * Devuelve la capacidad de carga.
     *
     * @return capacidad de carga en kilogramos
     */
    public Double getCapacidadCarga() {
        return capacidadCarga;
    }

    /**
     * Establece la capacidad de carga.
     *
     * @param capacidadCarga capacidad de carga en kilogramos
     */
    public void setCapacidadCarga(Double capacidadCarga) {
        this.capacidadCarga = capacidadCarga;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipoVehiculo obtenerTipo() {
        return TipoVehiculo.CAMIONETA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validarReglasEspecificas() throws DatoInvalidoException {
        if (cabina == null) {
            throw new DatoInvalidoException("El tipo de cabina de la camioneta es obligatorio.");
        }
        if (traccion == null) {
            throw new DatoInvalidoException("El tipo de tracción de la camioneta es obligatorio.");
        }
        if (capacidadCarga == null || capacidadCarga <= 0) {
            throw new DatoInvalidoException("La capacidad de carga debe ser mayor que cero.");
        }
    }

    /**
     * Calcula el impuesto: 90 + 2% del precio + 0,5 por kg de carga + 50 si es 4x4.
     *
     * @return monto del impuesto anual de patente
     */
    @Override
    public Double calcularImpuestoPatente() {
        Double recargo = traccion == TipoTraccion.TRACCION_4X4 ? RECARGO_4X4 : 0.0;
        return redondear(IMPUESTO_BASE + getPrecio() * ALICUOTA
                + capacidadCarga * CARGO_POR_KG + recargo);
    }

    /**
     * Devuelve la ficha técnica extendida de la camioneta.
     *
     * @return ficha técnica legible
     */
    @Override
    public String obtenerFichaTecnicaDetallada() {
        return "CAMIONETA | " + getMarca() + " " + getModelo() + " " + getAnio()
                + " | Cabina: " + cabina.getEtiqueta()
                + " | Tracción: " + traccion.getEtiqueta()
                + " | Carga: " + capacidadCarga + " kg"
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
        return "Camioneta{" + super.toString()
                + ", cabina=" + cabina
                + ", traccion=" + traccion
                + ", capacidadCarga=" + capacidadCarga + '}';
    }
}
