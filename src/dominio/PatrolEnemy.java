package dominio;

import java.awt.Point;
import java.util.List;

/**
 * Enemigo patrullero: sigue una secuencia de puntos de paso (waypoints)
 * formando patrones geométricos (cuadrado, rectángulo, triángulo, etc.).
 *
 * <p>El enemigo avanza de un waypoint al siguiente a velocidad constante.
 * Al llegar al último waypoint vuelve al primero, creando un recorrido
 * cíclico e infinito.</p>
 *
 * <p><b>Algoritmo de desplazamiento:</b> en cada tick se calcula el
 * vector dirección hacia el waypoint actual. Si la distancia restante
 * es menor que la velocidad, el enemigo "salta" al waypoint y avanza
 * al siguiente. Esto evita oscilaciones.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class PatrolEnemy extends Enemy {

    /** Lista de puntos de paso que el enemigo recorre en orden. */
    private final List<Point> waypoints;

    /** Índice del waypoint actualmente objetivo. */
    private int currentIndex;

    /**
     * Construye un enemigo patrullero.
     *
     * @param x         coordenada X inicial.
     * @param y         coordenada Y inicial.
     * @param radius    radio en píxeles.
     * @param speed     velocidad de patrulla en píxeles/tick.
     * @param waypoints lista de puntos de paso (mínimo 2).
     */
    public PatrolEnemy(double x, double y, int radius,
                       double speed, List<Point> waypoints) {
        super(x, y, radius, speed);
        this.waypoints    = waypoints;
        this.currentIndex = 0;
    }

    /**
     * Mueve el enemigo hacia el waypoint actual.
     * Al alcanzarlo avanza al siguiente en la lista (cíclicamente).
     *
     * @param mapW ancho del mapa (no usado para rebote, el movimiento
     *             es por waypoints dentro del mapa).
     * @param mapH alto del mapa.
     */
    @Override
    public void update(int mapW, int mapH) {
        if (waypoints == null || waypoints.isEmpty()) return;

        Point target = waypoints.get(currentIndex);
        double dx = target.x - x;
        double dy = target.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {
            // llegó al waypoint → avanza al siguiente
            x = target.x;
            y = target.y;
            currentIndex = (currentIndex + 1) % waypoints.size();
        } else {
            // avanza en dirección al waypoint
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }

    /**
     * Devuelve la lista de waypoints de este enemigo.
     *
     * @return lista de {@link Point} del recorrido.
     */
    public List<Point> getWaypoints() { return waypoints; }

    /**
     * Devuelve el índice del waypoint que se está alcanzando ahora.
     *
     * @return índice actual.
     */
    public int getCurrentIndex() { return currentIndex; }
}
