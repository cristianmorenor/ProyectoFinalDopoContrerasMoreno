package dominio;

/**
 * Enemigo acelerado: se mueve igual que {@link BasicEnemy} pero al doble
 * de velocidad, lo que lo hace considerablemente más peligroso.
 *
 * <p>El modificador {@link #SPEED_MULTIPLIER} está declarado como constante
 * para facilitar su ajuste en futuras versiones.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class FastEnemy extends BasicEnemy {

    /** Factor de multiplicación de velocidad respecto al enemigo básico. */
    public static final double SPEED_MULTIPLIER = 2.0;

    /**
     * Construye un enemigo rápido: la velocidad base se duplica
     * automáticamente.
     *
     * @param x         coordenada X inicial del centro.
     * @param y         coordenada Y inicial del centro.
     * @param radius    radio en píxeles.
     * @param direction dirección de movimiento.
     * @param baseSpeed velocidad base antes de multiplicar.
     */
    public FastEnemy(double x, double y, int radius,
                     Direction direction, double baseSpeed) {
        super(x, y, radius, direction, baseSpeed * SPEED_MULTIPLIER);
    }
}
