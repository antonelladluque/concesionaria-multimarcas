package web;

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
import util.Validador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP embebido, implementado únicamente con Java estándar.
 *
 * <p><b>Decisión de diseño:</b> se usa {@link ServerSocket} en lugar de un
 * framework para mantener el requerimiento "sin dependencias pesadas". Cada
 * petición se atiende en un hilo del pool y se delega al
 * {@link ServicioVehiculos}; la capa web solo traduce HTTP ↔ objetos del
 * dominio, sin duplicar reglas de negocio.</p>
 */
public class ServidorWeb {

    /** Puerto por defecto. */
    private static final Integer PUERTO_DEFECTO = 8080;

    /** Directorio por defecto de los archivos estáticos. */
    private static final Path ESTATICO_DEFECTO = Paths.get("web-static");

    /** Tipo de contenido JSON. */
    private static final String TIPO_JSON = "application/json; charset=utf-8";

    /** Servicio de negocio. */
    private final ServicioVehiculos servicio;

    /** Directorio de archivos estáticos. */
    private final Path raizEstatica;

    /** Puerto de escucha. */
    private final Integer puerto;

    /** Socket servidor. */
    private ServerSocket servidor;

    /** Pool de hilos para atender peticiones. */
    private ExecutorService pool;

    /** Bandera de ejecución. */
    private volatile boolean ejecutando;

    /**
     * Crea el servidor con el repositorio por defecto.
     *
     * @param puerto        puerto de escucha
     * @param raizEstatica  directorio con index.html, styles.css y app.js
     */
    public ServidorWeb(Integer puerto, Path raizEstatica) {
        this(puerto, raizEstatica, new RepositorioVehiculos());
    }

    /**
     * Crea el servidor con un repositorio inyectado.
     *
     * @param puerto        puerto de escucha
     * @param raizEstatica  directorio de archivos estáticos
     * @param repositorio   repositorio persistible de vehículos
     */
    public ServidorWeb(Integer puerto, Path raizEstatica, RepositorioVehiculos repositorio) {
        this.puerto = puerto;
        this.raizEstatica = raizEstatica.normalize();
        this.servicio = new ServicioVehiculos(repositorio);
    }

    /**
     * Carga el stock y comienza a atender peticiones hasta que se detenga.
     *
     * @throws IOException           si no se puede abrir el puerto o leer el stock
     * @throws DatoInvalidoException si el archivo de stock contiene datos corruptos
     */
    public void iniciar() throws IOException, DatoInvalidoException {
        servicio.inicializar();
        pool = Executors.newFixedThreadPool(12);
        servidor = new ServerSocket(puerto, 50, InetAddress.getLoopbackAddress());
        ejecutando = true;
        Runtime.getRuntime().addShutdownHook(new Thread(this::detener));
        System.out.println("Servidor web iniciado en http://localhost:" + puerto);
        System.out.println("Vehículos en stock: " + servicio.listarTodos().size());

        while (ejecutando) {
            try {
                Socket cliente = servidor.accept();
                pool.execute(() -> atender(cliente));
            } catch (IOException error) {
                if (ejecutando) {
                    System.out.println("Error al aceptar conexión: " + error.getMessage());
                }
            }
        }
        pool.shutdown();
    }

    /**
     * Detiene el servidor y libera el puerto.
     */
    public void detener() {
        ejecutando = false;
        try {
            if (servidor != null && !servidor.isClosed()) {
                servidor.close();
            }
        } catch (IOException error) {
            System.out.println("Error al cerrar el servidor: " + error.getMessage());
        }
    }

    /**
     * Atiende una petición HTTP y envía la respuesta.
     *
     * @param cliente socket del cliente
     */
    private void atender(Socket cliente) {
        try (cliente;
             BufferedReader lector = new BufferedReader(
                     new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8));
             OutputStream salida = cliente.getOutputStream()) {

            String solicitud = lector.readLine();
            if (solicitud == null || solicitud.isBlank()) {
                return;
            }
            String[] partes = solicitud.split(" ");
            if (partes.length < 2) {
                return;
            }
            String metodo = partes[0].toUpperCase();
            String objetivo = partes[1];

            Map<String, String> cabeceras = new HashMap<>();
            String cabecera;
            while ((cabecera = lector.readLine()) != null && !cabecera.isEmpty()) {
                int separador = cabecera.indexOf(':');
                if (separador > 0) {
                    cabeceras.put(cabecera.substring(0, separador).trim().toLowerCase(),
                            cabecera.substring(separador + 1).trim());
                }
            }

            Integer longitud = 0;
            try {
                longitud = Integer.parseInt(cabeceras.getOrDefault("content-length", "0"));
            } catch (NumberFormatException ignorado) {
                longitud = 0;
            }
            char[] cuerpoCaracteres = new char[longitud];
            Integer leidos = 0;
            while (leidos < longitud) {
                int cantidad = lector.read(cuerpoCaracteres, leidos, longitud - leidos);
                if (cantidad < 0) {
                    break;
                }
                leidos += cantidad;
            }
            String cuerpo = new String(cuerpoCaracteres, 0, leidos);

            Respuesta respuesta = enrutar(metodo, objetivo, cuerpo);
            escribir(salida, respuesta);
        } catch (IOException error) {
            System.out.println("Error en petición: " + error.getMessage());
        } catch (RuntimeException error) {
            System.out.println("Error inesperado en petición: " + error.getMessage());
        }
    }

    /**
     * Dirige la petición al controlador API o al archivo estático correspondiente.
     *
     * @param metodo    método HTTP
     * @param objetivo  ruta con query string
     * @param cuerpo    cuerpo de la petición
     * @return respuesta HTTP
     */
    private Respuesta enrutar(String metodo, String objetivo, String cuerpo) {
        try {
            int indiceConsulta = objetivo.indexOf('?');
            String ruta = indiceConsulta >= 0 ? objetivo.substring(0, indiceConsulta) : objetivo;
            String consulta = indiceConsulta >= 0 ? objetivo.substring(indiceConsulta + 1) : "";

            Map<String, String> parametros = new LinkedHashMap<>();
            parametros.putAll(parsearFormulario(consulta));
            parametros.putAll(parsearFormulario(cuerpo));

            if (ruta.startsWith("/api/")) {
                return api(metodo, ruta, parametros);
            }
            if (metodo.equals("GET") || metodo.equals("HEAD")) {
                return estatico(ruta);
            }
            return new Respuesta(405, Json.error("Método no permitido."), TIPO_JSON);
        } catch (DatoInvalidoException | VehiculoDuplicadoException
                 | VehiculoNoEncontradoException error) {
            return new Respuesta(400, Json.error(error.getMessage()), TIPO_JSON);
        } catch (NumberFormatException error) {
            return new Respuesta(400, Json.error("Se esperaba un valor numérico."), TIPO_JSON);
        } catch (IOException error) {
            return new Respuesta(500, Json.error("Error de persistencia: " + error.getMessage()),
                    TIPO_JSON);
        }
    }

    /**
     * Controlador de la API REST.
     *
     * @param metodo     método HTTP
     * @param ruta       ruta de la API
     * @param parametros parámetros de consulta y cuerpo
     * @return respuesta HTTP
     * @throws DatoInvalidoException         si un parámetro no es válido
     * @throws VehiculoDuplicadoException    si se repite un identificador
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     * @throws IOException                   si falla la persistencia
     */
    private Respuesta api(String metodo, String ruta, Map<String, String> parametros)
            throws DatoInvalidoException, VehiculoDuplicadoException,
            VehiculoNoEncontradoException, IOException {

        if (ruta.equals("/api/opciones") && metodo.equals("GET")) {
            return new Respuesta(200, Json.opciones(), TIPO_JSON);
        }

        if (ruta.equals("/api/vehiculos")) {
            if (metodo.equals("GET")) {
                TipoVehiculo tipo = enumOpcional(parametros.get("tipo"), TipoVehiculo.class);
                EstadoVehiculo estado = enumOpcional(parametros.get("estado"), EstadoVehiculo.class);
                List<Vehiculo> resultados = servicio.listarConFiltros(tipo, estado,
                        parametros.get("marca"));
                return new Respuesta(200, Json.lista(resultados), TIPO_JSON);
            }
            if (metodo.equals("POST")) {
                Vehiculo vehiculo = construirVehiculo(parametros);
                servicio.darDeAlta(vehiculo);
                return new Respuesta(201, Json.mensaje("Vehículo dado de alta correctamente."),
                        TIPO_JSON);
            }
            return new Respuesta(405, Json.error("Método no permitido."), TIPO_JSON);
        }

        if (ruta.equals("/api/simulacion") && metodo.equals("POST")) {
            SimulacionVenta simulacion = servicio.simularVenta(
                    parametros.get("id"),
                    Validador.parsearDouble(parametros.get("descuento"), "descuento"));
            return new Respuesta(200, Json.simulacion(simulacion), TIPO_JSON);
        }

        if (ruta.startsWith("/api/vehiculos/")) {
            String resto = ruta.substring("/api/vehiculos/".length());
            String id = URLDecoder.decode(resto.split("/")[0], StandardCharsets.UTF_8);
            if (metodo.equals("PUT") && resto.endsWith("/precio")) {
                servicio.cambiarPrecio(id, Validador.parsearDouble(parametros.get("precio"), "precio"));
                return new Respuesta(200, Json.mensaje("Precio actualizado."), TIPO_JSON);
            }
            if (metodo.equals("PUT") && resto.endsWith("/color")) {
                servicio.cambiarColor(id, parametros.get("color"));
                return new Respuesta(200, Json.mensaje("Color actualizado."), TIPO_JSON);
            }
            if (metodo.equals("DELETE") && !resto.contains("/")) {
                servicio.darDeBaja(id);
                return new Respuesta(200, Json.mensaje("Vehículo eliminado."), TIPO_JSON);
            }
        }

        return new Respuesta(404, Json.error("Recurso no encontrado."), TIPO_JSON);
    }

    /**
     * Construye un vehículo del tipo indicado a partir de parámetros HTTP.
     *
     * @param parametros parámetros del formulario
     * @return vehículo validado
     * @throws DatoInvalidoException si falta o es inválido algún dato
     */
    private Vehiculo construirVehiculo(Map<String, String> parametros)
            throws DatoInvalidoException {
        String identificador = Validador.validarTextoNoVacio(parametros.get("id"), "identificador");
        String marca = Validador.validarTextoNoVacio(parametros.get("marca"), "marca");
        String modelo = Validador.validarTextoNoVacio(parametros.get("modelo"), "modelo");
        Integer anio = Validador.parsearEntero(parametros.get("anio"), "anio");
        String color = Validador.validarTextoNoVacio(parametros.get("color"), "color");
        EstadoVehiculo estado = Validador.parsearEnum(parametros.get("estado"), EstadoVehiculo.class);
        Double kilometraje = estado == EstadoVehiculo.NUEVO
                ? 0.0
                : Validador.parsearDouble(parametros.get("kilometraje"), "kilometraje");
        Double precio = Validador.parsearDouble(parametros.get("precio"), "precio");

        TipoVehiculo tipo = Validador.parsearEnum(parametros.get("tipo"), TipoVehiculo.class);
        Vehiculo vehiculo = switch (tipo) {
            case AUTOMOVIL -> new Automovil(identificador, marca, modelo, anio, color, estado,
                    kilometraje, precio,
                    Validador.parsearEnum(parametros.get("carroceria"), TipoCarroceria.class),
                    Validador.parsearEntero(parametros.get("puertas"), "puertas"));
            case CAMIONETA -> new Camioneta(identificador, marca, modelo, anio, color, estado,
                    kilometraje, precio,
                    Validador.parsearEnum(parametros.get("cabina"), TipoCabina.class),
                    Validador.parsearEnum(parametros.get("traccion"), TipoTraccion.class),
                    Validador.parsearDouble(parametros.get("capacidadCarga"), "capacidadCarga"));
            case MOTOCICLETA -> new Motocicleta(identificador, marca, modelo, anio, color, estado,
                    kilometraje, precio,
                    Validador.parsearEnum(parametros.get("estilo"), EstiloMotocicleta.class),
                    Validador.parsearEntero(parametros.get("cilindrada"), "cilindrada"));
        };
        vehiculo.validar();
        return vehiculo;
    }

    /**
     * Convierte un parámetro opcional a enum.
     *
     * @param valor     texto del parámetro, puede ser {@code null} o vacío
     * @param tipoEnum  clase del enum
     * @param <E>       tipo del enum
     * @return constante del enum, o {@code null} si el parámetro está ausente
     * @throws DatoInvalidoException si el texto no es una constante válida
     */
    private <E extends Enum<E>> E enumOpcional(String valor, Class<E> tipoEnum)
            throws DatoInvalidoException {
        return valor == null || valor.isBlank() ? null : Validador.parsearEnum(valor, tipoEnum);
    }

    /**
     * Sirve un archivo estático protegiendo contra path traversal.
     *
     * @param ruta ruta solicitada
     * @return respuesta HTTP con el archivo o 404
     * @throws IOException si falla la lectura
     */
    private Respuesta estatico(String ruta) throws IOException {
        String nombre = ruta.equals("/") ? "index.html" : ruta.substring(1);
        Path archivo = raizEstatica.resolve(nombre).normalize();
        if (!archivo.startsWith(raizEstatica) || !Files.isRegularFile(archivo)) {
            return new Respuesta(404, Json.error("Recurso no encontrado."), TIPO_JSON);
        }
        String contenido = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
        return new Respuesta(200, contenido, tipoContenido(archivo));
    }

    /**
     * Devuelve el tipo MIME según la extensión del archivo.
     *
     * @param archivo archivo solicitado
     * @return tipo de contenido HTTP
     */
    private String tipoContenido(Path archivo) {
        String nombre = archivo.getFileName().toString().toLowerCase();
        if (nombre.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (nombre.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (nombre.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (nombre.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }

    /**
     * Analiza un cuerpo con formato application/x-www-form-urlencoded.
     *
     * @param texto texto a analizar
     * @return mapa de parámetros
     */
    private Map<String, String> parsearFormulario(String texto) {
        Map<String, String> parametros = new LinkedHashMap<>();
        if (texto == null || texto.isEmpty()) {
            return parametros;
        }
        for (String pareja : texto.split("&")) {
            if (pareja.isEmpty()) {
                continue;
            }
            String[] partes = pareja.split("=", 2);
            try {
                String clave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
                String valor = partes.length > 1
                        ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8)
                        : "";
                parametros.put(clave, valor);
            } catch (IllegalArgumentException ignorado) {
                // Se ignora un parámetro mal codificado; el resto sigue siendo válido.
            }
        }
        return parametros;
    }

    /**
     * Escribe la respuesta HTTP con cabeceras y cuerpo.
     *
     * @param salida   flujo de salida del socket
     * @param respuesta respuesta a enviar
     * @throws IOException si falla la escritura
     */
    private void escribir(OutputStream salida, Respuesta respuesta) throws IOException {
        byte[] cuerpo = respuesta.contenido.getBytes(StandardCharsets.UTF_8);
        String cabeceras = "HTTP/1.1 " + respuesta.codigo + " "
                + razon(respuesta.codigo) + "\r\n"
                + "Content-Type: " + respuesta.tipo + "\r\n"
                + "Content-Length: " + cuerpo.length + "\r\n"
                + "Cache-Control: no-store\r\n"
                + "Connection: close\r\n\r\n";
        salida.write(cabeceras.getBytes(StandardCharsets.US_ASCII));
        salida.write(cuerpo);
        salida.flush();
    }

    /**
     * Devuelve el texto descriptivo de un código HTTP.
     *
     * @param codigo código HTTP
     * @return razón del código
     */
    private String razon(Integer codigo) {
        return switch (codigo) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            default -> "Internal Server Error";
        };
    }

    /**
     * Punto de entrada del servidor web.
     *
     * @param args argumentos opcionales: puerto y directorio de archivos estáticos
     */
    public static void main(String[] args) {
        Integer puerto = PUERTO_DEFECTO;
        Path estatico = ESTATICO_DEFECTO;
        try {
            if (args.length > 0) {
                puerto = Integer.valueOf(args[0]);
            }
            if (args.length > 1) {
                estatico = Paths.get(args[1]);
            }
            new ServidorWeb(puerto, estatico).iniciar();
        } catch (NumberFormatException error) {
            System.out.println("El puerto debe ser un número entero.");
        } catch (DatoInvalidoException | IOException error) {
            System.out.println("No se pudo iniciar el servidor: " + error.getMessage());
        }
    }

    /**
     * Respuesta HTTP simple.
     */
    private static final class Respuesta {

        /** Código HTTP. */
        private final Integer codigo;

        /** Contenido de la respuesta. */
        private final String contenido;

        /** Tipo de contenido. */
        private final String tipo;

        /**
         * Crea una respuesta.
         *
         * @param codigo    código HTTP
         * @param contenido cuerpo de la respuesta
         * @param tipo      tipo de contenido
         */
        private Respuesta(Integer codigo, String contenido, String tipo) {
            this.codigo = codigo;
            this.contenido = contenido;
            this.tipo = tipo;
        }
    }
}
