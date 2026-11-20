import java.util.Date;

// One record = one status change made by a maintenance staff member.
public class MaintenanceRecord {

    private int recordID;
    private int crID;
    private int fixtureID;
    private String previousStatus;
    private String newStatus;
    private String reason;
    private String staffName;
    private String dateTime;

    public MaintenanceRecord(int recordID, int crID, int fixtureID, String previousStatus,
                             String newStatus, String reason, String staffName) {
        this.recordID = recordID;
        this.crID = crID;
        this.fixtureID = fixtureID;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.staffName = staffName;
        this.dateTime = new Date().toString(); // current date and time
    }

    // Getters
    public int getRecordID() { return recordID; }
    public int getCrID() { return crID; }
    public int getFixtureID() { return fixtureID; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public String getReason() { return reason; }
    public String getStaffName() { return staffName; }
    public String getDateTime() { return dateTime; }

    // Setters
    public void setRecordID(int recordID) { this.recordID = recordID; }
    public void setCrID(int crID) { this.crID = crID; }
    public void setFixtureID(int fixtureID) { this.fixtureID = fixtureID; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public void setReason(String reason) { this.reason = reason; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public void displayRecord() {
        System.out.println("Record #" + recordID);
        System.out.println("   CR ID / Fixture ID : " + crID + " / " + fixtureID);
        System.out.println("   Status change      : " + previousStatus + " -> " + newStatus);
        System.out.println("   Reason             : " + reason);
        System.out.println("   Updated by         : " + staffName);
        System.out.println("   Date and time      : " + dateTime);
    }
}
