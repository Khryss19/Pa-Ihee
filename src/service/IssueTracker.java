package paihee.service;

import paihee.model.ComfortRoom;
import paihee.model.Fixture;
import paihee.model.IssueReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalLong;

/**
 * NEW: keeps the list of issue reports and calculates the numbers shown to staff and administrators.
 * MainSystem checks who is allowed to do what; this class only stores and counts.
 */
public class IssueTracker {

    private final List<IssueReport> reports = new ArrayList<>();
    private int nextNumber = 1;

    // ---------- changing ----------

    public IssueReport open(ComfortRoom room, Fixture fixture, String issueType,
                            String notes, String reportedBy, LocalDateTime when) {
        String id = String.format("REQ-%03d", nextNumber++);
        IssueReport report = new IssueReport(id, room, fixture, issueType, notes, reportedBy, when);
        reports.add(report);
        return report;
    }

    /**
     * Keeps the queue in step when staff change a fixture directly (MainSystem.updateStatus):
     * Working closes the fixture's open report, Under Maintenance opens one if there is none.
     */
    void syncAfterStatusChange(ComfortRoom room, Fixture fixture, String newStatus,
                               String reason, String staffName, LocalDateTime when) {
        IssueReport open = findOpen(room, fixture);
        if (newStatus.equals(Fixture.WORKING)) {
            if (open != null) {
                open.resolve(staffName, when);
            }
        } else if (open == null) {
            open(room, fixture, reason, "", staffName, when);
        }
    }

    // ---------- looking up ----------

    public IssueReport findById(String id) {
        for (IssueReport report : reports) {
            if (report.getId().equalsIgnoreCase(id)) return report;
        }
        return null;
    }

    /** The open report of one fixture, or null. */
    public IssueReport findOpen(ComfortRoom room, Fixture fixture) {
        for (IssueReport report : reports) {
            if (!report.isResolved() && report.getRoom() == room && report.getFixture() == fixture) {
                return report;
            }
        }
        return null;
    }

    /** The open whole-room report of the same type, or null. */
    public IssueReport findOpenGeneral(ComfortRoom room, String issueType) {
        for (IssueReport report : reports) {
            if (!report.isResolved() && report.getRoom() == room && report.getFixture() == null
                    && report.getIssueType().equals(issueType)) {
                return report;
            }
        }
        return null;
    }

    private static Comparator<IssueReport> newestFirst() {
        return Comparator.comparing(IssueReport::getCreatedAt)
                .thenComparing(IssueReport::getId)
                .reversed();
    }

    /** All reports, newest first. */
    public List<IssueReport> getAll() {
        List<IssueReport> result = new ArrayList<>(reports);
        result.sort(newestFirst());
        return result;
    }

    /** Reports that still need repair, newest first. Pass 0 for all floors. */
    public List<IssueReport> getPending(int floor) {
        List<IssueReport> result = new ArrayList<>();
        for (IssueReport report : getAll()) {
            if (!report.isResolved() && (floor == 0 || report.getRoom().getFloor() == floor)) {
                result.add(report);
            }
        }
        return result;
    }

    public List<IssueReport> getActive() {
        return getPending(0);
    }

    public List<IssueReport> getResolved() {
        List<IssueReport> result = new ArrayList<>();
        for (IssueReport report : getAll()) {
            if (report.isResolved()) result.add(report);
        }
        return result;
    }

    // ---------- numbers ----------

    public int getTotalCount() { return reports.size(); }

    public int getPendingCount() { return getPending(0).size(); }

    public int countActiveInRoom(ComfortRoom room) {
        int count = 0;
        for (IssueReport report : reports) {
            if (!report.isResolved() && report.getRoom() == room) count++;
        }
        return count;
    }

    public int getFixedTodayCount() {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (IssueReport report : reports) {
            if (report.isResolved() && report.getResolvedAt().toLocalDate().equals(today)) count++;
        }
        return count;
    }

    public int getFixedThisWeekCount() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        int count = 0;
        for (IssueReport report : reports) {
            if (report.isResolved() && report.getResolvedAt().isAfter(weekAgo)) count++;
        }
        return count;
    }

    /** Average repair time in minutes, or empty if nothing has been fixed yet. */
    public OptionalLong getAverageFixMinutes() {
        long total = 0;
        int count = 0;
        for (IssueReport report : reports) {
            if (report.isResolved()) {
                total += report.getFixMinutes();
                count++;
            }
        }
        if (count == 0) return OptionalLong.empty();
        return OptionalLong.of(Math.round((double) total / count));
    }
}
