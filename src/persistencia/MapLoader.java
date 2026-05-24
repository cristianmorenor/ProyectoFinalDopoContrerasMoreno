package persistencia;

import dominio.*;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cargador de niveles desde archivos de configuración {@code .txt}.
 *
 * <p>Lee el archivo línea por línea e instancia los elementos del nivel
 * según las palabras clave del formato de configuración definido:</p>
 *
 * <pre>
 * # Comentario (ignorado)
 * TIME_LIMIT 90
 * GOAL 680 180 80 140
 * ZONE INITIAL    0   0 90 500
 * ZONE INTERMEDIATE 350 0 80 500
 * COIN NORMAL 200 250
 * COIN SKIN   350 130 GREEN
 * ENEMY BASIC  200 100 18 HORIZONTAL  4.5
 * ENEMY FAST   480 200 18 VERTICAL    2.5
 * ENEMY PATROL 300 150 16 3.0 300,150;500,150;500,350;300,350
 * WALL 100 200 20 100
 * LIFE 400 460
 * BOMB 300 300
 * CHECKPOINT 340 180 80 140
 * </pre>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class MapLoader {

    /**
     * Carga un nivel desde el archivo de configuración indicado.
     *
     * @param path ruta al archivo {@code .txt}.
     * @return {@link Level} construido con todos los elementos del archivo.
     * @throws DopoGameException si el archivo contiene una línea inválida o no se puede leer.
     */
    public Level loadLevel(String path) throws DopoGameException {
        // Número de nivel inferido del nombre del archivo (levelN.txt)
        int levelNumber = extractLevelNumber(path);
        int timeLimit   = Level.DEFAULT_TIME_LIMIT;

        // Primera pasada: buscar TIME_LIMIT para construir Level
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("TIME_LIMIT")) {
                    String[] parts = line.split("\\s+");
                    timeLimit = Integer.parseInt(parts[1]);
                    break;
                }
            }
        } catch (java.io.IOException e) {
            throw new DopoGameException(DopoGameException.INVALID_CONFIG + ": No se pudo leer " + path);
        }

        Level level = new Level(levelNumber, timeLimit);

        // Limpiar elementos auto-generados del nivel 1 (solo aplica si levelNumber==1)
        // Para niveles 2 y 3 el Level() no agrega nada, así que no hay problema.

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("TIME_LIMIT")) {
                    continue;
                }
                parseLine(level, line, lineNum, path);
            }
        } catch (java.io.IOException e) {
            throw new DopoGameException(DopoGameException.INVALID_CONFIG + ": Error leyendo " + path);
        }

        return level;
    }

    /**
     * Interpreta una línea del archivo y agrega el elemento correspondiente al nivel.
     */
    private void parseLine(Level level, String line, int lineNum, String path)
            throws DopoGameException {
        String[] parts = line.split("\\s+");
        try {
            switch (parts[0].toUpperCase()) {
                case "GOAL"       -> parseGoal(level, parts);
                case "ZONE"       -> parseZone(level, parts);
                case "COIN"       -> parseCoin(level, parts);
                case "ENEMY"      -> parseEnemy(level, parts);
                case "WALL"       -> parseWall(level, parts);
                case "LIFE"       -> parseLife(level, parts);
                case "BOMB"       -> parseBomb(level, parts);
                case "CHECKPOINT" -> parseCheckpoint(level, parts);
                default           -> ErrorLogger.getInstance()
                                        .warn("Línea " + lineNum + " ignorada: " + line);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new DopoGameException(
                DopoGameException.INVALID_CONFIG + " en '" + path + "' línea " + lineNum + ": " + line);
        }
    }

    private void parseGoal(Level level, String[] p) {
        int x = Integer.parseInt(p[1]), y  = Integer.parseInt(p[2]);
        int w = Integer.parseInt(p[3]), h  = Integer.parseInt(p[4]);
        level.setGoalZone(new GoalZone(x, y, w, h));
    }

    private void parseZone(Level level, String[] p) {
        String type = p[1].toUpperCase();
        int x = Integer.parseInt(p[2]), y = Integer.parseInt(p[3]);
        int w = Integer.parseInt(p[4]), h = Integer.parseInt(p[5]);
        SafeZone sz = switch (type) {
            case "INITIAL"      -> new InitialZone(x, y, w, h);
            case "INTERMEDIATE" -> new IntermediateZone(x, y, w, h);
            default             -> new SafeZone(x, y, w, h);
        };
        level.addSafeZone(sz);
    }

    private void parseCoin(Level level, String[] p) {
        String type = p[1].toUpperCase();
        int x = Integer.parseInt(p[2]), y = Integer.parseInt(p[3]);
        if ("SKIN".equals(type) && p.length > 4) {
            SkinType skin = SkinType.valueOf(p[4].toUpperCase());
            level.addCoin(new SkinCoin(x, y, skin));
        } else {
            level.addCoin(new NormalCoin(x, y));
        }
    }

    private void parseEnemy(Level level, String[] p) {
        String type = p[1].toUpperCase();
        double x    = Double.parseDouble(p[2]);
        double y    = Double.parseDouble(p[3]);
        int radius  = Integer.parseInt(p[4]);

        switch (type) {
            case "BASIC" -> {
                BasicEnemy.Direction dir = BasicEnemy.Direction.valueOf(p[5].toUpperCase());
                double speed = Double.parseDouble(p[6]);
                level.addEnemy(new BasicEnemy(x, y, radius, dir, speed));
            }
            case "VERTICAL" -> {
                // Tipo V: deslizador vertical explícito
                double speed = Double.parseDouble(p[5]);
                level.addEnemy(new VerticalSliderEnemy(x, y, radius, speed));
            }
            case "FAST" -> {
                BasicEnemy.Direction dir = BasicEnemy.Direction.valueOf(p[5].toUpperCase());
                double baseSpeed = Double.parseDouble(p[6]);
                level.addEnemy(new FastEnemy(x, y, radius, dir, baseSpeed));
            }
            case "PATROL" -> {
                double speed = Double.parseDouble(p[5]);
                List<Point> waypoints = parseWaypoints(p[6]);
                level.addEnemy(new PatrolEnemy(x, y, radius, speed, waypoints));
            }
            default -> ErrorLogger.getInstance().warn("Tipo de enemigo desconocido: " + type);
        }
    }

    private void parseWall(Level level, String[] p) {
        int x = Integer.parseInt(p[1]), y = Integer.parseInt(p[2]);
        int w = Integer.parseInt(p[3]), h = Integer.parseInt(p[4]);
        level.addWall(new Wall(x, y, w, h));
    }

    private void parseLife(Level level, String[] p) {
        int x = Integer.parseInt(p[1]), y = Integer.parseInt(p[2]);
        level.addLifeSource(new LifeSource(x, y));
    }

    private void parseBomb(Level level, String[] p) {
        int x = Integer.parseInt(p[1]), y = Integer.parseInt(p[2]);
        level.addBomb(new Bomb(x, y));
    }

    private void parseCheckpoint(Level level, String[] p) {
        int x = Integer.parseInt(p[1]), y  = Integer.parseInt(p[2]);
        int w = Integer.parseInt(p[3]), h  = Integer.parseInt(p[4]);
        level.addCheckPoint(new CheckPoint(x, y, w, h));
    }

    /**
     * Parsea waypoints del formato {@code x1,y1;x2,y2;...}.
     */
    private List<Point> parseWaypoints(String raw) {
        List<Point> pts = new ArrayList<>();
        for (String token : raw.split(";")) {
            String[] xy = token.split(",");
            pts.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
        return pts;
    }

    /**
     * Extrae el número de nivel desde el nombre de archivo.
     * Ej: "configs/level2.txt" → 2.
     */
    private int extractLevelNumber(String path) {
        try {
            String name = path.replaceAll(".*level(\\d+)\\.txt", "$1");
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
