package util;

import excepciones.DatoInvalidoException;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utilidad de entrada de datos por consola.
 *
 * <p><b>Remarks:</b> todas las lecturas se hacen con {@link Scanner#nextLine()}
 * y conversión posterior, de modo que no quedan restos de salto de línea en el
 * buffer (el clásico error al mezclar {@code nextInt()} con {@code nextLine()}).
 * Los bucles repiten la pregunta hasta obtener un dato válido y nunca abortan
 * el programa ante un {@link NumberFormatException} o
 * {@link InputMismatchException}.</p>
 */
public class LecturaConsola {

    /** Lector asociado a la entrada estándar. */
    private final Scanner scanner;

    /**
     * Crea el lector usando {@link System#in}.
     */
    public LecturaConsola() {
        this(new Scanner(System.in));
    }

    /**
     * Crea el lector con un {@link Scanner} inyectado (útil para pruebas).
     *
     * @param scanner lector a utilizar
     */
    public LecturaConsola(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Solicita un texto no vacío hasta que el usuario lo ingrese correctamente.
     *
     * @param mensaje mensaje de petición
     * @return texto validado y recortado
     */
    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine();
            try {
                return Validador.validarTextoNoVacio(entrada, mensaje);
            } catch (DatoInvalidoException error) {
                System.out.println("  ! " + error.getMessage());
            }
        }
    }

    /**
     * Solicita un número entero dentro de un rango inclusivo.
     *
     * @param mensaje mensaje de petición
     * @param minimo  valor mínimo aceptado
     * @param maximo  valor máximo aceptado
     * @return entero dentro del rango
     */
    public Integer leerEntero(String mensaje, Integer minimo, Integer maximo) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine();
            try {
                Integer valor = Validador.parsearEntero(entrada, mensaje);
                if (valor < minimo || (maximo != null && valor > maximo)) {
                    System.out.println("  ! Ingrese un valor entre " + minimo
                            + (maximo == null ? " y superior." : " y " + maximo + "."));
                    continue;
                }
                return valor;
            } catch (DatoInvalidoException | InputMismatchException error) {
                System.out.println("  ! Debe ingresar un número entero válido.");
            }
        }
    }

    /**
     * Solicita un número entero sin restricción de rango.
     *
     * @param mensaje mensaje de petición
     * @return entero leído
     */
    public Integer leerEntero(String mensaje) {
        return leerEntero(mensaje, Integer.MIN_VALUE, null);
    }

    /**
     * Solicita un número decimal dentro de un rango inclusivo.
     *
     * @param mensaje mensaje de petición
     * @param minimo  valor mínimo aceptado
     * @param maximo  valor máximo aceptado, o {@code null} si no hay límite superior
     * @return decimal dentro del rango
     */
    public Double leerDouble(String mensaje, Double minimo, Double maximo) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine();
            try {
                Double valor = Validador.parsearDouble(entrada, mensaje);
                if (valor < minimo || (maximo != null && valor > maximo)) {
                    System.out.println("  ! Ingrese un valor entre " + minimo
                            + (maximo == null ? " y superior." : " y " + maximo + "."));
                    continue;
                }
                return valor;
            } catch (DatoInvalidoException | InputMismatchException error) {
                System.out.println("  ! Debe ingresar un número decimal válido (use . o ,).");
            }
        }
    }

    /**
     * Muestra los valores de un enum numerados y solicita una opción válida.
     *
     * @param mensaje mensaje de petición
     * @param tipoEnum clase del enum
     * @param <E>      tipo del enum
     * @return constante seleccionada
     */
    public <E extends Enum<E>> E leerEnum(String mensaje, Class<E> tipoEnum) {
        E[] valores = tipoEnum.getEnumConstants();
        System.out.println(mensaje);
        for (int i = 0; i < valores.length; i++) {
            System.out.println("    " + (i + 1) + ") " + valores[i].name());
        }
        Integer opcion = leerEntero("  Opción: ", 1, valores.length);
        return valores[opcion - 1];
    }

    /**
     * Solicita una respuesta sí/no.
     *
     * @param mensaje mensaje de petición
     * @return {@code true} para sí, {@code false} para no
     */
    public Boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (s/n): ").toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            System.out.println("  ! Responda 's' o 'n'.");
        }
    }

    /**
     * Descarta lo que quede de la línea actual del buffer de entrada.
     * Se conserva para integraciones que mezclen otros lectores; con este
     * diseño no es necesario llamarlo después de cada lectura.
     */
    public void limpiarBuffer() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
