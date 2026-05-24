package persistencia;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger centralizado de errores del juego.
 *
 * <p><b>Patrón Singleton:</b> existe una única instancia en toda la aplicación,
 * garantizando que todos los errores se registren en el mismo archivo.</p>
 *
 * <p>Los errores se escriben en {@value #LOG_FILE} en formato:
 * <pre>
 *   [2026-05-19 16:30:01] ERROR: Descripción del error
 * </pre></p>
 *
 * <p>Ejemplo de uso:
 * <pre>
 *     ErrorLogger.getInstance().log("Error al cargar el nivel");
 *     ErrorLogger.getInstance().log(exception);
 * </pre></p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class ErrorLogger {

    /** Ruta del archivo de log de errores. */
    private static final String LOG_FILE = "dopo_errors.log";

    /** Formato de fecha y hora para cada entrada del log. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Instancia única del Singleton. */
    private static ErrorLogger instance;

    /**
     * Constructor privado: impide instanciación directa.
     */
    private ErrorLogger() {}

    /**
     * Retorna la instancia única del logger.
     *
     * @return instancia de {@code ErrorLogger}.
     */
    public static synchronized ErrorLogger getInstance() {
        if (instance == null) {
            instance = new ErrorLogger();
        }
        return instance;
    }

    /**
     * Registra un mensaje de error en el archivo de log.
     *
     * @param message descripción del error a registrar.
     */
    public void log(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = "[" + timestamp + "] ERROR: " + message;
        System.err.println(entry);
        writeToFile(entry);
    }

    /**
     * Registra una excepción completa (mensaje + stack trace) en el log.
     *
     * @param e excepción a registrar.
     */
    public void log(Exception e) {
        log(e.getClass().getSimpleName() + ": " + e.getMessage());
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            e.printStackTrace(pw);
        } catch (IOException ignored) {
            System.err.println("No se pudo escribir el stack trace en " + LOG_FILE);
        }
    }

    /**
     * Registra una advertencia (no es un error crítico).
     *
     * @param message descripción de la advertencia.
     */
    public void warn(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = "[" + timestamp + "] WARN:  " + message;
        System.err.println(entry);
        writeToFile(entry);
    }

    /**
     * Escribe una línea en el archivo de log, en modo append.
     *
     * @param line línea a escribir.
     */
    private void writeToFile(String line) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(line + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("No se pudo escribir en " + LOG_FILE + ": " + e.getMessage());
        }
    }
}
