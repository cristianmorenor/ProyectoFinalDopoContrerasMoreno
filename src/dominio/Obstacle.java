package dominio;

import java.awt.Rectangle;

/**
 * Representa un obstáculo/enemigo en The World's Hardest Game.
 *
 * <p>Los obstáculos son círculos azules que se mueven de forma oscilatoria
 * en dirección horizontal o vertical. Si el jugador colisiona con uno,
 * pierde una vida y vuelve al inicio.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-03
 */
public class Obstacle implements Collidable, Movable {

    /**
     * Direcciones de movimiento disponibles para un obstáculo.
     */
    public enum Direction {
        /** El obstáculo se desplaza horizontalmente. */
        HORIZONTAL,
        /** El obstáculo se desplaza verticalmente. */
        VERTICAL
    }

    /** Radio del círculo del obstáculo en píxeles. */
    private final int radius;

    /** Dirección de movimiento del obstáculo. */
    private final Direction direction;

    /** Posición X actual del centro del obstáculo. */
    private double x;

    /** Posición Y actual del centro del obstáculo. */
    private double y;

    /** Velocidad (puede ser negativa para indicar dirección contraria). */
    private double speed;

    /**
     * Construye un obstáculo con posición, tamaño, dirección y velocidad
     * iniciales.
     *
     * @param x         coordenada X inicial del centro.
     * @param y         coordenada Y inicial del centro.
     * @param radius    radio en píxeles.
     * @param direction dirección de movimiento.
     * @param speed     velocidad en píxeles/tick (negativo = dirección inversa).
     */
    public Obstacle(double x, double y, int radius,
                    Direction direction, double speed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.direction = direction;
        this.speed = speed;
    }

    /**
     * Actualiza la posición del obstáculo y rebota en los bordes del mapa.
     *
     * @param mapW ancho del mapa en píxeles.
     * @param mapH alto del mapa en píxeles.
     */
    @Override
    public void update(int mapW, int mapH) {
        if (direction == Direction.HORIZONTAL) {
            x += speed;
            if (x - radius < 0 || x + radius > mapW) {
                speed = -speed;
                x = Math.max(radius, Math.min(mapW - radius, x));
            }
        } else {
            y += speed;
            if (y - radius < 0 || y + radius > mapH) {
                speed = -speed;
                y = Math.max(radius, Math.min(mapH - radius, y));
            }
        }
    }

    /**
     * Devuelve el rectángulo de colisión del obstáculo (bounding box).
     *
     * @return {@link Rectangle} que encierra al círculo.
     */
    public Rectangle getBounds() {
        return new Rectangle((int)(x - radius), (int)(y - radius),
                radius * 2, radius * 2);
    }

    /** @return posición X del centro. */
    public double getX() { return x; }

    /** @return posición Y del centro. */
    public double getY() { return y; }

    /** @return radio del obstáculo. */
    public int getRadius() { return radius; }

    /** @return dirección de movimiento. */
    public Direction getDirection() { return direction; }
}
