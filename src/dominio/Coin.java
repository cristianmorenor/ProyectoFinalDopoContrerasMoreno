package dominio;

import java.awt.Rectangle;

/**
 * Representa una moneda coleccionable en el mapa de juego.
 *
 * <p>El jugador debe recolectar todas las monedas antes de poder
 * completar el nivel alcanzando la meta.</p>
 *
 * <p><b>Patrón Template Method:</b> el método {@link #applyEffect(Player)}
 * es abstracto y debe ser sobreescrito por cada subclase para definir
 * el efecto específico al ser recolectada.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 3.0
 * @since 2026-05-03
 */
public abstract class Coin implements Collidable, Collectible {

    /** Coordenada X del centro de la moneda. */
    private final int x;

    /** Coordenada Y del centro de la moneda. */
    private final int y;

    /** Radio de la moneda en pixeles. */
    private static final int RADIUS = 10;

    /** Indica si la moneda ya fue recolectada. */
    private boolean collected;

    /**
     * Construye una moneda en la posicion indicada.
     *
     * @param x coordenada X del centro.
     * @param y coordenada Y del centro.
     */
    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
        this.collected = false;
    }

    /**
     * Aplica el efecto específico de esta moneda al jugador que la recoge.
     *
     * <p>Cada subclase sobreescribe este método para definir su comportamiento:</p>
     * <ul>
     *   <li>{@link NormalCoin} — sin efecto adicional.</li>
     *   <li>{@link SkinCoin} — cambia el skin del jugador.</li>
     * </ul>
     *
     * @param player el jugador que recogió la moneda.
     */
    public abstract void applyEffect(Player player);

    /**
     * Marca la moneda como recolectada.
     */
    @Override
    public void collect() {
        this.collected = true;
    }

    /**
     * Reinicia la moneda a su estado inicial (no recolectada).
     */
    @Override
    public void reset() {
        this.collected = false;
    }

    /**
     * Devuelve el rectangulo de colision de la moneda (bounding box).
     *
     * @return Rectangle que encierra a la moneda.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
    }

    /** @return true si la moneda ya fue recolectada. */
    @Override
    public boolean isCollected() { return collected; }

    /** @return coordenada X del centro. */
    public int getX() { return x; }

    /** @return coordenada Y del centro. */
    public int getY() { return y; }

    /** @return radio de la moneda. */
    public int getRadius() { return RADIUS; }
}
