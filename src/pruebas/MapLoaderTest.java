package pruebas;

import dominio.*;
import persistencia.MapLoader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;

/**
 * Pruebas unitarias para {@link MapLoader}.
 * Crea archivos temporales en memoria para no depender del filesystem.
 * Nomenclatura: shouldXxx / shouldNotXxx.
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class MapLoaderTest {

    private MapLoader loader;
    private File tempFile;

    @Before
    public void setUp() throws IOException {
        loader = new MapLoader();
        // Usamos prefijo "level2" para que extractLevelNumber() retorne 2
        // y el Level se cree vacío (sin datos hardcodeados del nivel 1).
        tempFile = File.createTempFile("level2_test", ".txt");
        tempFile.deleteOnExit();
    }

    /**
     * Escribe contenido en el archivo temporal.
     */
    private void writeConfig(String content) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            pw.print(content);
        }
    }

    // ── Tiempo límite ─────────────────────────────────────────────────────────

    @Test
    public void shouldLoadTimeLimitFromFile() throws Exception {
        writeConfig("TIME_LIMIT 75\nGOAL 680 180 80 140\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertEquals("Tiempo límite debe ser 75", 75, lvl.getTimer().getTimeLimitSeconds());
    }

    @Test
    public void shouldUseDefaultTimeLimitWhenNotSpecified() throws Exception {
        writeConfig("# sin TIME_LIMIT\nGOAL 680 180 80 140\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertEquals(Level.DEFAULT_TIME_LIMIT, lvl.getTimer().getTimeLimitSeconds());
    }

    // ── GoalZone ─────────────────────────────────────────────────────────────

    @Test
    public void shouldLoadGoalZoneFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertNotNull("GoalZone no debe ser null", lvl.getGoalZone());
        assertEquals(680, lvl.getGoalZone().getX());
        assertEquals(200, lvl.getGoalZone().getY());
    }

    // ── Monedas ───────────────────────────────────────────────────────────────

    @Test
    public void shouldLoadNormalCoinFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nCOIN NORMAL 200 250\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse("Debe cargar al menos una moneda", lvl.getCoins().isEmpty());
        assertTrue("Debe ser NormalCoin", lvl.getCoins().get(0) instanceof NormalCoin);
    }

    @Test
    public void shouldLoadSkinCoinFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nCOIN SKIN 300 300 GREEN\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse(lvl.getCoins().isEmpty());
        // Busca la SkinCoin entre todas las monedas cargadas
        boolean hasSkinCoin = lvl.getCoins().stream().anyMatch(c -> c instanceof SkinCoin);
        assertTrue("Debe haber una SkinCoin", hasSkinCoin);
        SkinCoin sc = (SkinCoin) lvl.getCoins().stream()
                .filter(c -> c instanceof SkinCoin).findFirst().get();
        assertEquals(SkinType.GREEN, sc.getTargetSkin());
    }

    // ── Enemigos ──────────────────────────────────────────────────────────────

    @Test
    public void shouldLoadBasicEnemyFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nENEMY BASIC 200 100 18 HORIZONTAL 4.5\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse("Debe cargar al menos un enemigo", lvl.getEnemies().isEmpty());
        assertTrue(lvl.getEnemies().get(0) instanceof BasicEnemy);
    }

    @Test
    public void shouldLoadFastEnemyFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nENEMY FAST 300 200 18 VERTICAL 2.0\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse(lvl.getEnemies().isEmpty());
        boolean hasFast = lvl.getEnemies().stream().anyMatch(e -> e instanceof FastEnemy);
        assertTrue("Debe haber un FastEnemy", hasFast);
    }

    @Test
    public void shouldLoadPatrolEnemyFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\n"
                + "ENEMY PATROL 300 150 16 3.0 300,150;500,150;500,350;300,350\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse(lvl.getEnemies().isEmpty());
        boolean hasPatrol = lvl.getEnemies().stream().anyMatch(e -> e instanceof PatrolEnemy);
        assertTrue("Debe haber un PatrolEnemy", hasPatrol);
    }

    // ── Elementos especiales ──────────────────────────────────────────────────

    @Test
    public void shouldLoadWallFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nWALL 100 100 20 80\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse("Debe cargar al menos una pared", lvl.getWalls().isEmpty());
    }

    @Test
    public void shouldLoadBombFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nBOMB 200 200\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse("Debe cargar al menos una bomba", lvl.getBombs().isEmpty());
    }

    @Test
    public void shouldLoadLifeSourceFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nLIFE 300 300\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse("Debe cargar al menos una fuente de vida", lvl.getLifeSources().isEmpty());
    }

    @Test
    public void shouldLoadCheckpointFromFile() throws Exception {
        writeConfig("TIME_LIMIT 90\nGOAL 680 200 70 100\nCHECKPOINT 340 200 80 100\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertFalse("Debe cargar al menos un checkpoint", lvl.getCheckPoints().isEmpty());
    }

    // ── Comentarios e ignorados ───────────────────────────────────────────────

    @Test
    public void shouldIgnoreCommentLines() throws Exception {
        writeConfig("# Este es un comentario\nTIME_LIMIT 90\nGOAL 680 200 70 100\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertNotNull("El nivel se carga aunque haya comentarios", lvl);
    }

    @Test
    public void shouldIgnoreEmptyLines() throws Exception {
        writeConfig("\n\nTIME_LIMIT 90\n\nGOAL 680 200 70 100\n\n");
        Level lvl = loader.loadLevel(tempFile.getAbsolutePath());
        assertNotNull(lvl);
    }

    // ── Error handling ────────────────────────────────────────────────────────

    @Test(expected = dominio.DopoGameException.class)
    public void shouldThrowInvalidMapExceptionForNonExistentFile() throws Exception {
        loader.loadLevel("archivo_que_no_existe_999.txt");
    }

    @Test
    public void shouldLoadInitialAndIntermediateSafeZonesFromFile() throws Exception {
        File file = new File(tempFile.getParentFile(), "level2.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print("TIME_LIMIT 90\nGOAL 680 200 70 100\nZONE INITIAL 0 0 90 100\nZONE INTERMEDIATE 300 0 90 100\n");
        }
        Level lvl = loader.loadLevel(file.getAbsolutePath());
        assertEquals(2, lvl.getSafeZones().size());
        assertTrue(lvl.getSafeZones().get(0) instanceof InitialZone);
        assertTrue(lvl.getSafeZones().get(1) instanceof IntermediateZone);
    }

    @Test
    public void shouldLoadVerticalSliderEnemyFromFile() throws Exception {
        File file = new File(tempFile.getParentFile(), "level2.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print("TIME_LIMIT 90\nGOAL 680 200 70 100\nENEMY VERTICAL 300 150 18 3.5\n");
        }
        Level lvl = loader.loadLevel(file.getAbsolutePath());
        assertFalse(lvl.getEnemies().isEmpty());
        assertTrue(lvl.getEnemies().stream().anyMatch(e -> e instanceof VerticalSliderEnemy));
    }

    @Test
    public void shouldIgnoreUnknownEnemyTypesWithoutException() throws Exception {
        File file = new File(tempFile.getParentFile(), "level2.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print("TIME_LIMIT 90\nGOAL 680 200 70 100\nENEMY UNK 100 100 18 HORIZONTAL 4.0\n");
        }
        Level lvl = loader.loadLevel(file.getAbsolutePath());
        assertTrue("Los enemigos desconocidos deben ser ignorados", lvl.getEnemies().isEmpty());
    }

    @Test
    public void shouldInferLevelOneWhenFilenameDoesNotContainLevelNumber() throws Exception {
        File otherFile = new File(tempFile.getParentFile(), "configuracion.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(otherFile))) {
            pw.print("TIME_LIMIT 45\nGOAL 680 200 70 100\n");
        }
        Level lvl = loader.loadLevel(otherFile.getAbsolutePath());
        assertEquals("Cuando el nombre no contiene levelN debe inferir nivel 1", 1, lvl.getLevelNumber());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TESTS DE EXCEPCIONES
    // Estos tests verifican que las excepciones personalizadas se lanzan cuando
    // los datos del archivo .txt son inválidos.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * PRUEBA 1 — InvalidMapException con coordenadas que no son números.
     *
     * Cuando el archivo tiene "COIN NORMAL abc xyz" en vez de "COIN NORMAL 200 250",
     * el MapLoader no puede convertir "abc" a int → lanza InvalidMapException.
     */
    @Test(expected = dominio.DopoGameException.class)
    public void shouldThrowInvalidMapExceptionWhenCoinHasNonNumericCoords() throws Exception {
        writeConfig(
            "TIME_LIMIT 90\n" +
            "GOAL 680 200 70 100\n" +
            "COIN NORMAL abc xyz\n"   // ← "abc" no es un número válido
        );
        loader.loadLevel(tempFile.getAbsolutePath());
        // Si llegamos aquí sin excepción, el test FALLA
    }

    /**
     * PRUEBA 2 — InvalidMapException con ENEMY al que le faltan parámetros.
     *
     * "ENEMY BASIC 200 100 18" tiene solo 5 campos, pero necesita 7.
     * El parser intenta leer parts[5] y parts[6] → ArrayIndexOutOfBoundsException
     * → se convierte en InvalidMapException.
     */
    @Test(expected = dominio.DopoGameException.class)
    public void shouldThrowInvalidMapExceptionWhenEnemyIsMissingFields() throws Exception {
        writeConfig(
            "TIME_LIMIT 90\n" +
            "GOAL 680 200 70 100\n" +
            "ENEMY BASIC 200 100 18\n"  // ← le faltan: HORIZONTAL 4.5
        );
        loader.loadLevel(tempFile.getAbsolutePath());
    }

    /**
     * PRUEBA 3 — InvalidMapException con GOAL que tiene un campo de texto.
     *
     * "GOAL 680 ABC 80 140" intenta parsear "ABC" como int → falla.
     */
    @Test(expected = dominio.DopoGameException.class)
    public void shouldThrowInvalidMapExceptionWhenGoalHasInvalidNumber() throws Exception {
        writeConfig(
            "TIME_LIMIT 90\n" +
            "GOAL 680 ABC 80 140\n"  // ← "ABC" no es int
        );
        loader.loadLevel(tempFile.getAbsolutePath());
    }

    /**
     * PRUEBA 4 — InvalidMapException lleva el número de línea del error.
     *
     * Verificamos que el mensaje de la excepción incluya "Línea 3"
     * porque el error está en la tercera línea del archivo.
     */
    @Test
    public void shouldIncludeLineNumberInInvalidMapExceptionMessage() throws Exception {
        writeConfig(
            "TIME_LIMIT 90\n" +       // línea 1 — ok
            "GOAL 680 200 70 100\n" + // línea 2 — ok
            "COIN NORMAL abc xyz\n"   // línea 3 — ERROR aquí
        );
        try {
            loader.loadLevel(tempFile.getAbsolutePath());
            fail("Debería haber lanzado DopoGameException");
        } catch (dominio.DopoGameException e) {
            // El mensaje debe contener la línea del error
            assertTrue(
                "El mensaje debe indicar la línea del error, pero fue: " + e.getMessage(),
                e.getMessage().contains("3")
            );
        }
    }

    /**
     * PRUEBA 5 — SaveFileException cuando se intenta guardar en ruta inválida.
     *
     * Intentar guardar en una ruta que no existe lanza SaveFileException.
     */
    @Test(expected = dominio.DopoGameException.class)
    public void shouldThrowSaveFileExceptionWhenPathIsInvalid() throws Exception {
        persistencia.GameSaver saver = new persistencia.GameSaver();
        dominio.Game game = new dominio.Game();
        // Intentar guardar en una ruta imposible (directorio que no existe)
        saver.save(game, "Z:\\ruta\\que\\no\\existe\\partida");
    }

    /**
     * PRUEBA 6 — SaveFileException cuando se intenta cargar un archivo inexistente.
     */
    @Test(expected = dominio.DopoGameException.class)
    public void shouldThrowSaveFileExceptionWhenSaveFileNotFound() throws Exception {
        persistencia.GameSaver saver = new persistencia.GameSaver();
        saver.load("partida_que_no_existe_xyz");
    }

    /**
     * PRUEBA 7 — DopoGameException es la clase única de excepciones del juego.
     *
     * Puedes capturar cualquier excepción del juego con un solo catch.
     */
    @Test
    public void shouldBeCatchableAsDopoGameException() throws Exception {
        writeConfig(
            "TIME_LIMIT 90\n" +
            "GOAL 680 200 70 100\n" +
            "COIN NORMAL abc xyz\n"
        );
        try {
            loader.loadLevel(tempFile.getAbsolutePath());
            fail("Debería haber lanzado una excepción");
        } catch (dominio.DopoGameException e) {
            // Toda excepción del juego es DopoGameException
            assertNotNull("El mensaje no debe ser null", e.getMessage());
            assertTrue("Debe contener INVALID_CONFIG",
                    e.getMessage().contains(dominio.DopoGameException.INVALID_CONFIG));
        }
    }
}

