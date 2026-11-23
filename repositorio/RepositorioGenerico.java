package repositorio;

import excepciones.VehiculoDuplicadoException;
import excepciones.VehiculoNoEncontradoException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementación genérica en memoria del contrato {@link IRepositorio}.
 *
 * <p><b>Decisión de diseño:</b> se usa un {@link LinkedHashMap} para indexar
 * por identificador (búsquedas O(1)) y conservar el orden de inserción en el
 * listado. La extracción del id se inyecta con una función
 * {@link Function}, de modo que esta clase sea reutilizable para cualquier entidad.</p>
 *
 * @param <T>  tipo de entidad gestionada
 * @param <ID> tipo del identificador
 */
public class RepositorioGenerico<T, ID> implements IRepositorio<T, ID> {

    /** Mapa indexado por identificador. */
    protected final Map<ID, T> entidades = new LinkedHashMap<>();

    /** Función que extrae el identificador de una entidad. */
    protected final Function<T, ID> extractorId;

    /**
     * Crea el repositorio indicando cómo obtener el identificador.
     *
     * @param extractorId función que extrae el id de la entidad
     */
    public RepositorioGenerico(Function<T, ID> extractorId) {
        this.extractorId = extractorId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void agregar(T entidad) throws VehiculoDuplicadoException {
        ID id = extractorId.apply(entidad);
        if (entidades.containsKey(id)) {
            throw new VehiculoDuplicadoException(String.valueOf(id));
        }
        entidades.put(id, entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T buscarPorId(ID id) throws VehiculoNoEncontradoException {
        T entidad = entidades.get(id);
        if (entidad == null) {
            throw new VehiculoNoEncontradoException(String.valueOf(id));
        }
        return entidad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(entidades.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actualizar(T entidad) throws VehiculoNoEncontradoException {
        ID id = extractorId.apply(entidad);
        if (!entidades.containsKey(id)) {
            throw new VehiculoNoEncontradoException(String.valueOf(id));
        }
        entidades.put(id, entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(ID id) throws VehiculoNoEncontradoException {
        if (entidades.remove(id) == null) {
            throw new VehiculoNoEncontradoException(String.valueOf(id));
        }
    }

    /**
     * Reemplaza todo el contenido del repositorio, usado al cargar desde disco.
     *
     * @param nuevasEntidades colección de entidades a cargar
     */
    protected void reemplazarTodo(Collection<T> nuevasEntidades) {
        entidades.clear();
        for (T entidad : nuevasEntidades) {
            entidades.put(extractorId.apply(entidad), entidad);
        }
    }
}
