package ui.components;

import ui.Theme;
import ui.Ui;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.function.Function;

/** A drop-down list with rounded corners and a small chevron, matching the design. */
public class StyledComboBox<E> extends JComboBox<E> {

    private String placeholder;
    private Color fill = Theme.WHITE;
    private Function<Object, String> labeler = String::valueOf;

    public StyledComboBox() {
        setFont(Theme.BODY);
        setForeground(Theme.TEXT);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        setRenderer(new ItemRenderer());
        setMaximumRowCount(8);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Grey text shown while nothing is selected, e.g. "Choose a role". */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    /** Decides the text shown for each item (default: the item's toString). */
    public void setLabeler(Function<Object, String> labeler) {
        this.labeler = labeler;
    }

    @Override
    public void updateUI() {
        setUI(new StyledComboUI());
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = 40;
        return d;
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 40);
    }

    // ----- how each item in the list is drawn -----

    private class ItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, false, false);
            setText(value == null ? "" : labeler.apply(value));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setFont(Theme.BODY);
            setForeground(Theme.TEXT);
            setOpaque(index >= 0);
            setBackground(index >= 0 && isSelected ? Theme.HIGHLIGHT : Theme.WHITE);
            return this;
        }
    }

    // ----- how the whole combo box is drawn -----

    private static class StyledComboUI extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {
            return new ArrowButton();
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox);
            popup.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
            return popup;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            StyledComboBox<?> box = (StyledComboBox<?>) c;
            Graphics2D g2 = Ui.smooth(g);
            g2.setColor(box.fill);
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
            g2.setColor(box.hasFocus() ? Theme.DARK : Theme.BORDER);
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // The rounded background is already drawn in paint().
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            StyledComboBox<?> box = (StyledComboBox<?>) comboBox;
            Object value = comboBox.getSelectedItem();
            Object shown = (value != null) ? value : box.placeholder;
            if (shown == null) {
                return;
            }
            Component renderer = comboBox.getRenderer()
                    .getListCellRendererComponent(listBox, shown, -1, false, false);
            renderer.setFont(comboBox.getFont());
            renderer.setForeground(value != null ? Theme.TEXT : Theme.HINT);
            currentValuePane.paintComponent(g, renderer, comboBox,
                    bounds.x, bounds.y, bounds.width, bounds.height, false);
        }
    }

    private static class ArrowButton extends JButton {

        ArrowButton() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFocusable(false);
            setOpaque(false);
            setBorder(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Ui.smooth(g);
            g2.setColor(Theme.MUTED);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
            g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(28, 28);
        }
    }
}
