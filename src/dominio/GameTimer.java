package dominio;

/**
 * Temporizador de cuenta regresiva para el tiempo límite de un nivel.
 *
 * <p>Trabaja en ticks del bucle de juego. Dado que el timer del juego
 * dispara cada 16 ms (≈ 60 FPS), un segundo equivale a {@value #TICKS_PER_SECOND}
 * ticks. El tiempo límite se configura en segundos desde el archivo de nivel.</p>
 *
 * <p>La presentación llama a {@link #tick()} cada fotograma y consulta
 * {@link #getRemainingSeconds()} para mostrar el tiempo en el HUD.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-19
 */
public class GameTimer {

    /** Ticks equivalentes a un segundo (bucle a 60 FPS ≈ 16 ms/tick). */
    public static final int TICKS_PER_SECOND = 60;

    /** Tiempo límite original en segundos (para poder reiniciar). */
    private final int timeLimitSeconds;

    /** Ticks restantes hasta que se agota el tiempo. */
    private int remainingTicks;

    /** {@code true} si el temporizador está activo y contando. */
    private boolean running;

    /**
     * Construye el temporizador con el tiempo límite indicado.
     *
     * @param timeLimitSeconds tiempo límite en segundos (ej. 90).
     */
    public GameTimer(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
        this.remainingTicks   = timeLimitSeconds * TICKS_PER_SECOND;
        this.running          = false;
    }

    /**
     * Inicia la cuenta regresiva.
     */
    public void start() {
        running = true;
    }

    /**
     * Pausa el temporizador sin reiniciarlo.
     */
    public void pause() {
        running = false;
    }

    /**
     * Reinicia el temporizador al valor original y lo detiene.
     */
    public void reset() {
        remainingTicks = timeLimitSeconds * TICKS_PER_SECOND;
        running        = false;
    }

    /**
     * Avanza el temporizador un tick si está corriendo y queda tiempo.
     * Debe llamarse una vez por fotograma desde el bucle de juego.
     */
    public void tick() {
        if (running && remainingTicks > 0) {
            remainingTicks--;
        }
    }

    /**
     * Indica si el tiempo se ha agotado.
     *
     * @return {@code true} cuando {@code remainingTicks == 0}.
     */
    public boolean isTimeUp() {
        return remainingTicks <= 0;
    }

    /**
     * Devuelve los segundos restantes redondeados hacia arriba.
     *
     * @return segundos restantes (mínimo 0).
     */
    public int getRemainingSeconds() {
        return (int) Math.ceil((double) remainingTicks / TICKS_PER_SECOND);
    }

    /**
     * Devuelve los ticks restantes exactos.
     *
     * @return ticks restantes.
     */
    public int getRemainingTicks() {
        return remainingTicks;
    }

    /**
     * Establece directamente los ticks restantes.
     * Usado por {@code GameSaver} al restaurar una partida guardada.
     *
     * @param ticks ticks restantes (debe ser &gt;= 0).
     */
    public void setRemainingTicks(int ticks) {
        this.remainingTicks = Math.max(0, ticks);
    }

    /** @return {@code true} si el temporizador está corriendo. */
    public boolean isRunning() { return running; }

    /** @return tiempo límite original en segundos. */
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
}
