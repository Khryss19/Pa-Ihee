package paihee.model;

import java.time.LocalDateTime;
import java.util.List;

/** A toilet stall. Based on Fixture. */
public class Stall extends Fixture {
    private final int number;

    public Stall(int number, LocalDateTime installedAt) {
        super("STALL-" + number, installedAt);
        this.number = number;
    }

    @Override public String getName() { return "Stall " + number; }
    @Override public String showDetails() { return "Toilet stall"; }

    /** NEW */
    @Override public List<String> getIssueTypes() {
        return List.of("Clogged / Won't Flush", "Broken Lock", "Broken Door", "Bad Smell", "No Water", "Other");
    }
}
