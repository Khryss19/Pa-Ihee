package ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.ComfortRoom;
import model.Fixture;
import model.MaintenanceRecord;
import service.MainSystem;
import ui.components.ChipBar;
import ui.components.HeaderBar;
import ui.components.RoundedPanel;
import ui.components.ScrollColumn;
import ui.components.StatusBadge;
import ui.components.StyledComboBox;

/**
 * Screen 9: every saved maintenance record (read from the CSV file), newest first.
 * Filter by comfort room and by kind of entry. For maintenance staff and administrators.
 */
public class MaintenanceHistoryView extends Screen {

    private static final String ALL_ROOMS = "All comfort rooms";

    private static final int ALL = 0;
    private static final int REPORTED = 1;
    private static final int IN_REPAIR = 2;
    private static final int FIXED = 3;

    private final ScrollColumn content = new ScrollColumn();
    private final StyledComboBox<Object> roomBox = new StyledComboBox<>();
    private int filter = ALL;
    private boolean adjusting = false;   // true while the room dropdown is reset by code

    public MaintenanceHistoryView(Navigator nav, MainSystem system) {
        super(nav, system);

        roomBox.setLabeler(item -> item instanceof ComfortRoom ? Labels.room((ComfortRoom) item) : String.valueOf(item));
        roomBox.addItem(ALL_ROOMS);
        for (ComfortRoom room : system.getRooms()) {
            roomBox.addItem(room);
        }
        roomBox.addActionListener(e -> {
            if (!adjusting) {
                refresh();
            }
        });

        // The dropdown stays outside the scrolling list, so rebuilding the list never replaces it.
        JPanel picker = new JPanel(new BorderLayout());
        picker.setOpaque(false);
        picker.setBorder(BorderFactory.createEmptyBorder(0, 16, 10, 16));
        picker.add(roomBox, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(new HeaderBar("Maintenance History", nav::goHome, null), BorderLayout.NORTH);
        top.add(picker, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    /** Called on logout so the next person starts with no filters. */
    public void resetFilter() {
        filter = ALL;
        adjusting = true;
        roomBox.setSelectedIndex(0);
        adjusting = false;
    }

    @Override
    public void refresh() {
        content.clear();

        Object picked = roomBox.getSelectedItem();
        ComfortRoom room = (picked instanceof ComfortRoom) ? (ComfortRoom) picked : null;

        List<MaintenanceRecord> all;
        try {
            all = system.getHistory(room);
        } catch (IllegalArgumentException ex) {
            // EXCEPTION HANDLING: for example, a user who is not allowed to see the history.
            RoundedPanel box = Ui.card();
            box.add(Ui.label(ex.getMessage(), Theme.BODY, Theme.RED_FG));
            content.addItem(box, 0);
            content.revalidate();
            content.repaint();
            return;
        }

        List<MaintenanceRecord> shown = new ArrayList<>();
        for (MaintenanceRecord rec : all) {
            if (matches(rec)) {
                shown.add(rec);
            }
        }

        content.addItem(new ChipBar(List.of("All", "Reported", "In Repair", "Fixed"), filter, index -> {
            filter = index;
            refresh();
        }), 0);
        content.addItem(Ui.label("Showing " + shown.size() + " of " + all.size() + " records",
                Theme.SMALL_BOLD, Theme.MUTED), 10);
        content.addItem(Ui.label("Saved in " + system.getHistoryFilePath(), Theme.SMALL, Theme.MUTED), 2);

        if (shown.isEmpty()) {
            RoundedPanel empty = Ui.card();
            empty.add(Ui.label("No records to show.", Theme.BODY, Theme.MUTED));
            content.addItem(empty, 12);
        }
        for (MaintenanceRecord rec : shown) {
            content.addItem(buildCard(rec), 12);
        }
        content.revalidate();
        content.repaint();
    }

    private boolean matches(MaintenanceRecord rec) {
        String status = rec.getStatus();
        switch (filter) {
            case REPORTED:
                return status.equals(MaintenanceRecord.PROBLEM_REPORTED);
            case IN_REPAIR:
                return status.equals(Fixture.UNDER_MAINTENANCE);
            case FIXED:
                return status.equals(Fixture.WORKING) || status.equals(MaintenanceRecord.PROBLEM_RESOLVED);
            default:
                return true;
        }
    }

    private RoundedPanel buildCard(MaintenanceRecord rec) {
        RoundedPanel card = Ui.card();
        card.add(Ui.row(
                Ui.label(MaintenanceRecord.format(rec.getDateTime()), Theme.SMALL, Theme.MUTED),
                badgeFor(rec.getStatus())));
        card.gap(6);
        card.add(Ui.label(Labels.room(rec.getRoom()) + " " + Theme.BULLET + " " + rec.getFixtureName(),
                Theme.BODY_BOLD, Theme.TEXT));
        card.gap(3);
        card.add(wrapped(rec.getReason(), Theme.SMALL, Theme.MUTED));
        card.gap(3);
        card.add(Ui.label("By " + rec.getUpdatedBy(), Theme.SMALL, Theme.MUTED));
        return card;
    }

    private static StatusBadge badgeFor(String status) {
        if (status.equals(Fixture.WORKING) || status.equals(MaintenanceRecord.PROBLEM_RESOLVED)) {
            return StatusBadge.green(status);
        }
        if (status.equals(MaintenanceRecord.PROBLEM_REPORTED)) {
            return StatusBadge.orange(status);
        }
        return StatusBadge.red(status);
    }

    /** A label that wraps long text onto several lines instead of running off the screen. */
    private static JLabel wrapped(String text, java.awt.Font font, java.awt.Color color) {
        String safe = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return Ui.label("<html><div style='width:280px'>" + safe + "</div></html>", font, color);
    }
}