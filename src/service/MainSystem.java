package paihee.service;

import paihee.model.Administrator;
import paihee.model.ComfortRoom;
import paihee.model.Faculty;
import paihee.model.Fixture;
import paihee.model.IssueReport;
import paihee.model.MaintenanceRecord;
import paihee.model.MaintenanceStaff;
import paihee.model.Student;
import paihee.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class MainSystem {
    public static final String ROLE_STUDENT = "Student";
    public static final String ROLE_FACULTY = "Faculty";
    public static final String ROLE_STAFF = "Maintenance Staff";
    /** NEW */
    public static final String ROLE_ADMIN = "Administrator";
    /** NEW: the first choice in the login screen's role dropdown (covers Student and Faculty accounts). */
    public static final String ROLE_STUDENT_EMPLOYEE = "Student/Employee";

    private final List<ComfortRoom> rooms = new ArrayList<>();
    private final List<MaintenanceRecord> records = new ArrayList<>();
    private final List<Account> accounts = new ArrayList<>();   // NEW
    private final IssueTracker tracker = new IssueTracker();    // NEW
    private User currentUser;
    private int currentFloor = 1;                               // NEW

    public MainSystem() {
        loadSampleData();
        loadSampleReports();    // NEW
        loadSampleAccounts();   // NEW
    }

    public User login(String name, String role) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Please enter your name.");
        if (role == null) throw new IllegalArgumentException("Please select a role.");
        switch (role) {
            case ROLE_STUDENT -> currentUser = new Student(name.trim());
            case ROLE_FACULTY -> currentUser = new Faculty(name.trim());
            case ROLE_STAFF -> currentUser = new MaintenanceStaff(name.trim());
            case ROLE_ADMIN -> currentUser = new Administrator(name.trim());   // NEW
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        }
        return currentUser;
    }

    /**
     * NEW: the login screen. Checks the SR Code, password and the role chosen in the dropdown,
     * then signs in with the existing login(name, role). Wrong input throws IllegalArgumentException
     * with a message the screen shows.
     */
    public User login(String srCode, String password, String chosenRole) {
        String code = (srCode == null) ? "" : srCode.trim();
        if (code.isEmpty()) throw new IllegalArgumentException("Please enter your SR Code.");
        if (!code.matches("\\d{2}-\\d{5}")) throw new IllegalArgumentException("SR Code must look like 25-12345.");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Please enter your password.");
        if (chosenRole == null) throw new IllegalArgumentException("Please select a role.");

        Account account = null;
        for (Account a : accounts) {
            if (a.matches(code, password)) account = a;
        }
        if (account == null) throw new IllegalArgumentException("Incorrect SR Code or password.");
        if (!account.matchesChoice(chosenRole)) {
            throw new IllegalArgumentException("This account is not registered as " + chosenRole + ".");
        }
        User user = login(account.getName(), account.getRole());
        currentFloor = account.getFloor();
        return user;
    }

    public void logout() { currentUser = null; }

    public User getCurrentUser() { return currentUser; }

    /** NEW: the floor of the person who is logged in (used for "nearest comfort room"). */
    public int getCurrentFloor() { return currentFloor; }

    public List<ComfortRoom> getRooms() { return Collections.unmodifiableList(rooms); }

    /** NEW: the floors that have comfort rooms, lowest first. */
    public List<Integer> getFloors() {
        TreeSet<Integer> floors = new TreeSet<>();
        for (ComfortRoom r : rooms) floors.add(r.getFloor());
        return new ArrayList<>(floors);
    }

    /** NEW: read-only access to the repair queue numbers (pending, fixed today, average fix time...). */
    public IssueTracker getIssueTracker() { return tracker; }

    /** NEW: percentage of all fixtures that are working (the "92% Operational" figure). */
    public int getOperationalPercent() {
        int total = 0;
        int working = 0;
        for (ComfortRoom r : rooms) {
            for (Fixture f : r.getFixtures()) {
                total++;
                if (f.isWorking()) working++;
            }
        }
        return total == 0 ? 100 : (int) Math.round(100.0 * working / total);
    }

    public List<ComfortRoom> filterRooms(int floor, String status, String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        List<ComfortRoom> result = new ArrayList<>();
        for (ComfortRoom r : rooms) {
            if (floor != 0 && r.getFloor() != floor) continue;
            if (status != null && !r.getOverallStatus().equals(status)) continue;
            if (!key.isEmpty() && !r.getName().toLowerCase().contains(key)
            && !r.getBuilding().toLowerCase().contains(key)
            && !r.getType().toLowerCase().contains(key)) continue;
            result.add(r);
        }
        return result;
    }
    public ComfortRoom nearestWorking(int myFloor) {
        ComfortRoom best = null;
        for (ComfortRoom r : rooms) { 
            if (!r.getOverallStatus().equals(Fixture.WORKING)) continue;
            if (best == null || Math.abs(r.getFloor() - myFloor) < Math.abs(best.getFloor() - myFloor)) best = r;
        }
        return best;
    }
    public MaintenanceRecord updateStatus(ComfortRoom room, Fixture fixture, String newStatus, String reason) {
        if (currentUser == null || !currentUser.canUpdateStatus()) {
            throw new IllegalArgumentException("Only maintenance staff can update statuses.");
        }
        if (room == null || fixture == null) throw new IllegalArgumentException("Please choose a comfort room and a fixture.");
        if (reason == null || reason.trim().isEmpty()) throw new IllegalArgumentException("Please enter a short reason.");
        if (fixture.getStatus().equals(newStatus)) throw new IllegalArgumentException(fixture.getName() + " is already " + newStatus + ".");
 
        LocalDateTime now = LocalDateTime.now();
        fixture.changeStatus(newStatus, reason.trim(), now);
        MaintenanceRecord rec = new MaintenanceRecord(room, fixture.getName(), newStatus, reason.trim(), now, currentUser.getName());
        records.add(rec);
        tracker.syncAfterStatusChange(room, fixture, newStatus, reason.trim(), currentUser.getName(), now);   // NEW
        return rec;
    }
 
    public MaintenanceRecord reportProblem(ComfortRoom room, String description) {
        if (currentUser == null || !currentUser.canReportProblem()) {
            throw new IllegalArgumentException("Only students and employees can report problems.");
        }
        if (room == null) throw new IllegalArgumentException("Please choose a comfort room.");
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Please describe the problem (e.g., broken faucet, no water, bad smell).");
        }
        MaintenanceRecord rec = new MaintenanceRecord(room, "General", MaintenanceRecord.PROBLEM_REPORTED,
                description.trim(), LocalDateTime.now(), currentUser.getName() + " (" + currentUser.getRole() + ")");
        records.add(rec);
        return rec;
    }

    /**
     * NEW: the "Report an Issue" screen. Pass fixture = null for a problem with the whole room.
     * A problem with a fixture also marks that fixture Under Maintenance, so it shows up in the
     * queue and the room counts update. The report is written to the history like any other record.
     */
    public IssueReport reportIssue(ComfortRoom room, Fixture fixture, String issueType, String notes) {
        if (currentUser == null || !currentUser.canReportProblem()) {
            throw new IllegalArgumentException("Only students and employees can report problems.");
        }
        if (room == null) throw new IllegalArgumentException("Please choose a comfort room.");
        if (fixture != null && !room.getFixtures().contains(fixture)) {
            throw new IllegalArgumentException("Please choose a fixture from this comfort room.");
        }
        List<String> allowed = (fixture == null) ? IssueReport.GENERAL_ISSUES : fixture.getIssueTypes();
        if (issueType == null || !allowed.contains(issueType)) {
            throw new IllegalArgumentException("Please choose an issue type.");
        }
        String cleanNotes = (notes == null) ? "" : notes.trim();
        if (cleanNotes.length() > 200) throw new IllegalArgumentException("Notes must be 200 characters or fewer.");

        IssueReport existing = (fixture == null)
                ? tracker.findOpenGeneral(room, issueType)
                : tracker.findOpen(room, fixture);
        if (existing != null) {
            String what = (fixture == null) ? "This problem" : fixture.getName();
            throw new IllegalArgumentException(what + " already has an open report (#" + existing.getId() + ").");
        }

        LocalDateTime now = LocalDateTime.now();
        String reporter = currentUser.getName() + " (" + currentUser.getRole() + ")";
        if (fixture != null) fixture.changeStatus(Fixture.UNDER_MAINTENANCE, issueType, now);
        IssueReport report = tracker.open(room, fixture, issueType, cleanNotes, currentUser.getName(), now);
        records.add(new MaintenanceRecord(room, report.getFixtureName(), MaintenanceRecord.PROBLEM_REPORTED,
                cleanNotes.isEmpty() ? issueType : issueType + " - " + cleanNotes, now, reporter));
        return report;
    }

    /**
     * NEW: the "Mark as Working" button (staff only). A fixture problem goes through the existing
     * updateStatus(), so the fixture, the history and the report all stay in step.
     */
    public IssueReport resolveReport(String reportId) {
        if (currentUser == null || !currentUser.canUpdateStatus()) {
            throw new IllegalArgumentException("Only maintenance staff can update statuses.");
        }
        IssueReport report = tracker.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report " + reportId + " was not found.");
        if (report.isResolved()) throw new IllegalArgumentException(report.getId() + " is already resolved.");

        String reason = "Repaired: " + report.getIssueType();
        if (report.getFixture() != null) {
            updateStatus(report.getRoom(), report.getFixture(), Fixture.WORKING, reason);   // also closes the report
        } else {
            LocalDateTime now = LocalDateTime.now();
            records.add(new MaintenanceRecord(report.getRoom(), "General", MaintenanceRecord.PROBLEM_RESOLVED,
                    reason, now, currentUser.getName()));
            report.resolve(currentUser.getName(), now);
        }
        return report;
    }
 
    public List<MaintenanceRecord> getHistory(ComfortRoom room) {
        List<MaintenanceRecord> list = new ArrayList<>();
        for (MaintenanceRecord r : records) if (room == null || r.getRoom() == room) list.add(r);
        list.sort((a, b) -> b.getDateTime().compareTo(a.getDateTime()));
        return list;
    }
 
    private void loadSampleData() {
        LocalDateTime base = LocalDateTime.of(2026, 10, 6, 8, 0);
        ComfortRoom w1 = new ComfortRoom("Women's CR", "Women", "Building A", 1, 5, 0, base);
        ComfortRoom m1 = new ComfortRoom("Men's CR", "Men", "Building A", 1, 4, 3, base);
        ComfortRoom w2 = new ComfortRoom("Women's CR", "Women", "Building A", 2, 4, 0, base);
        ComfortRoom m2 = new ComfortRoom("Men's CR", "Men", "Building A", 2, 4, 3, base);
        ComfortRoom w3 = new ComfortRoom("Women's CR", "Women", "Building A", 3, 5, 0, base);
        ComfortRoom a3 = new ComfortRoom("All Gender CR", "All Gender", "Building A", 3, 2, 0, base);
        rooms.add(w1); rooms.add(m1); rooms.add(w2); rooms.add(m2); rooms.add(w3); rooms.add(a3);
 
        seed(a3, "Stall 1", Fixture.WORKING, "Cleaned and checked", LocalDateTime.of(2026, 10, 5, 10, 20), "Staff B");
        seed(m2, "Urinal 3", Fixture.WORKING, "Repair done", LocalDateTime.of(2026, 10, 6, 13, 5), "Staff B");
        seed(m1, "Stall 4", Fixture.WORKING, "Faucet fixed", LocalDateTime.of(2026, 10, 7, 14, 10), "Staff A");
        for (int i = 1; i <= 4; i++) {
            seed(w2, "Stall " + i, Fixture.UNDER_MAINTENANCE, "Pipe leak", LocalDateTime.of(2026, 10, 7, 16, 30), "Staff B");
        }
        seed(w3, "Stall 5", Fixture.UNDER_MAINTENANCE, "No water", LocalDateTime.of(2026, 10, 7, 7, 45), "Staff A");
        seed(m1, "Urinal 2", Fixture.UNDER_MAINTENANCE, "Flush not working", LocalDateTime.of(2026, 10, 8, 8, 40), "Staff A");
        seed(m1, "Stall 2", Fixture.UNDER_MAINTENANCE, "Broken door lock", LocalDateTime.of(2026, 10, 8, 9, 15), "Staff A");
    }
 
    private void seed(ComfortRoom room, String fixtureName, String status, String reason, LocalDateTime when, String by) {
        room.findFixture(fixtureName).changeStatus(status, reason, when);
        records.add(new MaintenanceRecord(room, fixtureName, status, reason, when, by));
    }

    // ---------- NEW: sample reports and accounts ----------

    /** One sample report, built from the sample history above. */
    private static class SampleIssue {
        final ComfortRoom room;
        final Fixture fixture;
        final String issueType;
        final String by;
        final LocalDateTime created;
        final LocalDateTime resolved;   // null = still open

        SampleIssue(ComfortRoom room, Fixture fixture, String issueType, String by,
                    LocalDateTime created, LocalDateTime resolved) {
            this.room = room;
            this.fixture = fixture;
            this.issueType = issueType;
            this.by = by;
            this.created = created;
            this.resolved = resolved;
        }
    }

    /**
     * Turns the sample data into reports so the queue and the logs are not empty:
     * every fixture that is Under Maintenance gets an open report, and every repair in the sample
     * history becomes a fixed report (the repair times are made up: 22, 56 and 40 minutes).
     */
    private void loadSampleReports() {
        List<SampleIssue> issues = new ArrayList<>();

        int[] repairMinutes = {22, 56, 40};
        int next = 0;
        for (MaintenanceRecord rec : records) {
            if (rec.getStatus().equals(Fixture.WORKING)) {
                int minutes = repairMinutes[next++ % repairMinutes.length];
                issues.add(new SampleIssue(rec.getRoom(), rec.getRoom().findFixture(rec.getFixtureName()),
                        rec.getReason(), rec.getUpdatedBy(),
                        rec.getDateTime().minusMinutes(minutes), rec.getDateTime()));
            }
        }
        for (ComfortRoom room : rooms) {
            for (Fixture fixture : room.getFixtures()) {
                if (!fixture.isWorking()) {
                    issues.add(new SampleIssue(room, fixture, fixture.getMaintenanceNote(),
                            lastUpdatedBy(room, fixture), fixture.getLastUpdated(), null));
                }
            }
        }

        issues.sort(Comparator.comparing((SampleIssue s) -> s.created));   // oldest first, so numbers go up with time
        for (SampleIssue s : issues) {
            IssueReport report = tracker.open(s.room, s.fixture, s.issueType, "", s.by, s.created);
            if (s.resolved != null) report.resolve(s.by, s.resolved);
        }
    }

    /** Who made the latest history entry for this fixture. */
    private String lastUpdatedBy(ComfortRoom room, Fixture fixture) {
        String by = "Staff";
        for (MaintenanceRecord rec : records) {
            if (rec.getRoom() == room && rec.getFixtureName().equals(fixture.getName())) by = rec.getUpdatedBy();
        }
        return by;
    }

    private void loadSampleAccounts() {
        accounts.add(new Account("25-12345", "student123", "Juan Dela Cruz", ROLE_STUDENT, 2));
        accounts.add(new Account("25-54321", "faculty123", "Maria Santos", ROLE_FACULTY, 3));
        accounts.add(new Account("25-00001", "staff123", "Staff A", ROLE_STAFF, 1));
        accounts.add(new Account("25-00002", "admin123", "Admin", ROLE_ADMIN, 1));
    }
}
