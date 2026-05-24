package dominio;

/**
 * Jugador con skin rojo: velocidad normal, tamaño normal.
 *
 * <p>Es el personaje base del juego. Sin habilidades especiales.
 * Extiende {@link Player} usando los valores estándar.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-19
 */
public class RedPlayer extends Player {

    /** Tamaño del jugador rojo en píxeles. */
    private static final int DEFAULT_SIZE  = 24;

    /** Velocidad del jugador rojo en píxeles por tick. */
    private static final double DEFAULT_SPEED = 3.0;

    /**
     * Construye un jugador rojo en la posición indicada.
     *
     * @param x coordenada X inicial.
     * @param y coordenada Y inicial.
     */
    public RedPlayer(double x, double y) {
        super(x, y, DEFAULT_SIZE, DEFAULT_SPEED, SkinType.RED);
    }
}
