package dominio;

/**
 * Contrato para cualquier implementación de Inteligencia Artificial
 * que controla un jugador de forma autónoma.
 *
 * <p>El método {@link #computeMove(Level, Player)} recibe el estado del
 * nivel y el jugador que controla, y retorna un {@link InputState} que
 * indica qué teclas "presionar" en ese tick.</p>
 *
 * <p>Las implementaciones concretas son:
 * <ul>
 *   <li>{@link RandomAI} — movimiento aleatorio.</li>
 *   <li>{@link ExpertAI} — sigue al objetivo más cercano evitando enemigos.</li>
 * </ul></p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public interface AIPlayer {

    /**
     * Calcula el movimiento a realizar en el tick actual.
     *
     * @param level  estado actual del nivel (posición de enemigos, monedas, meta).
     * @param self   el jugador que controla esta IA.
     * @return {@link InputState} con las direcciones a activar.
     */
    InputState computeMove(Level level, Player self);
}
