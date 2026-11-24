package paihee.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * NEW: one problem that needs repair (REQ-001, REQ-002, ...). It is what the Maintenance Queue,
 * the Facility Overview and the Maintenance Logs screens show.
 * fixture is null when the problem is about the whole room (no water, bad smell, ...).
 */
public class IssueReport {

    /** Issue types for a problem that is not about one particular fixture. */
    public static final List<String> GENERAL_ISSUES = List.of("No Water", "Broken Faucet", "Bad Smell", "Other");

    private final String id;
    private final ComfortRoom room;
    private final Fixture fixture;
    private final String issueType;
    private final String notes;
    private final String reportedBy;
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;

    public IssueReport(String id, ComfortRoom room, Fixture fixture, String issueType,
                       String notes, String reportedBy, LocalDateTime createdAt) {
        this.id = id;
        this.room = room;
        this.fixture = fixture;
        this.issueType = issueType;
        this.notes = notes == null ? "" : notes;
        this.reportedBy = reportedBy;
        this.createdAt = createdAt;
    }

    /** Closes the report. The caller is responsible for changing the fixture status. */
    public void resolve(String staffName, LocalDateTime when) {
        if (isResolved()) {
            throw new IllegalArgumentException(id + " is already resolved.");
        }
        if (when.isBefore(createdAt)) {
            throw new IllegalArgumentException("A repair cannot finish before it was reported.");
        }
        this.resolvedAt = when;
        this.resolvedBy = staffName;
    }

    public boolean isResolved() { return resolvedAt != null; }

    /** Minutes the repair took (0 while the report is open). */
    public long getFixMinutes() {
        return isResolved() ? Duration.between(createdAt, resolvedAt).toMinutes() : 0;
    }

    public String getFixtureName() { return fixture == null ? "General" : fixture.getName(); }

    public String getId() { return id; }
    public ComfortRoom getRoom() { return room; }
    public Fixture getFixture() { return fixture; }
    public String getIssueType() { return issueType; }
    public String getNotes() { return notes; }
    public String getReportedBy() { return reportedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public String getResolvedBy() { return resolvedBy; }
}
