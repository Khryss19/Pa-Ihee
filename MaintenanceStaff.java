import java.util.ArrayList;

// MaintenanceStaff inherits from User.
public class MaintenanceStaff extends User {

    public MaintenanceStaff(int userID, String name, String username, String password) {
        super(userID, name, username, password, "MAINTENANCE");
    }

    // Permission check: must be logged in AND be maintenance staff
    private boolean hasAccess() {
        if (isLoggedIn() && getRole().equals("MAINTENANCE")) {
            return true;
        }
        System.out.println("Access denied. This action is for maintenance staff only.");
        return false;
    }

    // Changes the status of a stall/urinal, saves the reason, and creates a MaintenanceRecord.
    public boolean updateFixtureStatus(ArrayList<ComfortRoom> rooms, ArrayList<MaintenanceRecord> records,
                                       int crID, int fixtureID, String newStatus, String reason) {
        if (!hasAccess()) {
            return false;
        }

        ComfortRoom room = findComfortRoom(rooms, crID);
        if (room == null) {
            System.out.println("Comfort room not found.");
            return false;
        }

        Fixture fixture = room.findFixture(fixtureID);
        if (fixture == null) {
            System.out.println("Fixture not found in this comfort room.");
            return false;
        }

        if (reason == null || reason.trim().equals("")) {
            System.out.println("Please enter a reason for the change.");
            return false;
        }

        if (fixture.getStatus().equals(newStatus)) {
            System.out.println("The fixture already has that status.");
            return false;
        }

        String previousStatus = fixture.getStatus(); // save the OLD status first
        boolean changed = fixture.changeStatus(newStatus, reason);
        if (!changed) {
            System.out.println("Invalid status. Use WORKING or UNDER_MAINTENANCE.");
            return false;
        }

        // Save a new record (old records are never deleted)
        MaintenanceRecord record = new MaintenanceRecord(records.size() + 1, crID, fixtureID,
                previousStatus, newStatus, reason, getName());
        records.add(record);
        System.out.println("Status updated. Maintenance record #" + record.getRecordID() + " saved.");
        return true;
    }

    public void viewMaintenanceHistory(ArrayList<MaintenanceRecord> records) {
        if (!hasAccess()) {
            return;
        }
        if (records.isEmpty()) {
            System.out.println("No maintenance records yet.");
            return;
        }
        for (int i = 0; i < records.size(); i++) {
            records.get(i).displayRecord();
        }
    }
}
