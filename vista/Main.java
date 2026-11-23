package vista;

/**
 * Punto de entrada de la aplicación.
 */
public final class Main {

    /** Constructor privado de clase de arranque. */
    private Main() {
    }

    /**
     * Arranca la interfaz de consola de la concesionaria.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        try {
            new ConsolaApp().iniciar();
        } catch (RuntimeException error) {
            System.out.println("Error fatal controlado: " + error.getMessage());
        }
    }
}
