package ui.components;

import ui.Ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * A panel with rounded corners that stacks its children from top to bottom.
 * Used for every white card, the dark banners, and (with no fill) as a plain vertical column.
 */
public class RoundedPanel extends JPanel {

    private final Color fill;
    private final Color border;
    private final int arc;

    /** fill or border may be null for "none". */
    public RoundedPanel(Color fill, Color border, int arc, int padding) {
        this.fill = fill;
        this.border = border;
        this.arc = arc;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    /** Children are left-aligned by default so they all stretch to the same width. */
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof JComponent) {
            ((JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        super.addImpl(comp, constraints, index);
    }

    /** Wide as the parent allows, but never taller than its content. */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, super.getMaximumSize().height);
    }

    public void addCentered(JComponent comp) {
        add(comp);
        comp.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /** Adds empty vertical space. */
    public void gap(int height) {
        add(Box.createVerticalStrut(height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (fill != null) {
            Graphics2D g2 = Ui.smooth(g);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            if (border != null) {
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
