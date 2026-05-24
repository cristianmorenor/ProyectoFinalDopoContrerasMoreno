package dominio;

/**
 * Fábrica de jugadores (Factory Method Pattern).
 *
 * <p>Crea instancias de {@link Player} según el {@link SkinType} solicitado,
 * sin que el código cliente conozca las clases concretas. Esto cumple el
 * Principio Abierto/Cerrado: agregar un nuevo skin solo requiere extender
 * esta clase, no modificarla.</p>
 *
 * <p>Ejemplo de uso:
 * <pre>
 *     Player p = PlayerFactory.create(SkinType.GREEN, 60, 230);
 * </pre></p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class PlayerFactory {

    /** Constructor privado: clase utilitaria, no se instancia. */
    private PlayerFactory() {}

    /**
     * Crea un jugador del tipo indicado en la posición dada.
     *
     * @param skin tipo de skin del jugador.
     * @param x    coordenada X inicial.
     * @param y    coordenada Y inicial.
     * @return instancia concreta de {@link Player}.
     * @throws IllegalArgumentException si el tipo de skin es desconocido (nunca debería ocurrir).
     */
    public static Player create(SkinType skin, double x, double y) {
        return switch (skin) {
            case RED   -> new RedPlayer(x, y);
            case BLUE  -> new BluePlayer(x, y);
            case GREEN -> new GreenPlayer(x, y);
        };
    }
}
