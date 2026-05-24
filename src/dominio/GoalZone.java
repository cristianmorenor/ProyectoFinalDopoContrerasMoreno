package dominio;

import java.awt.Rectangle;

/**
 * Zona de meta: el jugador debe llegar aquí para completar el nivel.
 *
 * <p>La meta solo se considera alcanzada cuando {@link Level#allCoinsCollected()}
 * retorna {@code true}. Si aún quedan monedas, visualmente aparece bloqueada.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-19
 */
public class GoalZone implements Collidable {

    /** Coordenada X de la esquina superior izquierda. */
    private final int x;

    /** Coordenada Y de la esquina superior izquierda. */
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
    public GoalZone(int x, int y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    /**
     * Comprueba si el jugador ha alcanzado la zona de meta.
     *
     * @param playerBounds rectángulo de colisión del jugador.
     * @return {@code true} si el jugador está dentro de la zona.
     */
    public boolean isReached(Rectangle playerBounds) {
        return getBounds().intersects(playerBounds);
    }

    /**
     * Devuelve el rectángulo de colisión de la meta.
     *
     * @return {@link Rectangle} de la zona de meta.
     */
    @Override
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
