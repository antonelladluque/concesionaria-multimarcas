package repositorio;

import excepciones.VehiculoDuplicadoException;
import excepciones.VehiculoNoEncontradoException;

import java.util.List;

/**
 * Contrato genérico de repositorio con operaciones CRUD básicas.
 *
 * @param <T>  tipo de entidad gestionada
 * @param <ID> tipo del identificador de la entidad
 */
public interface IRepositorio<T, ID> {

    /**
     * Agrega una entidad al repositorio.
     *
     * @param entidad entidad a agregar
     * @throws VehiculoDuplicadoException si ya existe una entidad con el mismo id
     */
    void agregar(T entidad) throws VehiculoDuplicadoException;

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador buscado
     * @return entidad encontrada
     * @throws VehiculoNoEncontradoException si no existe
     */
    T buscarPorId(ID id) throws VehiculoNoEncontradoException;

    /**
     * Devuelve todas las entidades almacenadas.
     *
     * @return lista con todas las entidades
     */
    List<T> listarTodos();

    /**
     * Reemplaza una entidad existente.
     *
     * @param entidad entidad con los datos actualizados
     * @throws VehiculoNoEncontradoException si el identificador no existe
     */
    void actualizar(T entidad) throws VehiculoNoEncontradoException;

    /**
     * Elimina una entidad por su identificador.
     *
     * @param id identificador de la entidad a eliminar
     * @throws VehiculoNoEncontradoException si no existe
     */
    void eliminar(ID id) throws VehiculoNoEncontradoException;
}
