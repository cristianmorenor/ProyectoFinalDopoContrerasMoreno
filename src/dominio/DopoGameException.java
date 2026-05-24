package dominio;

/**
 * Excepción única del juego The DOPO Hardest Game.
 *
 * <p>Usa constantes de mensaje para identificar cada tipo de error,
 * siguiendo un patrón de excepción plana (sin subclases anidadas).
 * Esto permite capturar cualquier error del juego con un único
 * bloque {@code catch (DopoGameException e)}.</p>
 *
 * <pre>
 *   try {
 *       // operación del juego
 *   } catch (DopoGameException e) {
 *       ErrorLogger.getInstance().log(e);
 *   }
 * </pre>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 3.0
 * @since 2026-05-20
 */
public class DopoGameException extends Exception {

    /** Error al leer o interpretar un archivo de configuración de nivel. */
    public static final String INVALID_CONFIG = "Error en archivo de configuración";

    /** Error al guardar o cargar el estado de una partida. */
    public static final String GAME_STATE_ERROR = "Error al guardar/cargar partida";

    /** Se encontró un tipo de enemigo no reconocido en la configuración. */
    public static final String UNKNOWN_ENEMY = "Tipo de enemigo desconocido";

    /**
     * Construye la excepción con un mensaje descriptivo.
     *
     * @param msg descripción del error ocurrido.
     */
    public DopoGameException(String msg) {
        super(msg);
    }

    /**
     * Construye la excepción con un mensaje y la causa original.
     *
     * @param msg   descripción del error ocurrido.
     * @param cause excepción que originó este error.
     */
}
