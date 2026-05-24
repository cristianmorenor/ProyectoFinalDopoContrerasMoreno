package dominio;

import java.awt.Rectangle;

/**
 * Fuente de vida: elemento especial que otorga una vida adicional al jugador
 * que la toca.
 *
 * <p>Una vez recogida desaparece del mapa (se marca como {@code collected}).
 * Implementa {@link Collectible} para integrarse con el sistema de
 * recolección del motor.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class LifeSource implements Collidable, Collectible {

    /** Coordenada X del centro. */
    private final int x;

    /** Coordenada Y del centro. */
    private final int y;

    /** Radio del icono de vida en píxeles. */
    private static final int RADIUS = 12;

    /** {@code true} si ya fue recogida. */
    private boolean collected;

    /**
     * Construye una fuente de vida en la posición indicada.
     *
     * @param x coordenada X del centro.
     * @param y coordenada Y del centro.
     */
    public LifeSource(int x, int y) {
        this.x         = x;
        this.y         = y;
        this.collected = false;
    }

    /**
     * Marca la fuente de vida como recogida.
     */
    @Override
    public void collect() {
        this.collected = true;
    }

    /**
     * Reinicia la fuente de vida a su estado inicial (disponible).
     */
    @Override
    public void reset() {
        this.collected = false;
    }

    /**
     * Indica si ya fue recogida.
     *
     * @return {@code true} si fue recolectada.
     */
    @Override
    public boolean isCollected() { return collected; }

    /**
     * Devuelve el rectángulo de colisión de la fuente de vida.
     *
     * @return {@link Rectangle} de colisión.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
    }

    /** @return coordenada X del centro. */
    public int getX() { return x; }

    /** @return coordenada Y del centro. */
    public int getY() { return y; }

    /** @return radio del elemento. */
    public int getRadius() { return RADIUS; }
}
