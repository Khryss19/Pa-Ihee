import java.util.ArrayList;


public class Administrator extends User {

    public Administrator(int userID, String name, String username, String password) {
        super(userID, name, username, password, "ADMIN");
    }

    // Permission check: must be logged in AND be an administrator
    private boolean hasAccess() {
        if (isLoggedIn() && getRole().equals("ADMIN")) {
            return true;
        }
        System.out.println("Access denied. This action is for administrators only.");
        return false;
    }

    public void viewMaintenanceRecords(ArrayList<MaintenanceRecord> records) {
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

    public void reviewProblemReports(ArrayList<ProblemReport> reports) {
        if (!hasAccess()) {
            return;
        }
        if (reports.isEmpty()) {
            System.out.println("No problem reports yet.");
            return;
        }
        for (int i = 0; i < reports.size(); i++) {
            reports.get(i).displayReport();
        }
    }

    // Marks a report as PENDING, REVIEWED, or RESOLVED
    public boolean updateReportStatus(ArrayList<ProblemReport> reports, int reportID, String newStatus) {
        if (!hasAccess()) {
            return false;
        }
        for (int i = 0; i < reports.size(); i++) {
            if (reports.get(i).getReportID() == reportID) {
                boolean changed = reports.get(i).updateReportStatus(newStatus);
                if (changed) {
                    System.out.println("Report #" + reportID + " is now " + newStatus + ".");
                } else {
                    System.out.println("Invalid status. Use PENDING, REVIEWED, or RESOLVED.");
                }
                return changed;
            }
        }
        System.out.println("Report not found.");
        return false;
    }
}
