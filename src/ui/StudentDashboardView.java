package ui;

import model.ComfortRoom;
import service.MainSystem;
import ui.components.ChipBar;
import ui.components.HeaderBar;
import ui.components.RoundedPanel;
import ui.components.ScrollColumn;
import ui.components.StatusBadge;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/** Screen 2: nearest available comfort room, floor filter, and one card per floor. */
public class StudentDashboardView extends Screen {

    private final ScrollColumn content = new ScrollColumn();
    private int selectedFloor = 0;   // 0 = All

    public StudentDashboardView(Navigator nav, MainSystem system) {
        super(nav, system);
        add(new HeaderBar("Student Dashboard", nav::logout, null), BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    public void resetFilter() {
        selectedFloor = 0;
    }

    @Override
    public void refresh() {
        content.clear();
        content.addItem(buildBanner(), 0);

        List<Integer> floors = system.getFloors();
        List<String> labels = new ArrayList<>();
        labels.add("All");
        for (int floor : floors) {
            labels.add(Labels.floor(floor));
        }
        int selectedIndex = (selectedFloor == 0) ? 0 : floors.indexOf(selectedFloor) + 1;
        content.addItem(new ChipBar(labels, selectedIndex, index -> {
            selectedFloor = (index == 0) ? 0 : floors.get(index - 1);
            refresh();
        }), 12);

        for (int floor : floors) {
            if (selectedFloor == 0 || selectedFloor == floor) {
                content.addItem(buildFloorCard(floor), 12);
            }
        }
        content.revalidate();
        content.repaint();
    }

    private RoundedPanel buildBanner() {
        // "Nearest working CR" feature: uses the floor of the logged-in person.
        ComfortRoom nearest = system.nearestWorking(system.getCurrentFloor());
        String text = (nearest == null)
                ? "No comfort room is working right now"
                : Labels.room(nearest) + " " + Theme.BULLET + " " + nearest.getOverallStatus();
        return Ui.banner("Nearest Available Comfort Room", text);
    }

    private RoundedPanel buildFloorCard(int floor) {
        RoundedPanel card = Ui.card();
        JLabel chevron = Ui.label(Theme.CHEVRON, Theme.TITLE, Theme.MUTED);
        card.add(Ui.row(Ui.label(Labels.floor(floor), Theme.HEADING, Theme.TEXT), chevron));
        card.gap(6);
        for (ComfortRoom room : system.filterRooms(floor, null, null)) {
            JPanel row = Ui.row(
                    Ui.label(room.getName(), Theme.BODY, Theme.TEXT),
                    StatusBadge.forStatus(room.getOverallStatus()));
            row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            card.add(row);
        }
        Ui.makeClickable(card, () -> nav.showFloorDetail(floor));
        return card;
    }
}
