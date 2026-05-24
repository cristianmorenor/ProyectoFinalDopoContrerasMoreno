package dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del dominio de The DOPO Hardest Game.
 *
 * <p>Gestiona los niveles disponibles, el contador global de muertes,
 * el modo de juego activo y delega operaciones de persistencia a la
 * capa {@code persistencia}.</p>
 *
 * <p>La capa de presentación ({@code HardestGameGUI}) interactúa
 * <b>exclusivamente</b> con esta clase, sin acceder directamente
 * a entidades del dominio.</p>
 *
 * <p><b>Patrón Strategy:</b> el {@link GameMode} puede intercambiarse
 * en tiempo de ejecución mediante {@link #setGameMode(GameMode)}.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 4.0
 * @since 2026-05-03
 */
public class Game {

    // ── Estado global ────────────────────────────────────────────────────────

    /** Número de muertes acumuladas durante toda la partida. */
    private int deaths;

    /** Lista de niveles disponibles en la partida. */
    private List<Level> levels;

    /** Índice del nivel actualmente en juego (base 0). */
    private int currentLevelIndex;

    // ── Colaboradores ────────────────────────────────────────────────────────

    /** Gestor de colisiones del juego. */
    private final CollisionManager collisionManager;

    /** Modo de juego activo (Strategy Pattern). */
    private GameMode gameMode;

    /** Skin seleccionado para nuevas partidas. */
    private SkinType selectedSkin;

    // ── Vidas por nivel según dificultad ─────────────────────────────────────

    /**
     * Retorna el número de vidas que debe tener el jugador en un nivel dado.
     * A mayor nivel (mayor dificultad), menos vidas de inicio.
     *
     * @param levelNumber número de nivel (base 1).
     * @return vidas iniciales para ese nivel.
     */
    public static int livesForLevel(int levelNumber) {
        if (levelNumber <= 3)  return 5;
        if (levelNumber <= 6)  return 4;
        if (levelNumber <= 9)  return 3;
        return 2;  // nivel 10
    }

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Construye un nuevo juego con el modo y skin por defecto.
     */
    public Game() {
        this(SkinType.RED);
    }

    /**
     * Construye un nuevo juego con el skin indicado.
     *
     * @param selectedSkin skin del jugador 1.
     */
    public Game(SkinType selectedSkin) {
        this.deaths            = 0;
        this.levels            = new ArrayList<>();
        this.currentLevelIndex = 0;
        this.collisionManager  = new CollisionManager();
        this.gameMode          = new PlayerMode();
        this.selectedSkin      = selectedSkin;

        levels.add(new Level(1, selectedSkin));
    }

    // ── Bucle de juego ────────────────────────────────────────────────────────

    /**
     * Actualiza el estado del juego delegando en el modo activo.
     * Debe llamarse una vez por tick desde la presentación.
     */
    public void update() {
        gameMode.update(this);
    }

    // ── Registro de muertes ───────────────────────────────────────────────────

    /**
     * Registra la muerte del jugador principal: incrementa el contador
     * de muertes y reinicia su posición al último checkpoint.
     *
     * <p><b>Regla del juego:</b> no hay límite de vidas, solo un
     * contador de muertes hasta que se agote el tiempo.</p>
     */
    public void registerDeath() {
        deaths++;
        getCurrentLevel().resetPlayer();
    }

    /**
     * Registra la muerte de un jugador específico.
     * Solo incrementa el contador de muertes y reinicia la posición.
     *
     * @param player el jugador que murió.
     */
    public void registerDeathFor(Player player) {
        deaths++;
        player.reset();
    }

    // ── Navegación de niveles ─────────────────────────────────────────────────

    /**
     * Avanza al siguiente nivel si existe uno disponible.
     */
    public void nextLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            getCurrentLevel().getTimer().start();
        }
    }

    /**
     * Agrega un nuevo nivel al juego.
     *
     * @param level nivel a agregar.
     */
    public void addLevel(Level level) {
        levels.add(level);
    }

    // ── Reinicio ──────────────────────────────────────────────────────────────

    /**
     * Reinicia la partida completa desde el primer nivel.
     * Recrea el nivel 1 con el skin actualmente seleccionado.
     */
    public void reset() {
        deaths             = 0;
        currentLevelIndex  = 0;
        levels.clear();
        levels.add(new Level(1, selectedSkin));
    }

    /**
     * Reinicia únicamente el nivel actual: resetea monedas, bombas
     * y el timer. Reposiciona al jugador al checkpoint.
     * Se usa cuando el jugador quiere reintentar un nivel.
     */
    public void resetCurrentLevel() {
        Level lvl = getCurrentLevel();
        Player p  = lvl.getPlayer();
        if (p != null) {
            p.fullReset(p.getCheckpointX(), p.getCheckpointY());
        }
        lvl.resetCoins();
        lvl.resetBombs();
        lvl.getTimer().reset();
        lvl.getTimer().start();
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    /** @return el nivel actualmente en juego. */
    public Level getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    /** @return número total de muertes de la partida. */
    public int getDeaths() { return deaths; }

    /**
     * Establece el contador de muertes directamente (usado al cargar partida).
     *
     * @param deaths número de muertes a restaurar.
     */
    public void setDeaths(int deaths) { this.deaths = Math.max(0, deaths); }

    /**
     * Posiciona el juego en un nivel específico por índice (base 0).
     * Usado por {@code GameSaver} al restaurar una partida guardada.
     *
     * @param index índice (base 0) del nivel a activar.
     */
    public void setCurrentLevelIndex(int index) {
        if (index >= 0 && index < levels.size()) {
            this.currentLevelIndex = index;
        }
    }

    /** @return el gestor de colisiones. */
    public CollisionManager getCollisionManager() { return collisionManager; }

    /** @return el modo de juego activo. */
    public GameMode getGameMode() { return gameMode; }

    /**
     * Establece un nuevo modo de juego (Strategy Pattern).
     *
     * @param gameMode nuevo modo a activar.
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /** @return skin seleccionado para la partida. */
    public SkinType getSelectedSkin() { return selectedSkin; }

    /**
     * Establece el skin del jugador para la partida.
     *
     * @param skin tipo de skin seleccionado.
     */
    public void setSelectedSkin(SkinType skin) {
        this.selectedSkin = skin;
    }

    /** @return número del nivel actual (base 1). */
    public int getCurrentLevelNumber() {
        return getCurrentLevel().getLevelNumber();
    }

    /** @return lista de todos los niveles. */
    public List<Level> getLevels() { return levels; }

    /** @return índice del nivel actual (base 0). */
    public int getCurrentLevelIndex() { return currentLevelIndex; }
}
