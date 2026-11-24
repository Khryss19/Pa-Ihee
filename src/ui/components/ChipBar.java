package ui.components;

import ui.Theme;
import ui.Ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.function.IntConsumer;

/** The row of filter chips ("All", "2nd Floor", ...). The selected chip is dark green. */
public class ChipBar extends JPanel {

    public ChipBar(List<String> labels, int selectedIndex, IntConsumer onSelect) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);
        for (int i = 0; i < labels.size(); i++) {
            if (i > 0) {
                add(Box.createHorizontalStrut(6));
            }
            final int index = i;
            Chip chip = new Chip(labels.get(i), i == selectedIndex);
            chip.addActionListener(e -> onSelect.accept(index));
            add(chip);
        }
    }

    private static class Chip extends JButton {

        private final boolean selected;

        Chip(String text, boolean selected) {
            super(text);
            this.selected = selected;
            setFont(Theme.CHIP);
            setForeground(selected ? Theme.WHITE : Theme.TEXT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
            setMargin(new Insets(0, 0, 0, 0));
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(getText()) + 30, 30);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Ui.smooth(g);
            if (selected) {
                g2.setColor(Theme.DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            } else {
                g2.setColor(Theme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
