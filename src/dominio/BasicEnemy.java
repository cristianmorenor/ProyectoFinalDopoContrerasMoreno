package dominio;

/**
 * Enemigo básico: se mueve horizontal o verticalmente y rebota en los bordes.
 *
 * <p>Es el tipo de enemigo más simple y el más frecuente en los niveles.
 * Su velocidad puede ser positiva (derecha/abajo) o negativa (izquierda/arriba).</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class BasicEnemy extends Enemy {

    /**
     * Dirección de desplazamiento del enemigo.
     */
    public enum Direction { HORIZONTAL, VERTICAL }

    /** Dirección de movimiento de este enemigo. */
    private final Direction direction;

    /**
     * Construye un enemigo básico.
     *
     * @param x         coordenada X inicial del centro.
     * @param y         coordenada Y inicial del centro.
     * @param radius    radio en píxeles.
     * @param direction dirección de movimiento.
     * @param speed     velocidad (negativa invierte la dirección inicial).
     */
    public BasicEnemy(double x, double y, int radius,
                      Direction direction, double speed) {
        super(x, y, radius, speed);
        this.direction = direction;
    }

    /**
     * Mueve el enemigo según su dirección y rebota al tocar los bordes del mapa.
     *
     * @param mapW ancho del área de juego.
     * @param mapH alto del área de juego.
     */
    @Override
    public void update(int mapW, int mapH) {
        if (direction == Direction.HORIZONTAL) {
            x += speed;
            if (x - radius < 0 || x + radius > mapW) {
                speed = -speed;
                x = Math.max(radius, Math.min(mapW - radius, x));
            }
        } else {
            y += speed;
            if (y - radius < 0 || y + radius > mapH) {
                speed = -speed;
                y = Math.max(radius, Math.min(mapH - radius, y));
            }
        }
    }

    /** @return dirección de movimiento. */
    public Direction getDirection() { return direction; }
}
