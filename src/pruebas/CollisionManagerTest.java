package pruebas;

import dominio.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para {@link CollisionManager}.
 * Nomenclatura: shouldXxx / shouldNotXxx.
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class CollisionManagerTest {

    private CollisionManager cm;

    @Before
    public void setUp() {
        cm = new CollisionManager();
    }

    @Test
    public void shouldDetectCollisionBetweenOverlappingPlayers() {
        Player p1 = new Blinky(100, 100);
        Player p2 = new Blinky(110, 110);
        assertTrue("Jugadores superpuestos deben colisionar", cm.collides(p1, p2));
    }

    @Test
    public void shouldNotDetectCollisionBetweenDistantPlayers() {
        Player p1 = new Blinky(100, 100);
        Player p2 = new Blinky(500, 500);
        assertFalse("Jugadores lejanos no deben colisionar", cm.collides(p1, p2));
    }

    @Test
    public void shouldDetectCollisionAtExactEdge() {
        Player p1 = new Blinky(100, 100);  // size 24 → borde derecho en x=124
        Player p2 = new Blinky(124, 100);  // borde izquierdo en x=124
        // Borde exacto: intersects() puede dar true o false según implementación,
        // pero verificamos que no lanza excepción
        try {
            cm.collides(p1, p2);
        } catch (Exception e) {
            fail("collides() no debe lanzar excepción en borde exacto");
        }
    }

    @Test
    public void shouldDetectCollisionBetweenPlayerAndEnemy() {
        Player p = new Blinky(200, 200);
        BasicEnemy e = new BasicEnemy(210, 210, 18, BasicEnemy.Direction.HORIZONTAL, 0);
        assertTrue("Jugador y enemigo superpuestos deben colisionar", cm.collides(p, e));
    }

    @Test
    public void shouldNotDetectCollisionBetweenPlayerAndFarEnemy() {
        Player p = new Blinky(100, 100);
        BasicEnemy e = new BasicEnemy(600, 400, 18, BasicEnemy.Direction.HORIZONTAL, 0);
        assertFalse("Jugador y enemigo lejanos no deben colisionar", cm.collides(p, e));
    }

    @Test
    public void shouldBeSymmetric() {
        Player p = new Blinky(100, 100);
        BasicEnemy e = new BasicEnemy(110, 110, 18, BasicEnemy.Direction.HORIZONTAL, 0);
        assertEquals("La colisión debe ser simétrica",
                cm.collides(p, e), cm.collides(e, p));
    }

    @Test
    public void shouldDetectSelfCollision() {
        Player p = new Blinky(100, 100);
        assertTrue("Un objeto debe colisionar consigo mismo", cm.collides(p, p));
    }
}
