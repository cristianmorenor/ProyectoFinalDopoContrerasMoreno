package pruebas;

import dominio.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para {@link Level}.
 * Nomenclatura: shouldXxx / shouldNotXxx.
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class LevelTest {

    private Level level;

    @Before
    public void setUp() {
        level = new Level(1);
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    public void shouldHaveCorrectLevelNumber() {
        assertEquals("Nivel debe ser 1", 1, level.getLevelNumber());
    }

    @Test
    public void shouldNotReturnNullPlayer() {
        assertNotNull("getPlayer() no debe ser null", level.getPlayer());
    }

    @Test
    public void shouldNotReturnNullGoalZone() {
        assertNotNull("getGoalZone() no debe ser null", level.getGoalZone());
    }

    @Test
    public void shouldHaveEnemies() {
        assertFalse("Debe haber al menos un enemigo", level.getEnemies().isEmpty());
    }

    @Test
    public void shouldHaveCoins() {
        assertFalse("Debe haber al menos una moneda", level.getCoins().isEmpty());
    }

    @Test
    public void shouldHaveSafeZones() {
        assertFalse("Debe haber al menos una zona segura", level.getSafeZones().isEmpty());
    }

    @Test
    public void shouldHaveCheckPoints() {
        assertFalse("Debe haber al menos un checkpoint", level.getCheckPoints().isEmpty());
    }

    @Test
    public void shouldHaveLifeSources() {
        assertFalse("Debe haber al menos una fuente de vida", level.getLifeSources().isEmpty());
    }

    @Test
    public void shouldNotReturnNullTimer() {
        assertNotNull("getTimer() no debe ser null", level.getTimer());
    }

    @Test
    public void shouldHaveDefaultTimeLimit() {
        assertEquals("Tiempo límite por defecto debe ser " + Level.DEFAULT_TIME_LIMIT,
                Level.DEFAULT_TIME_LIMIT, level.getTimer().getTimeLimitSeconds());
    }

    // ── Monedas ───────────────────────────────────────────────────────────────

    @Test
    public void shouldNotHaveCollectedCoinsInitially() {
        assertEquals("No debe haber monedas recogidas al inicio", 0, level.collectedCoinsCount());
    }

    @Test
    public void shouldNotHaveAllCoinsCollectedInitially() {
        assertFalse("allCoinsCollected() debe ser false al inicio", level.allCoinsCollected());
    }

    @Test
    public void shouldCountCollectedCoinsCorrectly() {
        level.getCoins().get(0).collect();
        level.getCoins().get(1).collect();
        assertEquals("Debe contar 2 monedas recogidas", 2, level.collectedCoinsCount());
    }

    @Test
    public void shouldDetectAllCoinsCollected() {
        for (Coin c : level.getCoins()) c.collect();
        assertTrue("allCoinsCollected() debe ser true", level.allCoinsCollected());
    }

    // ── Reinicio ──────────────────────────────────────────────────────────────

    @Test
    public void shouldResetCoinsAfterResetPlayer() {
        level.getCoins().get(0).collect();
        level.resetPlayer();
        assertEquals("Monedas deben resetearse", 0, level.collectedCoinsCount());
    }

    @Test
    public void shouldResetPlayerPositionAfterReset() {
        Player p = level.getPlayer();
        double startX = p.getX();
        p.move(false, true, false, true, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        level.resetPlayer();
        assertEquals("X debe volver al checkpoint", startX, p.getX(), 0.01);
    }

    // ── Actualización ─────────────────────────────────────────────────────────

    @Test
    public void shouldNotThrowOnUpdate() {
        try {
            level.update();
        } catch (Exception e) {
            fail("update() no debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void shouldTickTimerOnUpdate() {
        level.getTimer().start();
        int ticksBefore = level.getTimer().getRemainingTicks();
        level.update();
        assertEquals("El timer debe decrementar en 1 tick",
                ticksBefore - 1, level.getTimer().getRemainingTicks());
    }

    // ── Agregar elementos ─────────────────────────────────────────────────────

    @Test
    public void shouldAddEnemy() {
        int before = level.getEnemies().size();
        level.addEnemy(new BasicEnemy(100, 100, 18, BasicEnemy.Direction.HORIZONTAL, 3.0));
        assertEquals(before + 1, level.getEnemies().size());
    }

    @Test
    public void shouldAddCoin() {
        int before = level.getCoins().size();
        level.addCoin(new NormalCoin(50, 50));
        assertEquals(before + 1, level.getCoins().size());
    }

    @Test
    public void shouldAddWall() {
        int before = level.getWalls().size();
        level.addWall(new Wall(100, 100, 20, 80));
        assertEquals(before + 1, level.getWalls().size());
    }

    @Test
    public void shouldAddBomb() {
        int before = level.getBombs().size();
        level.addBomb(new Bomb(200, 200));
        assertEquals(before + 1, level.getBombs().size());
    }

    @Test
    public void shouldSetGoalZone() {
        GoalZone gz = new GoalZone(700, 200, 50, 100);
        level.setGoalZone(gz);
        assertEquals(gz, level.getGoalZone());
    }

    // ── Constantes del mapa ───────────────────────────────────────────────────

    @Test
    public void shouldHavePositiveMapWidth() {
        assertTrue(Level.MAP_WIDTH > 0);
    }

    @Test
    public void shouldHavePositiveMapHeight() {
        assertTrue(Level.MAP_HEIGHT > 0);
    }

    // ── Tests adicionales para cobertura ─────────────────────────────────────

    @Test
    public void shouldAllCoinsCollectedReturnTrueForEmptyLevel() {
        Level empty = new Level(2);  // nivel 2 vacío (sin coins hardcodeadas)
        assertTrue("Nivel sin monedas debe reportar allCoinsCollected=true",
                empty.allCoinsCollected());
    }

    @Test
    public void shouldCollectedCoinsCountZeroForEmptyLevel() {
        Level empty = new Level(2);
        assertEquals(0, empty.collectedCoinsCount());
    }

    @Test
    public void shouldAddLifeSource() {
        int before = level.getLifeSources().size();
        level.addLifeSource(new LifeSource(100, 100));
        assertEquals(before + 1, level.getLifeSources().size());
    }

    @Test
    public void shouldAddCheckPoint() {
        int before = level.getCheckPoints().size();
        level.addCheckPoint(new CheckPoint(200, 200, 50, 50));
        assertEquals(before + 1, level.getCheckPoints().size());
    }

    @Test
    public void shouldAddSafeZone() {
        int before = level.getSafeZones().size();
        level.addSafeZone(new SafeZone(0, 0, 50, 500));
        assertEquals(before + 1, level.getSafeZones().size());
    }

    @Test
    public void shouldAddPlayer() {
        int before = level.getPlayers().size();
        level.addPlayer(new Blinky(100, 100));
        assertEquals(before + 1, level.getPlayers().size());
    }

    @Test
    public void shouldResetAllPlayers() {
        Player p = level.getPlayer();
        double startX = p.getX();
        p.move(false, false, false, true, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        level.resetAllPlayers();
        assertEquals("resetAllPlayers restaura posición", startX, p.getX(), 0.01);
    }

    @Test
    public void shouldResetBombs() {
        Bomb b = new Bomb(200, 200);
        level.addBomb(b);
        b.explode();
        level.resetBombs();
        assertFalse("resetBombs debe restablecer las bombas", b.isExploded());
    }

    @Test
    public void shouldConstructLevel1WithSkin() {
        Level lvl = new Level(1, SkinType.BLUE);
        assertNotNull(lvl.getPlayer());
        assertEquals(SkinType.BLUE, lvl.getPlayer().getSkinType());
    }

    @Test
    public void shouldConstructLevel1WithSkinAndTime() {
        Level lvl = new Level(1, 60, SkinType.GREEN);
        assertEquals(60, lvl.getTimer().getTimeLimitSeconds());
        assertEquals(SkinType.GREEN, lvl.getPlayer().getSkinType());
    }

    @Test
    public void shouldReturnNullPlayerForEmptyLevel() {
        Level empty = new Level(2);
        assertNull("Nivel 2 sin jugador debe retornar null", empty.getPlayer());
    }

    @Test
    public void shouldGetDeprecatedGoalFromGoalZone() {
        assertNotNull("getGoal() debe retornar valor basado en GoalZone", level.getGoal());
    }

    @Test
    public void shouldGetDeprecatedGoalReturnNullWhenNoGoalZone() {
        Level empty = new Level(2);
        assertNull("Sin GoalZone getGoal() debe ser null", empty.getGoal());
    }

    @Test
    public void shouldGetObstaclesReturnEmpty() {
        assertTrue("getObstacles() debe retornar lista vacía", level.getObstacles().isEmpty());
    }

    @Test
    public void shouldHaveBothTypesOfEnemyInLevel1() {
        boolean hasBasic  = level.getEnemies().stream().anyMatch(e -> e instanceof BasicEnemy);
        boolean hasFast   = level.getEnemies().stream().anyMatch(e -> e instanceof FastEnemy);
        boolean hasPatrol = level.getEnemies().stream().anyMatch(e -> e instanceof PatrolEnemy);
        assertTrue("Nivel 1 debe tener BasicEnemy", hasBasic);
        assertTrue("Nivel 1 debe tener FastEnemy", hasFast);
        assertTrue("Nivel 1 debe tener PatrolEnemy", hasPatrol);
    }
}

