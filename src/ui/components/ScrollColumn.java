package ui.components;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * The scrollable list of cards on most screens. Each item is as wide as the window
 * and as tall as it needs to be.
 */
public class ScrollColumn extends JPanel implements Scrollable {

    public ScrollColumn() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 16, 20, 16));
    }

    /** Adds an item below the previous one, with topGap pixels of space above it. */
    public void addItem(JComponent item, int topGap) {
        if (topGap > 0) {
            add(Box.createVerticalStrut(topGap));
        }
        item.setAlignmentX(LEFT_ALIGNMENT);
        add(item);
    }

    public void clear() {
        removeAll();
    }

    // ----- Scrollable: follow the width of the window, scroll only vertically -----

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return Math.max(visibleRect.height - 40, 16);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
