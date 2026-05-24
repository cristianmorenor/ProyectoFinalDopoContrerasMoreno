package dominio;

import java.util.Random;

/**
 * IA aleatoria: elige una dirección al azar en cada tick.
 *
 * <p>Esta implementación es simple e impredecible, pero no es eficaz
 * para completar el nivel. Sirve como IA de baja dificultad o como
 * demostración del patrón {@link AIPlayer}.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class RandomAI implements AIPlayer {

    /** Generador de números aleatorios. */
    private final Random random;

    /**
     * Construye una IA aleatoria con semilla no determinista.
     */
    public RandomAI() {
        this.random = new Random();
    }

    /**
     * Elige aleatoriamente entre las cuatro direcciones cardinales
     * y la opción de quedarse quieto.
     *
     * @param level estado del nivel (no utilizado en esta implementación).
     * @param self  el jugador que controla la IA (no utilizado).
     * @return {@link InputState} con una dirección aleatoria activa.
     */
    @Override
    public InputState computeMove(Level level, Player self) {
        // 5 opciones: arriba, abajo, izquierda, derecha, quieto
        int choice = random.nextInt(5);
        return switch (choice) {
            case 0 -> new InputState(true,  false, false, false);
            case 1 -> new InputState(false, true,  false, false);
            case 2 -> new InputState(false, false, true,  false);
            case 3 -> new InputState(false, false, false, true);
            default -> InputState.IDLE;
        };
    }
}
