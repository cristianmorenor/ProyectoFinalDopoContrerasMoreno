package dominio;

import java.awt.Rectangle;

/**
 * Zona segura donde el jugador no puede ser golpeado por enemigos.
 *
 * <p><b>EN CONSTRUCCIÓN</b> – Las zonas seguras protegen al jugador
 * al inicio y al cruzar checkpoints. Funcionalidad completa en futuras entregas.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-14
 */
public class SafeZone {

    /** Posición X de la esquina superior izquierda. */
    private final int x;

    /** Posición Y de la esquina superior izquierda. */
    private final int y;

    /** Ancho de la zona segura en píxeles. */
    private final int width;

    /** Alto de la zona segura en píxeles. */
    private final int height;

    /**
     * Construye una zona segura con la posición y dimensiones indicadas.
     *
     * @param x      coordenada X.
     * @param y      coordenada Y.
     * @param width  ancho en píxeles.
     * @param height alto en píxeles.
     */
    public SafeZone(int x, int y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    /**
     * Comprueba si un rectángulo de colisión está dentro de la zona segura.
     *
     * @param bounds rectángulo de colisión a comprobar.
     * @return {@code true} si hay intersección con la zona segura.
     */
    public boolean contains(Rectangle bounds) {
        return getBounds().intersects(bounds);
    }

    /**
     * Devuelve el rectángulo de colisión de la zona segura.
     *
     * @return {@link Rectangle} de la zona segura.
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
