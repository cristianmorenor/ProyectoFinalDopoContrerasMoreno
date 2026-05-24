package dominio;

import java.awt.Rectangle;

/**
 * Representa la zona de meta en un nivel de The World's Hardest Game.
 *
 * <p>La meta es un rectángulo verde ubicado al final del mapa.
 * Cuando el jugador llega a esta zona, el nivel se completa.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-03
 */
public class Goal {

    /** Posición X de la esquina superior izquierda de la meta. */
    private final int x;

    /** Posición Y de la esquina superior izquierda de la meta. */
    private final int y;

    /** Ancho de la zona de meta en píxeles. */
    private final int width;

    /** Alto de la zona de meta en píxeles. */
    private final int height;

    /**
     * Construye la zona de meta con su posición y dimensiones.
     *
     * @param x      coordenada X.
     * @param y      coordenada Y.
     * @param width  ancho en píxeles.
     * @param height alto en píxeles.
     */
    public Goal(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Comprueba si el jugador ha alcanzado la meta.
     *
     * @param playerBounds rectángulo de colisión del jugador.
     * @return {@code true} si hay intersección con la zona de meta.
     */
    public boolean isReached(Rectangle playerBounds) {
        return getBounds().intersects(playerBounds);
    }

    /**
     * Devuelve el rectángulo de colisión de la meta.
     *
     * @return {@link Rectangle} de la zona de meta.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** @return coordenada X. */
    public int getX() { return x; }

    /** @return coordenada Y. */
    public int getY() { return y; }

    /** @return ancho. */
    public int getWidth() { return width; }

    /** @return alto. */
    public int getHeight() { return height; }
}
