package dominio;

import java.awt.Rectangle;

/**
 * Punto de control (checkpoint) del nivel.
 *
 * <p>Cuando el jugador entra en el área del checkpoint, se guarda su posición
 * actual como punto de respawn. Si muere, reaparece en el último checkpoint
 * activado (no en el inicio del nivel).</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-03
 */
public class CheckPoint implements Collidable {

    /** Coordenada X de la esquina superior izquierda del área de checkpoint. */
    private final int x;

    /** Coordenada Y de la esquina superior izquierda del área de checkpoint. */
    private final int y;

    /** Ancho del área de checkpoint en píxeles. */
    private final int width;

    /** Alto del área de checkpoint en píxeles. */
    private final int height;

    /** {@code true} si el checkpoint ya fue activado por al menos un jugador. */
    private boolean activated;

    /**
     * Construye un checkpoint con posición y dimensiones.
     *
     * @param x      coordenada X.
     * @param y      coordenada Y.
     * @param width  ancho en píxeles.
     * @param height alto en píxeles.
     */
    public CheckPoint(int x, int y, int width, int height) {
        this.x         = x;
        this.y         = y;
        this.width     = width;
        this.height    = height;
        this.activated = false;
    }

    /**
     * Activa el checkpoint y guarda la posición de respawn en el jugador.
     *
     * <p>Si el checkpoint ya estaba activado, no hace nada (evita
     * sobreescribir el checkpoint con el mismo punto).</p>
     *
     * @param player jugador que toca el checkpoint.
     */
    public void tryActivate(Player player) {
        if (!activated && getBounds().intersects(player.getBounds())) {
            activated = true;
            player.activateCheckpoint();
        }
    }

    /**
     * Reinicia el checkpoint a no activado.
     */
    public void reset() {
        activated = false;
    }

    /**
     * Devuelve el rectángulo de colisión del checkpoint.
     *
     * @return {@link Rectangle} del área de checkpoint.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** @return {@code true} si el checkpoint fue activado. */
    public boolean isActivated() { return activated; }

    /** @return coordenada X. */
    public int getX() { return x; }

    /** @return coordenada Y. */
    public int getY() { return y; }

    /** @return ancho. */
    public int getWidth() { return width; }

    /** @return alto. */
    public int getHeight() { return height; }
}
