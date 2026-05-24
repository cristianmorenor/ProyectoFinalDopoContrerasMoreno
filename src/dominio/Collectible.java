package dominio;

/**
 * Interfaz para elementos coleccionables del mapa.
 *
 * <p>Define el contrato para monedas, fuentes de vida y cualquier
 * objeto recogible que pueda recolectarse y reiniciarse.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-14
 */
public interface Collectible {

    /**
     * Marca el elemento como recogido.
     */
    void collect();

    /**
     * Indica si el elemento ya fue recogido.
     *
     * @return {@code true} si ya fue recolectado.
     */
    boolean isCollected();

    /**
     * Reinicia el elemento a su estado inicial (disponible para recoger).
     * Se llama cuando el jugador muere y el nivel se reinicia parcialmente.
     */
    void reset();
}

