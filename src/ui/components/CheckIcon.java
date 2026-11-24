package ui.components;

import ui.Ui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/** A small check mark drawn with lines, so it looks the same on every computer (no font needed). */
public class CheckIcon implements Icon {

    private final int size;
    private final Color color;

    public CheckIcon(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = Ui.smooth(g);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.6f, size / 7f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D path = new Path2D.Float();
        path.moveTo(x + size * 0.12, y + size * 0.55);
        path.lineTo(x + size * 0.40, y + size * 0.82);
        path.lineTo(x + size * 0.88, y + size * 0.20);
        g2.draw(path);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
