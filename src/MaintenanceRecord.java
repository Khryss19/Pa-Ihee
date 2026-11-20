import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Stores one past update, repair, or reported problem. */
public class MaintenanceRecord {
    /** Status used when a student/employee reports a problem (no fixture status is changed). */
    public static final String PROBLEM_REPORTED = "Problem Reported";

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH);

    private final ComfortRoom room;
    private final String fixtureName;
    private final String status;
    private final String reason;
    private final LocalDateTime dateTime;
    private final String updatedBy;

    public MaintenanceRecord(ComfortRoom room, String fixtureName, String status,
                             String reason, LocalDateTime dateTime, String updatedBy) {
        this.room = room;
        this.fixtureName = fixtureName;
        this.status = status;
        this.reason = reason;
        this.dateTime = dateTime;
        this.updatedBy = updatedBy;
    }

    public ComfortRoom getRoom() { return room; }
    public String getFixtureName() { return fixtureName; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getUpdatedBy() { return updatedBy; }

    /** Shared date/time format used everywhere in the program. */
    public static String format(LocalDateTime time) { return FORMAT.format(time); }

    /** The record as one table row. */
    public Object[] showRecord() {
        return new Object[]{format(dateTime), room.getName() + " - F" + room.getFloor(),
                fixtureName, status, reason, updatedBy};
    }
}