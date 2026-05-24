package dominio;

import java.awt.Rectangle;

/**
 * Gestiona las colisiones entre entidades del juego.
 *
 * <p>Detecta si dos objetos {@link Collidable} se superponen
 * comparando sus bounding-boxes.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-14
 */
public class CollisionManager {

    /**
     * Comprueba si dos objetos colisionables se intersectan.
     *
     * @param a primer objeto.
     * @param b segundo objeto.
     * @return {@code true} si sus bounding-boxes se solapan.
     */
    public boolean collides(Collidable a, Collidable b) {
        Rectangle ra = a.getBounds();
        Rectangle rb = b.getBounds();
        return ra.intersects(rb);
    }
}
