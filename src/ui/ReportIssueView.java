package ui;

import model.ComfortRoom;
import model.Fixture;
import model.IssueReport;
import service.MainSystem;
import ui.components.HeaderBar;
import ui.components.HintTextArea;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.ScrollColumn;
import ui.components.StyledComboBox;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Screen 4: choose a room, a fixture and an issue type, add notes, and submit. */
public class ReportIssueView extends Screen {

    /** The last item of the Fixture dropdown: a problem with the whole room (no water, bad smell...). */
    private static final String GENERAL = "General (whole room)";

    private final StyledComboBox<ComfortRoom> roomBox = new StyledComboBox<>();
    private final StyledComboBox<Object> fixtureBox = new StyledComboBox<>();
    private final StyledComboBox<String> typeBox = new StyledComboBox<>();
    private final HintTextArea notesArea = new HintTextArea("Briefly describe the issue...");
    private final JLabel errorLabel = Ui.label(" ", Theme.SMALL_BOLD, Theme.RED_FG);
    private boolean adjusting = false;   // true while one dropdown refills another

    public ReportIssueView(Navigator nav, MainSystem system) {
        super(nav, system);

        roomBox.setFill(Theme.FIELD_FILL);
        fixtureBox.setFill(Theme.FIELD_FILL);
        typeBox.setFill(Theme.FIELD_FILL);
        roomBox.setLabeler(item -> item instanceof ComfortRoom ? Labels.room((ComfortRoom) item) : String.valueOf(item));
        fixtureBox.setLabeler(item -> item instanceof Fixture ? ((Fixture) item).getName() : String.valueOf(item));

        for (ComfortRoom room : system.getRooms()) {
            roomBox.addItem(room);
        }
        populateFixtures();
        // Changing the room refills the fixtures; changing the fixture refills the issue types.
        roomBox.addActionListener(e -> {
            if (!adjusting) {
                populateFixtures();
            }
        });
        fixtureBox.addActionListener(e -> {
            if (!adjusting) {
                populateTypes();
            }
        });

        RoundedButton submitButton = new RoundedButton("Submit Report");
        submitButton.addActionListener(e -> submit());

        ScrollColumn content = new ScrollColumn();
        content.addItem(Ui.label("Report an Issue", Theme.TITLE, Theme.TEXT), 0);
        content.addItem(fieldCard("Comfort Room", roomBox), 12);
        content.addItem(fieldCard("Fixture", fixtureBox), 10);
        content.addItem(fieldCard("Issue Type", typeBox), 10);
        content.addItem(notesCard(), 10);
        content.addItem(errorLabel, 10);
        content.addItem(submitButton, 4);

        add(new HeaderBar("Report an Issue", () -> nav.showScreen(Navigator.FLOOR_DETAIL), null),
                BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    /** Called before the screen is shown so the right room is already selected. */
    public void prepare(ComfortRoom room) {
        if (room != null) {
            roomBox.setSelectedItem(room);   // refills the fixtures through the listener
        }
    }

    @Override
    public void refresh() {
        notesArea.setText("");
        errorLabel.setText(" ");
    }

    private void populateFixtures() {
        ComfortRoom room = (ComfortRoom) roomBox.getSelectedItem();
        adjusting = true;
        fixtureBox.removeAllItems();
        if (room != null) {
            for (Fixture fixture : room.getFixtures()) {
                fixtureBox.addItem(fixture);
            }
            fixtureBox.addItem(GENERAL);
        }
        adjusting = false;
        populateTypes();
    }

    private void populateTypes() {
        Object selected = fixtureBox.getSelectedItem();
        typeBox.removeAllItems();
        if (selected instanceof Fixture) {
            for (String type : ((Fixture) selected).getIssueTypes()) {
                typeBox.addItem(type);
            }
        } else if (selected != null) {
            for (String type : IssueReport.GENERAL_ISSUES) {
                typeBox.addItem(type);
            }
        }
    }

    private void submit() {
        try {
            Object selected = fixtureBox.getSelectedItem();
            Fixture fixture = (selected instanceof Fixture) ? (Fixture) selected : null;
            IssueReport report = system.reportIssue(
                    (ComfortRoom) roomBox.getSelectedItem(),
                    fixture,
                    (String) typeBox.getSelectedItem(),
                    notesArea.getText());
            nav.showReportSubmitted(report);
        } catch (IllegalArgumentException ex) {
            // EXCEPTION HANDLING: show what is wrong and keep the form open.
            errorLabel.setText(ex.getMessage());
        }
    }

    private RoundedPanel fieldCard(String caption, JComponent field) {
        RoundedPanel card = Ui.card();
        card.add(Ui.label(caption, Theme.BODY_BOLD, Theme.TEXT));
        card.gap(6);
        card.add(field);
        return card;
    }

    private RoundedPanel notesCard() {
        RoundedPanel card = Ui.card();
        card.add(Ui.label("Additional Notes (Optional)", Theme.BODY_BOLD, Theme.TEXT));
        card.gap(6);

        JScrollPane scroll = new JScrollPane(notesArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        RoundedPanel box = new RoundedPanel(Theme.FIELD_FILL, Theme.BORDER, 12, 2);
        box.setPreferredSize(new Dimension(100, 90));
        box.add(scroll);
        card.add(box);
        return card;
    }
}
