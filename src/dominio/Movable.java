package dominio;

/**
 * Contrato para cualquier entidad del juego que se mueve autónomamente
 * (enemigos, bombas animadas, etc.).
 *
 * <p>A diferencia del método {@code Player.move()} que responde al teclado,
 * {@code Movable.update()} se llama en cada tick del bucle de juego
 * sin intervención del usuario.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-19
 */
public interface Movable {

    /**
     * Actualiza la posición del objeto para el tick actual,
     * respetando los límites del mapa.
     *
     * @param mapW ancho del área de juego en píxeles.
     * @param mapH alto del área de juego en píxeles.
     */
    void update(int mapW, int mapH);
}