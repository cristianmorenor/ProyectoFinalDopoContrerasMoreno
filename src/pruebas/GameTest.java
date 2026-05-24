package pruebas;

import dominio.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para {@link Game}.
 * Nomenclatura: shouldXxx / shouldNotXxx.
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class GameTest {

    private Game game;

    @Before
    public void setUp() {
        game = new Game();
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    public void shouldStartWithZeroDeaths() {
        assertEquals("El contador de muertes debe iniciar en 0", 0, game.getDeaths());
    }

    @Test
    public void shouldNotReturnNullCurrentLevel() {
        assertNotNull("getCurrentLevel() no debe retornar null", game.getCurrentLevel());
    }

    @Test
    public void shouldStartAtLevelOne() {
        assertEquals("El primer nivel debe ser el 1", 1, game.getCurrentLevel().getLevelNumber());
    }

    @Test
    public void shouldNotReturnNullCollisionManager() {
        assertNotNull("getCollisionManager() no debe retornar null", game.getCollisionManager());
    }

    @Test
    public void shouldStartWithPlayerModeByDefault() {
        assertTrue("El modo inicial debe ser PlayerMode",
                game.getGameMode() instanceof PlayerMode);
    }

    @Test
    public void shouldStartWithRedSkinByDefault() {
        assertEquals("La skin por defecto debe ser RED", SkinType.RED, game.getSelectedSkin());
    }

    // ── Registro de muertes ───────────────────────────────────────────────────

    @Test
    public void shouldIncrementDeathCounterOnRegisterDeath() {
        game.registerDeath();
        assertEquals("Una muerte debe incrementar el contador a 1", 1, game.getDeaths());
    }

    @Test
    public void shouldAccumulateMultipleDeaths() {
        game.registerDeath();
        game.registerDeath();
        game.registerDeath();
        assertEquals("Tres muertes deben acumular a 3", 3, game.getDeaths());
    }

    @Test
    public void shouldResetPlayerPositionAfterDeath() {
        Player p = game.getCurrentLevel().getPlayer();
        double startX = p.getX();
        double startY = p.getY();
        p.move(false, true, false, true, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        game.registerDeath();
        assertEquals("Después de muerte, el jugador debe estar en checkpointX", startX, p.getX(), 0.01);
        assertEquals("Después de muerte, el jugador debe estar en checkpointY", startY, p.getY(), 0.01);
    }

    @Test
    public void shouldNotThrowWhenCallingUpdate() {
        try {
            game.update();
        } catch (Exception e) {
            fail("update() no debe lanzar excepción: " + e.getMessage());
        }
    }

    // ── Modo de juego ─────────────────────────────────────────────────────────

    @Test
    public void shouldChangeGameModeWithSetGameMode() {
        GameMode pvp = new PvPMode();
        game.setGameMode(pvp);
        assertSame("setGameMode() debe asignar el nuevo modo", pvp, game.getGameMode());
    }

    @Test
    public void shouldSupportPvPMode() {
        game.setGameMode(new PvPMode());
        assertTrue(game.getGameMode() instanceof PvPMode);
    }

    @Test
    public void shouldSupportPvMachineModeWithRandomAI() {
        game.setGameMode(new PvMachineMode(new RandomAI()));
        assertTrue(game.getGameMode() instanceof PvMachineMode);
    }

    // ── Skin ──────────────────────────────────────────────────────────────────

    @Test
    public void shouldChangeSelectedSkin() {
        game.setSelectedSkin(SkinType.BLUE);
        assertEquals(SkinType.BLUE, game.getSelectedSkin());
    }

    @Test
    public void shouldCreateGameWithCustomSkin() {
        Game g = new Game(SkinType.GREEN);
        assertEquals(SkinType.GREEN, g.getSelectedSkin());
    }

    // ── Reinicio ──────────────────────────────────────────────────────────────

    @Test
    public void shouldResetDeathsToZeroOnReset() {
        game.registerDeath();
        game.registerDeath();
        game.reset();
        assertEquals("Después de reset(), las muertes deben ser 0", 0, game.getDeaths());
    }

    @Test
    public void shouldReturnLevelOneAfterReset() {
        game.reset();
        assertEquals("Después de reset(), el nivel debe ser 1",
                1, game.getCurrentLevel().getLevelNumber());
    }

    @Test
    public void shouldNotReturnNullLevelAfterReset() {
        game.reset();
        assertNotNull("Después de reset(), getCurrentLevel() no debe ser null",
                game.getCurrentLevel());
    }

    // ── nextLevel ─────────────────────────────────────────────────────────────

    @Test
    public void shouldNotThrowWhenNoMoreLevels() {
        try {
            game.nextLevel();
        } catch (Exception e) {
            fail("nextLevel() no debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void shouldStayAtCurrentLevelWhenNoMoreLevels() {
        int nivelAntes = game.getCurrentLevel().getLevelNumber();
        game.nextLevel();
        assertEquals("Sin más niveles, el nivel no debe cambiar",
                nivelAntes, game.getCurrentLevel().getLevelNumber());
    }

    @Test
    public void shouldAdvanceToNextLevelWhenAvailable() {
        Level l2 = new Level(2);
        game.addLevel(l2);
        game.nextLevel();
        assertEquals(2, game.getCurrentLevel().getLevelNumber());
    }

    // ── registerDeathFor ─────────────────────────────────────────────────────

    @Test
    public void shouldIncrementDeathsOnRegisterDeathFor() {
        Player p = game.getCurrentLevel().getPlayer();
        game.registerDeathFor(p);
        assertEquals(1, game.getDeaths());
    }

    @Test
    public void shouldNotDecrementPlayerLivesOnRegisterDeathFor() {
        Player p = game.getCurrentLevel().getPlayer();
        int livesBefore = p.getLives();
        game.registerDeathFor(p);
        assertEquals("registerDeathFor ya no resta vidas", livesBefore, p.getLives());
    }

    // ── registerDeath descuenta vida ──────────────────────────────────────────

    @Test
    public void shouldNotDecrementLivesOnRegisterDeath() {
        Player p = game.getCurrentLevel().getPlayer();
        int livesBefore = p.getLives();
        game.registerDeath();
        assertEquals("registerDeath ya no resta vidas", livesBefore, p.getLives());
    }

    // ── livesForLevel ─────────────────────────────────────────────────────────

    @Test
    public void shouldHaveFiveLivesForLevel1() {
        assertEquals(5, Game.livesForLevel(1));
    }

    @Test
    public void shouldHaveFourLivesForLevel4() {
        assertEquals(4, Game.livesForLevel(4));
    }

    @Test
    public void shouldHaveThreeLivesForLevel7() {
        assertEquals(3, Game.livesForLevel(7));
    }

    @Test
    public void shouldHaveTwoLivesForLevel10() {
        assertEquals(2, Game.livesForLevel(10));
    }

    // ── setDeaths ─────────────────────────────────────────────────────────────

    @Test
    public void shouldSetDeathsToPositiveValue() {
        game.setDeaths(10);
        assertEquals(10, game.getDeaths());
    }

    @Test
    public void shouldClampNegativeDeathsToZero() {
        game.setDeaths(-3);
        assertEquals(0, game.getDeaths());
    }

    // ── setCurrentLevelIndex ──────────────────────────────────────────────────

    @Test
    public void shouldSetCurrentLevelIndexToValidIndex() {
        Level l2 = new Level(2);
        game.addLevel(l2);
        game.setCurrentLevelIndex(1);
        assertEquals(2, game.getCurrentLevelNumber());
    }

    @Test
    public void shouldIgnoreOutOfBoundsLevelIndex() {
        game.setCurrentLevelIndex(99);
        assertEquals("No debe cambiar el nivel con índice inválido",
                1, game.getCurrentLevelNumber());
    }

    @Test
    public void shouldIgnoreNegativeLevelIndex() {
        game.setCurrentLevelIndex(-1);
        assertEquals(1, game.getCurrentLevelNumber());
    }

    // ── getCurrentLevelIndex / getLevels ──────────────────────────────────────

    @Test
    public void shouldReturnZeroCurrentLevelIndexInitially() {
        assertEquals(0, game.getCurrentLevelIndex());
    }

    @Test
    public void shouldReturnLevelsList() {
        assertNotNull(game.getLevels());
        assertFalse(game.getLevels().isEmpty());
    }

    // ── resetCurrentLevel ─────────────────────────────────────────────────────

    @Test
    public void shouldResetPlayerPositionOnResetCurrentLevel() {
        Player p = game.getCurrentLevel().getPlayer();
        double ckX = p.getCheckpointX();
        double ckY = p.getCheckpointY();
        // Mover lejos del checkpoint
        p.move(false, true, false, true, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        game.resetCurrentLevel();
        // Debe volver al checkpoint
        assertEquals("X debe volver al checkpoint", ckX, p.getX(), 0.01);
        assertEquals("Y debe volver al checkpoint", ckY, p.getY(), 0.01);
    }

    @Test
    public void shouldResetCoinsOnResetCurrentLevel() {
        Level lvl = game.getCurrentLevel();
        if (!lvl.getCoins().isEmpty()) {
            lvl.getCoins().get(0).collect();
            game.resetCurrentLevel();
            assertFalse("Las monedas deben resetearse", lvl.getCoins().get(0).isCollected());
        }
    }

    // =========================================================================
    // Branch Coverage — Game ramas faltantes
    // =========================================================================

    @Test
    public void shouldNotAdvanceLevelWhenNoMoreLevels() {
        Game g = new Game();
        int lvlBefore = g.getCurrentLevelIndex();
        g.nextLevel();
        assertEquals("No debe avanzar sin niveles disponibles", lvlBefore, g.getCurrentLevelIndex());
    }

    @Test
    public void shouldAdvanceMultipleLevels() {
        Game g = new Game();
        g.addLevel(new Level(2));
        g.addLevel(new Level(3));
        g.nextLevel();
        g.nextLevel();
        assertEquals(3, g.getCurrentLevelNumber());
    }

    @Test
    public void shouldStopAtLastLevel() {
        Game g = new Game();
        g.addLevel(new Level(2));
        g.nextLevel();
        g.nextLevel(); // no hay nivel 3
        assertEquals(2, g.getCurrentLevelNumber());
    }

    // ── livesForLevel: cubrir todas las ramas ────────────────────────────────

    @Test
    public void shouldReturn5LivesForLevel1To3() {
        assertEquals(5, Game.livesForLevel(1));
        assertEquals(5, Game.livesForLevel(2));
        assertEquals(5, Game.livesForLevel(3));
    }

    @Test
    public void shouldReturn4LivesForLevel4To6() {
        assertEquals(4, Game.livesForLevel(4));
        assertEquals(4, Game.livesForLevel(5));
        assertEquals(4, Game.livesForLevel(6));
    }

    @Test
    public void shouldReturn3LivesForLevel7To9() {
        assertEquals(3, Game.livesForLevel(7));
        assertEquals(3, Game.livesForLevel(8));
        assertEquals(3, Game.livesForLevel(9));
    }

    @Test
    public void shouldReturn2LivesForLevel10OrHigher() {
        assertEquals(2, Game.livesForLevel(10));
        assertEquals(2, Game.livesForLevel(15));
    }

    // ── registerDeath con null player ────────────────────────────────────────

    @Test
    public void shouldRegisterDeathWithNonNullPlayer() {
        Game g = new Game();
        Player p = g.getCurrentLevel().getPlayer();
        int livesBefore = p.getLives();
        g.registerDeath();
        assertEquals(1, g.getDeaths());
        assertEquals("registerDeath ya no resta vidas", livesBefore, p.getLives());
    }

    // ── setCurrentLevelIndex: ambas ramas ────────────────────────────────────

    @Test
    public void shouldSetValidLevelIndexBranch() {
        Game g = new Game();
        g.addLevel(new Level(2));
        g.setCurrentLevelIndex(1);
        assertEquals(1, g.getCurrentLevelIndex());
    }

    @Test
    public void shouldIgnoreInvalidHighLevelIndexBranch() {
        Game g = new Game();
        g.setCurrentLevelIndex(999);
        assertEquals(0, g.getCurrentLevelIndex());
    }

    @Test
    public void shouldIgnoreNegativeLevelIndexBranch() {
        Game g = new Game();
        g.setCurrentLevelIndex(-1);
        assertEquals(0, g.getCurrentLevelIndex());
    }

    // ── resetCurrentLevel restaura timer ─────────────────────────────────────

    @Test
    public void shouldResetCurrentLevelRestartTimer() {
        Game g = new Game();
        g.getCurrentLevel().getTimer().start();
        for (int i = 0; i < 5; i++) g.getCurrentLevel().update();
        g.resetCurrentLevel();
        assertTrue("Timer debe reiniciarse",
                g.getCurrentLevel().getTimer().getRemainingTicks() > 0);
    }
}

