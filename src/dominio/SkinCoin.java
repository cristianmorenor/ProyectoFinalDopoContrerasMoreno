package dominio;

/**
 * Moneda especial que cambia <b>temporalmente</b> el skin del jugador
 * al ser recogida.
 *
 * <p>Al recogerla, el jugador adopta las estadísticas del skin indicado
 * por {@link #getTargetSkin()} durante un número determinado de ticks
 * (configurable desde el nivel). Al agotarse el tiempo, el jugador
 * vuelve a su skin original.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 3.0
 * @since 2026-05-14
 */
public class SkinCoin extends Coin {

    /** Skin que se aplica al jugador al recoger esta moneda. */
    private final SkinType targetSkin;

    /**
     * Construye una moneda de skin en la posición indicada.
     *
     * @param x          coordenada X del centro.
     * @param y          coordenada Y del centro.
     * @param targetSkin skin que se aplicará al recolectarla.
     */
    public SkinCoin(int x, int y, SkinType targetSkin) {
        super(x, y);
        this.targetSkin = targetSkin;
    }

    /**
     * Aplica el cambio temporal de skin al jugador que recoge la moneda.
     *
     * <p>Este método sobreescribe {@link Coin#applyEffect(Player)} para
     * implementar el efecto polimórfico específico de la SkinCoin.</p>
     *
     * @param player el jugador que recogió la moneda.
     */
    @Override
    public void applyEffect(Player player) {
        player.changeSkin(targetSkin);
    }

    /**
     * Devuelve el tipo de skin que otorga esta moneda.
     *
     * @return {@link SkinType} del skin objetivo.
     */
    public SkinType getTargetSkin() {
        return targetSkin;
    }
}
