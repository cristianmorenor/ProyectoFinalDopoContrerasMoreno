package dominio;

import java.awt.Rectangle;

/**
 * Pared estática que ni jugadores ni enemigos pueden atravesar.
 *
 * <p>Las paredes se definen en los archivos de configuración (.txt) y
 * son cargadas por {@code persistencia.MapLoader}. El motor de colisiones
 * las trata igual que los bordes del mapa.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class Wall implements Collidable {

    /** Coordenada X de la esquina superior izquierda. */
    private final int x;

    /** Coordenada Y de la esquina superior izquierda. */
    private final int y;

    /** Ancho de la pared en píxeles. */
    private final int width;

    /** Alto de la pared en píxeles. */
    private final int height;

    /**
     * Construye una pared en la posición y dimensiones indicadas.
     *
     * @param x      coordenada X.
     * @param y      coordenada Y.
     * @param width  ancho en píxeles.
     * @param height alto en píxeles.
     */
    public Wall(int x, int y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    /**
     * Devuelve el rectángulo de colisión de la pared.
     *
     * @return {@link Rectangle} de colisión.
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
