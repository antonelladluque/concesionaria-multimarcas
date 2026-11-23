package repositorio;

import excepciones.DatoInvalidoException;
import modelo.Vehiculo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio concreto de vehículos con persistencia en un archivo CSV.
 *
 * <p>El archivo se carga automáticamente al iniciar la aplicación y se
 * reescribe por completo después de cada alta, modificación o baja, garantizando
 * que el stock perdure entre ejecuciones.</p>
 */
public class RepositorioVehiculos extends RepositorioGenerico<Vehiculo, String>
        implements IRepositorioPersistible<Vehiculo, String> {

    /** Ruta por defecto del archivo de stock. */
    private static final Path ARCHIVO_POR_DEFECTO = Paths.get("stock.csv");

    /** Ruta del archivo de persistencia. */
    private final Path archivo;

    /**
     * Crea el repositorio usando {@code stock.csv} en el directorio actual.
     */
    public RepositorioVehiculos() {
        this(ARCHIVO_POR_DEFECTO);
    }

    /**
     * Crea el repositorio con una ruta de archivo configurable.
     *
     * @param archivo ruta del archivo CSV
     */
    public RepositorioVehiculos(Path archivo) {
        super(Vehiculo::getIdentificador);
        this.archivo = archivo;
    }

    /**
     * Devuelve la ruta del archivo de persistencia.
     *
     * @return ruta configurada
     */
    public Path getArchivo() {
        return archivo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cargar() throws IOException, DatoInvalidoException {
        if (!Files.exists(archivo)) {
            return;
        }
        List<Vehiculo> cargados = new ArrayList<>();
        int numeroLinea = 0;
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                try {
                    cargados.add(CsvVehiculoCodec.deserialize(linea));
                } catch (DatoInvalidoException | NumberFormatException error) {
                    throw new DatoInvalidoException("Error en la línea " + numeroLinea
                            + " de '" + archivo + "': " + error.getMessage(), error);
                }
            }
        }
        reemplazarTodo(cargados);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void guardar() throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (Vehiculo vehiculo : listarTodos()) {
                escritor.write(CsvVehiculoCodec.serialize(vehiculo));
                escritor.newLine();
            }
        }
    }
}
