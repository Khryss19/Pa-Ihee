package ui;

import model.IssueReport;
import service.IssueTracker;
import service.MainSystem;
import ui.components.CheckIcon;
import ui.components.ChipBar;
import ui.components.HeaderBar;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.ScrollColumn;
import ui.components.StatusBadge;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/** Screen 6: the repair queue for maintenance staff. "Mark as Working" closes a report. */
public class MaintenanceQueueView extends Screen {

    private final ScrollColumn content = new ScrollColumn();
    private int selectedFloor = 0;   // 0 = All

    public MaintenanceQueueView(Navigator nav, MainSystem system) {
        super(nav, system);
        add(new HeaderBar("Maintenance Queue", null, nav::logout), BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    public void resetFilter() {
        selectedFloor = 0;
    }

    @Override
    public void refresh() {
        rebuild(null, false);
    }

    /** Rebuilds the list. message is the confirmation (or error) box under the chips, or null for none. */
    private void rebuild(String message, boolean isError) {
        content.clear();
        IssueTracker tracker = system.getIssueTracker();

        content.addItem(Ui.banner("Queue Overview",
                tracker.getPendingCount() + " Pending " + Theme.BULLET + " "
                        + tracker.getFixedTodayCount() + " Fixed Today"), 0);

        List<Integer> floors = system.getFloors();
        List<String> labels = new ArrayList<>();
        labels.add("All");
        for (int floor : floors) {
            labels.add(Labels.floor(floor));
        }
        int selectedIndex = (selectedFloor == 0) ? 0 : floors.indexOf(selectedFloor) + 1;
        content.addItem(new ChipBar(labels, selectedIndex, index -> {
            selectedFloor = (index == 0) ? 0 : floors.get(index - 1);
            rebuild(null, false);
        }), 12);

        if (message != null) {
            RoundedPanel box = new RoundedPanel(isError ? Theme.RED_BG : Theme.GREEN_BG, null, 12, 10);
            JLabel text = Ui.label(message, Theme.SMALL_BOLD, isError ? Theme.RED_FG : Theme.GREEN_FG);
            if (!isError) {
                text.setIcon(new CheckIcon(12, Theme.GREEN_FG));
                text.setIconTextGap(6);
            }
            box.add(text);
            content.addItem(box, 12);
        }

        List<IssueReport> pending = tracker.getPending(selectedFloor);
        if (pending.isEmpty()) {
            RoundedPanel empty = Ui.card();
            empty.add(Ui.label("No pending issues. Everything here is working.", Theme.BODY, Theme.MUTED));
            content.addItem(empty, 12);
        }
        for (IssueReport report : pending) {
            content.addItem(buildCard(report), 12);
        }
        RoundedButton historyButton = new RoundedButton("View Maintenance History");
        historyButton.addActionListener(e -> nav.showScreen(Navigator.HISTORY));
        content.addItem(historyButton, 12);
        
        content.revalidate();
        content.repaint();
    }

    private RoundedPanel buildCard(IssueReport report) {
        RoundedPanel card = Ui.card();
        card.add(Ui.row(
                Ui.label("#" + report.getId() + " " + Theme.BULLET + " " + Labels.when(report.getCreatedAt()),
                        Theme.SMALL, Theme.MUTED),
                StatusBadge.red("Needs Repair")));
        card.gap(6);
        card.add(Ui.label(Labels.location(report), Theme.BODY_BOLD, Theme.TEXT));
        card.gap(3);
        card.add(Ui.label("Issue: " + report.getIssueType(), Theme.SMALL, Theme.MUTED));
        if (!report.getNotes().isEmpty()) {
            card.gap(3);
            card.add(Ui.label("Note: " + Ui.shorten(report.getNotes(), 46), Theme.SMALL, Theme.MUTED));
        }
        card.gap(12);

        RoundedButton fixButton = new RoundedButton("Mark as Working");
        fixButton.addActionListener(e -> markWorking(report));
        card.add(fixButton);
        return card;
    }

    private void markWorking(IssueReport report) {
        try {
            system.resolveReport(report.getId());
            rebuild("#" + report.getId() + " marked as Working", false);
        } catch (IllegalArgumentException ex) {
            // EXCEPTION HANDLING: for example, the report was already closed.
            rebuild(ex.getMessage(), true);
        }
    }
}
