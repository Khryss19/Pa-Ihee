package ui;

import model.ComfortRoom;
import model.Fixture;
import model.MaintenanceRecord;
import service.MainSystem;
import ui.components.HeaderBar;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.ScrollColumn;
import ui.components.StatusBadge;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;

/** Screen 3: every fixture of the comfort rooms on one floor, with a button to report an issue. */
public class FloorDetailView extends Screen {

    private final HeaderBar header;
    private final ScrollColumn content = new ScrollColumn();
    private int floor = 0;

    public FloorDetailView(Navigator nav, MainSystem system) {
        super(nav, system);
        header = new HeaderBar("Floor Detail", () -> nav.showScreen(Navigator.DASHBOARD), null);
        add(header, BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public void refresh() {
        header.setTitle(Labels.floor(floor) + " Detail");
        content.clear();

        List<ComfortRoom> rooms = system.filterRooms(floor, null, null);
        int gap = 0;
        for (ComfortRoom room : rooms) {
            content.addItem(buildRoomCard(room), gap);
            gap = 12;
        }

        RoundedButton reportButton = new RoundedButton("Report an Issue");
        reportButton.addActionListener(e -> nav.showReportForm(rooms.isEmpty() ? null : rooms.get(0)));
        content.addItem(reportButton, 12);

        content.revalidate();
        content.repaint();
    }

    private RoundedPanel buildRoomCard(ComfortRoom room) {
        RoundedPanel card = Ui.card();
        card.add(Ui.row(Ui.label(room.getName(), Theme.HEADING, Theme.TEXT),
                StatusBadge.forStatus(room.getOverallStatus())));
        card.gap(2);
        card.add(Ui.label(room.showDetail(), Theme.SMALL, Theme.MUTED));   // "Stalls working: 3/4 | Urinals working: 2/3"
        card.gap(6);
        for (Fixture fixture : room.getFixtures()) {
            card.add(buildFixtureRow(fixture));
        }
        card.gap(6);
        card.add(Ui.label("Last Updated: " + MaintenanceRecord.format(room.getLastUpdated()),
                Theme.SMALL, Theme.MUTED));
        return card;
    }

    private JPanel buildFixtureRow(Fixture fixture) {
        RoundedPanel name = Ui.column();
        name.add(Ui.label(fixture.getName(), Theme.BODY, Theme.TEXT));
        String note = fixture.getMaintenanceNote();
        if (!fixture.isWorking() && note != null && !note.equals("-")) {
            name.add(Ui.label(note, Theme.SMALL, Theme.MUTED));
        }
        JPanel row = Ui.row(name, StatusBadge.forStatus(fixture.getStatus()));
        row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return row;
    }
}
