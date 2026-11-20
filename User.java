import java.util.ArrayList;

// User is the PARENT class of Student, MaintenanceStaff, and Administrator.
public class User {

    private int userID;
    private String name;
    private String username;
    private String password;
    private String role; // STUDENT, EMPLOYEE, MAINTENANCE, or ADMIN
    private boolean loggedIn;

    public User(int userID, String name, String username, String password, String role) {
        this.userID = userID;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.loggedIn = false;
    }

    // Getters
    public int getUserID() { return userID; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return loggedIn; }

    // Setters
    public void setUserID(int userID) { this.userID = userID; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }

    // Checks the username and password. Returns true if they are correct.
    public boolean login(String enteredUsername, String enteredPassword) {
        if (username.equals(enteredUsername) && password.equals(enteredPassword)) {
            loggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedIn = false;
        System.out.println(name + " has been logged out.");
    }

    public void displayUserInfo() {
        System.out.println("User ID  : " + userID);
        System.out.println("Name     : " + name);
        System.out.println("Username : " + username);
        System.out.println("Role     : " + role);
    }

    // ----- Actions that EVERY role can do (children inherit these) -----

    // Shows a short list of comfort rooms (one line each)
    public void viewComfortRooms(ArrayList<ComfortRoom> rooms) {
        if (!loggedIn) {
            System.out.println("Access denied. Please log in first.");
            return;
        }
        if (rooms.isEmpty()) {
            System.out.println("No comfort rooms found.");
            return;
        }
        for (int i = 0; i < rooms.size(); i++) {
            ComfortRoom cr = rooms.get(i);
            System.out.println("[" + cr.getCrID() + "] " + cr.getName()
                    + " | " + cr.getBuilding() + ", Floor " + cr.getFloor()
                    + " | " + cr.getType()
                    + " | " + cr.getOverallStatus()
                    + " (" + cr.getWorkingCount() + "/" + cr.getTotalFixtures() + " working)");
        }
    }

    // Shows the full details of ONE comfort room (stalls and urinals included)
    public void viewComfortRoomDetails(ArrayList<ComfortRoom> rooms, int crID) {
        if (!loggedIn) {
            System.out.println("Access denied. Please log in first.");
            return;
        }
        ComfortRoom cr = findComfortRoom(rooms, crID);
        if (cr == null) {
            System.out.println("Comfort room not found.");
        } else {
            cr.displayComfortRoom();
        }
    }

    // Helper: finds a comfort room by its ID (protected = only this class and its children can use it)
    protected ComfortRoom findComfortRoom(ArrayList<ComfortRoom> rooms, int crID) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getCrID() == crID) {
                return rooms.get(i);
            }
        }
        return null;
    }
}
