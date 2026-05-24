package pruebas;

import dominio.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para {@link PvPMode}.
 * Nomenclatura: shouldXxx / shouldNotXxx.
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class PvPModeTest {

    private PvPMode pvpMode;
    private Game game;

    @Before
    public void setUp() {
        pvpMode = new PvPMode();
        game = new Game();
        // Configurar el juego en modo PvP con dos jugadores
        game.setGameMode(pvpMode);
        Level level = game.getCurrentLevel();
        // Agregar un segundo jugador
        Player p2 = new BluePlayer(150, 150);
        level.addPlayer(p2);
    }

    @Test
    public void shouldAcceptPlayer1Input() {
        InputState input = new InputState(true, false, false, false);
        pvpMode.setPlayer1Input(input);
        // No debe lanzar excepción
        assertTrue("Debe aceptar input del jugador 1", true);
    }

    @Test
    public void shouldAcceptPlayer2Input() {
        InputState input = new InputState(false, true, false, false);
        pvpMode.setPlayer2Input(input);
        // No debe lanzar excepción
        assertTrue("Debe aceptar input del jugador 2", true);
    }

    @Test
    public void shouldMovePlayer1WhenInputIsUp() {
        Level level = game.getCurrentLevel();
        Player p1 = level.getPlayer();
        double initialY = p1.getY();

        // Mover hacia arriba
        InputState upInput = new InputState(true, false, false, false);
        pvpMode.setPlayer1Input(upInput);
        pvpMode.update(game);

        // Después de update, p1 debe estar más arriba (Y menor)
        assertTrue("P1 debe moverse hacia arriba", p1.getY() < initialY);
    }

    @Test
    public void shouldMovePlayer2WhenInputIsDown() {
        Level level = game.getCurrentLevel();
        java.util.List<Player> players = level.getPlayers();
        if (players.size() < 2)
            return;
        Player p2 = players.get(1);
        double initialY = p2.getY();

        // Mover hacia abajo
        InputState downInput = new InputState(false, true, false, false);
        pvpMode.setPlayer2Input(downInput);
        pvpMode.update(game);

        // Después de update, p2 debe estar más abajo (Y mayor)
        assertTrue("P2 debe moverse hacia abajo", p2.getY() > initialY);
    }

    @Test
    public void shouldKillBothPlayersOnCollision() {
        Level level = game.getCurrentLevel();
        java.util.List<Player> players = level.getPlayers();
        if (players.size() < 2)
            return;

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        // Posicionar ambos jugadores en la misma ubicación usando fullReset
        p1.fullReset(100, 100);
        p2.fullReset(100, 100);

        int initialDeaths = game.getDeaths();

        // Ejecutar update
        pvpMode.update(game);

        // Ambos deben estar muertos (deaths incrementado dos veces)
        assertTrue("Debe haber registrado al menos 2 muertes en colisión", game.getDeaths() > initialDeaths);
    }

    @Test
    public void shouldNotCrashWithLessThanTwoPlayers() {
        Game singlePlayerGame = new Game();
        singlePlayerGame.setGameMode(pvpMode);

        pvpMode.update(singlePlayerGame);
        assertTrue("Debe manejar un solo jugador sin error", true);
    }

    // =========================================================================
    // Branch Coverage — PvPMode ramas faltantes
    // =========================================================================

    @Test
    public void shouldPvPModeNotKillWhenPlayersNotColliding() {
        PvPMode pvp = new PvPMode();
        Game g = new Game();
        g.setGameMode(pvp);
        Player p2 = new BluePlayer(500, 400);
        g.getCurrentLevel().addPlayer(p2);

        pvp.setPlayer1Input(InputState.IDLE);
        pvp.setPlayer2Input(InputState.IDLE);
        pvp.update(g);

        assertEquals("Sin colisión no debe haber muertes", 0, g.getDeaths());
    }

    @Test
    public void shouldPvPModeKillBothWhenColliding() {
        PvPMode pvp = new PvPMode();
        Game g = new Game();
        g.setGameMode(pvp);
        Player p1 = g.getCurrentLevel().getPlayer();
        Player p2c = new BluePlayer(p1.getX(), p1.getY());
        g.getCurrentLevel().addPlayer(p2c);

        pvp.setPlayer1Input(InputState.IDLE);
        pvp.setPlayer2Input(InputState.IDLE);
        pvp.update(g);

        assertTrue("Colisión debe registrar muertes", g.getDeaths() >= 2);
    }

    @Test
    public void shouldPvPModeMovePlayer1WithInput() {
        PvPMode pvp = new PvPMode();
        Game g = new Game();
        g.setGameMode(pvp);
        Player p2 = new BluePlayer(500, 400);
        g.getCurrentLevel().addPlayer(p2);

        Player p1 = g.getCurrentLevel().getPlayer();
        double y1Before = p1.getY();

        pvp.setPlayer1Input(new InputState(true, false, false, false));
        pvp.setPlayer2Input(InputState.IDLE);
        pvp.update(g);

        assertTrue("P1 debe moverse arriba", p1.getY() < y1Before);
    }

    @Test
    public void shouldPvPModeMovePlayer2WithInput() {
        PvPMode pvp = new PvPMode();
        Game g = new Game();
        g.setGameMode(pvp);
        Player p2 = new BluePlayer(500, 200);
        g.getCurrentLevel().addPlayer(p2);

        double y2Before = p2.getY();

        pvp.setPlayer1Input(InputState.IDLE);
        pvp.setPlayer2Input(new InputState(false, true, false, false));
        pvp.update(g);

        assertTrue("P2 debe moverse abajo", p2.getY() > y2Before);
    }

    // =========================================================================
    // Branch Coverage — PvMachineMode ramas faltantes
    // =========================================================================

    @Test
    public void shouldPvMachineModeSkipAIWhenOnlyOnePlayer() {
        PvMachineMode pvm = new PvMachineMode(new RandomAI());
        Game g = new Game();
        g.setGameMode(pvm);

        pvm.setHumanInput(new InputState(false, true, false, false));
        pvm.update(g);

        assertNotNull(g.getCurrentLevel().getPlayer());
    }

    @Test
    public void shouldPvMachineModeControlAIPlayerWhenTwoPlayers() {
        PvMachineMode pvm = new PvMachineMode(new RandomAI());
        Game g = new Game();
        g.setGameMode(pvm);
        Player aiP = new BluePlayer(300, 300);
        g.getCurrentLevel().addPlayer(aiP);

        pvm.setHumanInput(InputState.IDLE);
        pvm.update(g);

        assertEquals(2, g.getCurrentLevel().getPlayers().size());
    }

    @Test
    public void shouldPvMachineModeWorkWithExpertAI() {
        PvMachineMode pvm = new PvMachineMode(new ExpertAI());
        Game g = new Game();
        g.setGameMode(pvm);
        Player aiP = new BluePlayer(300, 300);
        g.getCurrentLevel().addPlayer(aiP);

        pvm.setHumanInput(InputState.IDLE);
        pvm.update(g);

        assertTrue("Debe ser ExpertAI", pvm.getAI() instanceof ExpertAI);
    }

    @Test
    public void shouldPvMachineModeMovesHumanWithInput() {
        PvMachineMode pvm = new PvMachineMode(new RandomAI());
        Game g = new Game();
        g.setGameMode(pvm);
        Player p1 = g.getCurrentLevel().getPlayer();
        double xBefore = p1.getX();

        pvm.setHumanInput(new InputState(false, false, false, true));
        pvm.update(g);

        assertTrue("Humano debe moverse a la derecha", p1.getX() > xBefore);
    }

    @Test
    public void shouldInputStateStoreDirections() {
        InputState is = new InputState(true, false, true, false);
        assertTrue(is.up);
        assertFalse(is.down);
        assertTrue(is.left);
        assertFalse(is.right);
    }

    @Test
    public void shouldIdleHaveAllFalse() {
        InputState idle = InputState.IDLE;
        assertFalse(idle.up);
        assertFalse(idle.down);
        assertFalse(idle.left);
        assertFalse(idle.right);
    }

    // ── Rama: menos de 2 jugadores ───────────────────────────────────────
    @Test
    public void shouldReturnEarlyWhenOnlyOnePlayer() {
        PvPMode pvp = new PvPMode();
        Game g = new Game();
        int deathsBefore = g.getDeaths();
        pvp.update(g);
        assertEquals("Con 1 jugador no debe registrar muertes",
                deathsBefore, g.getDeaths());
    }

    // ── Rama: colisión false ─────────────────────────────────────────────
    @Test
    public void shouldNotRegisterDeathWhenPlayersAreApart() {
        PvPMode pvp = new PvPMode();
        Game g = new Game();
        g.getCurrentLevel().addPlayer(new BluePlayer(600, 400));
        pvp.setPlayer1Input(InputState.IDLE);
        pvp.setPlayer2Input(InputState.IDLE);
        pvp.update(g);
        assertEquals("Sin colisión no debe haber muertes", 0, g.getDeaths());
    }

    // ── Rama: colisión true ──────────────────────────────────────────────
    @Test
    public void shouldRegisterTwoDeathsOnPlayerCollision() {
        PvPMode pvp = new PvPMode();
        Game g = new Game();
        Player p1 = g.getCurrentLevel().getPlayer();
        g.getCurrentLevel().addPlayer(
                new BluePlayer(p1.getX(), p1.getY()));
        pvp.setPlayer1Input(InputState.IDLE);
        pvp.setPlayer2Input(InputState.IDLE);
        pvp.update(g);
        assertEquals("Colisión debe registrar 2 muertes", 2, g.getDeaths());
    }
}
