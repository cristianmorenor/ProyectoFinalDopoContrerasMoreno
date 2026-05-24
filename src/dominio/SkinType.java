package dominio;

/**
 * Enumeración de los tipos de skin (personaje) disponibles en el juego.
 *
 * <p>Cada skin tiene estadísticas diferentes que afectan velocidad,
 * tamaño y comportamiento especial del jugador.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 2.0
 * @since 2026-05-19
 */
public enum SkinType {

    /**
     * Piel roja: velocidad normal, tamaño normal.
     * Es el personaje base del juego.
     */
    RED,

    /**
     * Piel azul: más rápido y más grande.
     */
    BLUE,

    /**
     * Piel verde: resiste el primer golpe, luego reduce su velocidad.
     */
    GREEN
}
