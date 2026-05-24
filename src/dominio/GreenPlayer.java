package dominio;

/**
 * Jugador con skin verde: resiste el primer golpe de un enemigo.
 *
 * <p>Mecánica especial (Template Method via {@link #onHit()}):</p>
 * <ol>
 *   <li>Al recibir el primer impacto, el escudo se consume y
 *       la velocidad se reduce a la mitad.</li>
 *   <li>A partir del segundo impacto, muere normalmente.</li>
 * </ol>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-19
 */
public class GreenPlayer extends Player {

    /** Tamaño del jugador verde en píxeles. */
    private static final int DEFAULT_SIZE  = 24;

    /** Velocidad normal del jugador verde. */
    private static final double DEFAULT_SPEED = 3.0;

    /** Factor de reducción de velocidad tras recibir el primer golpe. */
    private static final double SPEED_PENALTY = 0.5;

    /** {@code true} si el escudo todavía está activo. */
    private boolean shieldActive;

    /**
     * Construye un jugador verde con el escudo inicial activado.
     *
     * @param x coordenada X inicial.
     * @param y coordenada Y inicial.
     */
    public GreenPlayer(double x, double y) {
        super(x, y, DEFAULT_SIZE, DEFAULT_SPEED, SkinType.GREEN);
        this.shieldActive = true;
    }

    /**
     * Gestiona el impacto de un enemigo.
     *
     * <p>Si el escudo está activo lo consume y reduce la velocidad;
     * de lo contrario retorna {@code false} para que el llamador
     * registre la muerte.</p>
     *
     * @return {@code true} si el escudo absorbió el golpe
     *         (el jugador NO muere), {@code false} si debe morir.
     */
    public boolean onHit() {
        if (shieldActive) {
            shieldActive = false;
            speed *= SPEED_PENALTY;
            return true;   // golpe absorbido
        }
        return false;      // sin escudo → muere
    }

    /**
     * Reinicia el jugador y restaura el escudo y la velocidad original.
     */
    @Override
    public void reset() {
        super.reset();
        shieldActive = true;
        speed = DEFAULT_SPEED;
    }

    /**
     * Indica si el escudo todavía protege al jugador.
     *
     * @return {@code true} si el escudo está activo.
     */
    public boolean isShieldActive() {
        return shieldActive;
    }
}
