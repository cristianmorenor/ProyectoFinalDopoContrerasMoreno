package dominio;

import java.awt.Rectangle;

/**
 * Clase abstracta base para todos los enemigos del juego.
 *
 * <p>Define el contrato común: posición, radio, velocidad y actualización
 * de movimiento. Cada subclase implementa su propio patrón de desplazamiento
 * sobreescribiendo {@link #update(int, int)}.</p>
 *
 * <p><b>Patrón Template Method:</b> el esquema de actualización
 * está fijado aquí; el <em>cómo</em> se mueve queda en la subclase.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public abstract class Enemy implements Collidable, Movable {

    /** Posición X del centro del enemigo. */
    protected double x;

    /** Posición Y del centro del enemigo. */
    protected double y;

    /** Radio del enemigo en píxeles. */
    protected final int radius;

    /** Velocidad base del enemigo. */
    protected double speed;

    /**
     * Construye un enemigo con posición, radio y velocidad iniciales.
     *
     * @param x      coordenada X inicial del centro.
     * @param y      coordenada Y inicial del centro.
     * @param radius radio en píxeles.
     * @param speed  velocidad base en píxeles/tick.
     */
    public Enemy(double x, double y, int radius, double speed) {
        this.x      = x;
        this.y      = y;
        this.radius = radius;
        this.speed  = speed;
    }

    /**
     * Devuelve el rectángulo de colisión del enemigo (bounding box).
     *
     * @return {@link Rectangle} que encierra al círculo del enemigo.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)(x - radius), (int)(y - radius),
                             radius * 2, radius * 2);
    }

    /** @return posición X del centro. */
    public double getX() { return x; }

    /** @return posición Y del centro. */
    public double getY() { return y; }

    /** @return radio del enemigo. */
    public int getRadius() { return radius; }

    /** @return velocidad base actual (incluye signo = dirección). */
    public double getSpeed() { return speed; }

    /**
     * Establece la coordenada X del enemigo.
     * Usado por {@code GameSaver} al restaurar el estado de la partida.
     *
     * @param x nueva posición X del centro.
     */
    public void setX(double x) { this.x = x; }

    /**
     * Establece la coordenada Y del enemigo.
     * Usado por {@code GameSaver} al restaurar el estado de la partida.
     *
     * @param y nueva posición Y del centro.
     */
    public void setY(double y) { this.y = y; }

    /**
     * Establece la velocidad del enemigo (el signo codifica la dirección).
     * Usado por {@code GameSaver} al restaurar el estado de la partida.
     *
     * @param speed nueva velocidad (con signo).
     */
    public void setSpeed(double speed) { this.speed = speed; }
}
