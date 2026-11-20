import java.util.ArrayList;
import java.util.Scanner;

// Main starts the program, creates sample data, and shows the menus.
public class Main {

    // ArrayLists that store all the data of the system
    private static ArrayList<User> users = new ArrayList<User>();
    private static ArrayList<ComfortRoom> rooms = new ArrayList<ComfortRoom>();
    private static ArrayList<MaintenanceRecord> records = new ArrayList<MaintenanceRecord>();
    private static ArrayList<ProblemReport> reports = new ArrayList<ProblemReport>();

    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        createSampleData();

        boolean running = true;
        while (running) {
            System.out.println("\n===== PA-IHEE: CR Availability and Maintenance System =====");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    loginMenu();
                    break;
                case 2:
                    running = false;
                    System.out.println("Thank you for using Pa-Ihee. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ---------------- LOGIN ----------------

    private static void loginMenu() {
        String username = readText("Username: ");
        String password = readText("Password: ");

        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.login(username, password)) {
                found = true;
                System.out.println("\nLogin successful! Welcome, " + user.getName() + " (" + user.getRole() + ")");
                showUserMenu(user);
                break;
            }
        }
        if (!found) {
            System.out.println("Wrong username or password.");
        }
    }

    // Chooses the menu using the user's role (simple if-else)
    private static void showUserMenu(User user) {
        String role = user.getRole();

        if (role.equals("STUDENT") || role.equals("EMPLOYEE")) {
            studentMenu((Student) user); // cast the User back to a Student
        } else if (role.equals("MAINTENANCE")) {
            staffMenu((MaintenanceStaff) user);
        } else if (role.equals("ADMIN")) {
            adminMenu((Administrator) user);
        }
    }

    // ---------------- STUDENT / EMPLOYEE MENU ----------------

    private static void studentMenu(Student student) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n----- STUDENT / EMPLOYEE MENU -----");
            System.out.println("1. View all comfort rooms");
            System.out.println("2. View details of a comfort room");
            System.out.println("3. Search by CR name");
            System.out.println("4. Search by floor");
            System.out.println("5. Search by building");
            System.out.println("6. Show working comfort rooms");
            System.out.println("7. Filter by type (Male/Female)");
            System.out.println("8. Report a problem");
            System.out.println("9. Logout");
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    student.viewComfortRooms(rooms);
                    break;
                case 2:
                    student.viewComfortRoomDetails(rooms, readInt("Enter CR ID: "));
                    break;
                case 3:
                    student.viewComfortRooms(student.searchByName(rooms, readText("Enter CR name: ")));
                    break;
                case 4:
                    student.viewComfortRooms(student.searchByFloor(rooms, readInt("Enter floor: ")));
                    break;
                case 5:
                    student.viewComfortRooms(student.searchByBuilding(rooms, readText("Enter building: ")));
                    break;
                case 6:
                    student.viewComfortRooms(student.filterWorking(rooms));
                    break;
                case 7:
                    student.viewComfortRooms(student.filterByType(rooms, readText("Enter type: ")));
                    break;
                case 8:
                    int crID = readInt("Enter CR ID: ");
                    int fixtureID = readInt("Enter Fixture ID (0 if it is a general problem): ");
                    String description = readText("Describe the problem: ");
                    student.reportProblem(reports, rooms, crID, fixtureID, description);
                    break;
                case 9:
                    student.logout();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ---------------- MAINTENANCE STAFF MENU ----------------

    private static void staffMenu(MaintenanceStaff staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n----- MAINTENANCE STAFF MENU -----");
            System.out.println("1. View all comfort rooms");
            System.out.println("2. View details of a comfort room");
            System.out.println("3. Update a stall/urinal status");
            System.out.println("4. View maintenance history");
            System.out.println("5. Logout");
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    staff.viewComfortRooms(rooms);
                    break;
                case 2:
                    staff.viewComfortRoomDetails(rooms, readInt("Enter CR ID: "));
                    break;
                case 3:
                    int crID = readInt("Enter CR ID: ");
                    staff.viewComfortRoomDetails(rooms, crID);
                    int fixtureID = readInt("Enter Fixture ID to update: ");
                    System.out.println("1. WORKING");
                    System.out.println("2. UNDER_MAINTENANCE");
                    int statusChoice = readInt("Choose new status: ");
                    String newStatus = "";
                    if (statusChoice == 1) {
                        newStatus = Fixture.WORKING;
                    } else if (statusChoice == 2) {
                        newStatus = Fixture.UNDER_MAINTENANCE;
                    }
                    String reason = readText("Enter reason: ");
                    staff.updateFixtureStatus(rooms, records, crID, fixtureID, newStatus, reason);
                    break;
                case 4:
                    staff.viewMaintenanceHistory(records);
                    break;
                case 5:
                    staff.logout();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ---------------- ADMINISTRATOR MENU ----------------

    private static void adminMenu(Administrator admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n----- ADMINISTRATOR MENU -----");
            System.out.println("1. View all comfort rooms");
            System.out.println("2. View details of a comfort room");
            System.out.println("3. View maintenance records");
            System.out.println("4. Review problem reports");
            System.out.println("5. Update a report status");
            System.out.println("6. Logout");
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    admin.viewComfortRooms(rooms);
                    break;
                case 2:
                    admin.viewComfortRoomDetails(rooms, readInt("Enter CR ID: "));
                    break;
                case 3:
                    admin.viewMaintenanceRecords(records);
                    break;
                case 4:
                    admin.reviewProblemReports(reports);
                    break;
                case 5:
                    int reportID = readInt("Enter Report ID: ");
                    String newStatus = readText("New status (PENDING / REVIEWED / RESOLVED): ").toUpperCase();
                    admin.updateReportStatus(reports, reportID, newStatus);
                    break;
                case 6:
                    admin.logout();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ---------------- INPUT HELPERS ----------------

    // Reads a number. try-catch handles the case where the user types letters.
    private static int readInt(String message) {
        System.out.print(message);
        try {
            return Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return -1;
        }
    }

    private static String readText(String message) {
        System.out.print(message);
        return input.nextLine().trim();
    }

    // ---------------- SAMPLE DATA ----------------

    private static void createSampleData() {
        // Users (password is plain text only because this is a school project)
        users.add(new Student(1, "Juan Dela Cruz", "student1", "1234", "STUDENT"));
        users.add(new Student(2, "Maria Santos", "employee1", "1234", "EMPLOYEE"));
        users.add(new MaintenanceStaff(3, "Pedro Reyes", "staff1", "1234"));
        users.add(new Administrator(4, "Ana Lopez", "admin1", "1234"));

        // Six comfort rooms
        ComfortRoom cr1 = new ComfortRoom(1, "Main Building CR 1", "Main Building", 1, "Male");
        ComfortRoom cr2 = new ComfortRoom(2, "Main Building CR 2", "Main Building", 1, "Female");
        ComfortRoom cr3 = new ComfortRoom(3, "Main Building CR 3", "Main Building", 2, "Male");
        ComfortRoom cr4 = new ComfortRoom(4, "Main Building CR 4", "Main Building", 2, "Female");
        ComfortRoom cr5 = new ComfortRoom(5, "Library CR 1", "Library Building", 1, "Male");
        ComfortRoom cr6 = new ComfortRoom(6, "Library CR 2", "Library Building", 1, "Female");

        // Add stalls and urinals (female CRs have stalls only)
        addFixtures(cr1, 3, 2);
        addFixtures(cr2, 4, 0);
        addFixtures(cr3, 3, 2);
        addFixtures(cr4, 4, 0);
        addFixtures(cr5, 2, 2);
        addFixtures(cr6, 3, 0);

        rooms.add(cr1);
        rooms.add(cr2);
        rooms.add(cr3);
        rooms.add(cr4);
        rooms.add(cr5);
        rooms.add(cr6);

        // Make a few fixtures "under maintenance" so there is something to see
        cr2.findFixture(201).changeStatus(Fixture.UNDER_MAINTENANCE, "Clogged toilet");
        cr3.findFixture(302).changeStatus(Fixture.UNDER_MAINTENANCE, "Broken door lock");
        cr3.findFixture(304).changeStatus(Fixture.UNDER_MAINTENANCE, "Leaking flush");

        // Library CR 1: everything under maintenance
        for (int i = 0; i < cr5.getFixtures().size(); i++) {
            cr5.getFixtures().get(i).changeStatus(Fixture.UNDER_MAINTENANCE, "Water pipe repair");
        }
    }

    // Adds stalls then urinals. Fixture IDs look like 101, 102, ... (CR 1) and 201, 202, ... (CR 2)
    private static void addFixtures(ComfortRoom room, int stallCount, int urinalCount) {
        int idCounter = room.getCrID() * 100;
        for (int i = 1; i <= stallCount; i++) {
            idCounter++;
            room.addFixture(new Stall(idCounter, i));
        }
        for (int i = 1; i <= urinalCount; i++) {
            idCounter++;
            room.addFixture(new Urinal(idCounter, i));
        }
    }
}
