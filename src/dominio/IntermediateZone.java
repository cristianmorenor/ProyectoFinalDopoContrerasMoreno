package dominio;

/**
 * Zona intermedia del nivel (entre obstáculos).
 *
 * <p><b>EN CONSTRUCCIÓN</b> – Extiende {@link SafeZone} representando
 * áreas de descanso dentro del mapa. Funcionalidad completa en entregas futuras.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-14
 */
public class IntermediateZone extends SafeZone {

    /**
     * Construye la zona intermedia con la posición y dimensiones indicadas.
     *
     * @param x      coordenada X.
     * @param y      coordenada Y.
     * @param width  ancho en píxeles.
     * @param height alto en píxeles.
     */
    public IntermediateZone(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
