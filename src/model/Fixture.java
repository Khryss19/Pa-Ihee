package paihee.model;

import java.time.LocalDateTime;
import java.util.List;

/** The basic type for anything inside a comfort room that can be used or repaired. */
public abstract class Fixture {
    public static final String WORKING = "Working";
    public static final String UNDER_MAINTENANCE = "Under Maintenance";

    private final String id;
    private String status = WORKING;
    private String maintenanceNote = "-";
    private LocalDateTime lastUpdated;

    protected Fixture(String id, LocalDateTime installedAt) {
        this.id = id;
        this.lastUpdated = installedAt;
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getMaintenanceNote() { return maintenanceNote; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public boolean isWorking() { return status.equals(WORKING); }

    /** The only way to change the status (encapsulation). Rejects unknown statuses. */
    public void changeStatus(String newStatus, String reason, LocalDateTime when) {
        if (!newStatus.equals(WORKING) && !newStatus.equals(UNDER_MAINTENANCE)) {
            throw new IllegalArgumentException("Unknown status: " + newStatus);
        }
        this.status = newStatus;
        this.maintenanceNote = reason;
        this.lastUpdated = when;
    }

    public void addNote(String note) { this.maintenanceNote = note; }

    /** Display name such as "Stall 2" or "Urinal 1". */
    public abstract String getName();

    /** Each kind of fixture describes itself in its own way (polymorphism). */
    public abstract String showDetails();

    /** NEW: the problems a person can report for this kind of fixture ("Report an Issue" screen). */
    public abstract List<String> getIssueTypes();

    @Override
    public String toString() { return getName() + " (" + status + ")"; }
}
