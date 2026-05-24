package dominio;

import java.awt.Rectangle;

/**
 * Clase abstracta base para todos los jugadores.
 *
 * <p>Define comportamiento común: movimiento (incluyendo diagonal),
 * sistema de vidas, colisiones y reinicio. Cada subclase
 * ({@link RedPlayer}, {@link BluePlayer}, {@link GreenPlayer})
 * establece sus propias estadísticas mediante el constructor.</p>
 *
 * <p><b>Patrón Template Method:</b> el método {@link #onHit()} tiene
 * un comportamiento por defecto aquí, pero {@link GreenPlayer}
 * lo sobreescribe para implementar la mecánica del escudo.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 3.0
 * @since 2026-05-14
 */
public abstract class Player implements Collidable {

    /** Posición X actual del jugador. */
    protected double x;

    /** Posición Y actual del jugador. */
    protected double y;

    /** Posición X del último checkpoint activado (o spawn inicial). */
    protected double checkpointX;

    /** Posición Y del último checkpoint activado (o spawn inicial). */
    protected double checkpointY;

    /** Tamaño del jugador en píxeles (cuadrado). */
    protected int size;

    /** Velocidad del jugador en píxeles por tick. */
    protected double speed;

    /** Número de vidas restantes. */
    protected int lives;

    /** Tipo de skin activo actualmente. */
    protected SkinType skinType;

    /**
     * Skin original con el que fue creado el jugador.
     * Se usa para revertir el efecto de una {@link SkinCoin} al morir
     * o al recoger otra moneda.
     */
    private final SkinType originalSkin;

    /** Color del borde del jugador (índice de la paleta: 0=negro, 1=blanco, 2=dorado). */
    private int borderColorIndex;

    /** {@code true} si el escudo del skin GREEN todavía está activo. */
    protected boolean shieldActive;

    /** Número máximo de vidas por defecto. */
    public static final int DEFAULT_LIVES = 3;

    /**
     * Construye un jugador con posición, tamaño, velocidad y skin.
     *
     * @param x        coordenada X inicial.
     * @param y        coordenada Y inicial.
     * @param size     tamaño del jugador en píxeles.
     * @param speed    velocidad en píxeles/tick.
     * @param skinType tipo de skin del jugador.
     */
    public Player(double x, double y, int size, double speed, SkinType skinType) {
        this.x            = x;
        this.y            = y;
        this.checkpointX  = x;
        this.checkpointY  = y;
        this.size         = size;
        this.speed        = speed;
        this.skinType     = skinType;
        this.originalSkin = skinType;   // se fija al crear; nunca cambia
        this.lives        = DEFAULT_LIVES;
        this.borderColorIndex = 0;
        this.shieldActive = (skinType == SkinType.GREEN);
    }

    /**
     * Mueve al jugador según las direcciones activas, incluyendo movimiento
     * diagonal (cuando dos direcciones ortogonales se activan a la vez).
     *
     * @param up    mover hacia arriba.
     * @param down  mover hacia abajo.
     * @param left  mover hacia la izquierda.
     * @param right mover hacia la derecha.
     * @param mapW  ancho del área de juego en píxeles.
     * @param mapH  alto del área de juego en píxeles.
     */
    public void move(boolean up, boolean down,
                     boolean left, boolean right,
                     int mapW, int mapH) {

        if (up)    y = Math.max(0,           y - speed);
        if (down)  y = Math.min(mapH - size, y + speed);
        if (left)  x = Math.max(0,           x - speed);
        if (right) x = Math.min(mapW - size, x + speed);
    }

    /**
     * Procesa un impacto de enemigo.
     *
     * <p>Si el jugador tiene el skin GREEN y su escudo está activo,
     * el escudo se consume y la velocidad se reduce a la mitad.</p>
     *
     * @return {@code true} si el golpe fue absorbido (no muere),
     *         {@code false} si el jugador debe morir.
     */
    public boolean onHit() {
        if (skinType == SkinType.GREEN && shieldActive) {
            shieldActive = false;
            speed *= 0.5;
            return true;   // golpe absorbido
        }
        return false;      // sin escudo o no es verde → muere
    }

    /**
     * Activa un checkpoint: guarda la posición actual como punto de respawn.
     */
    public void activateCheckpoint() {
        checkpointX = x;
        checkpointY = y;
    }

    /**
     * Reinicia la posición del jugador al último checkpoint activado
     * y restaura el skin original (revirtiendo el efecto de SkinCoin).
     * Subclases pueden sobreescribir para restaurar estados adicionales.
     */
    public void reset() {
        x = checkpointX;
        y = checkpointY;
        restoreOriginalSkin();
    }

    /**
     * Reinicia completamente al jugador a su posición y estado de inicio.
     *
     * @param startX coordenada X de inicio absoluto.
     * @param startY coordenada Y de inicio absoluto.
     */
    public void fullReset(double startX, double startY) {
        x = startX;
        y = startY;
        checkpointX = startX;
        checkpointY = startY;
    }

    /**
     * Mueve instantáneamente al jugador a una coordenada sin alterar
     * su checkpoint.
     *
     * @param x coordenada X.
     * @param y coordenada Y.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // ─── Colisión ─────────────────────────────────────────────────────────────

    /**
     * Devuelve el rectángulo de colisión del jugador.
     *
     * @return {@link Rectangle} de colisión.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    /** @return posición X actual. */
    public double getX() { return x; }

    /** @return posición Y actual. */
    public double getY() { return y; }

    /** @return posición X del último checkpoint activado (spawn). */
    public double getCheckpointX() { return checkpointX; }

    /** @return posición Y del último checkpoint activado (spawn). */
    public double getCheckpointY() { return checkpointY; }

    /** @return tamaño del jugador. */
    public int getSize() { return size; }

    /** @return velocidad actual. */
    public double getSpeed() { return speed; }

    /** @return número de vidas restantes. */
    public int getLives() { return lives; }

    /**
     * Establece el número de vidas.
     *
     * @param lives nuevas vidas (debe ser &gt;= 0).
     */
    public void setLives(int lives) { this.lives = Math.max(0, lives); }

    /** Resta una vida al jugador. */
    public void loseLife() { if (lives > 0) lives--; }

    /** Agrega una vida al jugador. */
    public void gainLife() { lives++; }

    /** @return {@code true} si el jugador ya no tiene vidas. */
    public boolean isDead() { return lives <= 0; }

    /** @return tipo de skin activo. */
    public SkinType getSkinType() { return skinType; }

    /**
     * Devuelve el skin original con el que fue creado este jugador.
     * Permite revertir el efecto de una {@link SkinCoin}.
     *
     * @return {@link SkinType} original del jugador.
     */
    public SkinType getOriginalSkin() { return originalSkin; }

    /**
     * Restaura el skin activo al skin original del jugador,
     * incluyendo las estadísticas originales (tamaño y velocidad).
     * Se llama automáticamente al morir ({@link #reset()}) o al recoger
     * una nueva moneda de skin.
     */
    public void restoreOriginalSkin() {
        this.skinType = originalSkin;
        // Restaurar stats originales según el skin original
        switch (originalSkin) {
            case RED   -> { this.size = 24; this.speed = 3.0; }
            case BLUE  -> { this.size = 30; this.speed = 4.5; }
            case GREEN -> { this.size = 24; this.speed = 3.0; }
        }
        this.shieldActive = (originalSkin == SkinType.GREEN);
    }

    /**
     * Cambia temporalmente el skin del jugador al tipo indicado.
     * Aplica las estadísticas (tamaño/velocidad) del nuevo skin.
     * Se usa al recoger una {@link SkinCoin}.
     *
     * @param newSkin el nuevo tipo de skin a aplicar.
     */
    public void changeSkin(SkinType newSkin) {
        // Primero restaurar stats originales para evitar acumulación
        restoreOriginalSkin();
        this.skinType = newSkin;
        // Aplicar stats del nuevo skin
        switch (newSkin) {
            case RED   -> { this.size = 24; this.speed = 3.0; }
            case BLUE  -> { this.size = 30; this.speed = 4.5; }
            case GREEN -> { this.size = 24; this.speed = 3.0; }
        }
        this.shieldActive = (newSkin == SkinType.GREEN);
    }

    /** @return índice del color de borde seleccionado. */
    public int getBorderColorIndex() { return borderColorIndex; }

    /**
     * Establece el índice del color de borde.
     *
     * @param index índice de color (0=negro, 1=blanco, 2=dorado).
     */
    public void setBorderColorIndex(int index) { this.borderColorIndex = index; }

    /** @return {@code true} si el escudo está activo (solo útil con skin GREEN). */
    public boolean isShieldActive() { return shieldActive; }
}