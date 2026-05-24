package pruebas;

import dominio.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.awt.Point;

/**
 * Pruebas unitarias para la jerarquía de enemigos:
 * {@link Enemy}, {@link BasicEnemy}, {@link FastEnemy}, {@link PatrolEnemy}.
 * Nomenclatura: shouldXxx / shouldNotXxx.
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class EnemyTest {

    private static final int MAP_W = Level.MAP_WIDTH;
    private static final int MAP_H = Level.MAP_HEIGHT;

    // ── BasicEnemy ────────────────────────────────────────────────────────────

    @Test
    public void shouldStartAtGivenPosition() {
        BasicEnemy e = new BasicEnemy(200, 150, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        assertEquals(200.0, e.getX(), 0.01);
        assertEquals(150.0, e.getY(), 0.01);
    }

    @Test
    public void shouldHavePositiveRadius() {
        BasicEnemy e = new BasicEnemy(100, 100, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        assertTrue(e.getRadius() > 0);
    }

    @Test
    public void shouldMoveHorizontallyOnUpdate() {
        BasicEnemy e = new BasicEnemy(200, 150, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        double xBefore = e.getX();
        e.update(MAP_W, MAP_H);
        assertNotEquals("X debe cambiar con movimiento horizontal", xBefore, e.getX(), 0.001);
    }

    @Test
    public void shouldMoveVerticallyOnUpdate() {
        BasicEnemy e = new BasicEnemy(200, 150, 18, BasicEnemy.Direction.VERTICAL, 3.0);
        double yBefore = e.getY();
        e.update(MAP_W, MAP_H);
        assertNotEquals("Y debe cambiar con movimiento vertical", yBefore, e.getY(), 0.001);
    }

    @Test
    public void shouldBounceAtRightBorder() {
        BasicEnemy e = new BasicEnemy(MAP_W - 20, 200, 18, BasicEnemy.Direction.HORIZONTAL, 10.0);
        for (int i = 0; i < 5; i++)
            e.update(MAP_W, MAP_H);
        assertTrue("X debe estar dentro del mapa", e.getX() - e.getRadius() >= 0);
        assertTrue("X debe estar dentro del mapa", e.getX() + e.getRadius() <= MAP_W);
    }

    @Test
    public void shouldBounceAtBottomBorder() {
        BasicEnemy e = new BasicEnemy(200, MAP_H - 20, 18, BasicEnemy.Direction.VERTICAL, 10.0);
        for (int i = 0; i < 5; i++)
            e.update(MAP_W, MAP_H);
        assertTrue("Y debe estar dentro del mapa", e.getY() + e.getRadius() <= MAP_H);
    }

    @Test
    public void shouldNotReturnNullBounds() {
        BasicEnemy e = new BasicEnemy(200, 200, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        assertNotNull(e.getBounds());
    }

    @Test
    public void shouldReturnBoundsOfCorrectSize() {
        BasicEnemy e = new BasicEnemy(200, 200, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        assertEquals(36, e.getBounds().width);
        assertEquals(36, e.getBounds().height);
    }

    // ── FastEnemy ─────────────────────────────────────────────────────────────

    @Test
    public void shouldBeFasterThanBasicEnemy() {
        double baseSpeed = 3.0;
        FastEnemy fast = new FastEnemy(200, 200, 18, BasicEnemy.Direction.HORIZONTAL, baseSpeed);
        assertEquals("Velocidad del FastEnemy debe ser base * multiplicador",
                baseSpeed * FastEnemy.SPEED_MULTIPLIER, Math.abs(fast.getSpeed()), 0.001);
    }

    @Test
    public void shouldHaveDoubleSpeedMultiplier() {
        assertEquals(2.0, FastEnemy.SPEED_MULTIPLIER, 0.001);
    }

    @Test
    public void shouldMoveAtDoubleSpeedHorizontally() {
        FastEnemy fast = new FastEnemy(200, 200, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        double xBefore = fast.getX();
        fast.update(MAP_W, MAP_H);
        assertEquals("FastEnemy debe mover 6 px (3*2)", 6.0, Math.abs(fast.getX() - xBefore), 0.01);
    }

    // ── PatrolEnemy ───────────────────────────────────────────────────────────

    @Test
    public void shouldNotThrowOnUpdateWithWaypoints() {
        PatrolEnemy patrol = new PatrolEnemy(300, 150, 16, 3.0,
                Arrays.asList(new Point(300, 150), new Point(500, 150),
                        new Point(500, 350), new Point(300, 350)));
        try {
            patrol.update(MAP_W, MAP_H);
        } catch (Exception ex) {
            fail("update() no debe lanzar excepción: " + ex.getMessage());
        }
    }

    @Test
    public void shouldMoveTowardsFirstWaypoint() {
        PatrolEnemy patrol = new PatrolEnemy(300, 150, 16, 3.0,
                Arrays.asList(new Point(500, 150), new Point(300, 150)));
        double xBefore = patrol.getX();
        patrol.update(MAP_W, MAP_H);
        assertTrue("Debe moverse hacia el waypoint en X", patrol.getX() > xBefore);
    }

    @Test
    public void shouldCycleThroughWaypoints() {
        // Waypoints muy cercanos para forzar ciclo rápido
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 10, 50.0,
                Arrays.asList(new Point(100, 100), new Point(110, 100)));
        patrol.update(MAP_W, MAP_H);
        // Debe haber avanzado al waypoint siguiente
        assertTrue("El índice debe avanzar", patrol.getCurrentIndex() >= 0);
    }

    @Test
    public void shouldNotReturnNullWaypoints() {
        PatrolEnemy patrol = new PatrolEnemy(300, 150, 16, 3.0,
                Arrays.asList(new Point(300, 150)));
        assertNotNull(patrol.getWaypoints());
    }

    @Test
    public void shouldImplementCollidable() {
        Enemy e = new BasicEnemy(100, 100, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        assertTrue("Enemy debe implementar Collidable", e instanceof Collidable);
    }

    @Test
    public void shouldImplementMovable() {
        Enemy e = new BasicEnemy(100, 100, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        assertTrue("Enemy debe implementar Movable", e instanceof Movable);
    }

    // =========================================================================
    // Branch Coverage — BasicEnemy rebotes en todos los bordes
    // =========================================================================

    @Test
    public void shouldBounceBasicEnemyAtLeftBorder() {
        BasicEnemy e = new BasicEnemy(15, 200, 18, BasicEnemy.Direction.HORIZONTAL, -20.0);
        e.update(MAP_W, MAP_H);
        assertTrue("Velocidad debe invertirse a positiva", e.getSpeed() > 0);
        assertTrue("X debe clampearse a >= radius", e.getX() >= e.getRadius());
    }

    @Test
    public void shouldBounceBasicEnemyAtRightBorder() {
        BasicEnemy e = new BasicEnemy(MAP_W - 15, 200, 18, BasicEnemy.Direction.HORIZONTAL, 20.0);
        e.update(MAP_W, MAP_H);
        assertTrue("Velocidad debe invertirse a negativa", e.getSpeed() < 0);
        assertTrue("X debe clampearse a <= mapW-radius", e.getX() + e.getRadius() <= MAP_W);
    }

    @Test
    public void shouldNotBounceHorizontallyWhenInCenter() {
        BasicEnemy e = new BasicEnemy(200, 200, 18, BasicEnemy.Direction.HORIZONTAL, 3.0);
        double speedBefore = e.getSpeed();
        e.update(MAP_W, MAP_H);
        assertEquals("No debe rebotar en el centro", speedBefore, e.getSpeed(), 0.001);
    }

    @Test
    public void shouldBounceBasicEnemyAtTopBorder() {
        BasicEnemy e = new BasicEnemy(200, 15, 18, BasicEnemy.Direction.VERTICAL, -20.0);
        e.update(MAP_W, MAP_H);
        assertTrue("Velocidad debe invertirse a positiva", e.getSpeed() > 0);
        assertTrue("Y debe clampearse a >= radius", e.getY() >= e.getRadius());
    }

    @Test
    public void shouldBounceBasicEnemyAtBottomBorderDirect() {
        BasicEnemy e = new BasicEnemy(200, MAP_H - 15, 18, BasicEnemy.Direction.VERTICAL, 20.0);
        e.update(MAP_W, MAP_H);
        assertTrue("Velocidad debe invertirse a negativa", e.getSpeed() < 0);
        assertTrue("Y debe clampearse a <= mapH-radius", e.getY() + e.getRadius() <= MAP_H);
    }

    @Test
    public void shouldNotBounceVerticallyWhenInCenter() {
        BasicEnemy e = new BasicEnemy(200, 200, 18, BasicEnemy.Direction.VERTICAL, 3.0);
        double speedBefore = e.getSpeed();
        e.update(MAP_W, MAP_H);
        assertEquals("No debe rebotar en el centro", speedBefore, e.getSpeed(), 0.001);
    }

    // ── FastEnemy rebotes ────────────────────────────────────────────────────

    @Test
    public void shouldFastEnemyBounceAtLeftBorder() {
        FastEnemy fe = new FastEnemy(10, 200, 18, BasicEnemy.Direction.HORIZONTAL, -5.0);
        fe.update(MAP_W, MAP_H);
        assertTrue("FastEnemy debe rebotar en borde izquierdo", fe.getSpeed() > 0);
    }

    @Test
    public void shouldFastEnemyBounceAtTopBorder() {
        FastEnemy fe = new FastEnemy(200, 10, 18, BasicEnemy.Direction.VERTICAL, -5.0);
        fe.update(MAP_W, MAP_H);
        assertTrue("FastEnemy debe rebotar en borde superior", fe.getSpeed() > 0);
    }

    // ── VerticalSliderEnemy ──────────────────────────────────────────────────

    @Test
    public void shouldVerticalSliderBounceAtTop() {
        VerticalSliderEnemy vse = new VerticalSliderEnemy(200, 10, 18, -20.0);
        vse.update(MAP_W, MAP_H);
        assertTrue("Debe rebotar arriba", vse.getSpeed() > 0);
    }

    @Test
    public void shouldVerticalSliderBounceAtBottom() {
        VerticalSliderEnemy vse = new VerticalSliderEnemy(200, MAP_H - 10, 18, 20.0);
        vse.update(MAP_W, MAP_H);
        assertTrue("Debe rebotar abajo", vse.getSpeed() < 0);
    }

    // ── PatrolEnemy: null y empty waypoints ──────────────────────────────────

    @Test
    public void shouldHandleNullWaypointsGracefully() {
        PatrolEnemy pe = new PatrolEnemy(100, 100, 16, 3.0, null);
        pe.update(MAP_W, MAP_H);
        assertEquals("No debe moverse con null waypoints", 100.0, pe.getX(), 0.001);
    }

    @Test
    public void shouldHandleEmptyWaypointsGracefully() {
        PatrolEnemy pe = new PatrolEnemy(100, 100, 16, 3.0, new java.util.ArrayList<>());
        pe.update(MAP_W, MAP_H);
        assertEquals("No debe moverse con lista vacía", 100.0, pe.getX(), 0.001);
    }

    @Test
    public void shouldSnapToWaypointWhenCloseEnough() {
        PatrolEnemy pe = new PatrolEnemy(100, 100, 16, 50.0,
                Arrays.asList(new Point(102, 100), new Point(200, 200)));
        pe.update(MAP_W, MAP_H);
        assertEquals("Debe saltar al waypoint 0", 102.0, pe.getX(), 0.001);
        assertEquals("Debe avanzar al siguiente índice", 1, pe.getCurrentIndex());
    }

    @Test
    public void shouldMoveTowardDistantWaypoint() {
        PatrolEnemy pe = new PatrolEnemy(100, 100, 16, 3.0,
                Arrays.asList(new Point(300, 100)));
        double xBefore = pe.getX();
        pe.update(MAP_W, MAP_H);
        assertTrue("Debe acercarse al waypoint", pe.getX() > xBefore);
        assertEquals("No debe cambiar de índice", 0, pe.getCurrentIndex());
    }

    @Test
    public void shouldCycleBackToFirstWaypoint() {
        PatrolEnemy pe = new PatrolEnemy(100, 100, 16, 500.0,
                Arrays.asList(new Point(100, 100), new Point(105, 100)));
        pe.update(MAP_W, MAP_H);
        pe.update(MAP_W, MAP_H);
        assertEquals("Debe volver al primer waypoint", 0, pe.getCurrentIndex());
    }

    // ── PatrolEnemy: último waypoint vuelve a índice 0 ───────────────────
    @Test
    public void shouldWrapToFirstWaypointAfterLast() {
        PatrolEnemy pe = new PatrolEnemy(100, 100, 16, 500.0,
                Arrays.asList(new Point(100, 100), new Point(105, 100)));
        pe.update(Level.MAP_WIDTH, Level.MAP_HEIGHT); // avanza a índice 1
        pe.update(Level.MAP_WIDTH, Level.MAP_HEIGHT); // debe volver a 0
        assertEquals("Debe volver al índice 0", 0, pe.getCurrentIndex());
    }

    // ── PatrolEnemy: un solo waypoint ────────────────────────────────────
    @Test
    public void shouldHandleSingleWaypoint() {
        PatrolEnemy pe = new PatrolEnemy(100, 100, 16, 500.0,
                Arrays.asList(new Point(100, 100)));
        pe.update(Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals("Con un waypoint debe quedarse en índice 0",
                0, pe.getCurrentIndex());
    }

    // ── BasicEnemy: velocidad cero no mueve ──────────────────────────────
    @Test
    public void shouldNotMoveWhenSpeedIsZeroHorizontal() {
        BasicEnemy e = new BasicEnemy(200, 200, 18,
                BasicEnemy.Direction.HORIZONTAL, 0.0);
        double xBefore = e.getX();
        e.update(Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals("Con velocidad 0 no debe moverse", xBefore, e.getX(), 0.001);
    }

    @Test
    public void shouldNotMoveWhenSpeedIsZeroVertical() {
        BasicEnemy e = new BasicEnemy(200, 200, 18,
                BasicEnemy.Direction.VERTICAL, 0.0);
        double yBefore = e.getY();
        e.update(Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals("Con velocidad 0 no debe moverse", yBefore, e.getY(), 0.001);
    }

    // ── FastEnemy: rebote derecha y abajo ────────────────────────────────
    @Test
    public void shouldFastEnemyBounceAtRightBorder() {
        FastEnemy fe = new FastEnemy(Level.MAP_WIDTH - 10, 200, 18,
                BasicEnemy.Direction.HORIZONTAL, 20.0);
        fe.update(Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertTrue("FastEnemy debe rebotar en borde derecho", fe.getSpeed() < 0);
    }

    @Test
    public void shouldFastEnemyBounceAtBottomBorder() {
        FastEnemy fe = new FastEnemy(200, Level.MAP_HEIGHT - 10, 18,
                BasicEnemy.Direction.VERTICAL, 20.0);
        fe.update(Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertTrue("FastEnemy debe rebotar en borde inferior", fe.getSpeed() < 0);
    }

    // ── VerticalSliderEnemy: velocidad cero ──────────────────────────────
    @Test
    public void shouldVerticalSliderNotMoveWhenSpeedZero() {
        VerticalSliderEnemy vse = new VerticalSliderEnemy(200, 200, 18, 0.0);
        double yBefore = vse.getY();
        vse.update(Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals("Con velocidad 0 no debe moverse", yBefore, vse.getY(), 0.001);
    }

    // ── setX / setY / setSpeed ───────────────────────────────────────────
    @Test
    public void shouldSetXYAndSpeed() {
        BasicEnemy e = new BasicEnemy(100, 100, 18,
                BasicEnemy.Direction.HORIZONTAL, 3.0);
        e.setX(250.0);
        e.setY(300.0);
        e.setSpeed(-5.0);
        assertEquals(250.0, e.getX(), 0.001);
        assertEquals(300.0, e.getY(), 0.001);
        assertEquals(-5.0, e.getSpeed(), 0.001);
    }

}
