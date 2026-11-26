package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import model.Administrator;
import model.ComfortRoom;
import model.Faculty;
import model.Fixture;
import model.IssueReport;
import model.MaintenanceRecord;
import model.MaintenanceStaff;
import model.Student;
import model.User;

public class MainSystem {
    public static final String ROLE_STUDENT = "Student";
    public static final String ROLE_FACULTY = "Faculty";
    public static final String ROLE_STAFF = "Maintenance Staff";
    public static final String ROLE_ADMIN = "Administrator";
    /** The first choice in the login screen's role dropdown (covers Student and Faculty accounts). */
    public static final String ROLE_STUDENT_FACULTY = "Student/Faculty";

    /** NEW: where the maintenance history is saved (relative to the folder the app is run from). */
    public static final String HISTORY_FILE = "data/maintenance_history.csv";
    /** NEW: where the accounts are saved (passwords are stored hashed, never as plain text). */
    public static final String ACCOUNTS_FILE = "data/accounts.csv";

    private static final String GENERAL = "General";
    private static final String REPAIRED = "Repaired: ";

    private final List<ComfortRoom> rooms = new ArrayList<>();
    private final List<MaintenanceRecord> records = new ArrayList<>();
    private final List<Account> accounts = new ArrayList<>();
    private final IssueTracker tracker = new IssueTracker();
    private final HistoryStore store = new HistoryStore(HISTORY_FILE);   // NEW: the CSV "database"
    private final AccountStore accountStore = new AccountStore(ACCOUNTS_FILE);   // NEW: saved accounts
    private User currentUser;
    private int currentFloor = 1;

    public MainSystem() {
        loadRooms();
        loadAccounts();
        loadHistory();   // NEW: from the CSV file (or the sample data the very first time)
    }

    // ---------- login ----------

    public User login(String name, String role) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Please enter your name.");
        if (role == null) throw new IllegalArgumentException("Please select a role.");
        switch (role) {
            case ROLE_STUDENT -> currentUser = new Student(name.trim());
            case ROLE_FACULTY -> currentUser = new Faculty(name.trim());
            case ROLE_STAFF -> currentUser = new MaintenanceStaff(name.trim());
            case ROLE_ADMIN -> currentUser = new Administrator(name.trim());
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        }
        return currentUser;
    }

    /** The login screen: checks the SR Code, password and the role chosen in the dropdown. */
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

    /**
     * NEW: the "Create Account" screen. Checks the input, saves the new account to
     * data/accounts.csv, and signs the person in. Wrong input throws IllegalArgumentException.
     */
    public User register(String name, String srCode, String password, String confirm, String role, Integer floor) {
        String cleanName = (name == null) ? "" : name.trim().replaceAll("\\s+", " ");
        String code = (srCode == null) ? "" : srCode.trim();
        if (cleanName.isEmpty()) throw new IllegalArgumentException("Please enter your full name.");
        if (cleanName.length() > 40) throw new IllegalArgumentException("Name must be 40 characters or fewer.");
        if (code.isEmpty()) throw new IllegalArgumentException("Please enter your SR Code.");
        if (!code.matches("\\d{2}-\\d{5}")) throw new IllegalArgumentException("SR Code must look like 25-12345.");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters.");
        if (!password.equals(confirm)) throw new IllegalArgumentException("Passwords do not match.");
        if (role == null) throw new IllegalArgumentException("Please select a role.");
        if (!List.of(ROLE_STUDENT, ROLE_FACULTY, ROLE_STAFF, ROLE_ADMIN).contains(role)) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        if (floor == null) throw new IllegalArgumentException("Please select your usual floor.");
        for (Account a : accounts) {
            if (a.getSrCode().equals(code)) throw new IllegalArgumentException("An account with this SR Code already exists.");
        }

        Account account = Account.create(code, password, cleanName, role, floor);
        accounts.add(account);
        accountStore.append(account);

        String choice = (role.equals(ROLE_STUDENT) || role.equals(ROLE_FACULTY)) ? ROLE_STUDENT_FACULTY : role;
        return login(code, password, choice);
    }

    public void logout() { currentUser = null; }

    public User getCurrentUser() { return currentUser; }

    public int getCurrentFloor() { return currentFloor; }

    // ---------- rooms ----------

    public List<ComfortRoom> getRooms() { return Collections.unmodifiableList(rooms); }

    public List<Integer> getFloors() {
        TreeSet<Integer> floors = new TreeSet<>();
        for (ComfortRoom r : rooms) floors.add(r.getFloor());
        return new ArrayList<>(floors);
    }

    public IssueTracker getIssueTracker() { return tracker; }

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

    // ---------- changing things (every change is also saved to the CSV) ----------

    public MaintenanceRecord updateStatus(ComfortRoom room, Fixture fixture, String newStatus, String reason) {
        if (currentUser == null || !currentUser.canUpdateStatus()) {
            throw new IllegalArgumentException("Only maintenance staff can update statuses.");
        }
        if (room == null || fixture == null) throw new IllegalArgumentException("Please choose a comfort room and a fixture.");
        if (reason == null || reason.trim().isEmpty()) throw new IllegalArgumentException("Please enter a short reason.");
        if (fixture.getStatus().equals(newStatus)) throw new IllegalArgumentException(fixture.getName() + " is already " + newStatus + ".");

        LocalDateTime now = LocalDateTime.now();
        String cleanReason = reason.trim().replaceAll("\\s+", " ");
        fixture.changeStatus(newStatus, cleanReason, now);
        MaintenanceRecord rec = new MaintenanceRecord(room, fixture.getName(), newStatus, cleanReason, now, currentUser.getName());
        log(rec);
        tracker.syncAfterStatusChange(room, fixture, newStatus, cleanReason, currentUser.getName(), now);
        return rec;
    }

    /**
     * The "Report an Issue" screen. Pass fixture = null for a problem with the whole room.
     * A problem with a fixture also marks that fixture Under Maintenance.
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
        // One line only, so the note stays tidy in the cards and in the CSV.
        String cleanNotes = (notes == null) ? "" : notes.trim().replaceAll("\\s+", " ");
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
        log(new MaintenanceRecord(room, report.getFixtureName(), MaintenanceRecord.PROBLEM_REPORTED,
                cleanNotes.isEmpty() ? issueType : issueType + " - " + cleanNotes, now, reporter));
        return report;
    }

    /** The "Mark as Working" button (staff only). */
    public IssueReport resolveReport(String reportId) {
        if (currentUser == null || !currentUser.canUpdateStatus()) {
            throw new IllegalArgumentException("Only maintenance staff can update statuses.");
        }
        IssueReport report = tracker.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report " + reportId + " was not found.");
        if (report.isResolved()) throw new IllegalArgumentException(report.getId() + " is already resolved.");

        String reason = REPAIRED + report.getIssueType();
        if (report.getFixture() != null) {
            updateStatus(report.getRoom(), report.getFixture(), Fixture.WORKING, reason);   // also closes the report
        } else {
            LocalDateTime now = LocalDateTime.now();
            report.resolve(currentUser.getName(), now);   // first, so a failure leaves no history entry behind
            log(new MaintenanceRecord(report.getRoom(), GENERAL, MaintenanceRecord.PROBLEM_RESOLVED,
                    reason, now, currentUser.getName()));
        }
        return report;
    }

    // ---------- history ----------

    /** Newest first. Only staff and administrators may read it. Pass room = null for all rooms. */
    public List<MaintenanceRecord> getHistory(ComfortRoom room) {
        if (currentUser == null || !currentUser.canViewHistory()) {
            throw new IllegalArgumentException("Only maintenance staff and administrators can view the history.");
        }
        List<MaintenanceRecord> list = new ArrayList<>();
        for (MaintenanceRecord r : records) if (room == null || r.getRoom() == room) list.add(r);
        list.sort((a, b) -> b.getDateTime().compareTo(a.getDateTime()));
        return list;
    }

    /** Where the history is stored, for display on the history screen. */
    public String getHistoryFilePath() { return HISTORY_FILE; }

    /** Adds a record to memory and to the CSV file. */
    private void log(MaintenanceRecord rec) {
        records.add(rec);
        store.append(rec);
    }

    // ---------- startup: rooms, accounts, and history from the CSV ----------

    private void loadRooms() {
        LocalDateTime base = LocalDateTime.of(2026, 10, 1, 8, 0);
        rooms.add(new ComfortRoom("Women's CR", "Women", "Building A", 1, 3, 0, base));
        rooms.add(new ComfortRoom("All Gender CR", "All Gender", "Building A", 1, 2, 3, base));
        rooms.add(new ComfortRoom("Women's CR", "Women", "Building A", 2, 3, 0, base));
        rooms.add(new ComfortRoom("Men's CR", "Men", "Building A", 2, 2, 3, base));
        rooms.add(new ComfortRoom("Women's CR", "Women", "Building A", 3, 3, 0, base));
        rooms.add(new ComfortRoom("Men's CR", "Men", "Building A", 3, 2, 3, base));
    }

    /** Accounts come only from data/accounts.csv. No file yet means nobody has signed up yet. */
    private void loadAccounts() {
        if (accountStore.exists()) {
            accounts.addAll(accountStore.load());
        }
    }

    /**
     * If the CSV file exists, its records are the history. If not (first run), the sample history
     * is created and saved. Either way, the fixtures and the repair queue are then rebuilt by
     * replaying the history, so they always match it.
     */
    private void loadHistory() {
        if (store.exists()) {
            records.addAll(store.load(rooms));
        } else {
            records.addAll(sampleHistory());
            store.saveAll(records);
        }
        replayHistory();
    }

    /** Walks through the history from oldest to newest and applies each entry. */
    private void replayHistory() {
        List<MaintenanceRecord> ordered = new ArrayList<>(records);
        ordered.sort(Comparator.comparing(MaintenanceRecord::getDateTime));   // stable: ties keep file order
        for (MaintenanceRecord rec : ordered) {
            try {
                replay(rec);
            } catch (IllegalArgumentException e) {
                System.err.println("Skipped a history entry while loading: " + e.getMessage());
            }
        }
    }

    private void replay(MaintenanceRecord rec) {
        ComfortRoom room = rec.getRoom();
        String status = rec.getStatus();
        String by = plainName(rec.getUpdatedBy());
        LocalDateTime when = rec.getDateTime();

        if (rec.getFixtureName().equals(GENERAL)) {
            if (status.equals(MaintenanceRecord.PROBLEM_REPORTED)) {
                String[] parts = splitReason(rec.getReason());
                tracker.open(room, null, parts[0], parts[1], by, when);
            } else if (status.equals(MaintenanceRecord.PROBLEM_RESOLVED)) {
                String type = rec.getReason().startsWith(REPAIRED)
                        ? rec.getReason().substring(REPAIRED.length()) : rec.getReason();
                IssueReport open = tracker.findOpenGeneral(room, type);
                if (open != null) open.resolve(by, when);
            }
            return;
        }

        Fixture fixture = room.findFixture(rec.getFixtureName());
        if (fixture == null) return;

        if (status.equals(MaintenanceRecord.PROBLEM_REPORTED)) {
            String[] parts = splitReason(rec.getReason());
            fixture.changeStatus(Fixture.UNDER_MAINTENANCE, parts[0], when);
            if (tracker.findOpen(room, fixture) == null) {
                tracker.open(room, fixture, parts[0], parts[1], by, when);
            }
        } else {
            fixture.changeStatus(status, rec.getReason(), when);   // rejects an unknown status
            tracker.syncAfterStatusChange(room, fixture, status, rec.getReason(), by, when);
        }
    }

    /** "Broken Lock - door is stuck" -> {"Broken Lock", "door is stuck"}; no " - " -> {text, ""}. */
    private static String[] splitReason(String reason) {
        int cut = reason.indexOf(" - ");
        if (cut < 0) return new String[]{reason, ""};
        return new String[]{reason.substring(0, cut), reason.substring(cut + 3)};
    }

    /** "Juan Dela Cruz (Student)" -> "Juan Dela Cruz" */
    private static String plainName(String updatedBy) {
        int cut = updatedBy.indexOf(" (");
        return cut > 0 ? updatedBy.substring(0, cut) : updatedBy;
    }

    // ---------- sample history (used only when the CSV file does not exist yet) ----------

    private ComfortRoom findRoom(int floor, String name) {
        for (ComfortRoom r : rooms) {
            if (r.getFloor() == floor && r.getName().equals(name)) return r;
        }
        throw new IllegalStateException("Sample room missing: " + floor + " " + name);
    }

    private static MaintenanceRecord sample(ComfortRoom room, String fixture, String status, String reason,
                                            int month, int day, int hour, int minute, String by) {
        return new MaintenanceRecord(room, fixture, status, reason,
                LocalDateTime.of(2026, month, day, hour, minute), by);
    }

    private List<MaintenanceRecord> sampleHistory() {
        ComfortRoom m1 = findRoom(1, "All Gender CR");
        ComfortRoom w2 = findRoom(2, "Women's CR");
        ComfortRoom m2 = findRoom(2, "Men's CR");
        ComfortRoom w3 = findRoom(3, "Women's CR");
        ComfortRoom a3 = findRoom(3, "Men's CR");

        String um = Fixture.UNDER_MAINTENANCE;
        String ok = Fixture.WORKING;
        List<MaintenanceRecord> list = new ArrayList<>();

        // Repairs that are finished (a problem, then the fix: 22, 56 and 40 minutes later)
        list.add(sample(a3, "Stall 1", um, "Needs cleaning", 10, 5, 9, 58, "Staff B"));
        list.add(sample(a3, "Stall 1", ok, "Cleaned and checked", 10, 5, 10, 20, "Staff B"));
        list.add(sample(m2, "Urinal 3", um, "Flush not working", 10, 6, 12, 9, "Staff B"));
        list.add(sample(m2, "Urinal 3", ok, "Repair done", 10, 6, 13, 5, "Staff B"));
        list.add(sample(m1, "Urinal 2", um, "Flush not working", 10, 8, 8, 40, "Staff A"));
        list.add(sample(m1, "Stall 1", um, "Broken door lock", 10, 8, 9, 15, "Staff A"));

        // Problems that are still open
        for (int i = 1; i <= 3; i++) {
            list.add(sample(w2, "Stall " + i, um, "Pipe leak", 10, 7, 16, 30, "Staff B"));
        }
        list.add(sample(w3, "Stall 3", um, "No water", 10, 7, 7, 45, "Staff A"));
        list.add(sample(m1, "Urinal 2", um, "Flush not working", 10, 8, 8, 40, "Staff A"));
        list.add(sample(m1, "Stall 1", um, "Broken door lock", 10, 8, 9, 15, "Staff A"));

        list.sort(Comparator.comparing(MaintenanceRecord::getDateTime));
        return list;
    }
}