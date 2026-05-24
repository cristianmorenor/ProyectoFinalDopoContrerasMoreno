package dominio;

import java.awt.Rectangle;
import java.util.List;

/**
 * IA experta: navega el nivel de forma inteligente buscando monedas
 * y la meta, mientras evita enemigos cercanos y bordea paredes.
 *
 * <p><b>Algoritmo (Greedy con evasión y wall-steering):</b></p>
 * <ol>
 *   <li>Si hay algún enemigo a menos de {@link #DANGER_RADIUS} píxeles,
 *       huye en dirección opuesta (modo evasión).</li>
 *   <li>Si quedan monedas sin recoger, se dirige a la más cercana.</li>
 *   <li>Si todas las monedas fueron recogidas, se dirige a la zona de meta.</li>
 *   <li>En cualquier caso, si el movimiento directo colisiona con una pared,
 *       intenta movimientos alternativos (solo horizontal, solo vertical,
 *       o dirección invertida) para rodearla.</li>
 * </ol>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-19
 */
public class ExpertAI implements AIPlayer {

    /** Distancia a la que un enemigo se considera peligroso (píxeles). */
    private static final double DANGER_RADIUS = 80.0;

    /**
     * Calcula el movimiento más conveniente para el tick actual.
     *
     * @param level estado del nivel.
     * @param self  jugador controlado por la IA.
     * @return {@link InputState} con las direcciones a activar.
     */
    @Override
    public InputState computeMove(Level level, Player self) {
        double selfCX = self.getX() + self.getSize() / 2.0;
        double selfCY = self.getY() + self.getSize() / 2.0;

        // 1. ¿Hay enemigo peligroso cerca?
        for (Enemy e : level.getEnemies()) {
            double dx = e.getX() - selfCX;
            double dy = e.getY() - selfCY;
            if (Math.sqrt(dx * dx + dy * dy) < DANGER_RADIUS) {
                // huir en dirección opuesta — también con evasión de paredes
                InputState flee = flee(dx, dy);
                return avoidWalls(level, self, flee);
            }
        }

        // 2. Buscar moneda no recogida más cercana
        Coin nearest = nearestCoin(level.getCoins(), selfCX, selfCY);
        if (nearest != null) {
            InputState toward = moveToward(selfCX, selfCY,
                    nearest.getX(), nearest.getY());
            return avoidWalls(level, self, toward);
        }

        // 3. Ir a la meta
        GoalZone goal = level.getGoalZone();
        double gx = goal.getX() + goal.getWidth() / 2.0;
        double gy = goal.getY() + goal.getHeight() / 2.0;
        InputState toward = moveToward(selfCX, selfCY, gx, gy);
        return avoidWalls(level, self, toward);
    }

    /**
     * Si el movimiento {@code desired} lleva al jugador a colisionar con una pared,
     * intenta variantes alternativas: solo horizontal, solo vertical, o diagonal inversa.
     * Devuelve el primer InputState que NO colisiona, o {@code desired} si no hay
     * ninguna mejor opción.
     *
     * @param level   nivel con la lista de paredes.
     * @param self    jugador IA.
     * @param desired movimiento deseado original.
     * @return InputState sin colisión de pared, o el deseado como fallback.
     */
    private InputState avoidWalls(Level level, Player self, InputState desired) {
        List<Wall> walls = level.getWalls();
        if (walls.isEmpty()) return desired;

        // Probar el movimiento deseado
        if (!collidesWithWall(self, desired, walls)) return desired;

        // Alternativa 1: solo componente horizontal
        InputState horizontal = new InputState(false, false, desired.left, desired.right);
        if (!collidesWithWall(self, horizontal, walls)) return horizontal;

        // Alternativa 2: solo componente vertical
        InputState vertical = new InputState(desired.up, desired.down, false, false);
        if (!collidesWithWall(self, vertical, walls)) return vertical;

        // Alternativa 3: intenta diagonal inversa (si ambas están activas en desired)
        if ((desired.up || desired.down) && (desired.left || desired.right)) {
            boolean invUp = desired.down;
            boolean invDown = desired.up;
            boolean invLeft = desired.right;
            boolean invRight = desired.left;
            InputState inverted = new InputState(invUp, invDown, invLeft, invRight);
            if (!collidesWithWall(self, inverted, walls)) return inverted;
        }

        // Alternativa 4: quedarse quieto (evitar quedar atascado en la pared)
        return InputState.IDLE;
    }

    /**
     * Simula un tick de movimiento y comprueba si el jugador colisionaría con alguna pared.
     *
     * @param self   jugador actual.
     * @param input  movimiento a simular.
     * @param walls  lista de paredes del nivel.
     * @return {@code true} si el movimiento producido colisiona con alguna pared.
     */
    private boolean collidesWithWall(Player self, InputState input, List<Wall> walls) {
        double speed = self.getSpeed();
        double nx = self.getX();
        double ny = self.getY();

        if (input.up)    ny -= speed;
        if (input.down)  ny += speed;
        if (input.left)  nx -= speed;
        if (input.right) nx += speed;

        // Mantener dentro de los límites del mapa
        nx = Math.max(0, Math.min(Level.MAP_WIDTH  - self.getSize(), nx));
        ny = Math.max(0, Math.min(Level.MAP_HEIGHT - self.getSize(), ny));

        Rectangle future = new Rectangle((int) nx, (int) ny, self.getSize(), self.getSize());
        for (Wall w : walls) {
            if (future.intersects(w.getBounds())) return true;
        }
        return false;
    }

    /**
     * Genera un InputState que aleja al jugador del vector (dx, dy).
     * Prioriza la dirección con mayor diferencia de distancia.
     */
    private InputState flee(double dx, double dy) {
        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);
        
        if (absDx > absDy) {
            // Priorizar eje horizontal
            boolean left = dx > 0;
            boolean right = dx < 0;
            return new InputState(false, false, left, right);
        } else {
            // Priorizar eje vertical
            boolean up = dy > 0;
            boolean down = dy < 0;
            return new InputState(up, down, false, false);
        }
    }

    /**
     * Genera un InputState que mueve al jugador hacia el punto (tx, ty).
     * Prioriza la dirección con mayor diferencia de distancia.
     */
    private InputState moveToward(double sx, double sy, double tx, double ty) {
        double absDx = Math.abs(tx - sx);
        double absDy = Math.abs(ty - sy);
        
        if (absDx > absDy) {
            // Priorizar eje horizontal
            boolean left = tx < sx;
            boolean right = tx > sx;
            return new InputState(false, false, left, right);
        } else {
            // Priorizar eje vertical
            boolean up = ty < sy;
            boolean down = ty > sy;
            return new InputState(up, down, false, false);
        }
    }

    /**
     * Busca la moneda no recogida más cercana al punto (cx, cy).
     *
     * @param coins lista de monedas del nivel.
     * @param cx    coordenada X del jugador IA.
     * @param cy    coordenada Y del jugador IA.
     * @return la moneda más cercana no recogida, o {@code null} si no hay.
     */
    private Coin nearestCoin(List<Coin> coins, double cx, double cy) {
        Coin best = null;
        double bestDist = Double.MAX_VALUE;
        for (Coin c : coins) {
            if (c.isCollected()) continue;
            double dx = c.getX() - cx;
            double dy = c.getY() - cy;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < bestDist) {
                bestDist = dist;
                best = c;
            }
        }
        return best;
    }
}
