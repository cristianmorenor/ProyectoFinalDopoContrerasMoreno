package dominio;

/**
 * Moneda estándar del mapa que el jugador debe recolectar.
 *
 * <p>No produce ningún efecto especial al ser recogida; su única
 * función es contribuir al conteo de monedas necesario para
 * desbloquear la zona de meta.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 3.0
 * @since 2026-05-14
 */
public class NormalCoin extends Coin {

    /**
     * Construye una moneda normal en la posición indicada.
     *
     * @param x coordenada X del centro.
     * @param y coordenada Y del centro.
     */
    public NormalCoin(int x, int y) {
        super(x, y);
    }

    /**
     * Sin efecto adicional: la moneda normal solo contribuye al conteo.
     *
     * @param player el jugador que recogió la moneda (no se modifica).
     */
    @Override
    public void applyEffect(Player player) {
        // Si el jugador trae un skin temporal de otra SkinCoin, se restaura
        // al recoger una moneda normal (coherencia de mecánica)
        if (player.getSkinType() != player.getOriginalSkin()) {
            player.restoreOriginalSkin();
        }
    }
}
