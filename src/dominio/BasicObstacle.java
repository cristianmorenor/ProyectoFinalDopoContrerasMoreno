package dominio;

/**
 * Obstáculo básico de velocidad estándar.
 *
 * <p><b>EN CONSTRUCCIÓN</b> – Subclase de {@link Obstacle} con comportamiento
 * base. Se diferenciará de {@code FastObstacle} en entregas futuras.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-14
 */
public class BasicObstacle extends Obstacle {

    /**
     * Construye un obstáculo básico.
     *
     * @param x         coordenada X inicial del centro.
     * @param y         coordenada Y inicial del centro.
     * @param radius    radio en píxeles.
     * @param direction dirección de movimiento.
     * @param speed     velocidad en píxeles/tick.
     */
    public BasicObstacle(double x, double y, int radius,
                         Direction direction, double speed) {
        super(x, y, radius, direction, speed);
    }
}
