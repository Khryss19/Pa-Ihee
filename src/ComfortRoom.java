import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Represents one comfort room and the fixtures inside it. */
public class ComfortRoom {
    private final String name;      // e.g. "Men's CR"
    private final String type;      // Women, Men, or All Gender
    private final String building;
    private final int floor;
    private final List<Fixture> fixtures = new ArrayList<>();

    public ComfortRoom(String name, String type, String building, int floor,
                       int stalls, int urinals, LocalDateTime installedAt) {
        this.name = name;
        this.type = type;
        this.building = building;
        this.floor = floor;
        for (int i = 1; i <= stalls; i++) fixtures.add(new Stall(i, installedAt));
        for (int i = 1; i <= urinals; i++) fixtures.add(new Urinal(i, installedAt));
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getBuilding() { return building; }
    public int getFloor() { return floor; }
    public List<Fixture> getFixtures() { return fixtures; }

    /** Count working fixtures of one kind (Stall.class or Urinal.class). */
    public int countWorking(Class<? extends Fixture> kind) {
        int n = 0;
        for (Fixture f : fixtures) if (kind.isInstance(f) && f.isWorking()) n++;
        return n;
    }

    public int countTotal(Class<? extends Fixture> kind) {
        int n = 0;
        for (Fixture f : fixtures) if (kind.isInstance(f)) n++;
        return n;
    }

    /** "3/4" style text, or "-" when the room has none of that kind. */
    public String countText(Class<? extends Fixture> kind) {
        int total = countTotal(kind);
        return total == 0 ? "-" : countWorking(kind) + "/" + total;
    }

    /** Working if at least one fixture works; Under Maintenance when none do. */
    public String getOverallStatus() {
        for (Fixture f : fixtures) if (f.isWorking()) return Fixture.WORKING;
        return Fixture.UNDER_MAINTENANCE;
    }

    public LocalDateTime getLastUpdated() {
        LocalDateTime latest = fixtures.get(0).getLastUpdated();
        for (Fixture f : fixtures) if (f.getLastUpdated().isAfter(latest)) latest = f.getLastUpdated();
        return latest;
    }

    public Fixture findFixture(String fixtureName) {
        for (Fixture f : fixtures) if (f.getName().equalsIgnoreCase(fixtureName)) return f;
        return null;
    }

    /** One-line summary of working fixtures. */
    public String showDetail() {
        String urinals = countTotal(Urinal.class) > 0 ? "   |   Urinals working: " + countText(Urinal.class) : "";
        return "Stalls working: " + countText(Stall.class) + urinals;
    }

    @Override
    public String toString() { return name + "  -  Floor " + floor + ", " + building; }
}