package ui.components;

import ui.Theme;
import ui.Ui;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import java.awt.Graphics;

/** A multi-line text box with grey placeholder text. Put it inside a JScrollPane. */
public class HintTextArea extends JTextArea {

    private final String hint;

    public HintTextArea(String hint) {
        this.hint = hint;
        setOpaque(false);
        setFont(Theme.BODY);
        setForeground(Theme.TEXT);
        setCaretColor(Theme.TEXT);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Ui.paintHint(g, this, hint, true);
    }
}
