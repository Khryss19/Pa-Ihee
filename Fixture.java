import java.util.Date;

// Fixture is an ABSTRACT class (Abstraction).
// It is the parent of Stall and Urinal. We cannot create a Fixture object directly.
public abstract class Fixture {

    // The two allowed statuses (constants, so we do not mistype them)
    public static final String WORKING = "WORKING";
    public static final String UNDER_MAINTENANCE = "UNDER_MAINTENANCE";

    // Private attributes (Encapsulation)
    private int fixtureID;
    private int fixtureNumber;
    private String status;
    private String lastMaintenanceNote;
    private String lastUpdated;

    // Constructor: every new fixture starts as WORKING
    public Fixture(int fixtureID, int fixtureNumber) {
        this.fixtureID = fixtureID;
        this.fixtureNumber = fixtureNumber;
        this.status = WORKING;
        this.lastMaintenanceNote = "None";
        this.lastUpdated = "No updates yet";
    }

    // Getters
    public int getFixtureID() { return fixtureID; }
    public int getFixtureNumber() { return fixtureNumber; }
    public String getStatus() { return status; }
    public String getLastMaintenanceNote() { return lastMaintenanceNote; }
    public String getLastUpdated() { return lastUpdated; }

    // Setters
    public void setFixtureID(int fixtureID) { this.fixtureID = fixtureID; }
    public void setFixtureNumber(int fixtureNumber) { this.fixtureNumber = fixtureNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setLastMaintenanceNote(String note) { this.lastMaintenanceNote = note; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    // Changes the status, saves the note and the time.
    // Returns true if it worked, false if the new status is not valid.
    public boolean changeStatus(String newStatus, String note) {
        if (WORKING.equals(newStatus) || UNDER_MAINTENANCE.equals(newStatus)) {
            status = newStatus;
            lastMaintenanceNote = note;
            lastUpdated = new Date().toString();
            return true;
        }
        return false;
    }

    // Returns true if the fixture is working
    public boolean isWorking() {
        return status.equals(WORKING);
    }

    // Abstract method: no body here. Stall and Urinal MUST write their own version.
    public abstract void displayDetails();
}
