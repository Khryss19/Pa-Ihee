// Stall inherits from Fixture
public class Stall extends Fixture {

    public Stall(int fixtureID, int fixtureNumber) {
        super(fixtureID, fixtureNumber); // call the parent constructor
    }

    // Method overriding (Polymorphism): Stall has its own way of showing details
    @Override
    public void displayDetails() {
        System.out.println("   [Stall #" + getFixtureNumber() + "] (ID: " + getFixtureID() + ")");
        System.out.println("      Status       : " + getStatus());
        System.out.println("      Last note    : " + getLastMaintenanceNote());
        System.out.println("      Last updated : " + getLastUpdated());
    }
}
