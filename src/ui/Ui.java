package ui;

import ui.components.RoundedPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Small helpers that every screen uses to build its layout. */
public final class Ui {

    private Ui() {
    }

    // ---------- drawing ----------

    /** A copy of the Graphics with smooth edges and smooth text. The caller must dispose() it. */
    public static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    /** Draws the rounded background and border of an input field. */
    public static void paintField(Graphics g, JComponent c, Color fill, boolean focused) {
        Graphics2D g2 = smooth(g);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
        g2.setColor(focused ? Theme.DARK : Theme.BORDER);
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
        g2.dispose();
    }

    /** Draws grey placeholder text while a text field is empty. */
    public static void paintHint(Graphics g, JTextComponent c, String hint, boolean topAligned) {
        if (hint == null || c.getDocument().getLength() > 0) {
            return;
        }
        Graphics2D g2 = smooth(g);
        g2.setFont(c.getFont());
        g2.setColor(Theme.HINT);
        Insets in = c.getInsets();
        FontMetrics fm = g2.getFontMetrics();
        int y = topAligned
                ? in.top + fm.getAscent()
                : (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(hint, in.left, y);
        g2.dispose();
    }

    // ---------- building blocks ----------

    public static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /** Cuts long text to maxChars characters and adds "..." so it always fits on one line. */
    public static String shorten(String text, int maxChars) {
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, maxChars - 3).trim() + "...";
    }

    /** Left part fills the row, right part sits at the far right (for example a name and its status pill). */
    public static JPanel row(JComponent left, JComponent right) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.add(left, BorderLayout.CENTER);
        if (right != null) {
            JPanel holder = new JPanel(new GridBagLayout());   // keeps the pill at its normal height
            holder.setOpaque(false);
            holder.add(right);
            row.add(holder, BorderLayout.EAST);
        }
        return row;
    }

    /** A white rounded card that stacks its children vertically. */
    public static RoundedPanel card() {
        return new RoundedPanel(Theme.WHITE, null, 16, 14);
    }

    /** A transparent panel that stacks its children vertically. */
    public static RoundedPanel column() {
        return new RoundedPanel(null, null, 0, 0);
    }

    /** The dark green box at the top of several screens: small caption plus a bold line. */
    public static RoundedPanel banner(String caption, String text) {
        RoundedPanel banner = new RoundedPanel(Theme.DARK, null, 16, 14);
        banner.add(label(caption, Theme.SMALL, Theme.SOFT_TEXT));
        banner.gap(3);
        banner.add(label(text, new Font(Font.SANS_SERIF, Font.BOLD, 14), Theme.WHITE));
        return banner;
    }

    /** A button that looks like plain text (used for the back arrow and "Log out"). */
    public static JButton linkButton(String text, Font font, Color color, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(color);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    /** Makes a whole card (and everything inside it) react to clicks. */
    public static void makeClickable(Component root, Runnable action) {
        root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        root.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        if (root instanceof Container) {
            for (Component child : ((Container) root).getComponents()) {
                makeClickable(child, action);
            }
        }
    }
}
