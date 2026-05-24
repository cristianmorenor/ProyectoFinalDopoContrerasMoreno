package dominio;

/**
 * Modo jugador vs máquina.
 *
 * <p>El jugador humano controla el personaje 1 con las teclas configuradas.
 * La IA ({@link AIPlayer}) controla el personaje 2 autónomamente.</p>
 *
 * <p>La IA puede ser:
 * <ul>
 *   <li>{@link RandomAI} — mueve aleatoriamente (dificultad baja).</li>
 *   <li>{@link ExpertAI} — navega con estrategia greedy (dificultad alta).</li>
 * </ul></p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class PvMachineMode implements GameMode {

    /** IA que controla al jugador 2. */
    private final AIPlayer ai;

    /** Input del jugador humano (jugador 1) para el tick actual. */
    private InputState humanInput = InputState.IDLE;

    /**
     * Construye el modo con la IA indicada.
     *
     * @param ai implementación de IA a usar ({@link RandomAI} o {@link ExpertAI}).
     */
    public PvMachineMode(AIPlayer ai) {
        this.ai = ai;
    }

    /**
     * Establece el input del jugador humano.
     *
     * @param input estado de teclas del jugador humano.
     */
    public void setHumanInput(InputState input) {
        this.humanInput = input;
    }

    /**
     * Actualiza el nivel: mueve al jugador humano, calcula y aplica
     * el movimiento de la IA, avanza enemigos.
     *
     * @param game referencia al juego activo.
     */
    @Override
    public void update(Game game) {
        Level lvl = game.getCurrentLevel();
        lvl.update();

        java.util.List<Player> players = lvl.getPlayers();
        if (players.isEmpty()) return;

        Player human = players.get(0);
        human.move(humanInput.up, humanInput.down,
                   humanInput.left, humanInput.right,
                   Level.MAP_WIDTH, Level.MAP_HEIGHT);

        // Jugador IA
        if (players.size() >= 2) {
            Player aiPlayer = players.get(1);
            InputState aiMove = ai.computeMove(lvl, aiPlayer);
            aiPlayer.move(aiMove.up, aiMove.down,
                          aiMove.left, aiMove.right,
                          Level.MAP_WIDTH, Level.MAP_HEIGHT);
        }
    }

    /** @return la implementación de IA activa. */
    public AIPlayer getAI() { return ai; }
}
