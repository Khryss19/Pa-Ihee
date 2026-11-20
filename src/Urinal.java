import java.time.LocalDateTime;

/** A urinal. Based on Fixture. */
public class Urinal extends Fixture {
    private final int number;

    public Urinal(int number, LocalDateTime installedAt) {
        super("URINAL-" + number, installedAt);
        this.number = number;
    }

    @Override public String getName() { return "Urinal " + number; }
    @Override public String showDetails() { return "Urinal"; }
}