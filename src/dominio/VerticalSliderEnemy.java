package dominio;

/**
 * Enemigo deslizador vertical (Tipo V).
 *
 * <p>Se mueve exclusivamente en sentido vertical y rebota al tocar
 * los bordes superior e inferior del mapa. Es semánticamente distinto
 * de {@link BasicEnemy} con dirección VERTICAL: esta clase representa
 * explícitamente el "Tipo V" del enunciado del juego, facilitando
 * su identificación en el código y en los archivos de configuración.</p>
 *
 * <p>En los archivos {@code .txt} de nivel se define con la palabra
 * clave {@code ENEMY VERTICAL} (en lugar de {@code ENEMY BASIC ... VERTICAL}).</p>
 *
 * <pre>
 * Ejemplo en level.txt:
 *   ENEMY VERTICAL 300 100 18 3.5
 * </pre>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-20
 */
public class VerticalSliderEnemy extends BasicEnemy {

    /**
     * Construye un deslizador vertical.
     *
     * @param x      coordenada X fija (no cambia — solo se mueve en Y).
     * @param y      coordenada Y inicial.
     * @param radius radio en píxeles.
     * @param speed  velocidad vertical en píxeles/tick (positivo = hacia abajo).
     */
    public VerticalSliderEnemy(double x, double y, int radius, double speed) {
        super(x, y, radius, Direction.VERTICAL, speed);
    }
}
