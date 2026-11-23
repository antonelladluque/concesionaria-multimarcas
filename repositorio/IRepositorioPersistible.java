package repositorio;

import excepciones.DatoInvalidoException;

import java.io.IOException;

/**
 * Extensión del contrato de repositorio para implementaciones con persistencia.
 *
 * @param <T>  tipo de entidad gestionada
 * @param <ID> tipo del identificador de la entidad
 */
public interface IRepositorioPersistible<T, ID> extends IRepositorio<T, ID> {

    /**
     * Carga las entidades desde el soporte de persistencia.
     *
     * @throws IOException             si falla la lectura del archivo
     * @throws DatoInvalidoException   si el archivo contiene un registro corrupto
     */
    void cargar() throws IOException, DatoInvalidoException;

    /**
     * Guarda todas las entidades en el soporte de persistencia.
     *
     * @throws IOException si falla la escritura del archivo
     */
    void guardar() throws IOException;
}
