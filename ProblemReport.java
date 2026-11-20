import java.util.Date;

// A problem reported by a student or employee.
public class ProblemReport {

    private int reportID;
    private String reporterName;
    private int crID;
    private int fixtureID; // 0 means a general problem (example: bad smell, no water)
    private String description;
    private String dateReported;
    private String reportStatus; // PENDING, REVIEWED, or RESOLVED

    public ProblemReport(int reportID, String reporterName, int crID, int fixtureID, String description) {
        this.reportID = reportID;
        this.reporterName = reporterName;
        this.crID = crID;
        this.fixtureID = fixtureID;
        this.description = description;
        this.dateReported = new Date().toString();
        this.reportStatus = "PENDING"; // every new report starts as PENDING
    }

    // Getters
    public int getReportID() { return reportID; }
    public String getReporterName() { return reporterName; }
    public int getCrID() { return crID; }
    public int getFixtureID() { return fixtureID; }
    public String getDescription() { return description; }
    public String getDateReported() { return dateReported; }
    public String getReportStatus() { return reportStatus; }

    // Setters
    public void setReportID(int reportID) { this.reportID = reportID; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public void setCrID(int crID) { this.crID = crID; }
    public void setFixtureID(int fixtureID) { this.fixtureID = fixtureID; }
    public void setDescription(String description) { this.description = description; }
    public void setDateReported(String dateReported) { this.dateReported = dateReported; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }

    // Changes the report status. Returns false if the new status is not allowed.
    public boolean updateReportStatus(String newStatus) {
        if (newStatus.equals("PENDING") || newStatus.equals("REVIEWED") || newStatus.equals("RESOLVED")) {
            reportStatus = newStatus;
            return true;
        }
        return false;
    }

    public void displayReport() {
        System.out.println("Report #" + reportID + " [" + reportStatus + "]");
        System.out.println("   Reported by   : " + reporterName);
        System.out.println("   CR ID         : " + crID);
        if (fixtureID == 0) {
            System.out.println("   Fixture ID    : (general problem)");
        } else {
            System.out.println("   Fixture ID    : " + fixtureID);
        }
        System.out.println("   Problem       : " + description);
        System.out.println("   Date reported : " + dateReported);
    }
}
