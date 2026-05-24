package dominio;

import java.awt.Rectangle;

/**
 * Bomba: elemento especial que al activarse destruye a todos los jugadores
 * y enemigos dentro de su radio de explosión.
 *
 * <p>Una bomba puede ser activada por el contacto de un jugador. El radio
 * de explosión ({@link #EXPLOSION_RADIUS}) es mayor que el radio visual
 * de la bomba ({@link #RADIUS}).</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class Bomb implements Collidable {

    /** Radio visual de la bomba sin explotar. */
    private static final int RADIUS = 14;

    /** Radio de la explosión (mayor que el radio visual). */
    public static final int EXPLOSION_RADIUS = 60;

    /** Coordenada X del centro. */
    private final int x;

    /** Coordenada Y del centro. */
    private final int y;

    /** {@code true} si la bomba ya ha explotado. */
    private boolean exploded;

    /**
     * Construye una bomba en la posición indicada.
     *
     * @param x coordenada X del centro.
     * @param y coordenada Y del centro.
     */
    public Bomb(int x, int y) {
        this.x        = x;
        this.y        = y;
        this.exploded = false;
    }

    /**
     * Activa la explosión de la bomba.
     * Después de llamar a este método, {@link #isExploded()} retorna {@code true}.
     */
    public void explode() {
        this.exploded = true;
    }

    /**
     * Reinicia la bomba a su estado inicial (sin explotar).
     */
    public void reset() {
        this.exploded = false;
    }

    /**
     * Devuelve el rectángulo de colisión de la bomba (zona de contacto).
     *
     * @return {@link Rectangle} de colisión.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
    }

    /**
     * Devuelve el rectángulo de la zona de explosión.
     *
     * @return {@link Rectangle} del radio de explosión.
     */
    public Rectangle getExplosionBounds() {
        return new Rectangle(x - EXPLOSION_RADIUS, y - EXPLOSION_RADIUS,
                             EXPLOSION_RADIUS * 2, EXPLOSION_RADIUS * 2);
    }

    /** @return {@code true} si la bomba ya explotó. */
    public boolean isExploded() { return exploded; }

    /** @return coordenada X del centro. */
    public int getX() { return x; }

    /** @return coordenada Y del centro. */
    public int getY() { return y; }

    /** @return radio visual. */
    public int getRadius() { return RADIUS; }
}
