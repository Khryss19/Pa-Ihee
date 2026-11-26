import java.util.ArrayList;

public class ComfortRoom {

    private int crID;
    private String name;
    private String building;
    private int floor;
    private String type; // example: Male or Female
    private ArrayList<Fixture> fixtures; // holds Stall and Urinal objects

    public ComfortRoom(int crID, String name, String building, int floor, String type) {
        this.crID = crID;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.type = type;
        this.fixtures = new ArrayList<Fixture>();
    }

    // Getters
    public int getCrID() { return crID; }
    public String getName() { return name; }
    public String getBuilding() { return building; }
    public int getFloor() { return floor; }
    public String getType() { return type; }
    public ArrayList<Fixture> getFixtures() { return fixtures; }

    // Setters
    public void setCrID(int crID) { this.crID = crID; }
    public void setName(String name) { this.name = name; }
    public void setBuilding(String building) { this.building = building; }
    public void setFloor(int floor) { this.floor = floor; }
    public void setType(String type) { this.type = type; }

    // Adds a Stall or Urinal to this comfort room
    public void addFixture(Fixture fixture) {
        fixtures.add(fixture);
    }

    // Looks for a fixture using its ID. Returns null if not found.
    public Fixture findFixture(int fixtureID) {
        for (int i = 0; i < fixtures.size(); i++) {
            if (fixtures.get(i).getFixtureID() == fixtureID) {
                return fixtures.get(i);
            }
        }
        return null;
    }

    // Total number of fixtures
    public int getTotalFixtures() {
        return fixtures.size();
    }

    // Counts the fixtures that are WORKING
    public int getWorkingCount() {
        int count = 0;
        for (int i = 0; i < fixtures.size(); i++) {
            if (fixtures.get(i).isWorking()) {
                count++;
            }
        }
        return count;
    }

    // Counts the fixtures that are UNDER_MAINTENANCE
    public int getMaintenanceCount() {
        return getTotalFixtures() - getWorkingCount();
    }

    // Decides the overall status of the CR based on its fixtures
    public String getOverallStatus() {
        int total = getTotalFixtures();
        int working = getWorkingCount();

        if (total == 0) {
            return "NO FIXTURES";
        } else if (working == total) {
            return "FULLY AVAILABLE";
        } else if (working == 0) {
            return "NOT AVAILABLE";
        } else {
            return "PARTIALLY AVAILABLE";
        }
    }

    // Shows all the details of this CR, including every fixture
    public void displayComfortRoom() {
        System.out.println("--------------------------------------------");
        System.out.println("CR ID    : " + crID);
        System.out.println("Name     : " + name);
        System.out.println("Location : " + building + ", Floor " + floor);
        System.out.println("Type     : " + type);
        System.out.println("Overall  : " + getOverallStatus());
        System.out.println("Fixtures : " + getTotalFixtures() + " total, "
                + getWorkingCount() + " working, "
                + getMaintenanceCount() + " under maintenance");
        for (int i = 0; i < fixtures.size(); i++) {
            // Polymorphism: Java calls the Stall or Urinal version automatically
            fixtures.get(i).displayDetails();
        }
        System.out.println("--------------------------------------------");
    }
}
