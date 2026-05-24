package presentacion;

import dominio.*;
import persistencia.ErrorLogger;
import persistencia.GameSaver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Panel principal del juego The DOPO Hardest Game.
 *
 * <p>Es el único punto de entrada de la aplicación (contiene {@link #main}).
 * Gestiona múltiples estados internos mediante el enum {@link Estado}:</p>
 * <ul>
 *   <li>{@code MENU}        — menú principal animado.</li>
 *   <li>{@code SELECT_MODE} — pantalla de selección de modo.</li>
 *   <li>{@code SELECT_SKIN} — pantalla de selección de skin.</li>
 *   <li>{@code JUEGO}       — partida activa.</li>
 *   <li>{@code PAUSA}       — juego pausado.</li>
 *   <li>{@code VICTORIA}    — nivel completado.</li>
 * </ul>
 *
 * <p><b>Responsabilidades separadas:</b>
 * <ul>
 *   <li>Captura de input de teclado.</li>
 *   <li>Delegación de lógica al dominio ({@link Game}).</li>
 *   <li>Renderizado de cada estado.</li>
 * </ul></p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 3.0
 * @since 2026-05-03
 */
public class HardestGameGUI extends JPanel implements ActionListener, KeyListener {

    // =========================================================================
    // Punto de entrada único
    // =========================================================================

    /**
     * Método principal: único punto de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("The DOPO Hardest Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            HardestGameGUI gui = new HardestGameGUI(frame);
            frame.add(gui);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            gui.requestFocusInWindow();
        });
    }

    // =========================================================================
    // Estados internos
    // =========================================================================

    /**
     * Estados posibles de la interfaz gráfica.
     */
    private enum Estado {
        MENU, SELECT_MODE, SELECT_SKIN, SELECT_SKIN_P2, JUEGO, PAUSA, VICTORIA
    }

    /** Estado actual del panel. */
    private Estado estado = Estado.MENU;

    // =========================================================================
    // Dimensiones y constantes visuales
    // =========================================================================

    /** Ancho total del panel. */
    static final int W = 820;

    /** Alto total del panel. */
    static final int H = 560;

    /** Alto del HUD de juego. */
    private static final int HUD_H = 50;

    // ── Paleta de colores ─────────────────────────────────────────────────────
    private static final Color BG_PURPLE    = new Color(180, 170, 210);
    private static final Color CHECK_WHITE  = new Color(230, 228, 242);
    private static final Color CHECK_GRAY   = new Color(190, 186, 212);
    private static final Color ENEMY_DARK   = new Color(60,  55, 120);
    private static final Color PLAY_CYAN    = new Color(50, 190, 220);
    private static final Color PLAY_BORDER  = new Color(80,  30, 100);
    private static final Color GOAL_GREEN   = new Color(100, 220, 80);
    private static final Color PLAYER_RED   = new Color(220, 40,  40);
    private static final Color PLAYER_BLUE  = new Color(50,  120, 220);
    private static final Color PLAYER_GREEN = new Color(60,  200, 80);
    private static final Color HUD_BG       = new Color(40,  35,  70);
    private static final Color SAFE_ZONE_C  = new Color(100, 220, 80, 80);
    private static final Color CHECKPOINT_C = new Color(255, 200, 50, 100);
    private static final Color WALL_COLOR   = new Color(80,  60, 120);

    // =========================================================================
    // Timer único (menú + juego)
    // =========================================================================

    /** Timer único a ~60 FPS. */
    private final Timer timer = new Timer(16, this);

    // =========================================================================
    // Estado MENÚ
    // =========================================================================

    private final double[] mEnemyX, mEnemyY, mEnemySpd;
    private final boolean[] mEnemyH;
    private final JButton playBtn;

    // =========================================================================
    // Estado JUEGO
    // =========================================================================

    /** Modelo de dominio activo. */
    private Game game;

    /** Teclas presionadas actualmente. */
    private final Set<Integer> keys = new HashSet<>();

    /** Flash rojo de muerte activo. */
    private boolean deathFlash;
    private int     flashFrames;

    /** Selección de modo antes de iniciar. */
    private String selectedMode = "PLAYER";

    /** Skin seleccionado antes de iniciar (Jugador 1). */
    private SkinType selectedSkin = SkinType.RED;

    /** Skin seleccionado para el Jugador 2 en modo PvP. */
    private SkinType selectedSkin2 = SkinType.BLUE;

    /** GoalZone del segundo jugador (solo en modos de 2 jugadores). */
    private GoalZone goalZoneP2 = null;

    /** Opción seleccionada en el menú de pausa. */
    private int pausaIndex = 0;

    /** Servicio de guardado. */
    private final GameSaver gameSaver = new GameSaver();

    /** Nombre fijo del archivo de guardado. */
    private static final String SAVE_PATH = "partida_dopo";

    // =========================================================================
    // Ventana padre
    // =========================================================================
    private final JFrame parentFrame;

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * Construye el panel principal.
     *
     * @param parentFrame ventana JFrame contenedora.
     */
    public HardestGameGUI(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setPreferredSize(new Dimension(W, H));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        // Enemigos decorativos del menú
        int n = 14;
        mEnemyX = new double[n]; mEnemyY = new double[n];
        mEnemySpd = new double[n]; mEnemyH = new boolean[n];
        int spacing = (W - 120) / (n - 2);
        for (int i = 0; i < n - 2; i++) {
            mEnemyX[i]   = 70 + i * spacing;
            mEnemyY[i]   = (H - 130) + 10 + (i % 2) * 20;
            mEnemySpd[i] = (i % 2 == 0) ? 1.5 : -1.5;
            mEnemyH[i]   = true;
        }
        mEnemyX[n-2]=250; mEnemyY[n-2]=290; mEnemySpd[n-2]= 2; mEnemyH[n-2]=false;
        mEnemyX[n-1]=560; mEnemyY[n-1]=250; mEnemySpd[n-1]=-2; mEnemyH[n-1]=false;

        // Botón PLAY
        playBtn = buildPlayButton();
        playBtn.setBounds(W / 2 - 90, H - 148, 180, 52);
        add(playBtn);

        // Botón ℹ Info
        JButton infoBtn = buildIconButton("ℹ", Color.WHITE);
        infoBtn.setForeground(Color.BLACK);
        infoBtn.setBounds(W - 60, H - 60, 46, 46);
        infoBtn.addActionListener(e -> new InfoDialog(parentFrame));
        add(infoBtn);

        // Botón 📂 Cargar Partida (menú principal) — Tarea 9
        JButton loadBtn = new JButton("📂 Cargar Partida") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(60,130,100),0,getHeight(),new Color(30,90,60)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(new Color(100,255,160)); g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,19,19);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String txt = "📂 Cargar Partida";
                g2.drawString(txt, (getWidth()-fm.stringWidth(txt))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        loadBtn.setOpaque(false); loadBtn.setContentAreaFilled(false);
        loadBtn.setBorderPainted(false); loadBtn.setFocusable(false);
        loadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadBtn.setBounds(W / 2 - 90, H - 85, 180, 36);
        loadBtn.addActionListener(e -> cargarPartida());
        add(loadBtn);

        timer.start();
    }

    // =========================================================================
    // Transiciones de estado
    // =========================================================================

    /** Activa el estado MENÚ. */
    public void mostrarMenu() {
        estado = Estado.MENU;
        deathFlash = false;
        playBtn.setVisible(true);
        repaint();
    }

    /** Muestra la pantalla de selección de modo. */
    private void mostrarSeleccionModo() {
        estado = Estado.SELECT_MODE;
        playBtn.setVisible(false);
        repaint();
    }

    /** Muestra la pantalla de selección de skin (Jugador 1). */
    private void mostrarSeleccionSkin() {
        estado = Estado.SELECT_SKIN;
        repaint();
    }

    /**
     * Muestra la pantalla de selección de skin para el Jugador 2 (solo PvP).
     * Se muestra después de que el Jugador 1 confirma su skin.
     */
    private void mostrarSeleccionSkinP2() {
        estado = Estado.SELECT_SKIN_P2;
        skinIndex2 = 0;
        repaint();
    }

    /** Inicia el juego con el modo y skin seleccionados. Carga los 10 niveles. */
    public void iniciarJuego() {
        game = new Game(selectedSkin);
        cargarNivelesAdicionalesEnGame(game);
        goalZoneP2 = null;

        switch (selectedMode) {
            case "PVP" -> {
                // P2 usa selectedSkin2 (elegido en SELECT_SKIN_P2)
                Player p2 = PlayerFactory.create(selectedSkin2,
                        Level.MAP_WIDTH - 54, Level.MAP_HEIGHT / 2.0 - 12);
                game.getCurrentLevel().addPlayer(p2);
                game.setGameMode(new PvPMode());
                goalZoneP2 = new GoalZone(10, Level.MAP_HEIGHT / 2 - 50, 70, 100);
            }
            case "PVM_RANDOM" -> {
                Player aiP = PlayerFactory.create(SkinType.BLUE,
                        Level.MAP_WIDTH - 54, Level.MAP_HEIGHT / 2.0 - 12);
                game.getCurrentLevel().addPlayer(aiP);
                game.setGameMode(new PvMachineMode(new RandomAI()));
                goalZoneP2 = new GoalZone(10, Level.MAP_HEIGHT / 2 - 50, 70, 100);
            }
            case "PVM_EXPERT" -> {
                Player aiP = PlayerFactory.create(SkinType.BLUE,
                        Level.MAP_WIDTH - 54, Level.MAP_HEIGHT / 2.0 - 12);
                game.getCurrentLevel().addPlayer(aiP);
                game.setGameMode(new PvMachineMode(new ExpertAI()));
                goalZoneP2 = new GoalZone(10, Level.MAP_HEIGHT / 2 - 50, 70, 100);
            }
            default -> game.setGameMode(new PlayerMode());
        }

        game.getCurrentLevel().getTimer().start();
        deathFlash = false;
        estado = Estado.JUEGO;
        playBtn.setVisible(false);
        requestFocusInWindow();
    }

    /**
     * Carga los niveles 2–10 desde configs/ y los añade al juego.
     * Si un archivo no existe se omite sin romper el flujo.
     */
    private void cargarNivelesAdicionalesEnGame(Game g) {
        persistencia.MapLoader loader = new persistencia.MapLoader();
        for (int n = 2; n <= 10; n++) {
            String path = "configs/level" + n + ".txt";
            try {
                Level lvl = loader.loadLevel(path);
                Player p = PlayerFactory.create(selectedSkin,
                        30, Level.MAP_HEIGHT / 2.0 - 12);
                p.setLives(Game.livesForLevel(n));
                lvl.addPlayer(p);
                g.addLevel(lvl);
            } catch (Exception ex) {
                persistencia.ErrorLogger.getInstance()
                    .warn("No se pudo cargar " + path + ": " + ex.getMessage());
            }
        }
    }

    /** Pausa o reanuda el juego. */
    private void togglePausa() {
        if (estado == Estado.JUEGO) {
            estado = Estado.PAUSA;
            game.getCurrentLevel().getTimer().pause();
        } else if (estado == Estado.PAUSA) {
            estado = Estado.JUEGO;
            game.getCurrentLevel().getTimer().start();
        }
        repaint();
    }

    // =========================================================================
    // Timer — bucle único
    // =========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (estado) {
            case MENU        -> tickMenu();
            case JUEGO       -> tickJuego();
            default          -> {} // PAUSA, VICTORIA, SELECT_* no animados
        }
        repaint();
    }

    /** Anima los enemigos decorativos del menú. */
    private void tickMenu() {
        for (int i = 0; i < mEnemyX.length; i++) {
            if (mEnemyH[i]) {
                mEnemyX[i] += mEnemySpd[i];
                if (mEnemyX[i] < 60 || mEnemyX[i] > W - 60) mEnemySpd[i] = -mEnemySpd[i];
            } else {
                mEnemyY[i] += mEnemySpd[i];
                if (mEnemyY[i] < 200 || mEnemyY[i] > H - 70) mEnemySpd[i] = -mEnemySpd[i];
            }
        }
    }

    /** Actualiza la lógica del juego en cada tick. */
    private void tickJuego() {
        if (deathFlash) {
            if (--flashFrames <= 0) deathFlash = false;
            return;
        }

        Level     lvl = game.getCurrentLevel();
        GameTimer gt  = lvl.getTimer();

        // Tiempo agotado → derrota
        if (gt.isTimeUp()) {
            mostrarDialogoDerrota();
            return;
        }

        Player p1 = lvl.getPlayer();
        if (p1 == null) return;



        GameMode mode = game.getGameMode();

        // ── PASO 0: guardar posiciones previas de cada jugador ───────────────
        double[] prevX = new double[lvl.getPlayers().size()];
        double[] prevY = new double[lvl.getPlayers().size()];
        for (int i = 0; i < lvl.getPlayers().size(); i++) {
            prevX[i] = lvl.getPlayers().get(i).getX();
            prevY[i] = lvl.getPlayers().get(i).getY();
        }

        // ── PASO 1: asignar inputs y mover según el modo activo ──────────────
        if (mode instanceof PvPMode pvp) {
            pvp.setPlayer1Input(buildInput(
                    KeyEvent.VK_W, KeyEvent.VK_S,
                    KeyEvent.VK_A, KeyEvent.VK_D));
            pvp.setPlayer2Input(buildInput(
                    KeyEvent.VK_UP, KeyEvent.VK_DOWN,
                    KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT));
            game.update();

        } else if (mode instanceof PvMachineMode pvm) {
            pvm.setHumanInput(buildInput(
                    KeyEvent.VK_UP, KeyEvent.VK_DOWN,
                    KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT));
            game.update();

        } else {
            p1.move(
                keys.contains(KeyEvent.VK_UP)   || keys.contains(KeyEvent.VK_W),
                keys.contains(KeyEvent.VK_DOWN)  || keys.contains(KeyEvent.VK_S),
                keys.contains(KeyEvent.VK_LEFT)  || keys.contains(KeyEvent.VK_A),
                keys.contains(KeyEvent.VK_RIGHT) || keys.contains(KeyEvent.VK_D),
                Level.MAP_WIDTH, Level.MAP_HEIGHT
            );
            lvl.update();
        }

        // ── PASO 2: colisiones de todos los jugadores ────────────────────────
        for (int i = 0; i < lvl.getPlayers().size(); i++) {
            procesarColisionesJugador(lvl, lvl.getPlayers().get(i), prevX[i], prevY[i]);
            if (deathFlash) return;
        }

        // ── PASO 3: checkpoints ──────────────────────────────────────────────
        for (CheckPoint cp : lvl.getCheckPoints()) {
            cp.tryActivate(p1);
        }

        // ── PASO 4: condición de victoria ────────────────────────────────────
        if (lvl.allCoinsCollected()) {
            if (goalZoneP2 == null
                    && lvl.getGoalZone() != null
                    && lvl.getGoalZone().isReached(p1.getBounds())) {
                estado = Estado.VICTORIA;
                mostrarDialogoGanador();
            } else if (goalZoneP2 != null && lvl.getPlayers().size() >= 2) {
                Player p2 = lvl.getPlayers().get(1);
                boolean p1Wins = lvl.getGoalZone() != null && lvl.getGoalZone().isReached(p1.getBounds());
                boolean p2Wins = goalZoneP2.isReached(p2.getBounds());
                if (p1Wins || p2Wins) {
                    estado = Estado.VICTORIA;
                    mostrarDialogoGanador2(p1Wins ? 1 : 2);
                }
            }
        }
    }


    /**
     * Construye un InputState desde el estado actual de teclas.
     */
    private InputState buildInput(int upK, int downK, int leftK, int rightK) {
        return new InputState(
            keys.contains(upK),   keys.contains(downK),
            keys.contains(leftK), keys.contains(rightK)
        );
    }

    /**
     * Procesa las colisiones de un jugador con enemigos, monedas,
     * fuentes de vida, bombas y paredes.
     *
     * @param lvl    nivel activo.
     * @param player jugador a comprobar.
     * @param prevX  posición X del jugador antes del movimiento de este tick.
     * @param prevY  posición Y del jugador antes del movimiento de este tick.
     */
    private void procesarColisionesJugador(Level lvl, Player player,
                                           double prevX, double prevY) {
        // Enemigos
        for (Enemy obs : lvl.getEnemies()) {
            if (obs.getBounds().intersects(player.getBounds())) {
                // Si el jugador tiene el escudo activo (SkinType GREEN), lo absorbe
                if (player.onHit()) {
                    deathFlash  = true;
                    flashFrames = 12;
                    return;  // absorbió el golpe
                }
                // Muerte normal
                game.registerDeath();
                deathFlash  = true;
                flashFrames = 20;
                return;
            }
        }

        // Monedas — se usa applyEffect() polimórficamente en lugar de instanceof
        for (Coin coin : lvl.getCoins()) {
            if (!coin.isCollected() && coin.getBounds().intersects(player.getBounds())) {
                coin.collect();
                coin.applyEffect(player);   // NormalCoin restaura skin; SkinCoin cambia skin
            }
        }

        // Fuentes de vida
        for (LifeSource ls : lvl.getLifeSources()) {
            if (!ls.isCollected() && ls.getBounds().intersects(player.getBounds())) {
                ls.collect();
                player.gainLife();
            }
        }

        // Bombas: detonan al contacto con jugador O enemigo
        for (Bomb bomb : lvl.getBombs()) {
            if (bomb.isExploded()) continue;

            // Detonación por jugador
            boolean detonated = bomb.getBounds().intersects(player.getBounds());

            // Detonación por enemigo
            if (!detonated) {
                for (Enemy e : lvl.getEnemies()) {
                    if (bomb.getBounds().intersects(e.getBounds())) {
                        detonated = true;
                        break;
                    }
                }
            }

            if (detonated) {
                bomb.explode();
                // Destruir jugadores en el radio de explosión
                for (Player p : lvl.getPlayers()) {
                    if (bomb.getExplosionBounds().intersects(p.getBounds())) {
                        game.registerDeathFor(p);
                    }
                }
                // Destruir enemigos en el radio de explosión
                lvl.removeEnemiesInBounds(bomb.getExplosionBounds());
                deathFlash  = true;
                flashFrames = 25;
                return;
            }
        }

        // Paredes: bloquear movimiento (devolver a posición anterior del tick)
        for (Wall wall : lvl.getWalls()) {
            if (wall.getBounds().intersects(player.getBounds())) {
                // Devolver a la posición anterior sin modificar el checkpoint
                player.setPosition(prevX, prevY);
                return;
            }
        }
    }

    // =========================================================================
    // Diálogos de fin de partida
    // =========================================================================

    private void mostrarDialogoGanador() {
        int nivelCompletado = game.getCurrentLevelNumber();
        boolean hayMasNiveles = game.getCurrentLevelIndex() < game.getLevels().size() - 1;
        SwingUtilities.invokeLater(() -> {
            String msg = "<html><center><b>✅ ¡Nivel " + nivelCompletado + " completado!</b><br>"
                    + "Muertes: " + game.getDeaths()
                    + "<br>Tiempo restante: "
                    + game.getCurrentLevel().getTimer().getRemainingSeconds() + "s"
                    + "</center></html>";
            String[] opciones = hayMasNiveles
                    ? new String[]{"Siguiente nivel", "Menú"}
                    : new String[]{"¡Juego completado!", "Menú"};
            int opt = JOptionPane.showOptionDialog(parentFrame, msg,
                    hayMasNiveles ? "¡Nivel completado!" : "🏆 ¡JUEGO COMPLETADO!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, opciones, 0);
            if (opt == 0 && hayMasNiveles) {
                game.nextLevel();
                game.getCurrentLevel().getTimer().start();
                deathFlash = false;
                estado = Estado.JUEGO;
            } else {
                mostrarMenu();
            }
        });
    }



    /** Diálogo de ganador para modos de 2 jugadores indicando quién ganó. */
    private void mostrarDialogoGanador2(int ganador) {
        SwingUtilities.invokeLater(() -> {
            String emoji = ganador == 1 ? "🟥" : "🟦";
            String msg = "<html><center><b>" + emoji + " ¡Jugador " + ganador + " gana!</b><br>"
                    + "Muertes totales: " + game.getDeaths()
                    + "<br>Tiempo restante: "
                    + game.getCurrentLevel().getTimer().getRemainingSeconds() + "s"
                    + "</center></html>";
            int opt = JOptionPane.showOptionDialog(parentFrame, msg, "¡GANADOR!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new String[]{"Menú", "Revancha"}, 0);
            if (opt == 1) iniciarJuego(); else mostrarMenu();
        });
    }

    private void mostrarDialogoDerrota() {
        estado = Estado.MENU;
        SwingUtilities.invokeLater(() -> {
            int opt = JOptionPane.showOptionDialog(parentFrame,
                    "<html><center><b>¡Tiempo agotado!</b><br>Muertes: "
                            + game.getDeaths() + "</center></html>",
                    "Derrota", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE, null,
                    new String[]{"Menú", "Reintentar"}, 1);
            if (opt == 1) iniciarJuego(); else mostrarMenu();
        });
    }

    // =========================================================================
    // Pintura principal
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (estado) {
            case MENU        -> paintMenu(g2);
            case SELECT_MODE -> paintSelectMode(g2);
            case SELECT_SKIN -> paintSelectSkin(g2);
            case SELECT_SKIN_P2 -> paintSelectSkinP2(g2);
            case JUEGO       -> paintJuego(g2);
            case PAUSA       -> { paintJuego(g2); paintPausa(g2); }
            case VICTORIA    -> paintJuego(g2);
        }
    }

    // ─── Menú Principal ───────────────────────────────────────────────────────

    private void paintMenu(Graphics2D g2) {
        g2.setColor(BG_PURPLE); g2.fillRect(0, 0, W, H);

        // Caja del título
        g2.setColor(new Color(160, 155, 210, 200));
        g2.fillRoundRect(100, 18, W - 200, 150, 16, 16);
        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(100, 18, W - 200, 150, 16, 16);

        g2.setColor(new Color(60, 55, 80));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("THE DOPO", 310, 55);

        GradientPaint gp = new GradientPaint(130, 70, new Color(100, 160, 200),
                                              130, 145, new Color(60, 100, 160));
        g2.setPaint(gp);
        g2.setFont(new Font("Arial Black", Font.BOLD, 68));
        g2.drawString("HARDEST", 140, 122);
        g2.setFont(new Font("Arial Black", Font.BOLD, 54));
        g2.drawString("GAME", 275, 168);

        // Área mapa decorativo
        int mx = 160, my = 180, mw = W - 320, mh = 185;
        drawCheckerboard(g2, mx, my, mw, mh);
        g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(3));
        g2.drawRect(mx, my, mw, mh);
        g2.setColor(GOAL_GREEN);
        g2.fillRect(W / 2 - 25, my + mh - 30, 50, 30);

        // Franja inferior
        int sy = H - 148, sh = 75;
        drawCheckerboard(g2, 40, sy, W - 80, sh);
        g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(3));
        g2.drawRect(40, sy, W - 80, sh);

        // Enemigos decorativos
        for (int i = 0; i < mEnemyX.length; i++) {
            int r = 9;
            g2.setColor(ENEMY_DARK);
            g2.fillOval((int)mEnemyX[i]-r, (int)mEnemyY[i]-r, r*2, r*2);
        }
    }

    // ─── Pantalla Selección de Modo ───────────────────────────────────────────

    private void paintSelectMode(Graphics2D g2) {
        g2.setColor(new Color(30, 25, 55)); g2.fillRect(0, 0, W, H);
        g2.setFont(new Font("Arial Black", Font.BOLD, 28));
        g2.setColor(PLAY_CYAN);
        centrar(g2, "SELECCIONAR MODO DE JUEGO", H / 2 - 150);

        String[] modos = {"1 Jugador (Player)", "2 Jugadores (PvP)",
                "Vs IA Aleatoria", "Vs IA Experta"};
        String[] keys2  = {"PLAYER","PVP","PVM_RANDOM","PVM_EXPERT"};
        int startY = H / 2 - 80;
        for (int i = 0; i < modos.length; i++) {
            boolean sel = keys2[i].equals(selectedMode);
            g2.setColor(sel ? new Color(100, 220, 80) : new Color(160, 155, 200));
            g2.setFont(new Font("Arial", Font.BOLD, sel ? 22 : 19));
            centrar(g2, (sel ? "▶  " : "   ") + modos[i], startY + i * 55);
        }
        g2.setFont(new Font("Arial", Font.ITALIC, 14));
        g2.setColor(new Color(150, 145, 190));
        centrar(g2, "↑↓ para seleccionar  •  ENTER para confirmar  •  ESC para volver", H - 40);
    }

    // ─── Pantalla Selección de Skin ───────────────────────────────────────────

    private void paintSelectSkin(Graphics2D g2) {
        // Fondo degradado oscuro
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(18, 14, 40), 0, H, new Color(35, 28, 70));
        g2.setPaint(bgGrad); g2.fillRect(0, 0, W, H);

        // Título
        g2.setFont(new Font("Arial Black", Font.BOLD, 26));
        g2.setColor(PLAY_CYAN);
        centrar(g2, "SELECCIONAR PERSONAJE", 50);
        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(130, 125, 180));
        centrar(g2, "Cada tipo tiene estadísticas únicas — elige sabiamente", 72);

        // Datos de cada skin
        SkinType[] skins = SkinType.values();
        Color[] cols     = {PLAYER_RED, PLAYER_BLUE, PLAYER_GREEN};
        String[] nombres = {"ROJO", "AZUL", "VERDE"};
        String[] subtits = {"Equilibrado", "Velocista", "Resistente"};
        String[] habilidades = {
            "Sin habilidades especiales. El jugador estándar con estadísticas balanceadas.",
            "Mayor velocidad y tamaño. Escapa mejor de enemigos pero ocupa más espacio.",
            "Escudo que absorbe el primer golpe. Velocidad se reduce tras recibir daño."
        };
        // velocidad (1-5), tamaño (1-5), escudo (0 o 1)
        int[] velStat  = {3, 5, 3};
        int[] tamStat  = {3, 5, 3};
        boolean[] shield = {false, false, true};

        int cardW = 200, cardH = 340;
        int totalW = 3 * cardW + 2 * 20;
        int startX = (W - totalW) / 2;
        int startY = 95;

        for (int i = 0; i < skins.length; i++) {
            boolean sel = skins[i] == selectedSkin;
            int cx = startX + i * (cardW + 20);

            // Sombra de tarjeta
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRoundRect(cx + 4, startY + 4, cardW, cardH, 20, 20);

            // Fondo tarjeta
            Color cardBg = sel ? new Color(50, 40, 90) : new Color(28, 22, 58);
            g2.setColor(cardBg);
            g2.fillRoundRect(cx, startY, cardW, cardH, 20, 20);

            // Borde (resaltado si seleccionado)
            g2.setColor(sel ? cols[i] : new Color(60, 55, 100));
            g2.setStroke(new BasicStroke(sel ? 3f : 1.5f));
            g2.drawRoundRect(cx, startY, cardW, cardH, 20, 20);

            // Indicador de selección
            if (sel) {
                g2.setColor(cols[i]);
                g2.setFont(new Font("Arial Black", Font.BOLD, 11));
                g2.drawString("▶ SELECCIONADO", cx + 28, startY + 18);
            }

            // Avatar del jugador — tamaño proporcional al tamaño real del skin
            // RED=24px → avatar 44  BLUE=30px → avatar 56  GREEN=24px → avatar 44
            int[] realSizes = {24, 30, 24};
            int avatarSize = 16 + realSizes[i] * 4 / 3;   // escala visual
            int ax = cx + (cardW - avatarSize) / 2;
            int ay = startY + 32;
            // Sombra avatar
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(ax + 4, ay + 4, avatarSize, avatarSize);
            // Avatar
            g2.setColor(cols[i]);
            g2.fillRect(ax, ay, avatarSize, avatarSize);
            // Brillo
            g2.setColor(new Color(255, 255, 255, 110));
            g2.fillRect(ax + 4, ay + 4, avatarSize / 3, avatarSize / 5);
            // Borde avatar
            g2.setColor(sel ? Color.WHITE : cols[i].darker());
            g2.setStroke(new BasicStroke(sel ? 2.5f : 1.5f));
            g2.drawRect(ax, ay, avatarSize, avatarSize);
            // Etiqueta de tamaño proporcional
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(new Color(160, 155, 210));
            String szLabel = realSizes[i] + "px";
            FontMetrics fmSz = g2.getFontMetrics();
            g2.drawString(szLabel, ax + (avatarSize - fmSz.stringWidth(szLabel)) / 2,
                          ay + avatarSize + 12);
            // Escudo visual si aplica
            if (shield[i]) {
                // Aura verde exterior (más prominente)
                g2.setColor(new Color(80, 255, 120, 90));
                g2.fillOval(ax - 9, ay - 9, avatarSize + 18, avatarSize + 18);
                g2.setColor(new Color(80, 255, 120, 240));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(ax - 9, ay - 9, avatarSize + 18, avatarSize + 18);
                // Segunda aura interior pulsante
                g2.setColor(new Color(150, 255, 150, 50));
                g2.fillOval(ax - 4, ay - 4, avatarSize + 8, avatarSize + 8);
            }

            // Nombre del tipo
            g2.setFont(new Font("Arial Black", Font.BOLD, 18));
            g2.setColor(cols[i]);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(nombres[i], cx + (cardW - fm.stringWidth(nombres[i])) / 2, ay + avatarSize + 24);

            // Subtítulo
            g2.setFont(new Font("Arial", Font.ITALIC, 12));
            g2.setColor(sel ? new Color(200, 200, 255) : new Color(120, 115, 175));
            fm = g2.getFontMetrics();
            g2.drawString(subtits[i], cx + (cardW - fm.stringWidth(subtits[i])) / 2, ay + avatarSize + 40);

            // Separador
            g2.setColor(new Color(80, 70, 130));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(cx + 16, ay + avatarSize + 50, cx + cardW - 16, ay + avatarSize + 50);

            // Stats: Velocidad
            int statY = ay + avatarSize + 65;
            int statX = cx + 16;
            int barW  = cardW - 32;
            drawStatBar(g2, statX, statY,      barW, "VELOCIDAD", velStat[i], 5, new Color(80, 200, 255));
            drawStatBar(g2, statX, statY + 34, barW, "TAMAÑO",   tamStat[i], 5, new Color(255, 160, 60));
            if (shield[i]) {
                drawStatBar(g2, statX, statY + 68, barW, "ESCUDO", 1, 1, new Color(100, 255, 100));
            } else {
                drawStatBar(g2, statX, statY + 68, barW, "ESCUDO", 0, 1, new Color(100, 255, 100));
            }

            // Descripción breve
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(sel ? new Color(190, 185, 230) : new Color(100, 95, 155));
            drawWrappedText(g2, habilidades[i], cx + 12, statY + 105, cardW - 24, 14);
        }

        // Instrucciones
        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(130, 125, 185));
        centrar(g2, "← → para seleccionar  •  ENTER para confirmar  •  ESC para volver", H - 20);
    }

    /**
     * Versión para que el Jugador 2 elija su skin (mismo diseño que P1).
     */
    private void paintSelectSkinP2(Graphics2D g2) {
        // Fondo degradado oscuro
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(18, 14, 40), 0, H, new Color(35, 28, 70));
        g2.setPaint(bgGrad); g2.fillRect(0, 0, W, H);

        // Título
        g2.setFont(new Font("Arial Black", Font.BOLD, 26));
        g2.setColor(PLAY_CYAN);
        centrar(g2, "SELECCIONAR PERSONAJE - JUGADOR 2", 50);
        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(130, 125, 180));
        centrar(g2, "Cada tipo tiene estadísticas únicas — elige sabiamente", 72);

        // Datos de cada skin
        SkinType[] skins = SkinType.values();
        Color[] cols     = {PLAYER_RED, PLAYER_BLUE, PLAYER_GREEN};
        String[] nombres = {"ROJO", "AZUL", "VERDE"};
        String[] subtits = {"Equilibrado", "Velocista", "Resistente"};
        String[] habilidades = {
            "Sin habilidades especiales. El jugador estándar con estadísticas balanceadas.",
            "Mayor velocidad y tamaño. Escapa mejor de enemigos pero ocupa más espacio.",
            "Escudo que absorbe el primer golpe. Velocidad se reduce tras recibir daño."
        };
        // velocidad (1-5), tamaño (1-5), escudo (0 o 1)
        int[] velStat  = {3, 5, 3};
        int[] tamStat  = {3, 5, 3};
        boolean[] shield = {false, false, true};

        int cardW = 200, cardH = 340;
        int totalW = 3 * cardW + 2 * 20;
        int startX = (W - totalW) / 2;
        int startY = 95;

        for (int i = 0; i < skins.length; i++) {
            boolean sel = skins[i] == selectedSkin2;
            int cx = startX + i * (cardW + 20);

            // Sombra de tarjeta
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRoundRect(cx + 4, startY + 4, cardW, cardH, 20, 20);

            // Fondo tarjeta
            Color cardBg = sel ? new Color(50, 40, 90) : new Color(28, 22, 58);
            g2.setColor(cardBg);
            g2.fillRoundRect(cx, startY, cardW, cardH, 20, 20);

            // Borde (resaltado si seleccionado)
            g2.setColor(sel ? cols[i] : new Color(60, 55, 100));
            g2.setStroke(new BasicStroke(sel ? 3f : 1.5f));
            g2.drawRoundRect(cx, startY, cardW, cardH, 20, 20);

            // Indicador de selección
            if (sel) {
                g2.setColor(cols[i]);
                g2.setFont(new Font("Arial Black", Font.BOLD, 11));
                g2.drawString("▶ SELECCIONADO", cx + 28, startY + 18);
            }

            int[] realSizes = {24, 30, 24};
            int avatarSize = 16 + realSizes[i] * 4 / 3;
            int ax = cx + (cardW - avatarSize) / 2;
            int ay = startY + 32;
            // Sombra avatar
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(ax + 4, ay + 4, avatarSize, avatarSize);
            // Avatar
            g2.setColor(cols[i]);
            g2.fillRect(ax, ay, avatarSize, avatarSize);
            // Brillo
            g2.setColor(new Color(255, 255, 255, 110));
            g2.fillRect(ax + 4, ay + 4, avatarSize / 3, avatarSize / 5);
            // Borde avatar
            g2.setColor(sel ? Color.WHITE : cols[i].darker());
            g2.setStroke(new BasicStroke(sel ? 2.5f : 1.5f));
            g2.drawRect(ax, ay, avatarSize, avatarSize);
            // Etiqueta de tamaño proporcional
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(new Color(160, 155, 210));
            String szLabel = realSizes[i] + "px";
            FontMetrics fmSz = g2.getFontMetrics();
            g2.drawString(szLabel, ax + (avatarSize - fmSz.stringWidth(szLabel)) / 2,
                          ay + avatarSize + 12);
            // Escudo visual si aplica
            if (shield[i]) {
                g2.setColor(new Color(80, 255, 120, 90));
                g2.fillOval(ax - 9, ay - 9, avatarSize + 18, avatarSize + 18);
                g2.setColor(new Color(80, 255, 120, 240));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(ax - 9, ay - 9, avatarSize + 18, avatarSize + 18);
                g2.setColor(new Color(150, 255, 150, 50));
                g2.fillOval(ax - 4, ay - 4, avatarSize + 8, avatarSize + 8);
            }

            // Nombre del tipo
            g2.setFont(new Font("Arial Black", Font.BOLD, 18));
            g2.setColor(cols[i]);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(nombres[i], cx + (cardW - fm.stringWidth(nombres[i])) / 2, ay + avatarSize + 24);

            // Subtítulo
            g2.setFont(new Font("Arial", Font.ITALIC, 12));
            g2.setColor(sel ? new Color(200, 200, 255) : new Color(120, 115, 175));
            fm = g2.getFontMetrics();
            g2.drawString(subtits[i], cx + (cardW - fm.stringWidth(subtits[i])) / 2, ay + avatarSize + 40);

            // Separador
            g2.setColor(new Color(80, 70, 130));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(cx + 16, ay + avatarSize + 50, cx + cardW - 16, ay + avatarSize + 50);

            // Stats: Velocidad
            int statY = ay + avatarSize + 65;
            int statX = cx + 16;
            int barW  = cardW - 32;
            drawStatBar(g2, statX, statY,      barW, "VELOCIDAD", velStat[i], 5, new Color(80, 200, 255));
            drawStatBar(g2, statX, statY + 34, barW, "TAMAÑO",   tamStat[i], 5, new Color(255, 160, 60));
            if (shield[i]) {
                drawStatBar(g2, statX, statY + 68, barW, "ESCUDO", 1, 1, new Color(100, 255, 100));
            } else {
                drawStatBar(g2, statX, statY + 68, barW, "ESCUDO", 0, 1, new Color(100, 255, 100));
            }

            // Descripción breve
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(sel ? new Color(190, 185, 230) : new Color(100, 95, 155));
            drawWrappedText(g2, habilidades[i], cx + 12, statY + 105, cardW - 24, 14);
        }

        // Instrucciones
        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(130, 125, 185));
        centrar(g2, "← → para seleccionar  •  ENTER para confirmar  •  ESC para volver", H - 20);
    }

    /**
     * Dibuja una barra de estadística con etiqueta y valor.
     */
    private void drawStatBar(Graphics2D g2, int x, int y, int maxW,
                             String label, int val, int maxVal, Color barColor) {
        // Etiqueta
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.setColor(new Color(160, 155, 210));
        g2.drawString(label, x, y + 11);
        // Fondo barra
        int labelW = 68;
        int barX = x + labelW, barY = y, barH = 12;
        int bw = maxW - labelW;
        g2.setColor(new Color(40, 35, 70));
        g2.fillRoundRect(barX, barY, bw, barH, 6, 6);
        // Relleno proporcional
        if (maxVal > 0 && val > 0) {
            int filled = (int)((val / (double)maxVal) * bw);
            GradientPaint bp = new GradientPaint(barX, barY, barColor.darker(), barX, barY + barH, barColor);
            g2.setPaint(bp);
            g2.fillRoundRect(barX, barY, filled, barH, 6, 6);
        }
        // Borde barra
        g2.setColor(new Color(70, 65, 110));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(barX, barY, bw, barH, 6, 6);
    }

    /**
     * Dibuja texto con salto de línea automático dentro de un ancho máximo.
     */
    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y;
        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth) {
                g2.drawString(line.toString(), x, curY);
                line = new StringBuilder(word);
                curY += lineHeight;
            } else {
                line = new StringBuilder(test);
            }
        }
        if (!line.isEmpty()) g2.drawString(line.toString(), x, curY);
    }

    /**
     * Devuelve el nombre del tipo de jugador según su clase.
     */
    private String playerTypeName(Player p) {
        if (p instanceof GreenPlayer) return "VERDE";
        if (p instanceof BluePlayer)  return "AZUL";
        return "ROJO";
    }

    // ─── Estado JUEGO ─────────────────────────────────────────────────────────

    private void paintJuego(Graphics2D g2) {
        Level lvl       = game.getCurrentLevel();
        int collected   = lvl.collectedCoinsCount();
        int total       = lvl.getCoins().size();
        boolean allCoins = lvl.allCoinsCollected();
        int timeLeft    = lvl.getTimer().getRemainingSeconds();
        Player p1       = lvl.getPlayer();

        // ── HUD ───────────────────────────────────────────────────────────────
        g2.setColor(HUD_BG); g2.fillRect(0, 0, W, HUD_H);

        g2.setFont(new Font("Arial Black", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString("Nivel: " + lvl.getLevelNumber(), 12, 32);
        g2.drawString("Muertes: " + game.getDeaths(), 130, 32);

        // Tiempo (rojo si < 15 s)
        g2.setColor(timeLeft < 15 ? new Color(255, 80, 80) : new Color(100, 220, 255));
        g2.drawString("⏱ " + timeLeft + "s", 280, 32);

        // Monedas
        g2.setColor(allCoins ? new Color(80, 255, 120) : new Color(255, 215, 0));
        g2.drawString("🪙 " + collected + "/" + total, 390, 32);

        // ── Info de jugadores (tipo + vidas) ──────────────────────────────────
        java.util.List<Player> allPlayers = lvl.getPlayers();
        int hudX = 500;
        for (int pi = 0; pi < allPlayers.size(); pi++) {
            Player p = allPlayers.get(pi);
            Color pc = skinColor(p.getSkinType());
            // Miniatura proporcional al tamaño real del skin
            int miniSz = (int)(p.getSize() * 0.62);   // RED/GREEN=14, BLUE=18
            int miniY  = HUD_H / 2 - miniSz / 2;
            g2.setColor(pc);
            g2.fillRect(hudX, miniY, miniSz, miniSz);
            g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(1));
            g2.drawRect(hudX, miniY, miniSz, miniSz);
            // Escudo: anillo verde visible en el mini-sprite
            if (p.getSkinType() == SkinType.GREEN) {
                g2.setColor(new Color(80, 255, 120, 200));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(hudX - 3, miniY - 3, miniSz + 6, miniSz + 6);
            }
            // Etiqueta: «P1 ROJO» / «P2 AZUL» etc.
            String playerLabel = "P" + (pi + 1) + " " + playerTypeName(p);
            g2.setFont(new Font("Arial Black", Font.BOLD, 11));
            g2.setColor(pc);
            g2.drawString(playerLabel, hudX + miniSz + 4, miniY + 10);
            // Vidas
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(new Color(255, 100, 100));
            g2.drawString("❤ " + p.getLives(), hudX + miniSz + 4, miniY + 24);
            hudX += 115;
        }

        // Modo
        g2.setColor(new Color(160, 155, 200));
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("[P] Pausa  [ESC] Menú", W - 165, 32);

        // ── Mapa ─────────────────────────────────────────────────────────────
        g2.translate(30, HUD_H + 12);
        drawCheckerboard(g2, 0, 0, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(3));
        g2.drawRect(0, 0, Level.MAP_WIDTH, Level.MAP_HEIGHT);

        // Zonas seguras
        g2.setColor(SAFE_ZONE_C);
        for (SafeZone sz : lvl.getSafeZones()) {
            g2.fillRect(sz.getX(), sz.getY(), sz.getWidth(), sz.getHeight());
        }

        // Checkpoints
        g2.setColor(CHECKPOINT_C);
        for (CheckPoint cp : lvl.getCheckPoints()) {
            g2.fillRect(cp.getX(), cp.getY(), cp.getWidth(), cp.getHeight());
            if (cp.isActivated()) {
                g2.setColor(new Color(255, 220, 60, 200));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                g2.drawString("✓", cp.getX() + cp.getWidth()/2 - 6, cp.getY() + cp.getHeight()/2 + 6);
                g2.setColor(CHECKPOINT_C);
            }
        }

        // Paredes
        g2.setColor(WALL_COLOR);
        for (Wall wall : lvl.getWalls()) {
            g2.fillRect(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
            g2.setColor(WALL_COLOR.darker()); g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
            g2.setColor(WALL_COLOR);
        }

        // Meta P1 (derecha)
        GoalZone goal = lvl.getGoalZone();
        if (goal != null) {
            Color goalColor = allCoins ? GOAL_GREEN : new Color(130, 130, 130);
            g2.setColor(goalColor); g2.fillRect(goal.getX(), goal.getY(), goal.getWidth(), goal.getHeight());
            // Etiqueta P1
            g2.setFont(new Font("Arial Black", Font.BOLD, 10));
            g2.setColor(allCoins ? Color.WHITE : new Color(180,180,180));
            g2.drawString(goalZoneP2 != null ? "META P1" : "META",
                    goal.getX() + 5, goal.getY() + 14);
            if (!allCoins) {
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                g2.drawString("🔒", goal.getX() + 8, goal.getY() + goal.getHeight()/2 + 8);
            }
        }
        // Meta P2 (izquierda) — solo en modos de 2 jugadores
        if (goalZoneP2 != null) {
            Color gc2 = allCoins ? new Color(80, 150, 255) : new Color(130, 130, 130);
            g2.setColor(gc2); g2.fillRect(goalZoneP2.getX(), goalZoneP2.getY(), goalZoneP2.getWidth(), goalZoneP2.getHeight());
            g2.setFont(new Font("Arial Black", Font.BOLD, 10));
            g2.setColor(allCoins ? Color.WHITE : new Color(180,180,180));
            g2.drawString("META P2", goalZoneP2.getX() + 4, goalZoneP2.getY() + 14);
            if (!allCoins) {
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                g2.drawString("🔒", goalZoneP2.getX() + 8, goalZoneP2.getY() + goalZoneP2.getHeight()/2 + 8);
            }
        }

        // Fuentes de vida
        for (LifeSource ls : lvl.getLifeSources()) {
            if (!ls.isCollected()) {
                g2.setColor(new Color(255, 80, 80));
                g2.fillOval(ls.getX()-ls.getRadius(), ls.getY()-ls.getRadius(),
                            ls.getRadius()*2, ls.getRadius()*2);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Arial Black", Font.BOLD, 10));
                g2.drawString("+", ls.getX()-4, ls.getY()+4);
            }
        }

        // Bombas
        for (Bomb bomb : lvl.getBombs()) {
            if (!bomb.isExploded()) {
                g2.setColor(new Color(40, 40, 40));
                g2.fillOval(bomb.getX()-bomb.getRadius(), bomb.getY()-bomb.getRadius(),
                            bomb.getRadius()*2, bomb.getRadius()*2);
                g2.setColor(new Color(255, 200, 0)); g2.setFont(new Font("Segoe UI Emoji",Font.PLAIN,14));
                g2.drawString("💣", bomb.getX()-8, bomb.getY()+6);
            }
        }

        // Monedas
        for (Coin coin : lvl.getCoins()) {
            if (!coin.isCollected()) {
                int cx = coin.getX(), cy = coin.getY(), cr = coin.getRadius();
                g2.setColor(new Color(0,0,0,50)); g2.fillOval(cx-cr+2, cy-cr+2, cr*2, cr*2);
                Color coinColor = (coin instanceof SkinCoin sc)
                        ? skinColor(sc.getTargetSkin())
                        : new Color(255, 210, 0);
                g2.setColor(coinColor);      g2.fillOval(cx-cr, cy-cr, cr*2, cr*2);
                g2.setColor(coinColor.darker()); g2.setStroke(new BasicStroke(2));
                g2.drawOval(cx-cr, cy-cr, cr*2, cr*2);
            }
        }

        // Enemigos
        for (Enemy obs : lvl.getEnemies()) {
            int r = obs.getRadius();
            int ox = (int)obs.getX()-r, oy = (int)obs.getY()-r, d = r*2;
            g2.setColor(new Color(0,0,0,60)); g2.fillOval(ox+2, oy+2, d, d);
            Color ec = (obs instanceof PatrolEnemy) ? new Color(120, 60, 180) : ENEMY_DARK;
            g2.setColor(ec); g2.fillOval(ox, oy, d, d);
            g2.setColor(new Color(130,120,200,180)); g2.fillOval(ox+3, oy+2, r-2, r-2);
        }

        // Jugadores
        for (Player p : lvl.getPlayers()) {
            paintPlayer(g2, p);
        }

        // Flash de muerte
        if (deathFlash) {
            g2.setColor(new Color(255,0,0,80));
            g2.fillRect(0, 0, Level.MAP_WIDTH, Level.MAP_HEIGHT);
        }

        g2.translate(-30, -(HUD_H + 12));  // restaurar translate
    }

    /** Dibuja un jugador con su color de skin y borde configurado. */
    private void paintPlayer(Graphics2D g2, Player p) {
        int px = (int)p.getX(), py = (int)p.getY(), ps = p.getSize();
        Color c = skinColor(p.getSkinType());

        // ── Trail de velocidad para skin BLUE ───────────────────────────────────
        if (p.getSkinType() == SkinType.BLUE) {
            for (int t = 3; t >= 1; t--) {
                int alpha = 20 * t;
                int offset = t * 4;
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                g2.fillRect(px - offset, py, ps, ps);
            }
        }

        // ── Escudo para skin GREEN — aura doble prominente ─────────────────
        if (p.getSkinType() == SkinType.GREEN) {
            // Aura exterior
            g2.setColor(new Color(80, 255, 120, 55));
            g2.fillOval(px - 7, py - 7, ps + 14, ps + 14);
            // Borde del escudo
            g2.setColor(new Color(80, 255, 120, 220));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(px - 7, py - 7, ps + 14, ps + 14);
            // Aura interior
            g2.setColor(new Color(150, 255, 180, 40));
            g2.fillOval(px - 3, py - 3, ps + 6, ps + 6);
        }

        // ── Sombra del cuerpo ─────────────────────────────────────────────────
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRect(px + 2, py + 2, ps, ps);

        // ── Cuerpo ────────────────────────────────────────────────────────────
        g2.setColor(c);
        g2.fillRect(px, py, ps, ps);

        // ── Borde ─────────────────────────────────────────────────────────────
        Color[] borders = {Color.BLACK, Color.WHITE, new Color(255, 215, 0)};
        g2.setColor(borders[Math.min(p.getBorderColorIndex(), borders.length - 1)]);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(px, py, ps, ps);

        // ── Brillo ────────────────────────────────────────────────────────────
        g2.setColor(new Color(255, 255, 255, 110));
        g2.fillRect(px + 3, py + 3, Math.max(6, ps / 3), Math.max(3, ps / 5));
    }

    // ─── Pausa ────────────────────────────────────────────────────────────────

    private static final String[] PAUSA_OPCIONES = {
        "▶  Continuar",
        "💾  Guardar partida",
        "📂  Cargar partida",
        "🏠  Volver al Menú"
    };

    private void paintPausa(Graphics2D g2) {
        // Fondo semitransparente
        g2.setColor(new Color(10, 8, 30, 195));
        g2.fillRect(0, 0, W, H);

        // Panel central
        int pw = 420, ph = 310;
        int px = (W - pw) / 2, py = (H - ph) / 2 - 20;
        g2.setColor(new Color(25, 20, 55, 230));
        g2.fillRoundRect(px, py, pw, ph, 24, 24);
        g2.setColor(PLAY_CYAN); g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(px, py, pw, ph, 24, 24);

        // Título
        g2.setFont(new Font("Arial Black", Font.BOLD, 32));
        g2.setColor(PLAY_CYAN);
        centrar(g2, "PAUSA", py + 52);

        // Separador
        g2.setColor(new Color(80, 75, 140));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(px + 30, py + 64, px + pw - 30, py + 64);

        // Opciones
        int startY = py + 105;
        for (int i = 0; i < PAUSA_OPCIONES.length; i++) {
            boolean sel = i == pausaIndex;
            if (sel) {
                g2.setColor(new Color(50, 190, 220, 60));
                g2.fillRoundRect(px + 20, startY + i * 54 - 26, pw - 40, 40, 12, 12);
                g2.setColor(PLAY_CYAN); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(px + 20, startY + i * 54 - 26, pw - 40, 40, 12, 12);
            }
            g2.setFont(new Font("Arial", Font.BOLD, sel ? 20 : 17));
            g2.setColor(sel ? Color.WHITE : new Color(160, 155, 200));
            centrar(g2, PAUSA_OPCIONES[i], startY + i * 54);
        }

        // Pie de página
        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(120, 115, 170));
        centrar(g2, "↑↓ navegar  •  ENTER confirmar  •  P / ESC continuar", py + ph - 18);
    }

    // ─── Utilidades de dibujo ─────────────────────────────────────────────────

    private void centrar(Graphics2D g2, String text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (W - fm.stringWidth(text)) / 2, y);
    }

    private void drawCheckerboard(Graphics2D g2, int x, int y, int w, int h) {
        int cell = 22;
        for (int col = 0; col*cell < w; col++) {
            for (int row = 0; row*cell < h; row++) {
                g2.setColor((col+row)%2==0 ? CHECK_WHITE : CHECK_GRAY);
                int cw = Math.min(cell, x+w - (x+col*cell));
                int ch = Math.min(cell, y+h - (y+row*cell));
                g2.fillRect(x+col*cell, y+row*cell, cw, ch);
            }
        }
    }

    private Color skinColor(SkinType skin) {
        return switch (skin) {
            case RED   -> PLAYER_RED;
            case BLUE  -> PLAYER_BLUE;
            case GREEN -> PLAYER_GREEN;
        };
    }

    // ─── Fábricas de botones ──────────────────────────────────────────────────

    private JButton buildPlayButton() {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,PLAY_CYAN.brighter(),0,getHeight(),PLAY_CYAN.darker()));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
                g2.setColor(PLAY_BORDER); g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2,2,getWidth()-4,getHeight()-4,28,28);
                g2.setFont(new Font("Arial Black", Font.BOLD, 26));
                g2.setColor(PLAY_BORDER);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("JUGAR",(getWidth()-fm.stringWidth("JUGAR"))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> mostrarSeleccionModo());
        return btn;
    }

    private JButton buildIconButton(String icon, Color color) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color); g2.fillOval(0,0,getWidth()-1,getHeight()-1);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon,(getWidth()-fm.stringWidth(icon))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusable(false);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // Teclado
    // =========================================================================

    private final String[] MODE_KEYS = {"PLAYER","PVP","PVM_RANDOM","PVM_EXPERT"};
    private int modeIndex = 0;
    private final SkinType[] SKIN_VALS = SkinType.values();
    private int skinIndex  = 0;
    /** Indice de skin para el Jugador 2 en modo PvP. */
    private int skinIndex2 = 0;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        keys.add(code);

        switch (estado) {
            case MENU            -> {}
            case SELECT_MODE     -> handleSelectModeKey(code);
            case SELECT_SKIN     -> handleSelectSkinKey(code);
            case SELECT_SKIN_P2  -> handleSelectSkinP2Key(code);
            case JUEGO -> {
                if (code == KeyEvent.VK_ESCAPE) mostrarMenu();
                if (code == KeyEvent.VK_P)      togglePausa();
            }
            case PAUSA -> handlePausaKey(code);
            default -> {}
        }
    }

    private void handleSelectModeKey(int code) {
        if (code == KeyEvent.VK_UP)    modeIndex = (modeIndex - 1 + MODE_KEYS.length) % MODE_KEYS.length;
        if (code == KeyEvent.VK_DOWN)  modeIndex = (modeIndex + 1) % MODE_KEYS.length;
        if (code == KeyEvent.VK_ENTER) {
            selectedMode = MODE_KEYS[modeIndex];
            mostrarSeleccionSkin();
        }
        if (code == KeyEvent.VK_ESCAPE) mostrarMenu();
        selectedMode = MODE_KEYS[modeIndex];
    }

    private void handleSelectSkinKey(int code) {
        if (code == KeyEvent.VK_UP   || code == KeyEvent.VK_LEFT)  skinIndex = (skinIndex - 1 + SKIN_VALS.length) % SKIN_VALS.length;
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT) skinIndex = (skinIndex + 1) % SKIN_VALS.length;
        if (code == KeyEvent.VK_ENTER) {
            selectedSkin = SKIN_VALS[skinIndex];
            // En modo PvP, P2 también elige su propio skin
            if ("PVP".equals(selectedMode)) {
                mostrarSeleccionSkinP2();
            } else {
                iniciarJuego();
            }
        }
        if (code == KeyEvent.VK_ESCAPE) mostrarSeleccionModo();
        selectedSkin = SKIN_VALS[skinIndex];
    }

    /**
     * Maneja el teclado en la pantalla de selección de skin del Jugador 2 (PvP).
     *
     * @param code código de la tecla presionada.
     */
    private void handleSelectSkinP2Key(int code) {
        if (code == KeyEvent.VK_UP   || code == KeyEvent.VK_LEFT)  skinIndex2 = (skinIndex2 - 1 + SKIN_VALS.length) % SKIN_VALS.length;
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT) skinIndex2 = (skinIndex2 + 1) % SKIN_VALS.length;
        if (code == KeyEvent.VK_ENTER) {
            selectedSkin2 = SKIN_VALS[skinIndex2];
            iniciarJuego();
        }
        if (code == KeyEvent.VK_ESCAPE) mostrarSeleccionSkin();
        selectedSkin2 = SKIN_VALS[skinIndex2];
        repaint();
    }

    /**
     * Maneja el teclado en el menú de pausa.
     * ↑↓ navegan opciones, ENTER confirma, P/ESC reanuda.
     */
    private void handlePausaKey(int code) {
        if (code == KeyEvent.VK_P || code == KeyEvent.VK_ESCAPE) {
            togglePausa();   // reanudar directamente
            return;
        }
        if (code == KeyEvent.VK_UP) {
            pausaIndex = (pausaIndex - 1 + PAUSA_OPCIONES.length) % PAUSA_OPCIONES.length;
        }
        if (code == KeyEvent.VK_DOWN) {
            pausaIndex = (pausaIndex + 1) % PAUSA_OPCIONES.length;
        }
        if (code == KeyEvent.VK_ENTER) {
            ejecutarOpcionPausa(pausaIndex);
        }
        repaint();
    }

    /**
     * Ejecuta la opción seleccionada en el menú de pausa.
     * Aquí es donde se llaman guardar/cargar y donde las excepciones
     * se vuelven VISIBLES al usuario como diálogos.
     */
    private void ejecutarOpcionPausa(int opcion) {
        switch (opcion) {
            case 0 -> togglePausa();              // Continuar
            case 1 -> guardarPartida();           // Guardar
            case 2 -> cargarPartida();            // Cargar
            case 3 -> { togglePausa(); mostrarMenu(); }  // Menú
        }
    }

    /**
     * Exporta (guarda) la partida dejando que el usuario elija la ubicación.
     * Persiste el modo de juego activo (PLAYER, PVP, PVM_RANDOM, PVM_EXPERT).
     * Usa {@link JFileChooser} con filtro {@code .dopo}.
     * Si el guardado falla → muestra la {@link SaveFileException} en pantalla.
     */
    private void guardarPartida() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Exportar partida");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de partida DOPO (*.dopo)", "dopo"));
        fc.setSelectedFile(new java.io.File("mi_partida.dopo"));

        int result = fc.showSaveDialog(parentFrame);
        if (result != JFileChooser.APPROVE_OPTION) return;  // usuario canceló

        // Quitar la extensión si ya la escribió (GameSaver la agrega solo)
        String path = fc.getSelectedFile().getAbsolutePath();
        if (path.endsWith(".dopo")) path = path.substring(0, path.length() - 5);

        try {
            gameSaver.save(game, path, selectedMode);   // persiste el modo activo
            JOptionPane.showMessageDialog(
                parentFrame,
                "<html><b>✅ Partida exportada correctamente.</b><br><br>"
                    + "Archivo: <code>" + path + ".dopo</code><br>"
                    + "Nivel: " + game.getCurrentLevel().getLevelNumber()
                    + "  |  Muertes: " + game.getDeaths()
                    + "  |  Modo: " + selectedMode + "</html>",
                "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (DopoGameException ex) {
            ErrorLogger.getInstance().log(ex);
            JOptionPane.showMessageDialog(
                parentFrame,
                "<html><b>❌ Error al exportar la partida</b><br><br>"
                    + "<b>Tipo:</b> SaveFileException<br>"
                    + "<b>Mensaje:</b> " + ex.getMessage() + "<br><br>"
                    + "<i>Registrado en <code>dopo_errors.log</code></i></html>",
                "SaveFileException", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Importa (carga) una partida elegida por el usuario con {@link JFileChooser}.
     * Restaura: nivel, muertes, skin, vidas y modo de juego (incluyendo PvP/PvM).
     * Muestra la {@link SaveFileException} en pantalla si el archivo falla.
     */
    private void cargarPartida() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Importar partida");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de partida DOPO (*.dopo)", "dopo"));

        int result = fc.showOpenDialog(parentFrame);
        if (result != JFileChooser.APPROVE_OPTION) return;  // usuario canceló

        String path = fc.getSelectedFile().getAbsolutePath();
        if (path.endsWith(".dopo")) path = path.substring(0, path.length() - 5);

        if (!gameSaver.saveExists(path)) {
            JOptionPane.showMessageDialog(
                parentFrame,
                "<html><b>❌ Archivo no encontrado</b><br><br>"
                    + "<b>Tipo:</b> SaveFileException<br>"
                    + "<b>Causa:</b> El archivo <code>" + path + ".dopo</code> no existe.<br><br>"
                    + "<i>Selecciona un archivo .dopo válido.</i></html>",
                "SaveFileException — Archivo no encontrado",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            // Cargar el resultado completo (juego + modo guardado)
            persistencia.GameSaver.LoadResult lr = gameSaver.loadFull(path);
            game = lr.game;

            // Restaurar skin y modo en la GUI
            selectedSkin = game.getSelectedSkin();
            selectedMode = lr.modeName;

            // Siempre cargar los niveles adicionales que falten
            int savedLevelIdx = game.getCurrentLevelIndex();
            if (game.getLevels().size() <= savedLevelIdx + 1) {
                cargarNivelesAdicionalesEnGame(game);
                game.setCurrentLevelIndex(savedLevelIdx);   // reposicionar
            }

            // Configurar modo de juego y goalZoneP2 según el modo restaurado
            goalZoneP2 = null;
            switch (selectedMode) {
                case "PVP" -> {
                    // Usar el skin guardado del jugador 2, o el seleccionado si no hay guardado
                    SkinType p2Skin = selectedSkin;
                    if (lr.savedPlayerInfos.size() > 1 && lr.savedPlayerInfos.get(1) != null) {
                        p2Skin = lr.savedPlayerInfos.get(1).skinType;
                    }
                    Player p2 = PlayerFactory.create(p2Skin,
                            Level.MAP_WIDTH - 54, Level.MAP_HEIGHT / 2.0 - 12);
                    game.getCurrentLevel().addPlayer(p2);
                    // Restaurar posición guardada del jugador 2
                    if (lr.savedPlayerPositions.size() > 1) {
                        double[] pos = lr.savedPlayerPositions.get(1);
                        if (pos[0] >= 0 && pos[1] >= 0) p2.fullReset(pos[0], pos[1]);
                    }
                    game.setGameMode(new PvPMode());
                    goalZoneP2 = new GoalZone(10, Level.MAP_HEIGHT / 2 - 50, 70, 100);
                }
                case "PVM_RANDOM" -> {
                    Player aiP = PlayerFactory.create(SkinType.BLUE,
                            Level.MAP_WIDTH - 54, Level.MAP_HEIGHT / 2.0 - 12);
                    game.getCurrentLevel().addPlayer(aiP);
                    // Restaurar posición guardada del jugador IA
                    if (lr.savedPlayerPositions.size() > 1) {
                        double[] pos = lr.savedPlayerPositions.get(1);
                        if (pos[0] >= 0 && pos[1] >= 0) aiP.fullReset(pos[0], pos[1]);
                    }
                    game.setGameMode(new PvMachineMode(new RandomAI()));
                    goalZoneP2 = new GoalZone(10, Level.MAP_HEIGHT / 2 - 50, 70, 100);
                }
                case "PVM_EXPERT" -> {
                    Player aiP = PlayerFactory.create(SkinType.BLUE,
                            Level.MAP_WIDTH - 54, Level.MAP_HEIGHT / 2.0 - 12);
                    game.getCurrentLevel().addPlayer(aiP);
                    // Restaurar posición guardada del jugador IA
                    if (lr.savedPlayerPositions.size() > 1) {
                        double[] pos = lr.savedPlayerPositions.get(1);
                        if (pos[0] >= 0 && pos[1] >= 0) aiP.fullReset(pos[0], pos[1]);
                    }
                    game.setGameMode(new PvMachineMode(new ExpertAI()));
                    goalZoneP2 = new GoalZone(10, Level.MAP_HEIGHT / 2 - 50, 70, 100);
                }
                default -> game.setGameMode(new PlayerMode());
            }


            game.getCurrentLevel().getTimer().start();
            deathFlash = false;
            if (estado == Estado.PAUSA) {
                // salir del menú de pausa sin togglePausa (el timer ya está corriendo)
                estado = Estado.JUEGO;
            } else {
                estado = Estado.JUEGO;
            }
            playBtn.setVisible(false);
            requestFocusInWindow();
            JOptionPane.showMessageDialog(
                parentFrame,
                "<html><b>✅ Partida importada correctamente.</b><br><br>"
                    + "Nivel: " + game.getCurrentLevel().getLevelNumber()
                    + "  |  Muertes acumuladas: " + game.getDeaths()
                    + "  |  Modo: " + selectedMode + "</html>",
                "Importación exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (DopoGameException ex) {
            ErrorLogger.getInstance().log(ex);
            JOptionPane.showMessageDialog(
                parentFrame,
                "<html><b>❌ Error al importar la partida</b><br><br>"
                    + "<b>Tipo:</b> SaveFileException<br>"
                    + "<b>Mensaje:</b> " + ex.getMessage() + "<br><br>"
                    + "<i>El archivo puede estar corrupto. Revisa <code>dopo_errors.log</code></i></html>",
                "SaveFileException", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override public void keyReleased(KeyEvent e) { keys.remove(e.getKeyCode()); }
    @Override public void keyTyped(KeyEvent e) {}
}
