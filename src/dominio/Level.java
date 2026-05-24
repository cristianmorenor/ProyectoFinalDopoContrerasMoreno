package dominio;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Representa un nivel completo del juego The DOPO Hardest Game.
 *
 * <p>Un nivel contiene todos los elementos del mapa: jugadores, enemigos,
 * monedas, paredes, zonas seguras, checkpoints, bombas, fuentes de vida,
 * y la zona de meta. También gestiona el temporizador de cuenta regresiva.</p>
 *
 * <p>Los niveles pueden cargarse desde archivos {@code .txt} usando
 * {@code persistencia.MapLoader}, o construirse directamente en código
 * (nivel 1 hardcodeado para demo y tests).</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 4.0
 * @since 2026-05-03
 */
public class Level {

    // ── Constantes del mapa ──────────────────────────────────────────────────

    /** Ancho del área de juego en píxeles. */
    public static final int MAP_WIDTH  = 760;

    /** Alto del área de juego en píxeles. */
    public static final int MAP_HEIGHT = 500;

    /** Tiempo límite por defecto en segundos si el nivel no especifica uno. */
    public static final int DEFAULT_TIME_LIMIT = 90;

    /**
     * Vidas por defecto para el jugador en el nivel 1 introductorio.
     * Se define aquí para evitar dependencia circular {@code Level → Game}.
     */
    public static final int DEFAULT_PLAYER_LIVES = 5;

    // ── Identificación ───────────────────────────────────────────────────────

    /** Número identificador del nivel (base 1). */
    private final int levelNumber;

    // ── Entidades ────────────────────────────────────────────────────────────

    /** Lista de jugadores del nivel (1 en modo normal, 2 en PvP/PvM). */
    private final List<Player> players;

    /** Lista de enemigos activos en el nivel. */
    private final List<Enemy> enemies;

    /** Lista de monedas del nivel. */
    private final List<Coin> coins;

    /** Lista de paredes estáticas del nivel. */
    private final List<Wall> walls;

    /** Lista de checkpoints del nivel. */
    private final List<CheckPoint> checkPoints;

    /** Lista de zonas seguras (inicio e intermedias). */
    private final List<SafeZone> safeZones;

    /** Lista de bombas del nivel. */
    private final List<Bomb> bombs;

    /** Lista de fuentes de vida del nivel. */
    private final List<LifeSource> lifeSources;

    // ── Objetivo y tiempo ────────────────────────────────────────────────────

    /** Zona de meta que el jugador debe alcanzar. */
    private GoalZone goalZone;

    /** Temporizador de cuenta regresiva. */
    private final GameTimer timer;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Construye el nivel 1 con skin RED por defecto (compatibilidad y tests).
     *
     * @param levelNumber número del nivel (mayor o igual a 1).
     */
    public Level(int levelNumber) {
        this(levelNumber, DEFAULT_TIME_LIMIT, SkinType.RED);
    }

    /**
     * Construye un nivel vacío con número, tiempo límite y skin.
     * Si {@code levelNumber == 1} inicializa automáticamente los
     * elementos hardcodeados del primer nivel introductorio.
     *
     * @param levelNumber      número del nivel.
     * @param timeLimitSeconds tiempo límite en segundos.
     */
    public Level(int levelNumber, int timeLimitSeconds) {
        this(levelNumber, timeLimitSeconds, SkinType.RED);
    }

    /**
     * Construye un nivel con número, tiempo límite y skin del jugador.
     *
     * @param levelNumber      número del nivel.
     * @param selectedSkin     skin del jugador principal.
     */
    public Level(int levelNumber, SkinType selectedSkin) {
        this(levelNumber, DEFAULT_TIME_LIMIT, selectedSkin);
    }

    /**
     * Constructor completo.
     *
     * @param levelNumber      número del nivel.
     * @param timeLimitSeconds tiempo límite en segundos.
     * @param selectedSkin     skin del jugador principal.
     */
    public Level(int levelNumber, int timeLimitSeconds, SkinType selectedSkin) {
        this.levelNumber = levelNumber;
        this.players     = new ArrayList<>();
        this.enemies     = new ArrayList<>();
        this.coins       = new ArrayList<>();
        this.walls       = new ArrayList<>();
        this.checkPoints = new ArrayList<>();
        this.safeZones   = new ArrayList<>();
        this.bombs       = new ArrayList<>();
        this.lifeSources = new ArrayList<>();
        this.timer       = new GameTimer(timeLimitSeconds);
        if (levelNumber == 1) {
            initLevel1(selectedSkin);
        }
    }

    // ── Inicialización nivel 1 (hardcodeado, introductorio) ─────────────────

    /**
     * Configura el nivel 1: nivel introductorio que muestra todos los tipos
     * de enemigo (Basic, Fast, Patrol), una fuente de vida visible,
     * un checkpoint, una SkinCoin y dificultad baja para aprender.
     *
     * @param skin skin del jugador principal.
     */
    private void initLevel1(SkinType skin) {
        // ── Jugador 1 (zona izquierda segura) ──────────────────────────────
        Player p = PlayerFactory.create(skin, 30, MAP_HEIGHT / 2.0 - 12);
        p.setLives(DEFAULT_PLAYER_LIVES);   // sin dependencia circular Level→Game
        players.add(p);

        // ── Zona de meta (derecha) ─────────────────────────────────────────
        goalZone = new GoalZone(MAP_WIDTH - 80, MAP_HEIGHT / 2 - 60, 70, 120);

        // ── Zona segura inicial (izquierda) ────────────────────────────────
        safeZones.add(new InitialZone(0, 0, 95, MAP_HEIGHT));

        // ── Zona segura intermedia (centro) ───────────────────────────────
        safeZones.add(new IntermediateZone(MAP_WIDTH / 2 - 45, 0, 90, MAP_HEIGHT));

        // ── Checkpoint funcional en zona intermedia ────────────────────────
        checkPoints.add(new CheckPoint(MAP_WIDTH / 2 - 35, MAP_HEIGHT / 2 - 30, 70, 60));

        // ── 3 monedas normales (pocas, bien distribuidas) ──────────────────
        coins.add(new NormalCoin(210, 150));
        coins.add(new NormalCoin(430, 250));
        coins.add(new NormalCoin(620, 350));

        // ── 1 SkinCoin (moneda de tipo especial, visible) ──────────────────
        coins.add(new SkinCoin(320, 400, SkinType.GREEN));

        // ── Enemigo BASIC horizontal (lento, introductorio) ───────────────
        enemies.add(new BasicEnemy(200, 130, 16, BasicEnemy.Direction.HORIZONTAL, 2.5));

        // ── Enemigo BASIC vertical ─────────────────────────────────────────
        enemies.add(new BasicEnemy(450, 200, 16, BasicEnemy.Direction.VERTICAL, 2.5));

        // ── Enemigo FAST (más rápido, diferenciable visualmente) ──────────
        enemies.add(new FastEnemy(600, 300, 14, BasicEnemy.Direction.HORIZONTAL, -2.0));

        // ── Enemigo PATROL (patrón triangular simple, visible) ────────────
        List<Point> triangle = Arrays.asList(
            new Point(250, 100),
            new Point(500, 100),
            new Point(380, 380)
        );
        enemies.add(new PatrolEnemy(250, 100, 14, 2.0, triangle));

        // ── Fuente de vida (bien visible en zona central-inferior) ─────────
        lifeSources.add(new LifeSource(380, 460));

        // ── Enemigo VERTICAL SLIDER (Tipo V, exclusivo vertical) ──────────
        enemies.add(new VerticalSliderEnemy(550, 150, 16, 3.0));

        // ── Pared sólida (barrera visible en zona central) ─────────────────
        walls.add(new Wall(470, 160, 15, 120));

        // ── Bomba (zona inferior-derecha, visible) ────────────────────────
        bombs.add(new Bomb(570, 430));
    }

    // ── Actualización del bucle de juego ─────────────────────────────────────

    /**
     * Actualiza todos los elementos móviles del nivel en cada tick.
     */
    public void update() {
        for (Enemy e : enemies) {
            e.update(MAP_WIDTH, MAP_HEIGHT);
        }
        timer.tick();
    }

    // ── Gestión de jugadores ─────────────────────────────────────────────────

    /**
     * Reinicia al jugador principal (índice 0) al último checkpoint activo.
     * También resetea monedas, bombas y fuentes de vida.
     */
    public void resetPlayer() {
        if (!players.isEmpty()) {
            players.get(0).reset();
        }
        resetCoins();
        resetBombs();
    }

    /**
     * Reinicia a todos los jugadores al último checkpoint activo.
     */
    public void resetAllPlayers() {
        for (Player p : players) {
            p.reset();
        }
        resetCoins();
        resetBombs();
    }

    /**
     * Devuelve todas las monedas y fuentes de vida a su estado inicial.
     */
    public void resetCoins() {
        for (Coin c : coins) c.reset();
        for (LifeSource ls : lifeSources) ls.reset();
    }

    /**
     * Reinicia las bombas a su estado sin explotar.
     */
    public void resetBombs() {
        for (Bomb b : bombs) b.reset();
    }

    // ── Consultas de estado ───────────────────────────────────────────────────

    /**
     * Indica si todas las monedas del nivel han sido recolectadas.
     *
     * @return {@code true} cuando no quedan monedas pendientes.
     */
    public boolean allCoinsCollected() {
        for (Coin c : coins) {
            if (!c.isCollected()) return false;
        }
        return true;
    }

    /**
     * Devuelve el número de monedas ya recolectadas.
     *
     * @return cantidad de monedas recogidas.
     */
    public int collectedCoinsCount() {
        int count = 0;
        for (Coin c : coins) {
            if (c.isCollected()) count++;
        }
        return count;
    }

    // ── Métodos de construcción (usados por MapLoader) ────────────────────────

    /** Agrega un jugador al nivel. */
    public void addPlayer(Player p) { players.add(p); }

    /** Agrega un enemigo al nivel. */
    public void addEnemy(Enemy e) { enemies.add(e); }

    /**
     * Elimina de la lista de enemigos todos aquellos cuya bounding-box
     * se superponga con el rectángulo dado (usado al explotar una bomba).
     *
     * @param bounds rectángulo de explosión.
     */
    public void removeEnemiesInBounds(java.awt.Rectangle bounds) {
        enemies.removeIf(e -> bounds.intersects(e.getBounds()));
    }


    /** Agrega una moneda al nivel. */
    public void addCoin(Coin c) { coins.add(c); }

    /** Agrega una pared al nivel. */
    public void addWall(Wall w) { walls.add(w); }

    /** Agrega un checkpoint al nivel. */
    public void addCheckPoint(CheckPoint cp) { checkPoints.add(cp); }

    /** Agrega una zona segura al nivel. */
    public void addSafeZone(SafeZone sz) { safeZones.add(sz); }

    /** Agrega una bomba al nivel. */
    public void addBomb(Bomb b) { bombs.add(b); }

    /** Agrega una fuente de vida al nivel. */
    public void addLifeSource(LifeSource ls) { lifeSources.add(ls); }

    /** Establece la zona de meta del nivel. */
    public void setGoalZone(GoalZone gz) { this.goalZone = gz; }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return número identificador del nivel. */
    public int getLevelNumber() { return levelNumber; }

    /** @return el jugador principal (índice 0). */
    public Player getPlayer() {
        return players.isEmpty() ? null : players.get(0);
    }

    /**
     * @return vista no modificable de todos los jugadores del nivel.
     *         Usar {@link #addPlayer(Player)} para agregar jugadores.
     */
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }

    /** @return vista no modificable de los enemigos activos. */
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }

    /** @return vista no modificable de las monedas. */
    public List<Coin> getCoins() { return Collections.unmodifiableList(coins); }

    /** @return vista no modificable de las paredes. */
    public List<Wall> getWalls() { return Collections.unmodifiableList(walls); }

    /** @return vista no modificable de los checkpoints. */
    public List<CheckPoint> getCheckPoints() { return Collections.unmodifiableList(checkPoints); }

    /** @return vista no modificable de las zonas seguras. */
    public List<SafeZone> getSafeZones() { return Collections.unmodifiableList(safeZones); }

    /** @return vista no modificable de las bombas. */
    public List<Bomb> getBombs() { return Collections.unmodifiableList(bombs); }

    /** @return vista no modificable de las fuentes de vida. */
    public List<LifeSource> getLifeSources() { return Collections.unmodifiableList(lifeSources); }

    /** @return zona de meta del nivel. */
    public GoalZone getGoalZone() { return goalZone; }

    /** @return temporizador del nivel. */
    public GameTimer getTimer() { return timer; }

    // ── Compatibilidad con código previo ──────────────────────────────────────

    /**
     * Devuelve la meta como {@link Goal} para compatibilidad con tests previos.
     *
     * @return nuevo {@link Goal} basado en la GoalZone activa, o {@code null}.
     * @deprecated Usar {@link #getGoalZone()} en código nuevo.
     */
    @Deprecated
    public Goal getGoal() {
        if (goalZone == null) return null;
        return new Goal(goalZone.getX(), goalZone.getY(),
                        goalZone.getWidth(), goalZone.getHeight());
    }

    /**
     * @deprecated Usar {@link #getEnemies()} en código nuevo.
     * @return lista de obstáculos (vacía — los enemigos son Enemy, no Obstacle).
     */
  
}