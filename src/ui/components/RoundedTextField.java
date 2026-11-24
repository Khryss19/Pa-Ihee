package ui.components;

import ui.Theme;
import ui.Ui;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/** A text field with rounded corners and grey placeholder text. */
public class RoundedTextField extends JTextField {

    private final String hint;
    private final Color fill;

    public RoundedTextField(String hint, Color fill) {
        this.hint = hint;
        this.fill = fill;
        setOpaque(false);
        setFont(Theme.BODY);
        setForeground(Theme.TEXT);
        setCaretColor(Theme.TEXT);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Ui.paintField(g, this, fill, hasFocus());
        super.paintComponent(g);
        Ui.paintHint(g, this, hint, false);
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
}
