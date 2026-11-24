package service;

/** NEW: a login account (SR Code + password) that belongs to one kind of user. */
public class Account {
    private final String srCode;
    private final String password;
    private final String name;
    private final String role;     // one of the MainSystem.ROLE_* constants
    private final int floor;       // the floor this person is usually on ("nearest comfort room")

    public Account(String srCode, String password, String name, String role, int floor) {
        this.srCode = srCode;
        this.password = password;
        this.name = name;
        this.role = role;
        this.floor = floor;
    }

    public boolean matches(String srCode, String password) {
        return this.srCode.equals(srCode) && this.password.equals(password);
    }

    /** The login screen has one "Student/Employee" choice that covers both students and faculty. */
    public boolean matchesChoice(String chosenRole) {
        if (chosenRole.equals(MainSystem.ROLE_STUDENT_EMPLOYEE)) {
            return role.equals(MainSystem.ROLE_STUDENT) || role.equals(MainSystem.ROLE_FACULTY);
        }
        return role.equals(chosenRole);
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public int getFloor() { return floor; }
}
