package repositorio;

import excepciones.DatoInvalidoException;
import modelo.Automovil;
import modelo.Camioneta;
import modelo.EstadoVehiculo;
import modelo.EstiloMotocicleta;
import modelo.Motocicleta;
import modelo.TipoCabina;
import modelo.TipoCarroceria;
import modelo.TipoTraccion;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import util.Validador;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringJoiner;

/**
 * Codificador y decodificador de vehículos en formato CSV delimitado por '|'.
 *
 * <p><b>Decisión de diseño:</b> los campos de texto se codifican en Base64 URL
 * para que el delimitador, los saltos de línea y los acentos nunca corrompan
 * un registro. El primer campo indica el tipo concreto, lo que permite
 * reconstruir polimórficamente la subclase correcta al leer el archivo.</p>
 */
public final class CsvVehiculoCodec {

    /** Delimitador de campos. */
    private static final String SEPARADOR = "\\|";

    /** Delimitador usado al escribir. */
    private static final String SEPARADOR_SALIDA = "|";

    /** Cantidad de campos comunes antes de los atributos específicos. */
    private static final int CAMPOS_COMUNES = 9;

    /** Constructor privado de clase utilitaria. */
    private CsvVehiculoCodec() {
    }

    /**
     * Serializa un vehículo en una línea CSV.
     *
     * @param vehiculo vehículo a serializar
     * @return línea CSV con el tipo y todos sus atributos
     */
    public static String serialize(Vehiculo vehiculo) {
        StringJoiner fila = new StringJoiner(SEPARADOR_SALIDA);
        fila.add(vehiculo.obtenerTipo().name());
        fila.add(codificar(vehiculo.getIdentificador()));
        fila.add(codificar(vehiculo.getMarca()));
        fila.add(codificar(vehiculo.getModelo()));
        fila.add(String.valueOf(vehiculo.getAnio()));
        fila.add(codificar(vehiculo.getColor()));
        fila.add(vehiculo.getEstado().name());
        fila.add(String.valueOf(vehiculo.getKilometraje()));
        fila.add(String.valueOf(vehiculo.getPrecio()));

        if (vehiculo instanceof Automovil) {
            Automovil auto = (Automovil) vehiculo;
            fila.add(auto.getCarroceria().name());
            fila.add(String.valueOf(auto.getPuertas()));
        } else if (vehiculo instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) vehiculo;
            fila.add(camioneta.getCabina().name());
            fila.add(camioneta.getTraccion().name());
            fila.add(String.valueOf(camioneta.getCapacidadCarga()));
        } else if (vehiculo instanceof Motocicleta) {
            Motocicleta moto = (Motocicleta) vehiculo;
            fila.add(moto.getEstilo().name());
            fila.add(String.valueOf(moto.getCilindrada()));
        }
        return fila.toString();
    }

    /**
     * Reconstruye un vehículo a partir de una línea CSV y valida su contenido.
     *
     * @param linea línea CSV leída del archivo
     * @return vehículo reconstruido
     * @throws DatoInvalidoException si la línea está incompleta o es corrupta
     */
    public static Vehiculo deserialize(String linea) throws DatoInvalidoException {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length < CAMPOS_COMUNES) {
            throw new DatoInvalidoException("Registro CSV incompleto: " + campos.length + " campos.");
        }

        TipoVehiculo tipo = Validador.parsearEnum(campos[0], TipoVehiculo.class);
        String identificador = decodificar(campos[1]);
        String marca = decodificar(campos[2]);
        String modelo = decodificar(campos[3]);
        Integer anio = Validador.parsearEntero(campos[4], "anio");
        String color = decodificar(campos[5]);
        EstadoVehiculo estado = Validador.parsearEnum(campos[6], EstadoVehiculo.class);
        Double kilometraje = Validador.parsearDouble(campos[7], "kilometraje");
        Double precio = Validador.parsearDouble(campos[8], "precio");

        Vehiculo vehiculo = switch (tipo) {
            case AUTOMOVIL -> {
                verificarCantidad(campos, 11, "automóvil");
                yield new Automovil(identificador, marca, modelo, anio, color, estado, kilometraje,
                        precio, Validador.parsearEnum(campos[9], TipoCarroceria.class),
                        Validador.parsearEntero(campos[10], "puertas"));
            }
            case CAMIONETA -> {
                verificarCantidad(campos, 12, "camioneta");
                yield new Camioneta(identificador, marca, modelo, anio, color, estado, kilometraje,
                        precio, Validador.parsearEnum(campos[9], TipoCabina.class),
                        Validador.parsearEnum(campos[10], TipoTraccion.class),
                        Validador.parsearDouble(campos[11], "capacidadCarga"));
            }
            case MOTOCICLETA -> {
                verificarCantidad(campos, 11, "motocicleta");
                yield new Motocicleta(identificador, marca, modelo, anio, color, estado, kilometraje,
                        precio, Validador.parsearEnum(campos[9], EstiloMotocicleta.class),
                        Validador.parsearEntero(campos[10], "cilindrada"));
            }
        };

        vehiculo.validar();
        return vehiculo;
    }

    /**
     * Comprueba la cantidad exacta de campos según el tipo de vehículo.
     *
     * @param campos     arreglo de campos leídos
     * @param esperados  cantidad esperada
     * @param nombreTipo nombre del tipo para el mensaje
     * @throws DatoInvalidoException si la cantidad no coincide
     */
    private static void verificarCantidad(String[] campos, int esperados, String nombreTipo)
            throws DatoInvalidoException {
        if (campos.length != esperados) {
            throw new DatoInvalidoException("Registro de " + nombreTipo + " con "
                    + campos.length + " campos; se esperaban " + esperados + ".");
        }
    }

    /**
     * Codifica un texto a Base64 URL.
     *
     * @param texto texto original
     * @return texto codificado
     */
    private static String codificar(String texto) {
        return Base64.getUrlEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodifica un campo Base64 URL.
     *
     * @param campo campo codificado
     * @return texto original
     * @throws DatoInvalidoException si el campo no es Base64 válido
     */
    private static String decodificar(String campo) throws DatoInvalidoException {
        try {
            return new String(Base64.getUrlDecoder().decode(campo), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException causa) {
            throw new DatoInvalidoException("Campo de texto corrupto en el archivo CSV.", causa);
        }
    }
}
