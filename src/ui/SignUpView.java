package ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import service.MainSystem;
import ui.components.HeaderBar;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.RoundedPasswordField;
import ui.components.RoundedTextField;
import ui.components.ScrollColumn;
import ui.components.StyledComboBox;

/** Screen 10: create an account (saved in data/accounts.csv), then go straight to the home screen. */
public class SignUpView extends Screen {

    private final RoundedTextField nameField = new RoundedTextField("e.g. Juan Dela Cruz", Theme.FIELD_FILL);
    private final RoundedTextField srField = new RoundedTextField("e.g. 25-12345", Theme.FIELD_FILL);
    private final RoundedPasswordField passwordField =
            new RoundedPasswordField("At least 6 characters", Theme.FIELD_FILL);
    private final RoundedPasswordField confirmField =
            new RoundedPasswordField("Type the password again", Theme.FIELD_FILL);
    private final StyledComboBox<String> roleBox = new StyledComboBox<>();
    private final StyledComboBox<Integer> floorBox = new StyledComboBox<>();
    private final JLabel errorLabel = Ui.label(" ", Theme.SMALL_BOLD, Theme.RED_FG);

    public SignUpView(Navigator nav, MainSystem system) {
        super(nav, system);

        roleBox.setFill(Theme.FIELD_FILL);
        roleBox.setPlaceholder("Choose a role");
        roleBox.addItem(MainSystem.ROLE_STUDENT);
        roleBox.addItem(MainSystem.ROLE_FACULTY);
        roleBox.addItem(MainSystem.ROLE_STAFF);
        roleBox.addItem(MainSystem.ROLE_ADMIN);
        roleBox.setSelectedIndex(-1);

        floorBox.setFill(Theme.FIELD_FILL);
        floorBox.setLabeler(item -> Labels.floor((Integer) item));
        for (int floor : system.getFloors()) {
            floorBox.addItem(floor);
        }

        RoundedButton createButton = new RoundedButton("Create Account");
        createButton.addActionListener(e -> submit());

        ScrollColumn content = new ScrollColumn();
        content.addItem(Ui.label("Create Account", Theme.TITLE, Theme.TEXT), 0);
        content.addItem(fieldCard("Full Name", nameField), 12);
        content.addItem(fieldCard("SR Code", srField), 10);
        content.addItem(fieldCard("Password", passwordField), 10);
        content.addItem(fieldCard("Confirm Password", confirmField), 10);
        content.addItem(fieldCard("Role", roleBox), 10);
        content.addItem(fieldCard("Usual Floor (for nearest comfort room)", floorBox), 10);
        content.addItem(errorLabel, 10);
        content.addItem(createButton, 4);

        add(new HeaderBar("Sign Up", () -> nav.showScreen(Navigator.LOGIN), null), BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    private void submit() {
        try {
            system.register(nameField.getText(), srField.getText(),
                    new String(passwordField.getPassword()), new String(confirmField.getPassword()),
                    (String) roleBox.getSelectedItem(), (Integer) floorBox.getSelectedItem());
            nav.goHome();
        } catch (IllegalArgumentException ex) {
            // EXCEPTION HANDLING: show what is wrong and keep the form open.
            errorLabel.setText(ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        nameField.setText("");
        srField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        roleBox.setSelectedIndex(-1);
        if (floorBox.getItemCount() > 0) {
            floorBox.setSelectedIndex(0);
        }
        errorLabel.setText(" ");
    }

    private RoundedPanel fieldCard(String caption, JComponent field) {
        RoundedPanel card = Ui.card();
        card.add(Ui.label(caption, Theme.BODY_BOLD, Theme.TEXT));
        card.gap(6);
        card.add(field);
        return card;
    }
}