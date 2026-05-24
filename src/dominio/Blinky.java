package dominio;

/**
 * Alias de {@link RedPlayer} mantenido por compatibilidad.
 *
 * <p>Utilizado internamente por algunos tests existentes.
 * Los niveles nuevos deben usar {@link PlayerFactory} en lugar de
 * instanciar Blinky directamente.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-14
 */
public class Blinky extends RedPlayer {

    /**
     * Construye un Blinky (jugador rojo) en la posición indicada.
     *
     * @param x coordenada X inicial.
     * @param y coordenada Y inicial.
     */
    public Blinky(double x, double y) {
        super(x, y);
    }
}