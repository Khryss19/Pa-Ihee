package ui;

import service.MainSystem;
import ui.components.LogoMark;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.RoundedPasswordField;
import ui.components.RoundedTextField;
import ui.components.StyledComboBox;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/** Screen 1: SR Code, password and role. */
public class LoginView extends Screen {

    private final RoundedTextField srField = new RoundedTextField("e.g. 25-12345", Theme.WHITE);
    private final RoundedPasswordField passwordField =
            new RoundedPasswordField("\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022", Theme.WHITE);
    private final StyledComboBox<String> roleBox = new StyledComboBox<>();
    private final JLabel errorLabel = Ui.label(" ", Theme.SMALL_BOLD, Theme.RED_FG);

    public LoginView(Navigator nav, MainSystem system) {
        super(nav, system);

        roleBox.setPlaceholder("Choose a role");
        roleBox.addItem(MainSystem.ROLE_STUDENT_EMPLOYEE);
        roleBox.addItem(MainSystem.ROLE_STAFF);
        roleBox.addItem(MainSystem.ROLE_ADMIN);
        roleBox.setSelectedIndex(-1);

        RoundedButton loginButton = new RoundedButton("Log In");
        loginButton.addActionListener(e -> attemptLogin());
        srField.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        // logo + name
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        brand.add(new LogoMark(48));
        JLabel name = Ui.label("Pa-Ihee", Theme.DISPLAY, Theme.TEXT);
        name.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        brand.add(name);

        RoundedPanel form = Ui.column();
        form.setBorder(BorderFactory.createEmptyBorder(56, 24, 24, 24));
        form.add(brand);
        form.gap(4);
        form.add(Ui.label(Theme.TAGLINE, Theme.BODY, Theme.MUTED));
        form.gap(36);
        form.add(Ui.label("SR Code", Theme.BODY_BOLD, Theme.TEXT));
        form.gap(6);
        form.add(srField);
        form.gap(16);
        form.add(Ui.label("Password", Theme.BODY_BOLD, Theme.TEXT));
        form.gap(6);
        form.add(passwordField);
        form.gap(16);
        form.add(Ui.label("Select Role", Theme.BODY_BOLD, Theme.TEXT));
        form.gap(6);
        form.add(roleBox);
        form.gap(12);
        form.add(errorLabel);
        form.gap(12);
        form.add(loginButton);

        add(form, BorderLayout.NORTH);
    }

    private void attemptLogin() {
        try {
            system.login(srField.getText(), new String(passwordField.getPassword()),
                    (String) roleBox.getSelectedItem());
            nav.goHome();
        } catch (IllegalArgumentException ex) {
            // EXCEPTION HANDLING: show the message and let the user try again.
            errorLabel.setText(ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        srField.setText("");
        passwordField.setText("");
        roleBox.setSelectedIndex(-1);
        errorLabel.setText(" ");
    }
}
