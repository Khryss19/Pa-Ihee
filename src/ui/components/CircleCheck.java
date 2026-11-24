package ui.components;

import ui.Theme;
import ui.Ui;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** The big green circle with a check mark on the "Report Submitted!" screen. */
public class CircleCheck extends JComponent {

    private final int size;

    public CircleCheck(int size) {
        this.size = size;
        Dimension d = new Dimension(size, size);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Ui.smooth(g);
        int x = (getWidth() - size) / 2;
        g2.setColor(Theme.GREEN_BG);
        g2.fillOval(x, 0, size, size);
        g2.dispose();
        int iconSize = size / 2;
        new CheckIcon(iconSize, Theme.GREEN_FG).paintIcon(this, g, x + (size - iconSize) / 2, (size - iconSize) / 2);
    }
}
