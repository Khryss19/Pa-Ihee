import java.time.LocalDateTime;

/** A toilet stall. Based on Fixture. */
public class Stall extends Fixture {
    private final int number;

    public Stall(int number, LocalDateTime installedAt) {
        super("STALL-" + number, installedAt);
        this.number = number;
    }

    @Override public String getName() { return "Stall " + number; }
    @Override public String showDetails() { return "Toilet stall"; }
}