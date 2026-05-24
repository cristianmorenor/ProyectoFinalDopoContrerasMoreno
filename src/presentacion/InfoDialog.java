package presentacion;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo de información del juego The World's Hardest Game.
 *
 * <p>Muestra los datos del proyecto: autores, fecha, versión y
 * asignatura. Se abre desde el botón ℹ del menú principal.</p>
 *
 * @author David Contreras
 * @author Cristian Moreno
 * @version 1.0
 * @since 2026-05-03
 */
public class InfoDialog extends JDialog {

    /**
     * Construye y muestra el diálogo de información.
     *
     * @param parent ventana padre del diálogo.
     */
    public InfoDialog(Frame parent) {
        super(parent, "Información del Proyecto", true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 35, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Título
        JLabel title = styledLabel("THE WORLD'S HARDEST GAME",
                new Font("Arial Black", Font.BOLD, 18),
                new Color(100, 190, 230));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        // Línea separadora
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(100, 90, 140));
        sep.setMaximumSize(new Dimension(320, 2));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(16));

        // Datos
        panel.add(infoRow("Proyecto:",  "Proyecto Final – Entrega 1"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("Asignatura:", "Desarrollo Orientado a Objetos"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("Autor 1:",   "David Contreras"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("Autor 2:",   "Cristian Moreno"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("Fecha:",     "03 de mayo de 2026"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("Versión:",   "1.0"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(infoRow("Lenguaje:",  "Java 17 + Swing"));
        panel.add(Box.createVerticalStrut(20));

        // Estado de entrega
        JLabel estado = styledLabel(
                "Estado: Entrega 1 — Mapa + Enemigo funcional",
                new Font("Arial", Font.ITALIC, 12),
                new Color(160, 220, 140));
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(estado);
        panel.add(Box.createVerticalStrut(20));

        // Botón cerrar
        JButton closeBtn = new JButton("Cerrar");
        closeBtn.setFont(new Font("Arial Black", Font.BOLD, 13));
        closeBtn.setBackground(new Color(100, 190, 230));
        closeBtn.setForeground(new Color(40, 35, 70));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dispose());
        panel.add(closeBtn);

        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Crea un panel con etiqueta de clave y valor para mostrar información.
     *
     * @param key   nombre del campo.
     * @param value valor del campo.
     * @return {@link JPanel} con las dos etiquetas dispuestas horizontalmente.
     */
    private JPanel infoRow(String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(400, 24));

        JLabel keyLbl = new JLabel(key);
        keyLbl.setFont(new Font("Arial", Font.BOLD, 13));
        keyLbl.setForeground(new Color(180, 170, 230));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        valLbl.setForeground(Color.WHITE);

        row.add(keyLbl);
        row.add(valLbl);
        return row;
    }

    /**
     * Crea una etiqueta con fuente y color personalizados.
     *
     * @param text  texto de la etiqueta.
     * @param font  fuente a aplicar.
     * @param color color del texto.
     * @return {@link JLabel} configurada.
     */
    private JLabel styledLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }
}
