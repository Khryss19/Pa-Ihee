// Urinal inherits from Fixture
public class Urinal extends Fixture {

    public Urinal(int fixtureID, int fixtureNumber) {
        super(fixtureID, fixtureNumber);
    }

    // Method overriding (Polymorphism): Urinal has its own way of showing details
    @Override
    public void displayDetails() {
        System.out.println("   [Urinal #" + getFixtureNumber() + "] (ID: " + getFixtureID() + ")");
        System.out.println("      Status       : " + getStatus());
        System.out.println("      Last note    : " + getLastMaintenanceNote());
        System.out.println("      Last updated : " + getLastUpdated());
    }
}
