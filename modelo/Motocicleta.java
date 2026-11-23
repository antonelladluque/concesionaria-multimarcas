package modelo;

import excepciones.DatoInvalidoException;

import java.io.Serializable;
import java.util.Locale;

/**
 * Motocicleta. Especifica estilo y cilindrada, con su propia fórmula tributaria.
 */
public class Motocicleta extends Vehiculo implements Serializable {

    /** Versión serializable de la clase. */
    private static final long serialVersionUID = 1L;

    /** Impuesto fijo base. */
    private static final Double IMPUESTO_BASE = 25.0;

    /** Alícuota aplicada al precio. */
    private static final Double ALICUOTA = 0.01;

    /** Cargo por centímetro cúbico. */
    private static final Double CARGO_POR_CC = 0.15;

    /** Estilo de la motocicleta. */
    private EstiloMotocicleta estilo;

    /** Cilindrada en centímetros cúbicos. */
    private Integer cilindrada;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Motocicleta() {
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
     * @param estilo        estilo de motocicleta
     * @param cilindrada    cilindrada en cc
     */
    public Motocicleta(String identificador, String marca, String modelo, Integer anio,
                       String color, EstadoVehiculo estado, Double kilometraje, Double precio,
                       EstiloMotocicleta estilo, Integer cilindrada) {
        super(identificador, marca, modelo, anio, color, estado, kilometraje, precio);
        this.estilo = estilo;
        this.cilindrada = cilindrada;
    }

    /**
     * Devuelve el estilo de la motocicleta.
     *
     * @return estilo de la motocicleta
     */
    public EstiloMotocicleta getEstilo() {
        return estilo;
    }

    /**
     * Establece el estilo de la motocicleta.
     *
     * @param estilo estilo de la motocicleta
     */
    public void setEstilo(EstiloMotocicleta estilo) {
        this.estilo = estilo;
    }

    /**
     * Devuelve la cilindrada.
     *
     * @return cilindrada en centímetros cúbicos
     */
    public Integer getCilindrada() {
        return cilindrada;
    }

    /**
     * Establece la cilindrada.
     *
     * @param cilindrada cilindrada en centímetros cúbicos
     */
    public void setCilindrada(Integer cilindrada) {
        this.cilindrada = cilindrada;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipoVehiculo obtenerTipo() {
        return TipoVehiculo.MOTOCICLETA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validarReglasEspecificas() throws DatoInvalidoException {
        if (estilo == null) {
            throw new DatoInvalidoException("El estilo de la motocicleta es obligatorio.");
        }
        if (cilindrada == null || cilindrada <= 0) {
            throw new DatoInvalidoException("La cilindrada debe ser mayor que cero.");
        }
    }

    /**
     * Calcula el impuesto: 25 + 1% del precio + 0,15 por cc.
     *
     * @return monto del impuesto anual de patente
     */
    @Override
    public Double calcularImpuestoPatente() {
        return redondear(IMPUESTO_BASE + getPrecio() * ALICUOTA + cilindrada * CARGO_POR_CC);
    }

    /**
     * Devuelve la ficha técnica extendida de la motocicleta.
     *
     * @return ficha técnica legible
     */
    @Override
    public String obtenerFichaTecnicaDetallada() {
        return "MOTOCICLETA | " + getMarca() + " " + getModelo() + " " + getAnio()
                + " | Estilo: " + estilo.getEtiqueta()
                + " | Cilindrada: " + cilindrada + " cc"
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
        return "Motocicleta{" + super.toString()
                + ", estilo=" + estilo
                + ", cilindrada=" + cilindrada + '}';
    }
}
