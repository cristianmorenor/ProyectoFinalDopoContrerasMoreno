package pruebas;

import dominio.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.Rectangle;

/**
 * Pruebas unitarias para {@link Player} y sus subclases.
 *
 * <p>
 * Cubre: movimiento, límites, vidas, skins, checkpoints y habilidades
 * especiales de {@link BluePlayer} y {@link GreenPlayer}.
 * Se eliminaron tests de getters triviales (sin lógica) para evitar
 * ruido sin valor; el comportamiento queda cubierto de forma indirecta.
 * </p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class PlayerTest {

    private static final int MAP_W = Level.MAP_WIDTH;
    private static final int MAP_H = Level.MAP_HEIGHT;

    private Player player;

    @Before
    public void setUp() {
        player = new Blinky(100, 100);
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    public void shouldHavePositiveSize() {
        assertTrue("El tamaño debe ser positivo", player.getSize() > 0);
    }

    @Test
    public void shouldHavePositiveSpeed() {
        assertTrue("La velocidad debe ser positiva", player.getSpeed() > 0);
    }

    @Test
    public void shouldHaveDefaultLives() {
        assertEquals("Vidas iniciales deben ser " + Player.DEFAULT_LIVES,
                Player.DEFAULT_LIVES, player.getLives());
    }

    @Test
    public void shouldNotBeDeadWithDefaultLives() {
        assertFalse("No debe estar muerto con vidas iniciales", player.isDead());
    }

    // ── Movimiento ────────────────────────────────────────────────────────────

    @Test
    public void shouldIncreaseXWhenMovingRight() {
        double xBefore = player.getX();
        player.move(false, false, false, true, MAP_W, MAP_H);
        assertTrue("Mover derecha incrementa X", player.getX() > xBefore);
    }

    @Test
    public void shouldDecreaseXWhenMovingLeft() {
        double xBefore = player.getX();
        player.move(false, false, true, false, MAP_W, MAP_H);
        assertTrue("Mover izquierda decrementa X", player.getX() < xBefore);
    }

    @Test
    public void shouldIncreaseYWhenMovingDown() {
        double yBefore = player.getY();
        player.move(false, true, false, false, MAP_W, MAP_H);
        assertTrue("Mover abajo incrementa Y", player.getY() > yBefore);
    }

    @Test
    public void shouldDecreaseYWhenMovingUp() {
        double yBefore = player.getY();
        player.move(true, false, false, false, MAP_W, MAP_H);
        assertTrue("Mover arriba decrementa Y", player.getY() < yBefore);
    }

    @Test
    public void shouldNotChangePosWhenNoInput() {
        double xBefore = player.getX();
        double yBefore = player.getY();
        player.move(false, false, false, false, MAP_W, MAP_H);
        assertEquals("Sin input X no cambia", xBefore, player.getX(), 0.01);
        assertEquals("Sin input Y no cambia", yBefore, player.getY(), 0.01);
    }

    @Test
    public void shouldMoveDiagonally() {
        double xBefore = player.getX();
        double yBefore = player.getY();
        player.move(true, false, false, true, MAP_W, MAP_H);
        assertTrue("Diagonal: X aumenta", player.getX() > xBefore);
        assertTrue("Diagonal: Y disminuye", player.getY() < yBefore);
    }

    // ── Límites del mapa ──────────────────────────────────────────────────────

    @Test
    public void shouldNotPassTopBorder() {
        Player p = new Blinky(100, 0);
        p.move(true, false, false, false, MAP_W, MAP_H);
        assertTrue("Y no puede ser negativa", p.getY() >= 0);
    }

    @Test
    public void shouldNotPassLeftBorder() {
        Player p = new Blinky(0, 100);
        p.move(false, false, true, false, MAP_W, MAP_H);
        assertTrue("X no puede ser negativa", p.getX() >= 0);
    }

    @Test
    public void shouldNotPassRightBorder() {
        Player p = new Blinky(MAP_W - 1, 100);
        for (int i = 0; i < 10; i++)
            p.move(false, false, false, true, MAP_W, MAP_H);
        assertTrue("X + size no supera el ancho", p.getX() + p.getSize() <= MAP_W);
    }

    @Test
    public void shouldNotPassBottomBorder() {
        Player p = new Blinky(100, MAP_H - 1);
        for (int i = 0; i < 10; i++)
            p.move(false, true, false, false, MAP_W, MAP_H);
        assertTrue("Y + size no supera el alto", p.getY() + p.getSize() <= MAP_H);
    }

    // ── Reinicio y checkpoints ────────────────────────────────────────────────

    @Test
    public void shouldReturnToInitialPositionAfterReset() {
        player.move(false, true, false, true, MAP_W, MAP_H);
        player.reset();
        assertEquals("X vuelve al checkpoint", 100.0, player.getX(), 0.01);
        assertEquals("Y vuelve al checkpoint", 100.0, player.getY(), 0.01);
    }

    @Test
    public void shouldSaveCheckpointPosition() {
        player.move(false, false, false, true, MAP_W, MAP_H);
        player.activateCheckpoint();
        double cx = player.getX();
        double cy = player.getY();
        player.move(false, true, false, true, MAP_W, MAP_H);
        player.reset();
        assertEquals("Reset vuelve al checkpoint, no al inicio", cx, player.getX(), 0.01);
        assertEquals("Reset vuelve al checkpoint, no al inicio", cy, player.getY(), 0.01);
    }

    @Test
    public void shouldFullResetToGivenPosition() {
        player.move(false, true, false, true, MAP_W, MAP_H);
        player.fullReset(50, 75);
        assertEquals(50.0, player.getX(), 0.01);
        assertEquals(75.0, player.getY(), 0.01);
    }

    @Test
    public void shouldFullResetUpdateCheckpoint() {
        player.fullReset(50, 75);
        player.move(false, true, false, true, MAP_W, MAP_H);
        player.reset();
        assertEquals("Checkpoint debe ser la posición de fullReset", 50.0, player.getX(), 0.01);
        assertEquals("Checkpoint debe ser la posición de fullReset", 75.0, player.getY(), 0.01);
    }

    // ── getBounds ─────────────────────────────────────────────────────────────

    @Test
    public void shouldReturnBoundsMatchingCurrentPosition() {
        Rectangle b = player.getBounds();
        assertEquals("Bounds.x == getX()", (int) player.getX(), b.x);
        assertEquals("Bounds.y == getY()", (int) player.getY(), b.y);
        assertEquals("Bounds.width == getSize()", player.getSize(), b.width);
        assertEquals("Bounds.height == getSize()", player.getSize(), b.height);
    }

    // ── Vidas ─────────────────────────────────────────────────────────────────

    @Test
    public void shouldDecrementLivesOnLoseLife() {
        int before = player.getLives();
        player.loseLife();
        assertEquals(before - 1, player.getLives());
    }

    @Test
    public void shouldIncrementLivesOnGainLife() {
        player.gainLife();
        assertEquals(Player.DEFAULT_LIVES + 1, player.getLives());
    }

    @Test
    public void shouldBeDeadWhenLivesReachZero() {
        player.setLives(0);
        assertTrue(player.isDead());
    }

    @Test
    public void shouldClampLivesToZeroWhenNegative() {
        player.setLives(-1);
        assertEquals("setLives(-1) debe quedar en 0", 0, player.getLives());
    }

    @Test
    public void shouldNotGoNegativeLives() {
        player.setLives(0);
        player.loseLife();
        assertEquals(0, player.getLives());
    }

    // ── Skins y comparación entre tipos ──────────────────────────────────────

    @Test
    public void shouldCreateBluePlayerFasterThanRed() {
        Player blue = new BluePlayer(0, 0);
        Player red = new RedPlayer(0, 0);
        assertTrue("BluePlayer debe ser más rápido", blue.getSpeed() > red.getSpeed());
    }

    @Test
    public void shouldCreateBluePlayerLargerThanRed() {
        Player blue = new BluePlayer(0, 0);
        Player red = new RedPlayer(0, 0);
        assertTrue("BluePlayer debe ser más grande", blue.getSize() > red.getSize());
    }

    @Test
    public void shouldGreenPlayerAbsorbFirstHit() {
        GreenPlayer green = new GreenPlayer(0, 0);
        assertTrue("El primer golpe debe ser absorbido", green.onHit());
    }

    @Test
    public void shouldGreenPlayerDieOnSecondHit() {
        GreenPlayer green = new GreenPlayer(0, 0);
        green.onHit(); // primer golpe absorbido
        assertFalse("El segundo golpe debe matar", green.onHit());
    }

    @Test
    public void shouldGreenPlayerHaveShieldActiveInitially() {
        GreenPlayer green = new GreenPlayer(0, 0);
        assertTrue(green.isShieldActive());
    }

    @Test
    public void shouldGreenPlayerLoseShieldAfterHit() {
        GreenPlayer green = new GreenPlayer(0, 0);
        green.onHit();
        assertFalse(green.isShieldActive());
    }

    @Test
    public void shouldGreenPlayerHaveReducedSpeedAfterHit() {
        GreenPlayer green = new GreenPlayer(0, 0);
        double speedBefore = green.getSpeed();
        green.onHit();
        assertTrue("La velocidad del GreenPlayer debe reducirse tras el hit",
                green.getSpeed() < speedBefore);
    }

    @Test
    public void shouldRedPlayerNotAbsorbHit() {
        Player red = new RedPlayer(0, 0);
        assertFalse("RedPlayer no debe absorber el golpe", red.onHit());
    }

    @Test
    public void shouldBluePlayerNotAbsorbHit() {
        Player blue = new BluePlayer(0, 0);
        assertFalse("BluePlayer no debe absorber el golpe", blue.onHit());
    }

    // ── PlayerFactory ─────────────────────────────────────────────────────────

    @Test
    public void shouldCreateRedPlayerFromFactory() {
        Player p = PlayerFactory.create(SkinType.RED, 10, 10);
        assertTrue(p instanceof RedPlayer);
        assertEquals(SkinType.RED, p.getSkinType());
    }

    @Test
    public void shouldCreateBluePlayerFromFactory() {
        Player p = PlayerFactory.create(SkinType.BLUE, 10, 10);
        assertTrue(p instanceof BluePlayer);
        assertEquals(SkinType.BLUE, p.getSkinType());
    }

    @Test
    public void shouldCreateGreenPlayerFromFactory() {
        Player p = PlayerFactory.create(SkinType.GREEN, 10, 10);
        assertTrue(p instanceof GreenPlayer);
        assertEquals(SkinType.GREEN, p.getSkinType());
    }

    // ── BorderColorIndex ──────────────────────────────────────────────────────

    @Test
    public void shouldSetBorderColorIndex() {
        player.setBorderColorIndex(2);
        assertEquals(2, player.getBorderColorIndex());
    }

    @Test
    public void shouldHaveZeroBorderColorIndexByDefault() {
        assertEquals(0, player.getBorderColorIndex());
    }

    // ── GreenPlayer reset restaura escudo ─────────────────────────────────────

    @Test
    public void shouldGreenPlayerRestoreShieldOnReset() {
        GreenPlayer green = new GreenPlayer(0, 0);
        green.onHit(); // consume el escudo
        assertFalse("Escudo consumido", green.isShieldActive());
        green.reset();
        assertTrue("reset() debe restaurar el escudo", green.isShieldActive());
    }

    // =========================================================================
    // Branch Coverage — move() ramas individuales
    // =========================================================================

    @Test
    public void shouldCoverMoveOnlyUp() {
        Player p = new RedPlayer(100, 100);
        double yBefore = p.getY();
        p.move(true, false, false, false, MAP_W, MAP_H);
        assertTrue("up=true debe decrementar Y", p.getY() < yBefore);
    }

    @Test
    public void shouldCoverMoveOnlyDown() {
        Player p = new RedPlayer(100, 100);
        double yBefore = p.getY();
        p.move(false, true, false, false, MAP_W, MAP_H);
        assertTrue("down=true debe incrementar Y", p.getY() > yBefore);
    }

    @Test
    public void shouldCoverMoveOnlyLeft() {
        Player p = new RedPlayer(100, 100);
        double xBefore = p.getX();
        p.move(false, false, true, false, MAP_W, MAP_H);
        assertTrue("left=true debe decrementar X", p.getX() < xBefore);
    }

    @Test
    public void shouldCoverMoveOnlyRight() {
        Player p = new RedPlayer(100, 100);
        double xBefore = p.getX();
        p.move(false, false, false, true, MAP_W, MAP_H);
        assertTrue("right=true debe incrementar X", p.getX() > xBefore);
    }

    @Test
    public void shouldCoverMoveAllFalse() {
        Player p = new RedPlayer(100, 100);
        double x = p.getX(), y = p.getY();
        p.move(false, false, false, false, MAP_W, MAP_H);
        assertEquals("Sin input X no cambia", x, p.getX(), 0.001);
        assertEquals("Sin input Y no cambia", y, p.getY(), 0.001);
    }

    @Test
    public void shouldCoverMoveAllTrue() {
        Player p = new RedPlayer(200, 200);
        p.move(true, true, true, true, MAP_W, MAP_H);
        assertNotNull("El jugador sigue existiendo", p.getBounds());
    }

    // ── move() bordes: Math.max/Math.min clampea ─────────────────────────────

    @Test
    public void shouldClampYToZeroWhenMovingUpAtTopBorder() {
        Player p = new RedPlayer(100, 0);
        p.move(true, false, false, false, MAP_W, MAP_H);
        assertEquals("Y debe clampearse a 0", 0.0, p.getY(), 0.001);
    }

    @Test
    public void shouldClampXToZeroWhenMovingLeftAtLeftBorder() {
        Player p = new RedPlayer(0, 100);
        p.move(false, false, true, false, MAP_W, MAP_H);
        assertEquals("X debe clampearse a 0", 0.0, p.getX(), 0.001);
    }

    @Test
    public void shouldClampXToMaxWhenMovingRightAtRightBorder() {
        double maxX = MAP_W - 24;
        Player p = new RedPlayer(maxX, 100);
        p.move(false, false, false, true, MAP_W, MAP_H);
        assertEquals("X debe clampearse al máximo", maxX, p.getX(), 0.001);
    }

    @Test
    public void shouldClampYToMaxWhenMovingDownAtBottomBorder() {
        double maxY = MAP_H - 24;
        Player p = new RedPlayer(100, maxY);
        p.move(false, true, false, false, MAP_W, MAP_H);
        assertEquals("Y debe clampearse al máximo", maxY, p.getY(), 0.001);
    }

    // ── move() centro: sin clampeo ───────────────────────────────────────────

    @Test
    public void shouldMoveUpWithoutClampingWhenNotAtBorder() {
        Player p = new RedPlayer(200, 200);
        double yBefore = p.getY();
        p.move(true, false, false, false, MAP_W, MAP_H);
        assertEquals(yBefore - p.getSpeed(), p.getY(), 0.001);
    }

    @Test
    public void shouldMoveDownWithoutClampingWhenNotAtBorder() {
        Player p = new RedPlayer(200, 200);
        double yBefore = p.getY();
        p.move(false, true, false, false, MAP_W, MAP_H);
        assertEquals(yBefore + p.getSpeed(), p.getY(), 0.001);
    }

    @Test
    public void shouldMoveLeftWithoutClampingWhenNotAtBorder() {
        Player p = new RedPlayer(200, 200);
        double xBefore = p.getX();
        p.move(false, false, true, false, MAP_W, MAP_H);
        assertEquals(xBefore - p.getSpeed(), p.getX(), 0.001);
    }

    @Test
    public void shouldMoveRightWithoutClampingWhenNotAtBorder() {
        Player p = new RedPlayer(200, 200);
        double xBefore = p.getX();
        p.move(false, false, false, true, MAP_W, MAP_H);
        assertEquals(xBefore + p.getSpeed(), p.getX(), 0.001);
    }

    // ── loseLife() ambas ramas ────────────────────────────────────────────────

    @Test
    public void shouldDecrementLivesWhenAlive() {
        Player p = new RedPlayer(0, 0);
        int before = p.getLives();
        p.loseLife();
        assertEquals(before - 1, p.getLives());
    }

    @Test
    public void shouldNotDecrementLivesWhenAlreadyDead() {
        Player p = new RedPlayer(0, 0);
        p.setLives(0);
        p.loseLife();
        assertEquals(0, p.getLives());
    }

    // ── isDead() ambas ramas ─────────────────────────────────────────────────

    @Test
    public void shouldReturnTrueWhenDeadWithZeroLives() {
        Player p = new RedPlayer(0, 0);
        p.setLives(0);
        assertTrue(p.isDead());
    }

    @Test
    public void shouldReturnFalseWhenAliveWithPositiveLives() {
        Player p = new RedPlayer(0, 0);
        assertFalse(p.isDead());
    }

    // ── setLives() ramas ─────────────────────────────────────────────────────

    @Test
    public void shouldSetLivesToPositiveValueWithoutClamping() {
        Player p = new RedPlayer(0, 0);
        p.setLives(7);
        assertEquals(7, p.getLives());
    }

    @Test
    public void shouldSetLivesToZero() {
        Player p = new RedPlayer(0, 0);
        p.setLives(0);
        assertEquals(0, p.getLives());
    }

    @Test
    public void shouldClampNegativeLivesToZero() {
        Player p = new RedPlayer(0, 0);
        p.setLives(-5);
        assertEquals(0, p.getLives());
    }

    // ── GreenPlayer.onHit() ambas ramas ──────────────────────────────────────

    @Test
    public void shouldAbsorbHitWhenShieldActive() {
        GreenPlayer gp = new GreenPlayer(100, 100);
        assertTrue(gp.onHit());
        assertFalse(gp.isShieldActive());
    }

    @Test
    public void shouldNotAbsorbHitWhenShieldInactive() {
        GreenPlayer gp = new GreenPlayer(100, 100);
        gp.onHit();
        assertFalse(gp.onHit());
    }

    @Test
    public void shouldReduceSpeedWhenShieldAbsorbs() {
        GreenPlayer gp = new GreenPlayer(100, 100);
        double speedBefore = gp.getSpeed();
        gp.onHit();
        assertTrue(gp.getSpeed() < speedBefore);
    }

    @Test
    public void shouldRestoreShieldAndSpeedOnReset() {
        GreenPlayer gp = new GreenPlayer(100, 100);
        double originalSpeed = gp.getSpeed();
        gp.onHit();
        gp.reset();
        assertTrue(gp.isShieldActive());
        assertEquals(originalSpeed, gp.getSpeed(), 0.001);
    }

    // ── onHit() para Red y Blue ──────────────────────────────────────────────

    @Test
    public void shouldRedPlayerAlwaysReturnFalseOnHit() {
        assertFalse(new RedPlayer(50, 50).onHit());
    }

    @Test
    public void shouldBluePlayerAlwaysReturnFalseOnHit() {
        assertFalse(new BluePlayer(50, 50).onHit());
    }

    // ── BluePlayer tamaño/velocidad ──────────────────────────────────────────

    @Test
    public void shouldBluePlayerBeWiderThanRed() {
        assertTrue(new BluePlayer(0, 0).getSize() > new RedPlayer(0, 0).getSize());
    }

    @Test
    public void shouldBluePlayerBeFasterThanRed() {
        assertTrue(new BluePlayer(0, 0).getSpeed() > new RedPlayer(0, 0).getSpeed());
    }

    // ── PlayerFactory switch: 3 ramas ────────────────────────────────────────

    @Test
    public void shouldFactoryCreateRedPlayer() {
        assertTrue(PlayerFactory.create(SkinType.RED, 10, 20) instanceof RedPlayer);
    }

    @Test
    public void shouldFactoryCreateBluePlayer() {
        assertTrue(PlayerFactory.create(SkinType.BLUE, 10, 20) instanceof BluePlayer);
    }

    @Test
    public void shouldFactoryCreateGreenPlayer() {
        assertTrue(PlayerFactory.create(SkinType.GREEN, 10, 20) instanceof GreenPlayer);
    }

    // ── restoreOriginalSkin / changeSkin ───────────────────────────────────────

    @Test
    public void shouldRestoreOriginalSkinCorrectly() {
        Player p = new RedPlayer(0, 0);
        assertEquals(SkinType.RED, p.getOriginalSkin());
        p.restoreOriginalSkin();
        assertEquals(SkinType.RED, p.getSkinType());
    }

    @Test
    public void shouldChangeSkinToBlueAndApplyStats() {
        Player p = new RedPlayer(100, 100);
        int origSize = p.getSize();
        double origSpeed = p.getSpeed();
        p.changeSkin(SkinType.BLUE);
        assertEquals("Skin debe ser BLUE", SkinType.BLUE, p.getSkinType());
        assertEquals("Size debe ser 30 (BluePlayer)", 30, p.getSize());
        assertEquals("Speed debe ser 4.5 (BluePlayer)", 4.5, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldChangeSkinToGreenAndApplyStats() {
        Player p = new BluePlayer(100, 100);
        p.changeSkin(SkinType.GREEN);
        assertEquals(SkinType.GREEN, p.getSkinType());
        assertEquals(24, p.getSize());
        assertEquals(3.0, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldRestoreOriginalStatsOnRestore() {
        Player p = new RedPlayer(100, 100);
        p.changeSkin(SkinType.BLUE);
        assertEquals(30, p.getSize());
        p.restoreOriginalSkin();
        assertEquals("Debe volver a RED", SkinType.RED, p.getSkinType());
        assertEquals("Size debe volver a 24", 24, p.getSize());
        assertEquals("Speed debe volver a 3.0", 3.0, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldRestoreSkinOnDeath() {
        Player p = new RedPlayer(100, 100);
        p.changeSkin(SkinType.BLUE);
        assertEquals(SkinType.BLUE, p.getSkinType());
        p.reset(); // simula muerte
        assertEquals("Muerte debe restaurar skin original", SkinType.RED, p.getSkinType());
        assertEquals("Stats deben restaurarse", 24, p.getSize());
    }

    @Test
    public void shouldChangeSkinTwiceAndKeepOriginal() {
        Player p = new RedPlayer(100, 100);
        p.changeSkin(SkinType.BLUE);
        p.changeSkin(SkinType.GREEN);
        assertEquals(SkinType.GREEN, p.getSkinType());
        assertEquals("Original sigue siendo RED", SkinType.RED, p.getOriginalSkin());
    }

    // ── Bordes del mapa ──────────────────────────────────────────────────
    @Test
    public void shouldClampPlayerAtTopBorder() {
        Player p = new RedPlayer(100, 0);
        p.move(true, false, false, false, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(0.0, p.getY(), 0.01);
    }

    @Test
    public void shouldClampPlayerAtBottomBorder() {
        Player p = new RedPlayer(100, Level.MAP_HEIGHT - 24);
        p.move(false, true, false, false, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(Level.MAP_HEIGHT - p.getSize(), p.getY(), 0.01);
    }

    @Test
    public void shouldClampPlayerAtLeftBorder() {
        Player p = new RedPlayer(0, 100);
        p.move(false, false, true, false, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(0.0, p.getX(), 0.01);
    }

    @Test
    public void shouldClampPlayerAtRightBorder() {
        Player p = new RedPlayer(Level.MAP_WIDTH - 24, 100);
        p.move(false, false, false, true, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(Level.MAP_WIDTH - p.getSize(), p.getX(), 0.01);
    }

    // ── loseLife e isDead ────────────────────────────────────────────────
    @Test
    public void shouldNotDecrementLivesBelowZero() {
        Player p = new RedPlayer(100, 100);
        p.setLives(0);
        p.loseLife();
        assertEquals(0, p.getLives());
    }

    @Test
    public void shouldReturnTrueIsDeadWhenNoLives() {
        Player p = new RedPlayer(100, 100);
        p.setLives(0);
        assertTrue(p.isDead());
    }

    @Test
    public void shouldReturnFalseIsDeadWhenHasLives() {
        Player p = new RedPlayer(100, 100);
        assertFalse(p.isDead());
    }

    // ── changeSkin los 3 casos ───────────────────────────────────────────
    @Test
    public void shouldChangeSkinToRed() {
        Player p = new BluePlayer(100, 100);
        p.changeSkin(SkinType.RED);
        assertEquals(SkinType.RED, p.getSkinType());
        assertEquals(24, p.getSize());
        assertEquals(3.0, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldChangeSkinToBlue() {
        Player p = new RedPlayer(100, 100);
        p.changeSkin(SkinType.BLUE);
        assertEquals(SkinType.BLUE, p.getSkinType());
        assertEquals(30, p.getSize());
        assertEquals(4.5, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldChangeSkinToGreen() {
        Player p = new RedPlayer(100, 100);
        p.changeSkin(SkinType.GREEN);
        assertEquals(SkinType.GREEN, p.getSkinType());
        assertEquals(24, p.getSize());
        assertEquals(3.0, p.getSpeed(), 0.001);
    }

    // ── restoreOriginalSkin los 3 casos ─────────────────────────────────
    @Test
    public void shouldRestoreRedSkinStats() {
        Player p = new RedPlayer(100, 100);
        p.changeSkin(SkinType.BLUE);
        p.restoreOriginalSkin();
        assertEquals(SkinType.RED, p.getSkinType());
        assertEquals(24, p.getSize());
        assertEquals(3.0, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldRestoreBlueSkinStats() {
        Player p = new BluePlayer(100, 100);
        p.changeSkin(SkinType.RED);
        p.restoreOriginalSkin();
        assertEquals(SkinType.BLUE, p.getSkinType());
        assertEquals(30, p.getSize());
        assertEquals(4.5, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldRestoreGreenSkinStats() {
        Player p = new GreenPlayer(100, 100);
        p.changeSkin(SkinType.RED);
        p.restoreOriginalSkin();
        assertEquals(SkinType.GREEN, p.getSkinType());
        assertEquals(24, p.getSize());
        assertEquals(3.0, p.getSpeed(), 0.001);
    }

    // ── borderColorIndex ─────────────────────────────────────────────────
    @Test
    public void shouldSetAndGetBorderColorIndex() {
        Player p = new RedPlayer(100, 100);
        p.setBorderColorIndex(2);
        assertEquals(2, p.getBorderColorIndex());
    }
    // ── GreenPlayer: onHit con escudo activo ─────────────────────────────
    @Test
    public void shouldAbsorbFirstHitWithShield() {
        GreenPlayer p = new GreenPlayer(100, 100);
        boolean absorbed = p.onHit();
        assertTrue("El primer golpe debe ser absorbido", absorbed);
        assertFalse("El escudo debe desactivarse", p.isShieldActive());
        assertEquals("La velocidad debe reducirse a la mitad",
                1.5, p.getSpeed(), 0.001);
    }

    @Test
    public void shouldDieOnSecondHit() {
        GreenPlayer p = new GreenPlayer(100, 100);
        p.onHit(); // consume el escudo
        boolean absorbed = p.onHit(); // segundo golpe
        assertFalse("El segundo golpe no debe absorberse", absorbed);
    }

    @Test
    public void shouldRestoreShieldOnReset() {
        GreenPlayer p = new GreenPlayer(100, 100);
        p.onHit(); // consume escudo
        p.reset();
        assertTrue("Reset debe restaurar el escudo", p.isShieldActive());
        assertEquals("Reset debe restaurar velocidad original",
                3.0, p.getSpeed(), 0.001);
    }

    // ── RedPlayer: onHit siempre retorna false ───────────────────────────
    @Test
    public void shouldRedPlayerAlwaysDieOnHit() {
        Player p = new RedPlayer(100, 100);
        assertFalse("RedPlayer no tiene escudo", p.onHit());
    }

    // ── Player.move(): las 4 ramas de borde ──────────────────────────────
    @Test
    public void shouldClampAtTopBorder() {
        Player p = new RedPlayer(100, 0);
        p.move(true, false, false, false, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(0.0, p.getY(), 0.01);
    }

    @Test
    public void shouldClampAtBottomBorder() {
        Player p = new RedPlayer(100, Level.MAP_HEIGHT);
        p.move(false, true, false, false, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(Level.MAP_HEIGHT - p.getSize(), p.getY(), 0.01);
    }

    @Test
    public void shouldClampAtLeftBorder() {
        Player p = new RedPlayer(0, 100);
        p.move(false, false, true, false, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(0.0, p.getX(), 0.01);
    }

    @Test
    public void shouldClampAtRightBorder() {
        Player p = new RedPlayer(Level.MAP_WIDTH, 100);
        p.move(false, false, false, true, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        assertEquals(Level.MAP_WIDTH - p.getSize(), p.getX(), 0.01);
    }

}
