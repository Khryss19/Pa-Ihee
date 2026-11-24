package ui.components;

import model.Fixture;
import ui.Theme;
import ui.Ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** The small colored pill: "Working", "Under Maintenance", "Needs Repair", "Fixed * 22m". */
public class StatusBadge extends JLabel {

    private final Color background;

    public StatusBadge(String text, Color background, Color foreground) {
        super(text);
        this.background = background;
        setForeground(foreground);
        setFont(Theme.SMALL_BOLD);
        setOpaque(false);
        setIconTextGap(4);
        setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
    }

    /** Green for "Working", red for anything else (e.g. "Under Maintenance"). */
    public static StatusBadge forStatus(String status) {
        return Fixture.WORKING.equals(status) ? green(status) : red(status);
    }

    public static StatusBadge green(String text) {
        return new StatusBadge(text, Theme.GREEN_BG, Theme.GREEN_FG);
    }

    public static StatusBadge red(String text) {
        return new StatusBadge(text, Theme.RED_BG, Theme.RED_FG);
    }

    public static StatusBadge orange(String text) {
        return new StatusBadge(text, Theme.ORANGE_BG, Theme.ORANGE_FG);
    }

    /** Green pill with the text followed by a check mark, e.g. "West [check]". */
    public static StatusBadge greenCheck(String text) {
        StatusBadge badge = green(text);
        badge.setIcon(new CheckIcon(10, Theme.GREEN_FG));
        badge.setHorizontalTextPosition(SwingConstants.LEADING);
        return badge;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Ui.smooth(g);
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
