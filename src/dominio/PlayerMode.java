package dominio;

/**
 * Modo de juego estándar para un solo jugador.
 *
 * <p>Implementa {@link GameMode} con la lógica básica de actualización:
 * avanza el nivel activo en cada tick del bucle de juego.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-14
 */
public class PlayerMode implements GameMode {

    /**
     * Actualiza el nivel actual del juego (mueve obstáculos, etc.).
     *
     * @param game referencia al juego activo.
     */
    @Override
    public void update(Game game) {
        game.getCurrentLevel().update();
    }
}
