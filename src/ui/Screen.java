package ui;

import service.MainSystem;
import ui.components.ScrollColumn;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

/** The base class of every screen. refresh() is called each time the screen is shown. */
public abstract class Screen extends JPanel {

    protected final Navigator nav;
    protected final MainSystem system;

    protected Screen(Navigator nav, MainSystem system) {
        super(new BorderLayout());
        this.nav = nav;
        this.system = system;
        setBackground(Theme.BACKGROUND);
    }

    /** Rebuilds the screen from the latest data. */
    public abstract void refresh();

    /** Wraps a column of cards in a scroll pane that matches the page background. */
    protected static JScrollPane scrollPane(ScrollColumn column) {
        JScrollPane pane = new JScrollPane(column,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBorder(null);
        pane.setViewportBorder(null);
        pane.getViewport().setBackground(Theme.BACKGROUND);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        return pane;
    }
}
