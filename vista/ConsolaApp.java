package vista;

import excepciones.DatoInvalidoException;
import excepciones.VehiculoDuplicadoException;
import excepciones.VehiculoNoEncontradoException;
import modelo.Automovil;
import modelo.Camioneta;
import modelo.EstadoVehiculo;
import modelo.EstiloMotocicleta;
import modelo.Motocicleta;
import modelo.SimulacionVenta;
import modelo.TipoCabina;
import modelo.TipoCarroceria;
import modelo.TipoTraccion;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import repositorio.RepositorioVehiculos;
import servicio.ServicioVehiculos;
import util.LecturaConsola;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Aplicación de consola: menús, lectura de datos y presentación.
 *
 * <p>Esta clase delega todas las decisiones de negocio a
 * {@link ServicioVehiculos}; no valida reglas de dominio por su cuenta y jamás
 * accede directamente al archivo de persistencia.</p>
 */
public class ConsolaApp {

    /** Lector de datos con control de errores. */
    private final LecturaConsola lector = new LecturaConsola();

    /** Repositorio concrete usado por el servicio. */
    private final RepositorioVehiculos repositorio = new RepositorioVehiculos();

    /** Servicio de negocio. */
    private final ServicioVehiculos servicio = new ServicioVehiculos(repositorio);

    /**
     * Inicia la aplicación: carga el stock y ejecuta el bucle del menú.
     */
    public void iniciar() {
        try {
            servicio.inicializar();
            System.out.println("Stock cargado desde '" + repositorio.getArchivo() + "'. "
                    + servicio.listarTodos().size() + " vehículo(s).");
        } catch (IOException | DatoInvalidoException error) {
            System.out.println("Advertencia: no se pudo cargar el stock: " + error.getMessage());
        }

        boolean salir = false;
        while (!salir) {
            try {
                mostrarMenu();
                Integer opcion = lector.leerEntero("  Opción: ", 0, 7);
                salir = procesarOpcion(opcion);
            } catch (VehiculoDuplicadoException | VehiculoNoEncontradoException
                     | DatoInvalidoException error) {
                System.out.println("  ! Error de negocio: " + error.getMessage());
            } catch (IOException error) {
                System.out.println("  ! Error de persistencia: " + error.getMessage());
            } catch (NoSuchElementException error) {
                System.out.println("Entrada finalizada. Saliendo...");
                salir = true;
            } catch (RuntimeException error) {
                System.out.println("  ! Error inesperado controlado: " + error.getMessage());
            }
        }
        System.out.println("Hasta luego.");
    }

    /**
     * Muestra el menú principal.
     */
    private void mostrarMenu() {
        System.out.println(System.lineSeparator()
                + "===== CONCESIONARIA MULTIMARCAS - GESTIÓN DE STOCK ====="
                + System.lineSeparator()
                + "  1) Alta de vehículo" + System.lineSeparator()
                + "  2) Baja de vehículo" + System.lineSeparator()
                + "  3) Modificación (precio o color)" + System.lineSeparator()
                + "  4) Listado general" + System.lineSeparator()
                + "  5) Filtros (tipo / estado / marca)" + System.lineSeparator()
                + "  6) Simular venta e impuestos" + System.lineSeparator()
                + "  7) Guardar stock ahora" + System.lineSeparator()
                + "  0) Salir");
    }

    /**
     * Ejecuta la opción elegida.
     *
     * @param opcion opción del menú principal
     * @return {@code true} si se debe salir
     * @throws DatoInvalidoException         si un dato no es válido
     * @throws VehiculoDuplicadoException    si se repite un identificador
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     * @throws IOException                   si falla la persistencia
     */
    private boolean procesarOpcion(Integer opcion) throws DatoInvalidoException,
            VehiculoDuplicadoException, VehiculoNoEncontradoException, IOException {
        switch (opcion) {
            case 1 -> altaVehiculo();
            case 2 -> bajaVehiculo();
            case 3 -> modificarVehiculo();
            case 4 -> System.out.println(FormateadorSalida.tabla(servicio.listarTodos()));
            case 5 -> filtrar();
            case 6 -> simular();
            case 7 -> {
                servicio.guardar();
                System.out.println("  Stock guardado correctamente.");
            }
            case 0 -> {
                return true;
            }
            default -> System.out.println("  Opción no reconocida.");
        }
        return false;
    }

    /**
     * Solicita los datos y da de alta un vehículo del tipo elegido.
     *
     * @throws DatoInvalidoException        si los datos ingresados son inválidos
     * @throws VehiculoDuplicadoException   si el identificador ya existe
     * @throws IOException                  si falla la persistencia
     */
    private void altaVehiculo() throws DatoInvalidoException,
            VehiculoDuplicadoException, IOException {
        System.out.println("-- ALTA DE VEHÍCULO --");
        TipoVehiculo tipo = lector.leerEnum("  Tipo de vehículo:", TipoVehiculo.class);
        String identificador = lector.leerTexto("  Identificador (patente/código): ");
        String marca = lector.leerTexto("  Marca: ");
        String modelo = lector.leerTexto("  Modelo: ");
        Integer anio = lector.leerEntero("  Año (1950-2026): ",
                Vehiculo.ANIO_MINIMO, Vehiculo.ANIO_MAXIMO);
        String color = lector.leerTexto("  Color: ");
        EstadoVehiculo estado = lector.leerEnum("  Estado:", EstadoVehiculo.class);
        Double kilometraje = estado == EstadoVehiculo.NUEVO
                ? 0.0
                : lector.leerDouble("  Kilometraje: ", 0.1, null);
        Double precio = lector.leerDouble("  Precio: ", 0.01, null);

        Vehiculo vehiculo = switch (tipo) {
            case AUTOMOVIL -> new Automovil(identificador, marca, modelo, anio, color, estado,
                    kilometraje, precio,
                    lector.leerEnum("  Carrocería:", TipoCarroceria.class),
                    lector.leerEntero("  Puertas (2-5): ", 2, 5));
            case CAMIONETA -> new Camioneta(identificador, marca, modelo, anio, color, estado,
                    kilometraje, precio,
                    lector.leerEnum("  Cabina/caja:", TipoCabina.class),
                    lector.leerEnum("  Tracción:", TipoTraccion.class),
                    lector.leerDouble("  Capacidad de carga (kg): ", 0.1, null));
            case MOTOCICLETA -> new Motocicleta(identificador, marca, modelo, anio, color, estado,
                    kilometraje, precio,
                    lector.leerEnum("  Estilo:", EstiloMotocicleta.class),
                    lector.leerEntero("  Cilindrada (cc): ", 1, null));
        };

        servicio.darDeAlta(vehiculo);
        System.out.println("  Alta exitosa." + FormateadorSalida.ficha(vehiculo));
    }

    /**
     * Da de baja un vehículo previa confirmación.
     *
     * @throws DatoInvalidoException         si el id está vacío
     * @throws VehiculoNoEncontradoException si no existe
     * @throws IOException                   si falla la persistencia
     */
    private void bajaVehiculo() throws DatoInvalidoException,
            VehiculoNoEncontradoException, IOException {
        System.out.println("-- BAJA DE VEHÍCULO --");
        String identificador = lector.leerTexto("  Identificador a eliminar: ");
        Vehiculo vehiculo = servicio.buscarPorId(identificador);
        if (lector.leerSiNo("  ¿Confirma la baja de " + vehiculo.getMarca() + " "
                + vehiculo.getModelo() + "?")) {
            servicio.darDeBaja(identificador);
            System.out.println("  Vehículo eliminado.");
        } else {
            System.out.println("  Operación cancelada.");
        }
    }

    /**
     * Modifica el precio o el color de un vehículo.
     *
     * @throws DatoInvalidoException         si el dato nuevo es inválido
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     * @throws IOException                   si falla la persistencia
     */
    private void modificarVehiculo() throws DatoInvalidoException,
            VehiculoNoEncontradoException, IOException {
        System.out.println("-- MODIFICACIÓN --");
        String identificador = lector.leerTexto("  Identificador del vehículo: ");
        System.out.println(FormateadorSalida.ficha(servicio.buscarPorId(identificador)));
        Integer campo = lector.leerEntero("  1) Precio   2) Color -> ", 1, 2);
        if (campo == 1) {
            Double nuevoPrecio = lector.leerDouble("  Nuevo precio: ", 0.01, null);
            servicio.cambiarPrecio(identificador, nuevoPrecio);
        } else {
            String nuevoColor = lector.leerTexto("  Nuevo color: ");
            servicio.cambiarColor(identificador, nuevoColor);
        }
        System.out.println("  Modificación guardada.");
    }

    /**
     * Solicita el criterio de filtrado y muestra los resultados.
     *
     * @throws DatoInvalidoException si la marca está vacía
     */
    private void filtrar() throws DatoInvalidoException {
        System.out.println("-- FILTROS --");
        Integer opcion = lector.leerEntero("  1) Tipo  2) Estado  3) Marca -> ", 1, 3);
        List<Vehiculo> resultados = switch (opcion) {
            case 1 -> servicio.listarPorTipo(
                    lector.leerEnum("  Tipo:", TipoVehiculo.class));
            case 2 -> servicio.listarPorEstado(
                    lector.leerEnum("  Estado:", EstadoVehiculo.class));
            default -> servicio.listarPorMarca(lector.leerTexto("  Marca (o parte): "));
        };
        System.out.println(FormateadorSalida.tabla(resultados));
    }

    /**
     * Simula una venta con descuento, impuesto y tasación.
     *
     * @throws DatoInvalidoException         si el descuento o el id son inválidos
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     */
    private void simular() throws DatoInvalidoException, VehiculoNoEncontradoException {
        System.out.println("-- SIMULACIÓN DE VENTA --");
        String identificador = lector.leerTexto("  Identificador del vehículo: ");
        Double descuento = lector.leerDouble("  Descuento (%) : ", 0.0, 100.0);
        SimulacionVenta resultado = servicio.simularVenta(identificador, descuento);
        System.out.println(FormateadorSalida.simulacion(resultado));
    }
}
