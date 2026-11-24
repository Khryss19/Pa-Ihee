package ui;

import model.ComfortRoom;
import model.IssueReport;
import service.IssueTracker;
import service.MainSystem;
import ui.components.HeaderBar;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.ScrollColumn;
import ui.components.StatusBadge;
import util.TextUtil;
import util.TimeUtil;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.OptionalLong;

/** Screen 7: building status, repair statistics and active issues, for administrators. */
public class FacilityOverviewView extends Screen {

    /** How many active issues are listed here; the rest are in the Maintenance Logs. */
    private static final int MAX_ACTIVE_SHOWN = 3;

    private final ScrollColumn content = new ScrollColumn();

    public FacilityOverviewView(Navigator nav, MainSystem system) {
        super(nav, system);
        add(new HeaderBar("Facility Overview", null, nav::logout), BorderLayout.NORTH);
        add(scrollPane(content), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        content.clear();
        IssueTracker tracker = system.getIssueTracker();
        List<IssueReport> active = tracker.getActive();

        content.addItem(Ui.banner("Building Facility Status",
                system.getOperationalPercent() + "% Operational " + Theme.BULLET + " "
                        + TextUtil.countOf(active.size(), "Active Issue")), 0);

        OptionalLong average = tracker.getAverageFixMinutes();
        JPanel stats = new JPanel(new GridLayout(1, 2, 10, 0));
        stats.setOpaque(false);
        stats.add(statCard("Avg. Fix Time", average.isPresent() ? TimeUtil.minutes(average.getAsLong()) : "-"));
        stats.add(statCard("Fixed This Week", TextUtil.countOf(tracker.getFixedThisWeekCount(), "Issue")));
        content.addItem(stats, 12);

        content.addItem(buildFloorSummary(tracker), 12);

        int shown = Math.min(active.size(), MAX_ACTIVE_SHOWN);
        for (int i = 0; i < shown; i++) {
            content.addItem(buildActiveCard(active.get(i)), 12);
        }
        if (active.size() > shown) {
            content.addItem(Ui.label("+ " + (active.size() - shown) + " more active "
                    + (active.size() - shown == 1 ? "issue" : "issues") + " in the Maintenance Logs",
                    Theme.SMALL, Theme.MUTED), 10);
        }

        RoundedButton logsButton = new RoundedButton("View Maintenance Logs");
        logsButton.addActionListener(e -> nav.showScreen(Navigator.LOGS));
        content.addItem(logsButton, 12);

        content.revalidate();
        content.repaint();
    }

    private RoundedPanel statCard(String caption, String value) {
        RoundedPanel card = new RoundedPanel(Theme.WHITE, null, 16, 12);
        card.add(Ui.label(caption, Theme.SMALL, Theme.MUTED));
        card.gap(4);
        card.add(Ui.label(value, Theme.STAT, Theme.TEXT));
        return card;
    }

    private RoundedPanel buildFloorSummary(IssueTracker tracker) {
        RoundedPanel card = Ui.card();
        card.add(Ui.label("Floor Status Summary", Theme.BODY_BOLD, Theme.TEXT));
        card.gap(8);

        for (int floor : system.getFloors()) {
            List<ComfortRoom> rooms = system.filterRooms(floor, null, null);
            int issues = 0;
            for (ComfortRoom room : rooms) {
                issues += tracker.countActiveInRoom(room);
            }

            JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            badges.setOpaque(false);
            if (issues == 0) {
                badges.add(StatusBadge.green("All Working"));
            } else {
                for (ComfortRoom room : rooms) {
                    int count = tracker.countActiveInRoom(room);
                    if (count > 0) {
                        badges.add(StatusBadge.orange(Labels.roomShort(room) + " " + Theme.BULLET + " "
                                + TextUtil.countOf(count, "Issue")));
                    } else {
                        badges.add(StatusBadge.greenCheck(Labels.roomShort(room)));
                    }
                }
            }
            JPanel row = Ui.row(Ui.label(Labels.floor(floor), Theme.BODY, Theme.TEXT), badges);
            row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            card.add(row);
        }
        return card;
    }

    private RoundedPanel buildActiveCard(IssueReport report) {
        RoundedPanel card = Ui.card();
        card.add(Ui.row(
                Ui.label("Active Issue " + Theme.BULLET + " #" + report.getId(), Theme.SMALL_BOLD, Theme.ORANGE_FG),
                Ui.label(Labels.when(report.getCreatedAt()), Theme.SMALL, Theme.MUTED)));
        card.gap(6);
        card.add(Ui.label(Labels.location(report), Theme.BODY_BOLD, Theme.TEXT));
        card.gap(3);
        card.add(Ui.label(report.getIssueType() + " " + Theme.BULLET + " Assigned to Maintenance",
                Theme.SMALL, Theme.MUTED));
        return card;
    }
}
