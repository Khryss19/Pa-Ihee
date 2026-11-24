import service.MainSystem;
import ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Start here: run this file to open Pa-Ihee. */
public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            // The cross-platform look keeps the colors the same on Windows, macOS and Linux.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // If it fails, Swing simply keeps its default look.
        }

        MainSystem system = new MainSystem();
        SwingUtilities.invokeLater(() -> new MainFrame(system).setVisible(true));
    }
}
