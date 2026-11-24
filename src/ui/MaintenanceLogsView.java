package ui;

import model.IssueReport;
import service.IssueTracker;
import service.MainSystem;
import ui.components.ChipBar;
import ui.components.HeaderBar;
import ui.components.RoundedPanel;
import ui.components.ScrollColumn;
import ui.components.StatusBadge;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

/** Screen 8: the full repair history with All / Active / Resolved filters. */
public class MaintenanceLogsView extends Screen {

    private static final int ALL = 0;
    private static final int ACTIVE = 1;
    private static final int RESOLVED = 2;

    private final ScrollColumn content = new ScrollColumn();
    private int filter = ALL;

    public MaintenanceLogsView(Navigator nav, MainSystem system) {
        super(nav, system);
        add(new HeaderBar("Maintenance Logs", () -> nav.showScreen(Navigator.OVERVIEW), null), BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    public void resetFilter() {
        filter = ALL;
    }

    @Override
    public void refresh() {
        content.clear();
        IssueTracker tracker = system.getIssueTracker();

        List<String> labels = Arrays.asList(
                "All (" + tracker.getTotalCount() + ")",
                "Active (" + tracker.getActive().size() + ")",
                "Resolved (" + tracker.getResolved().size() + ")");
        content.addItem(new ChipBar(labels, filter, index -> {
            filter = index;
            refresh();
        }), 0);

        List<IssueReport> shown = (filter == ACTIVE) ? tracker.getActive()
                : (filter == RESOLVED) ? tracker.getResolved()
                : tracker.getAll();

        if (shown.isEmpty()) {
            RoundedPanel empty = Ui.card();
            empty.add(Ui.label("No reports to show.", Theme.BODY, Theme.MUTED));
            content.addItem(empty, 12);
        }
        for (IssueReport report : shown) {
            content.addItem(buildCard(report), 12);
        }
        content.revalidate();
        content.repaint();
    }

    private RoundedPanel buildCard(IssueReport report) {
        StatusBadge badge = report.isResolved()
                ? StatusBadge.green("Fixed " + Theme.BULLET + " " + report.getFixMinutes() + "m")
                : StatusBadge.red("Needs Repair");

        RoundedPanel card = Ui.card();
        card.add(Ui.row(
                Ui.label("#" + report.getId() + " " + Theme.BULLET + " " + Labels.when(report.getCreatedAt()),
                        Theme.SMALL, Theme.MUTED),
                badge));
        card.gap(6);
        card.add(Ui.label(Labels.location(report), Theme.BODY_BOLD, Theme.TEXT));
        card.gap(3);
        String who = report.isResolved() ? "Resolved by " + report.getResolvedBy() : "Assigned to Maintenance";
        card.add(Ui.label(report.getIssueType() + " " + Theme.BULLET + " " + who, Theme.SMALL, Theme.MUTED));
        return card;
    }
}
