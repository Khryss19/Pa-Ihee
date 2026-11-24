package ui.components;

import ui.Theme;
import ui.Ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * The title row at the top of a screen. With onBack it shows "<- Title" as a clickable back button;
 * with onLogout it also shows a "Log out" link on the right.
 */
public class HeaderBar extends JPanel {

    private final JButton backButton;
    private final JLabel titleLabel;

    public HeaderBar(String title, Runnable onBack, Runnable onLogout) {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));

        if (onBack != null) {
            backButton = Ui.linkButton(Theme.BACK + "  " + title, Theme.HEADING, Theme.TEXT, onBack);
            titleLabel = null;
            add(backButton, BorderLayout.WEST);
        } else {
            titleLabel = Ui.label(title, Theme.HEADING, Theme.TEXT);
            backButton = null;
            add(titleLabel, BorderLayout.WEST);
        }
        if (onLogout != null) {
            add(Ui.linkButton("Log out", Theme.SMALL_BOLD, Theme.MUTED, onLogout), BorderLayout.EAST);
        }
    }

    public void setTitle(String title) {
        if (backButton != null) {
            backButton.setText(Theme.BACK + "  " + title);
        } else {
            titleLabel.setText(title);
        }
    }
}
