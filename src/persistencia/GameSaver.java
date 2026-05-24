package persistencia;

import dominio.*;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de persistencia para guardar y cargar el estado completo de una partida.
 *
 * <p><b>Formato del archivo {@code .dopo}:</b></p>
 * <pre>
 *   deaths=5
 *   level=3
 *   skin=RED
 *   lives=4
 *   mode=PLAYER
 *   shield=true
 *   player.count=2
 *   player.0.x=120.0
 *   player.0.y=230.0
 *   player.0.checkpointX=30.0
 *   player.0.checkpointY=238.0
 *   player.1.x=680.0
 *   player.1.y=230.0
 *   player.1.checkpointX=680.0
 *   player.1.checkpointY=230.0
 *   timer.remainingTicks=2400
 *   coins.collected=1,0,1,0,1
 *   bombs.exploded=0,1,0
 *   lifesources.collected=1,0
 *   checkpoints.activated=0,1
 *   enemies.x=200.0,450.0,600.0
 *   enemies.y=130.0,200.0,300.0
 *   enemies.speed=2.5,-2.5,4.0
 * </pre>
 *
 * <p>Retrocompatibilidad: si el archivo usa el formato antiguo {@code player.x=},
 * se lee correctamente para el jugador 0.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 5.0
 * @since 2026-05-20
 */
public class GameSaver {

    /** Extension de los archivos de guardado. */
    public static final String SAVE_EXTENSION = ".dopo";

    // ── Guardar ────────────────────────────────────────────────────────────────

    /**
     * Guarda el estado completo de la partida incluyendo todos los jugadores
     * (util en modos PvP/PvM para restaurar la posicion del jugador 2).
     *
     * @param game     el juego cuyo estado se guarda.
     * @param path     ruta base del archivo (sin extension).
     * @param modeName modo activo: "PLAYER", "PVP", "PVM_RANDOM", "PVM_EXPERT".
     * @throws DopoGameException si ocurre un error de escritura.
     */
    public void save(Game game, String path, String modeName) throws DopoGameException {
        String fullPath = path + SAVE_EXTENSION;
        Level  lvl      = game.getCurrentLevel();
        Player p        = lvl.getPlayer();
        int    lives    = (p != null) ? p.getLives() : Game.livesForLevel(game.getCurrentLevelNumber());
        boolean shield  = (p instanceof GreenPlayer gp) && gp.isShieldActive();

        try (PrintWriter pw = new PrintWriter(new FileWriter(fullPath))) {
            // ── Datos globales ─────────────────────────────────────────────────
            pw.println("deaths=" + game.getDeaths());
            pw.println("level="  + game.getCurrentLevelNumber());
            pw.println("skin="   + game.getSelectedSkin().name());
            pw.println("lives="  + lives);
            pw.println("mode="   + (modeName != null ? modeName.toUpperCase() : "PLAYER"));
            pw.println("shield=" + shield);

            // ── Posiciones de TODOS los jugadores (PvP/PvM) ───────────────────
            List<Player> players = lvl.getPlayers();
            pw.println("player.count=" + players.size());
            for (int i = 0; i < players.size(); i++) {
                Player pi = players.get(i);
                pw.println("player." + i + ".x="           + pi.getX());
                pw.println("player." + i + ".y="           + pi.getY());
                pw.println("player." + i + ".checkpointX=" + pi.getCheckpointX());
                pw.println("player." + i + ".checkpointY=" + pi.getCheckpointY());
                // Guardar tipo de skin de cada jugador
                pw.println("player." + i + ".skinType="   + pi.getSkinType().name());
            }
            // Retrocompatibilidad: formato antiguo para jugador 0
            if (p != null) {
                pw.println("player.x="           + p.getX());
                pw.println("player.y="           + p.getY());
                pw.println("player.checkpointX=" + p.getCheckpointX());
                pw.println("player.checkpointY=" + p.getCheckpointY());
            }

            // ── Tiempo restante ────────────────────────────────────────────────
            pw.println("timer.remainingTicks=" + lvl.getTimer().getRemainingTicks());

            // ── Monedas (indice -> recogida) ───────────────────────────────────
            pw.println("coins.collected="    + booleanList(lvl.getCoins(),
                c -> ((Coin) c).isCollected()));

            // ── Bombas (indice -> explotada) ───────────────────────────────────
            pw.println("bombs.exploded="     + booleanList(lvl.getBombs(),
                b -> ((Bomb) b).isExploded()));

            // ── Fuentes de vida (indice -> usada) ──────────────────────────────
            pw.println("lifesources.collected=" + booleanList(lvl.getLifeSources(),
                ls -> ((LifeSource) ls).isCollected()));

            // ── Checkpoints (indice -> activado) ──────────────────────────────
            pw.println("checkpoints.activated=" + booleanList(lvl.getCheckPoints(),
                cp -> ((CheckPoint) cp).isActivated()));

            // ── Enemigos (X, Y, speed) ─────────────────────────────────────────
            List<Enemy> enemies = lvl.getEnemies();
            pw.println("enemies.x="     + doubleList(enemies, e -> ((Enemy) e).getX()));
            pw.println("enemies.y="     + doubleList(enemies, e -> ((Enemy) e).getY()));
            pw.println("enemies.speed=" + doubleList(enemies, e -> ((Enemy) e).getSpeed()));

            ErrorLogger.getInstance().warn("Partida guardada en: " + fullPath);
        } catch (IOException e) {
            ErrorLogger.getInstance().log(e);
            throw new DopoGameException(DopoGameException.GAME_STATE_ERROR + ": "
                    + "No se pudo guardar la partida en: " + fullPath);
        }
    }

    /**
     * Sobrecarga de compatibilidad: guarda con modo PLAYER por defecto.
     *
     * @param game el juego cuyo estado se guarda.
     * @param path ruta base del archivo (sin extension).
     * @throws DopoGameException si ocurre un error de escritura.
     */
    public void save(Game game, String path) throws DopoGameException {
        save(game, path, "PLAYER");
    }

    // ── Cargar ─────────────────────────────────────────────────────────────────

    /**
     * Información guardada de un jugador.
     */
    public static class SavedPlayerInfo {
        public final double x, y, checkpointX, checkpointY;
        public final SkinType skinType;

        public SavedPlayerInfo(double x, double y, double cpx, double cpy, SkinType skin) {
            this.x = x;
            this.y = y;
            this.checkpointX = cpx;
            this.checkpointY = cpy;
            this.skinType = skin;
        }
    }

    /**
     * Resultado de una operacion de carga completa.
     */
    public static class LoadResult {
        /** Juego con estado completamente restaurado. */
        public final Game game;
        /** Modo de juego guardado. */
        public final String modeName;
        /**
         * Información guardada de cada jugador.
         * El indice 0 es el jugador 1, el 1 es el jugador 2, etc.
         * Se usa para restaurar posiciones y skins en modos multi-jugador (PvP/PvM).
         */
        public final List<SavedPlayerInfo> savedPlayerInfos;
        /**
         * Posiciones guardadas de cada jugador (para retrocompatibilidad).
         * Cada entrada es {@code [x, y, checkpointX, checkpointY]}.
         */
        public final List<double[]> savedPlayerPositions;

        public LoadResult(Game game, String modeName, List<SavedPlayerInfo> infos, List<double[]> positions) {
            this.game                 = game;
            this.modeName             = modeName;
            this.savedPlayerInfos     = infos;
            this.savedPlayerPositions = positions;
        }
    }

    /**
     * Carga el estado completo de una partida guardada.
     *
     * <p>Restaura: nivel, muertes, skin, vidas, modo, escudo del green,
     * posicion exacta de todos los jugadores, tiempo restante, monedas recogidas,
     * bombas explotadas, fuentes de vida usadas, checkpoints activados
     * y posicion+velocidad de cada enemigo.</p>
     *
     * @param path ruta base del archivo (sin extension).
     * @return {@link LoadResult} con el juego restaurado y el modo guardado.
     * @throws DopoGameException si el archivo no existe o tiene formato invalido.
     */
    public LoadResult loadFull(String path) throws DopoGameException {
        String fullPath = path + SAVE_EXTENSION;
        if (!Files.exists(Paths.get(fullPath))) {
            throw new DopoGameException(DopoGameException.GAME_STATE_ERROR + ": "
                    + "Archivo de guardado no encontrado: " + fullPath);
        }

        // ── Valores por defecto ────────────────────────────────────────────────
        int      deaths      = 0;
        int      levelNumber = 1;
        int      lives       = -1;
        SkinType skin        = SkinType.RED;
        String   modeName    = "PLAYER";
        boolean  shield      = true;

        double   playerX     = -1;
        double   playerY     = -1;
        double   checkpointX = -1;
        double   checkpointY = -1;
        int      timerTicks  = -1;

        // Información de jugadores por indice (nuevo formato)
        Map<Integer, Object[]> playerInfoMap = new HashMap<>();  // [x, y, cpx, cpy, skinType]
        // Posiciones de jugadores por indice (antiguo formato, para retrocompatibilidad)
        Map<Integer, double[]> playerPosMap = new HashMap<>();

        String   coinsStr        = null;
        String   bombsStr        = null;
        String   lifesStr        = null;
        String   checkpointsStr  = null;
        String   enemiesXStr     = null;
        String   enemiesYStr     = null;
        String   enemiesSpeedStr = null;

        try (BufferedReader br = new BufferedReader(new FileReader(fullPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if      (line.startsWith("deaths="))               deaths      = parseInt(line, 7);
                else if (line.startsWith("level="))                levelNumber = parseInt(line, 6);
                else if (line.startsWith("skin="))                 skin        = SkinType.valueOf(line.substring(5).toUpperCase().trim());
                else if (line.startsWith("lives="))                lives       = parseInt(line, 6);
                else if (line.startsWith("mode="))                 modeName    = line.substring(5).toUpperCase().trim();
                else if (line.startsWith("shield="))               shield      = Boolean.parseBoolean(line.substring(7));
                // Nuevo formato multi-jugador: player.N.x=
                else if (line.matches("player\\.\\d+\\.x=.*")) {
                    int idx = parsePlayerIndex(line);
                    Object[] info = playerInfoMap.computeIfAbsent(idx, k -> new Object[]{-1.0, -1.0, -1.0, -1.0, null});
                    info[0] = parseDouble(line, line.indexOf('=') + 1);
                } else if (line.matches("player\\.\\d+\\.y=.*")) {
                    int idx = parsePlayerIndex(line);
                    Object[] info = playerInfoMap.computeIfAbsent(idx, k -> new Object[]{-1.0, -1.0, -1.0, -1.0, null});
                    info[1] = parseDouble(line, line.indexOf('=') + 1);
                } else if (line.matches("player\\.\\d+\\.checkpointX=.*")) {
                    int idx = parsePlayerIndex(line);
                    Object[] info = playerInfoMap.computeIfAbsent(idx, k -> new Object[]{-1.0, -1.0, -1.0, -1.0, null});
                    info[2] = parseDouble(line, line.indexOf('=') + 1);
                } else if (line.matches("player\\.\\d+\\.checkpointY=.*")) {
                    int idx = parsePlayerIndex(line);
                    Object[] info = playerInfoMap.computeIfAbsent(idx, k -> new Object[]{-1.0, -1.0, -1.0, -1.0, null});
                    info[3] = parseDouble(line, line.indexOf('=') + 1);
                } else if (line.matches("player\\.\\d+\\.skinType=.*")) {
                    int idx = parsePlayerIndex(line);
                    Object[] info = playerInfoMap.computeIfAbsent(idx, k -> new Object[]{-1.0, -1.0, -1.0, -1.0, null});
                    try {
                        info[4] = SkinType.valueOf(line.substring(line.indexOf('=') + 1).toUpperCase().trim());
                    } catch (IllegalArgumentException e) {
                        info[4] = null;  // fallback a default
                    }
                    // No asignar valores numéricos a partir de la línea skinType;
                    // ese campo sólo guarda la enumeración del skin.
                // Formato antiguo (retrocompatibilidad, jugador 0)
                } else if (line.startsWith("player.x="))           playerX     = parseDouble(line, 9);
                else if (line.startsWith("player.y="))             playerY     = parseDouble(line, 9);
                else if (line.startsWith("player.checkpointX="))   checkpointX = parseDouble(line, 20);
                else if (line.startsWith("player.checkpointY="))   checkpointY = parseDouble(line, 20);
                else if (line.startsWith("timer.remainingTicks=")) timerTicks  = parseInt(line, 21);
                else if (line.startsWith("coins.collected="))       coinsStr    = line.substring(16);
                else if (line.startsWith("bombs.exploded="))        bombsStr    = line.substring(15);
                else if (line.startsWith("lifesources.collected=")) lifesStr    = line.substring(22);
                else if (line.startsWith("checkpoints.activated=")) checkpointsStr = line.substring(22);
                else if (line.startsWith("enemies.x="))            enemiesXStr     = line.substring(10);
                else if (line.startsWith("enemies.y="))            enemiesYStr     = line.substring(10);
                else if (line.startsWith("enemies.speed="))        enemiesSpeedStr = line.substring(14);
            }
        } catch (IOException | IllegalArgumentException e) {
            ErrorLogger.getInstance().log(e);
            throw new DopoGameException(DopoGameException.GAME_STATE_ERROR + ": "
                    + "Error al leer el archivo de guardado: " + fullPath);
        }

        // Fusionar formato antiguo en el mapa si el nuevo no tiene el jugador 0
        if (!playerPosMap.containsKey(0) && playerX >= 0 && playerY >= 0) {
            playerPosMap.put(0, new double[]{playerX, playerY, checkpointX, checkpointY});
        }

        // ── Construir el juego ─────────────────────────────────────────────────
        Game game = new Game(skin);
        game.setDeaths(deaths);

        // Cargar niveles 2..levelNumber desde configs
        MapLoader loader = new MapLoader();
        for (int n = 2; n <= levelNumber; n++) {
            String configPath = "configs/level" + n + ".txt";
            try {
                Level lvl = loader.loadLevel(configPath);
                Player pl = PlayerFactory.create(skin, 30, Level.MAP_HEIGHT / 2.0 - 12);
                pl.setLives(Game.livesForLevel(n));
                lvl.addPlayer(pl);
                game.addLevel(lvl);
            } catch (Exception e) {
                ErrorLogger.getInstance().warn("No se pudo cargar " + configPath + ": " + e.getMessage());
                Level fallback = new Level(n, skin);
                game.addLevel(fallback);
            }
        }

        // Posicionar en el nivel guardado
        game.setCurrentLevelIndex(levelNumber - 1);
        Level lvl = game.getCurrentLevel();

        // ── Restaurar jugador 0 ────────────────────────────────────────────────
        Player p = lvl.getPlayer();
        double[] pos0 = playerPosMap.get(0);
        if (p != null) {
            // Vidas
            if (lives >= 0) p.setLives(lives);

            // Posicion exacta (usar nuevo formato si existe, sino antiguo)
            double px = (pos0 != null && pos0[0] >= 0) ? pos0[0] : playerX;
            double py = (pos0 != null && pos0[1] >= 0) ? pos0[1] : playerY;
            double cpx = (pos0 != null && pos0[2] >= 0) ? pos0[2] : checkpointX;
            double cpy = (pos0 != null && pos0[3] >= 0) ? pos0[3] : checkpointY;

            if (px >= 0 && py >= 0) {
                p.fullReset(px, py);
            }
            // Checkpoint guardado
            if (cpx >= 0 && cpy >= 0) {
                double savedX = p.getX();
                double savedY = p.getY();
                p.fullReset(cpx, cpy);
                p.activateCheckpoint();
                p.fullReset(savedX, savedY);
            }

            // Estado del escudo (GreenPlayer)
            if (p instanceof GreenPlayer gp) {
                if (!shield && gp.isShieldActive()) {
                    gp.onHit();  // consume el escudo para restaurar el estado correcto
                }
            }
        }

        // ── Restaurar timer ────────────────────────────────────────────────────
        if (timerTicks >= 0) {
            lvl.getTimer().setRemainingTicks(timerTicks);
        }

        // ── Restaurar monedas ──────────────────────────────────────────────────
        restoreBooleanList(coinsStr, lvl.getCoins(), (obj, val) -> {
            if (val) ((Coin) obj).collect();
        });

        // ── Restaurar bombas ───────────────────────────────────────────────────
        restoreBooleanList(bombsStr, lvl.getBombs(), (obj, val) -> {
            if (val) ((Bomb) obj).explode();
        });

        // ── Restaurar fuentes de vida ──────────────────────────────────────────
        restoreBooleanList(lifesStr, lvl.getLifeSources(), (obj, val) -> {
            if (val) ((LifeSource) obj).collect();
        });

        // ── Restaurar checkpoints activados ────────────────────────────────────
        if (checkpointsStr != null && p != null) {
            String[] parts = checkpointsStr.split(",");
            List<CheckPoint> cps = lvl.getCheckPoints();
            for (int i = 0; i < parts.length && i < cps.size(); i++) {
                if ("1".equals(parts[i].trim())) {
                    cps.get(i).tryActivate(p);
                }
            }
        }

        // ── Restaurar enemigos ─────────────────────────────────────────────────
        if (enemiesXStr != null && enemiesYStr != null && enemiesSpeedStr != null) {
            String[] xs     = enemiesXStr.split(",");
            String[] ys     = enemiesYStr.split(",");
            String[] speeds = enemiesSpeedStr.split(",");
            List<Enemy> enemies = lvl.getEnemies();
            for (int i = 0; i < enemies.size() && i < xs.length; i++) {
                try {
                    enemies.get(i).setX(Double.parseDouble(xs[i].trim()));
                    enemies.get(i).setY(Double.parseDouble(ys[i].trim()));
                    enemies.get(i).setSpeed(Double.parseDouble(speeds[i].trim()));
                } catch (NumberFormatException ignore) { /* entrada corrupta */ }
            }
        }

        // ── Construir lista de información de jugadores ──────────────────────────
        List<SavedPlayerInfo> savedPlayerInfos = new ArrayList<>();
        for (int i = 0; i < playerInfoMap.size(); i++) {
            Object[] info = playerInfoMap.get(i);
            if (info != null) {
                double x = (Double) info[0];
                double y = (Double) info[1];
                double cpx = (Double) info[2];
                double cpy = (Double) info[3];
                SkinType st = (SkinType) info[4];
                if (st == null) st = skin;  // usar skin global como fallback
                savedPlayerInfos.add(new SavedPlayerInfo(x, y, cpx, cpy, st));
            }
        }

        // ── Construir lista de posiciones por indice (retrocompatibilidad) ─────
        List<double[]> savedPositions = new ArrayList<>();
        for (int i = 0; i < playerInfoMap.size(); i++) {
            Object[] info = playerInfoMap.get(i);
            if (info != null) {
                double x = (Double) info[0];
                double y = (Double) info[1];
                double cpx = (Double) info[2];
                double cpy = (Double) info[3];
                savedPositions.add(new double[]{x, y, cpx, cpy});
            }
        }

        ErrorLogger.getInstance().warn("Partida cargada desde: " + fullPath
            + " (nivel=" + levelNumber + ", muertes=" + deaths + ", modo=" + modeName + ")");
        return new LoadResult(game, modeName, savedPlayerInfos, savedPositions);
    }

    /**
     * Carga compatible retroactiva: retorna solo el {@link Game}.
     *
     * @param path ruta base del archivo (sin extension).
     * @return un nuevo {@link Game} con el estado restaurado.
     * @throws DopoGameException si el archivo no existe o tiene formato invalido.
     */
    public Game load(String path) throws DopoGameException {
        return loadFull(path).game;
    }

    /**
     * Comprueba si existe un archivo de guardado.
     *
     * @param path ruta sin extension.
     * @return {@code true} si el archivo de guardado existe.
     */
    public boolean saveExists(String path) {
        return Files.exists(Paths.get(path + SAVE_EXTENSION));
    }

    // ── Helpers internos ───────────────────────────────────────────────────────

    @FunctionalInterface
    private interface BooleanExtractor {
        boolean apply(Object obj);
    }

    @FunctionalInterface
    private interface DoubleExtractor {
        double apply(Object obj);
    }

    @FunctionalInterface
    private interface StateSetter {
        void apply(Object obj, boolean val);
    }

    /** Convierte una lista a cadena de 0/1 separados por coma. */
    private <T> String booleanList(List<T> list, BooleanExtractor fn) {
        if (list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (T item : list) {
            if (sb.length() > 0) sb.append(',');
            sb.append(fn.apply(item) ? '1' : '0');
        }
        return sb.toString();
    }

    /** Convierte una lista a cadena de doubles separados por coma. */
    private <T> String doubleList(List<T> list, DoubleExtractor fn) {
        if (list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (T item : list) {
            if (sb.length() > 0) sb.append(',');
            sb.append(fn.apply(item));
        }
        return sb.toString();
    }

    /** Restaura una lista de objetos desde una cadena de 0/1. */
    private <T> void restoreBooleanList(String csv, List<T> list, StateSetter setter) {
        if (csv == null || csv.isBlank()) return;
        String[] parts = csv.split(",");
        for (int i = 0; i < parts.length && i < list.size(); i++) {
            setter.apply(list.get(i), "1".equals(parts[i].trim()));
        }
    }

    /**
     * Extrae el indice N de una linea con formato {@code player.N.campo=valor}.
     *
     * @param line linea del archivo de guardado.
     * @return indice del jugador.
     */
    private int parsePlayerIndex(String line) {
        // "player.N.xxx=..." -> indice N
        int dot1 = line.indexOf('.');       // despues de "player"
        int dot2 = line.indexOf('.', dot1 + 1);
        return Integer.parseInt(line.substring(dot1 + 1, dot2));
    }

    private int    parseInt(String line, int from)    { return Integer.parseInt(line.substring(from).trim()); }
    private double parseDouble(String line, int from) { return Double.parseDouble(line.substring(from).trim()); }
}
