package dominio;

/**
 * Encapsula el estado de movimiento de un jugador en un tick dado.
 *
 * <p>La interfaz {@link AIPlayer} retorna un {@code InputState} con las
 * direcciones que la IA desea activar. Esto desacopla la lógica de IA
 * del sistema de teclado de la presentación.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class InputState {

    /** Mover hacia arriba. */
    public final boolean up;

    /** Mover hacia abajo. */
    public final boolean down;

    /** Mover hacia la izquierda. */
    public final boolean left;

    /** Mover hacia la derecha. */
    public final boolean right;

    /**
     * Construye un estado de entrada con los cuatro valores de dirección.
     *
     * @param up    moverse hacia arriba.
     * @param down  moverse hacia abajo.
     * @param left  moverse a la izquierda.
     * @param right moverse a la derecha.
     */
    public InputState(boolean up, boolean down, boolean left, boolean right) {
        this.up    = up;
        this.down  = down;
        this.left  = left;
        this.right = right;
    }

    /** Estado de reposo: ninguna dirección activa. */
    public static final InputState IDLE = new InputState(false, false, false, false);
}
