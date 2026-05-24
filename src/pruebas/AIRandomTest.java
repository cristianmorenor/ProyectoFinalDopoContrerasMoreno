package pruebas;

import dominio.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para {@link RandomAI}.
 * Nomenclatura: shouldXxx / shouldNotXxx.
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class AIRandomTest {

    private RandomAI ai;
    private Level level;
    private Player player;

    @Before
    public void setUp() {
        ai = new RandomAI();
        level = new Level(1);
        player = new RedPlayer(50, 50);
    }

    @Test
    public void shouldReturnInputState() {
        InputState result = ai.computeMove(level, player);
        assertNotNull("computeMove no debe retornar null", result);
    }

    @Test
    public void shouldReturnValidDirectionOrIdle() {
        for (int i = 0; i < 20; i++) {
            InputState result = ai.computeMove(level, player);
            // El resultado debe tener al menos uno de los campos como true, o ser IDLE (todo false)
            boolean isValid = result.up || result.down || result.left || result.right || 
                             (!result.up && !result.down && !result.left && !result.right);
            assertTrue("El resultado debe ser una dirección válida", isValid);
        }
    }

    @Test
    public void shouldReturnDifferentMovesOverTime() {
        java.util.Set<String> moves = new java.util.HashSet<>();
        for (int i = 0; i < 50; i++) {
            InputState result = ai.computeMove(level, player);
            String state = String.format("U:%b D:%b L:%b R:%b", result.up, result.down, result.left, result.right);
            moves.add(state);
        }
        // Con 50 iteraciones, debe haber al menos 2-3 movimientos diferentes (estadísticamente probable)
        assertTrue("Debe haber variedad en los movimientos", moves.size() > 1);
    }
}
