package dominio;

/**
 * Modo PvP (jugador contra jugador) para dos jugadores en la misma PC.
 *
 * <p><b>Controles:</b>
 * <ul>
 *   <li>Jugador 1: teclas WASD</li>
 *   <li>Jugador 2: teclas de flecha (↑↓←→)</li>
 * </ul></p>
 *
 * <p><b>Reglas especiales PvP:</b>
 * <ul>
 *   <li>Gana el jugador que llegue a la meta (con todas las monedas) primero.</li>
 *   <li>Si los dos jugadores colisionan entre sí, ambos mueren y vuelven
 *       al último checkpoint.</li>
 * </ul></p>
 *
 * <p>El movimiento de cada jugador se procesa por separado. La presentación
 * ({@code HardestGameGUI}) pasa el estado del teclado para ambos jugadores
 * mediante {@link #setPlayer1Input(InputState)} y
 * {@link #setPlayer2Input(InputState)}.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-14
 */
public class PvPMode implements GameMode {

    /** Input del jugador 1 para el tick actual. */
    private InputState p1Input = InputState.IDLE;

    /** Input del jugador 2 para el tick actual. */
    private InputState p2Input = InputState.IDLE;

    /**
     * Establece el estado de input del jugador 1.
     *
     * @param input estado de teclas del jugador 1.
     */
    public void setPlayer1Input(InputState input) {
        this.p1Input = input;
    }

    /**
     * Establece el estado de input del jugador 2.
     *
     * @param input estado de teclas del jugador 2.
     */
    public void setPlayer2Input(InputState input) {
        this.p2Input = input;
    }

    /**
     * Actualiza el nivel: mueve ambos jugadores, avanza enemigos y
     * comprueba la colisión entre jugadores (muerte mutua).
     *
     * @param game referencia al juego activo.
     */
    @Override
    public void update(Game game) {
        Level lvl = game.getCurrentLevel();
        lvl.update();  // mueve enemigos y tick del timer

        java.util.List<Player> players = lvl.getPlayers();
        if (players.size() < 2) return;

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        // Mover jugador 1
        p1.move(p1Input.up, p1Input.down, p1Input.left, p1Input.right,
                Level.MAP_WIDTH, Level.MAP_HEIGHT);

        // Mover jugador 2
        p2.move(p2Input.up, p2Input.down, p2Input.left, p2Input.right,
                Level.MAP_WIDTH, Level.MAP_HEIGHT);

        // Colisión mutua: ambos mueren
        if (p1.getBounds().intersects(p2.getBounds())) {
            game.registerDeathFor(p1);
            game.registerDeathFor(p2);
        }
    }
}
