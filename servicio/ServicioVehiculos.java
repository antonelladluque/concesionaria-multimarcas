package servicio;

import excepciones.DatoInvalidoException;
import excepciones.VehiculoDuplicadoException;
import excepciones.VehiculoNoEncontradoException;
import modelo.EstadoVehiculo;
import modelo.SimulacionVenta;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import repositorio.IRepositorioPersistible;
import util.Validador;

import java.io.IOException;
import java.util.List;

/**
 * Servicio de negocio del stock de la concesionaria.
 *
 * <p>Coordina la consola con el repositorio: aplica validaciones de dominio,
 * ejecuta las operaciones CRUD y guarda automáticamente los cambios. La vista
 * nunca accede al repositorio ni a archivos directamente.</p>
 */
public class ServicioVehiculos {

    /** Repositorio persistible inyectado. */
    private final IRepositorioPersistible<Vehiculo, String> repositorio;

    /**
     * Crea el servicio con su repositorio.
     *
     * @param repositorio repositorio persistible de vehículos
     */
    public ServicioVehiculos(IRepositorioPersistible<Vehiculo, String> repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Carga el stock desde el soporte de persistencia al iniciar la aplicación.
     *
     * @throws IOException           si no se puede leer el archivo
     * @throws DatoInvalidoException si el archivo contiene datos corruptos
     */
    public void inicializar() throws IOException, DatoInvalidoException {
        repositorio.cargar();
    }

    /**
     * Da de alta un vehículo y persiste el cambio.
     *
     * @param vehiculo vehículo a agregar
     * @throws DatoInvalidoException       si el vehículo no supera la validación
     * @throws VehiculoDuplicadoException  si el identificador ya existe
     * @throws IOException                 si falla la escritura del archivo
     */
    public void darDeAlta(Vehiculo vehiculo)
            throws DatoInvalidoException, VehiculoDuplicadoException, IOException {
        vehiculo.validar();
        repositorio.agregar(vehiculo);
        persistir();
    }

    /**
     * Busca un vehículo por identificador.
     *
     * @param identificador identificador buscado
     * @return vehículo encontrado
     * @throws DatoInvalidoException         si el identificador está vacío
     * @throws VehiculoNoEncontradoException si no existe
     */
    public Vehiculo buscarPorId(String identificador)
            throws DatoInvalidoException, VehiculoNoEncontradoException {
        return repositorio.buscarPorId(Validador.validarTextoNoVacio(identificador, "identificador"));
    }

    /**
     * Devuelve todo el stock.
     *
     * @return lista con todos los vehículos
     */
    public List<Vehiculo> listarTodos() {
        return repositorio.listarTodos();
    }

    /**
     * Filtra el stock por tipo de vehículo.
     *
     * @param tipo tipo buscado
     * @return lista de vehículos del tipo indicado
     */
    public List<Vehiculo> listarPorTipo(TipoVehiculo tipo) {
        return listarTodos().stream()
                .filter(vehiculo -> vehiculo.obtenerTipo() == tipo)
                .toList();
    }

    /**
     * Filtra el stock por estado comercial.
     *
     * @param estado estado buscado
     * @return lista de vehículos con ese estado
     */
    public List<Vehiculo> listarPorEstado(EstadoVehiculo estado) {
        return listarTodos().stream()
                .filter(vehiculo -> vehiculo.getEstado() == estado)
                .toList();
    }

    /**
     * Filtra el stock por marca (comparación sin distinción de mayúsculas).
     *
     * @param marca marca buscada
     * @return lista de vehículos de esa marca
     * @throws DatoInvalidoException si la marca está vacía
     */
    public List<Vehiculo> listarPorMarca(String marca) throws DatoInvalidoException {
        String marcaValidada = Validador.validarTextoNoVacio(marca, "marca").toLowerCase();
        return listarTodos().stream()
                .filter(vehiculo -> vehiculo.getMarca().toLowerCase().contains(marcaValidada))
                .toList();
    }

    /**
     * Filtra el stock aplicando varios criterios a la vez.
     *
     * @param tipo   tipo buscado, o {@code null} para no filtrar
     * @param estado estado buscado, o {@code null} para no filtrar
     * @param marca  marca o parte de ella, o {@code null}/{@code blank} para no filtrar
     * @return lista de vehículos que cumplen todos los criterios
     * @throws DatoInvalidoException si la marca informada está vacía
     */
    public List<Vehiculo> listarConFiltros(TipoVehiculo tipo, EstadoVehiculo estado, String marca)
            throws DatoInvalidoException {
        String marcaBuscada = marca == null || marca.isBlank()
                ? null
                : Validador.validarTextoNoVacio(marca, "marca").toLowerCase();
        return listarTodos().stream()
                .filter(vehiculo -> tipo == null || vehiculo.obtenerTipo() == tipo)
                .filter(vehiculo -> estado == null || vehiculo.getEstado() == estado)
                .filter(vehiculo -> marcaBuscada == null
                        || vehiculo.getMarca().toLowerCase().contains(marcaBuscada))
                .toList();
    }

    /**
     * Modifica el precio de un vehículo y persiste el cambio.
     *
     * @param identificador identificador del vehículo
     * @param nuevoPrecio   nuevo precio, mayor que cero
     * @throws DatoInvalidoException         si el precio es inválido
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     * @throws IOException                   si falla la escritura
     */
    public void cambiarPrecio(String identificador, Double nuevoPrecio)
            throws DatoInvalidoException, VehiculoNoEncontradoException, IOException {
        Validador.validarPrecio(nuevoPrecio);
        Vehiculo vehiculo = buscarPorId(identificador);
        vehiculo.setPrecio(nuevoPrecio);
        repositorio.actualizar(vehiculo);
        persistir();
    }

    /**
     * Modifica el color de un vehículo y persiste el cambio.
     *
     * @param identificador identificador del vehículo
     * @param nuevoColor    nuevo color
     * @throws DatoInvalidoException         si el color está vacío
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     * @throws IOException                   si falla la escritura
     */
    public void cambiarColor(String identificador, String nuevoColor)
            throws DatoInvalidoException, VehiculoNoEncontradoException, IOException {
        String colorValidado = Validador.validarTextoNoVacio(nuevoColor, "color");
        Vehiculo vehiculo = buscarPorId(identificador);
        vehiculo.setColor(colorValidado);
        repositorio.actualizar(vehiculo);
        persistir();
    }

    /**
     * Da de baja un vehículo y persiste el cambio.
     *
     * @param identificador identificador del vehículo a eliminar
     * @throws DatoInvalidoException         si el identificador está vacío
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     * @throws IOException                   si falla la escritura
     */
    public void darDeBaja(String identificador)
            throws DatoInvalidoException, VehiculoNoEncontradoException, IOException {
        repositorio.eliminar(Validador.validarTextoNoVacio(identificador, "identificador"));
        persistir();
    }

    /**
     * Simula la venta de un vehículo usando polimorfismo e interfaces.
     *
     * @param identificador     identificador del vehículo
     * @param porcentajeDescuento descuento entre 0 y 100
     * @return simulación con todos los importes calculados
     * @throws DatoInvalidoException         si el descuento o el id son inválidos
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     */
    public SimulacionVenta simularVenta(String identificador, Double porcentajeDescuento)
            throws DatoInvalidoException, VehiculoNoEncontradoException {
        Double descuento = Validador.validarPorcentaje(porcentajeDescuento);
        Vehiculo vehiculo = buscarPorId(identificador);
        Double precioFinal = vehiculo.aplicarDescuento(descuento);
        Double impuesto = vehiculo.calcularImpuestoPatente();
        Double tasado = vehiculo.calcularValorTasado();
        Double total = Math.round((precioFinal + impuesto) * 100.0) / 100.0;
        return new SimulacionVenta(vehiculo, descuento, precioFinal, impuesto, tasado, total);
    }

    /**
     * Fuerza la escritura del stock en disco.
     *
     * @throws IOException si falla la escritura
     */
    public void guardar() throws IOException {
        persistir();
    }

    /**
     * Persiste el estado actual del repositorio.
     *
     * @throws IOException si falla la escritura
     */
    private void persistir() throws IOException {
        repositorio.guardar();
    }
}
