package dominio;

/**
 * Jugador con skin azul: más rápido y más grande que el rojo.
 *
 * <p>Su mayor tamaño lo hace más difícil de maniobrar en espacios estrechos,
 * pero su velocidad le permite escapar de los enemigos con más facilidad.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-19
 */
public class BluePlayer extends Player {

    /** Tamaño del jugador azul en píxeles (mayor que el rojo). */
    private static final int DEFAULT_SIZE  = 30;

    /** Velocidad del jugador azul (mayor que el rojo). */
    private static final double DEFAULT_SPEED = 4.5;

    /**
     * Construye un jugador azul en la posición indicada.
     *
     * @param x coordenada X inicial.
     * @param y coordenada Y inicial.
     */
    public BluePlayer(double x, double y) {
        super(x, y, DEFAULT_SIZE, DEFAULT_SPEED, SkinType.BLUE);
    }
}
