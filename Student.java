import java.util.ArrayList;

// Student inherits from User. The role can be "STUDENT" or "EMPLOYEE"
// (school employees use the same features as students).
public class Student extends User {

    public Student(int userID, String name, String username, String password, String role) {
        super(userID, name, username, password, role);
    }

    // Permission check: must be logged in AND be a student/employee
    private boolean hasAccess() {
        if (isLoggedIn() && (getRole().equals("STUDENT") || getRole().equals("EMPLOYEE"))) {
            return true;
        }
        System.out.println("Access denied. This action is for students and employees only.");
        return false;
    }

    // Search by CR name (partial match, ignores uppercase/lowercase)
    public ArrayList<ComfortRoom> searchByName(ArrayList<ComfortRoom> rooms, String keyword) {
        ArrayList<ComfortRoom> results = new ArrayList<ComfortRoom>();
        if (!hasAccess()) {
            return results;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(rooms.get(i));
            }
        }
        return results;
    }

    // Search by floor number
    public ArrayList<ComfortRoom> searchByFloor(ArrayList<ComfortRoom> rooms, int floor) {
        ArrayList<ComfortRoom> results = new ArrayList<ComfortRoom>();
        if (!hasAccess()) {
            return results;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getFloor() == floor) {
                results.add(rooms.get(i));
            }
        }
        return results;
    }

    // Search by building name
    public ArrayList<ComfortRoom> searchByBuilding(ArrayList<ComfortRoom> rooms, String building) {
        ArrayList<ComfortRoom> results = new ArrayList<ComfortRoom>();
        if (!hasAccess()) {
            return results;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getBuilding().toLowerCase().contains(building.toLowerCase())) {
                results.add(rooms.get(i));
            }
        }
        return results;
    }

    // Filter: only comfort rooms with at least one working fixture
    public ArrayList<ComfortRoom> filterWorking(ArrayList<ComfortRoom> rooms) {
        ArrayList<ComfortRoom> results = new ArrayList<ComfortRoom>();
        if (!hasAccess()) {
            return results;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getWorkingCount() > 0) {
                results.add(rooms.get(i));
            }
        }
        return results;
    }

    // Filter by type (Male / Female)
    public ArrayList<ComfortRoom> filterByType(ArrayList<ComfortRoom> rooms, String type) {
        ArrayList<ComfortRoom> results = new ArrayList<ComfortRoom>();
        if (!hasAccess()) {
            return results;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getType().equalsIgnoreCase(type)) {
                results.add(rooms.get(i));
            }
        }
        return results;
    }

    // Report a problem. Use fixtureID = 0 for a general problem (no water, bad smell, etc.)
    public boolean reportProblem(ArrayList<ProblemReport> reports, ArrayList<ComfortRoom> rooms,
                                 int crID, int fixtureID, String description) {
        if (!hasAccess()) {
            return false;
        }
        ComfortRoom room = findComfortRoom(rooms, crID);
        if (room == null) {
            System.out.println("Comfort room not found.");
            return false;
        }
        if (fixtureID != 0 && room.findFixture(fixtureID) == null) {
            System.out.println("That fixture does not exist in this comfort room.");
            return false;
        }
        if (description == null || description.trim().equals("")) {
            System.out.println("Please describe the problem.");
            return false;
        }
        ProblemReport report = new ProblemReport(reports.size() + 1, getName(), crID, fixtureID, description);
        reports.add(report);
        System.out.println("Problem report submitted. Report ID: " + report.getReportID());
        return true;
    }
}
