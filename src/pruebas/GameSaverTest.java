package pruebas;

import dominio.*;
import persistencia.GameSaver;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Pruebas unitarias para {@link GameSaver}.
 *
 * <p>Verifica guardado/carga, restauración de estado completo incluyendo
 * el modo de juego (PLAYER, PVP, PVM_RANDOM, PVM_EXPERT), y manejo de errores.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 */
public class GameSaverTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private GameSaver saver;
    private Game game;

    @Before
    public void setUp() {
        saver = new GameSaver();
        game  = new Game(SkinType.RED);
    }

    // ── saveExists ────────────────────────────────────────────────────────────

    @Test
    public void shouldReturnFalseForNonExistentSave() {
        assertFalse("No debe existir archivo inexistente",
                saver.saveExists("archivo_que_no_existe_xyz_abc"));
    }

    @Test
    public void shouldReturnTrueAfterSaving() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/partida";
        saver.save(game, path);
        assertTrue("Debe existir el archivo tras guardar", saver.saveExists(path));
    }

    // ── Guardar y cargar — modo PLAYER (default) ──────────────────────────────

    @Test
    public void shouldSaveAndLoadDeaths() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/p1";
        game.registerDeath();
        game.registerDeath();
        saver.save(game, path);
        Game loaded = saver.load(path);
        assertEquals("Las muertes deben restaurarse", 2, loaded.getDeaths());
    }

    @Test
    public void shouldSaveAndLoadSkinRed() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/p2";
        saver.save(game, path);
        Game loaded = saver.load(path);
        assertEquals("El skin RED debe restaurarse", SkinType.RED, loaded.getSelectedSkin());
    }

    @Test
    public void shouldSaveAndLoadSkinBlue() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/p3";
        Game blueGame = new Game(SkinType.BLUE);
        saver.save(blueGame, path);
        Game loaded = saver.load(path);
        assertEquals(SkinType.BLUE, loaded.getSelectedSkin());
    }

    @Test
    public void shouldSaveAndLoadSkinGreen() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/p4";
        Game greenGame = new Game(SkinType.GREEN);
        saver.save(greenGame, path);
        Game loaded = saver.load(path);
        assertEquals(SkinType.GREEN, loaded.getSelectedSkin());
    }

    @Test
    public void shouldSaveAndLoadLevelOne() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/p5";
        saver.save(game, path);
        Game loaded = saver.load(path);
        assertEquals("Nivel 1 debe restaurarse", 1, loaded.getCurrentLevelNumber());
    }

    @Test
    public void shouldLoadGameWithLevelOnePlayer() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/p6";
        saver.save(game, path);
        Game loaded = saver.load(path);
        assertNotNull("El jugador del nivel 1 no debe ser null",
                loaded.getCurrentLevel().getPlayer());
    }

    @Test
    public void shouldSaveWithCorrectExtension() throws Exception {
        String base = tempFolder.newFolder().getAbsolutePath() + "/partida_test";
        saver.save(game, base);
        File f = new File(base + GameSaver.SAVE_EXTENSION);
        assertTrue("El archivo .dopo debe existir", f.exists());
    }

    // ── Persistencia del modo de juego ────────────────────────────────────────

    @Test
    public void shouldSaveAndLoadModePlayer() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/mode_player";
        saver.save(game, path, "PLAYER");
        GameSaver.LoadResult lr = saver.loadFull(path);
        assertEquals("Modo PLAYER debe restaurarse", "PLAYER", lr.modeName);
    }

    @Test
    public void shouldSaveAndLoadModePvP() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/mode_pvp";
        saver.save(game, path, "PVP");
        GameSaver.LoadResult lr = saver.loadFull(path);
        assertEquals("Modo PVP debe restaurarse", "PVP", lr.modeName);
    }

    @Test
    public void shouldSaveAndLoadModePvMRandom() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/mode_pvm_random";
        saver.save(game, path, "PVM_RANDOM");
        GameSaver.LoadResult lr = saver.loadFull(path);
        assertEquals("Modo PVM_RANDOM debe restaurarse", "PVM_RANDOM", lr.modeName);
    }

    @Test
    public void shouldSaveAndLoadModePvMExpert() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/mode_pvm_expert";
        saver.save(game, path, "PVM_EXPERT");
        GameSaver.LoadResult lr = saver.loadFull(path);
        assertEquals("Modo PVM_EXPERT debe restaurarse", "PVM_EXPERT", lr.modeName);
    }

    @Test
    public void shouldRestoreGreenPlayerAsShieldedWhenSavedSettingIsTrue() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/green_shield";
        Game greenGame = new Game(SkinType.GREEN);
        saver.save(greenGame, path);
        GameSaver.LoadResult lr = saver.loadFull(path);
        assertTrue("GreenPlayer debe conservar su escudo activo",
                lr.game.getCurrentLevel().getPlayer() instanceof GreenPlayer
                        && ((GreenPlayer) lr.game.getCurrentLevel().getPlayer()).isShieldActive());
    }

    @Test
    public void shouldRestoreGreenPlayerSpeedWhenShieldFalseInSaveFile() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/green_no_shield";
        Game greenGame = new Game(SkinType.GREEN);
        saver.save(greenGame, path);
        File file = new File(path + GameSaver.SAVE_EXTENSION);
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        content = content.replace("shield=true", "shield=false");
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));

        GameSaver.LoadResult lr = saver.loadFull(path);
        Player p = lr.game.getCurrentLevel().getPlayer();
        assertFalse("El verde no debe tener escudo", ((GreenPlayer) p).isShieldActive());
        assertEquals("La velocidad debe reducirse a la mitad tras restaurar el escudo consumido",
                1.5, p.getSpeed(), 0.0001);
    }

    @Test
    public void shouldLoadFullGameWithMultipleLevelsWhenLevelTwoIsSaved() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/multi_level";
        Level level2 = new Level(2);
        game.addLevel(level2);
        game.setCurrentLevelIndex(1);

        saver.save(game, path, "PLAYER");
        GameSaver.LoadResult lr = saver.loadFull(path);

        assertEquals("Debe restaurar el nivel 2", 2, lr.game.getCurrentLevelNumber());
        assertEquals("Debe cargar dos niveles en el juego restaurado", 2, lr.game.getLevels().size());
    }

    @Test
    public void shouldDefaultModeToPlayerWhenNotSpecified() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/mode_default";
        saver.save(game, path);   // sin modo explícito → "PLAYER"
        GameSaver.LoadResult lr = saver.loadFull(path);
        assertEquals("Modo por defecto debe ser PLAYER", "PLAYER", lr.modeName);
    }

    @Test
    public void shouldLoadFullReturnSameGameAsLoad() throws Exception {
        String path = tempFolder.newFolder().getAbsolutePath() + "/full_vs_simple";
        game.setDeaths(5);
        saver.save(game, path, "PLAYER");
        GameSaver.LoadResult lr = saver.loadFull(path);
        assertEquals("loadFull debe devolver el mismo juego que load",
                game.getDeaths(), lr.game.getDeaths());
    }

    // ── Errores ───────────────────────────────────────────────────────────────

    @Test(expected = DopoGameException.class)
    public void shouldThrowSaveFileExceptionOnInvalidPath() throws Exception {
        saver.save(game, "Z:\\ruta\\imposible\\partida");
    }

    @Test(expected = DopoGameException.class)
    public void shouldThrowSaveFileExceptionOnMissingFile() throws Exception {
        saver.load("archivo_inexistente_9999");
    }

    @Test(expected = DopoGameException.class)
    public void shouldThrowSaveFileExceptionOnLoadFullMissingFile() throws Exception {
        saver.loadFull("archivo_que_no_existe_loadfull");
    }

    // ── livesForLevel ─────────────────────────────────────────────────────────

    @Test
    public void shouldHaveFiveLivesForLevels1To3() {
        assertEquals(5, Game.livesForLevel(1));
        assertEquals(5, Game.livesForLevel(2));
        assertEquals(5, Game.livesForLevel(3));
    }

    @Test
    public void shouldHaveFourLivesForLevels4To6() {
        assertEquals(4, Game.livesForLevel(4));
        assertEquals(4, Game.livesForLevel(5));
        assertEquals(4, Game.livesForLevel(6));
    }

    @Test
    public void shouldHaveThreeLivesForLevels7To9() {
        assertEquals(3, Game.livesForLevel(7));
        assertEquals(3, Game.livesForLevel(8));
        assertEquals(3, Game.livesForLevel(9));
    }

    @Test
    public void shouldHaveTwoLivesForLevel10() {
        assertEquals(2, Game.livesForLevel(10));
    }

    // ── setDeaths / setCurrentLevelIndex ─────────────────────────────────────

    @Test
    public void shouldSetDeathsDirectly() {
        game.setDeaths(7);
        assertEquals(7, game.getDeaths());
    }

    @Test
    public void shouldNotAllowNegativeDeaths() {
        game.setDeaths(-5);
        assertEquals(0, game.getDeaths());
    }

    @Test
    public void shouldSetCurrentLevelIndexWhenValid() {
        Level l2 = new Level(2);
        game.addLevel(l2);
        game.setCurrentLevelIndex(1);
        assertEquals(2, game.getCurrentLevelNumber());
    }

    @Test
    public void shouldIgnoreInvalidLevelIndex() {
        int before = game.getCurrentLevelIndex();
        game.setCurrentLevelIndex(99);
        assertEquals("Índice inválido no debe cambiar el nivel", before, game.getCurrentLevelIndex());
    }
}
