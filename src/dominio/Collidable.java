package dominio;

import java.awt.Rectangle;

/**
 * Representa cualquier elemento que puede colisionar.
 */
public interface Collidable {

    /**
     * Devuelve el área de colisión del objeto.
     *
     * @return rectángulo de colisión.
     */
    Rectangle getBounds();
}