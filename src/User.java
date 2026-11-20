import java.util.List;

/** The basic type of user. Each kind of user shows a different menu (polymorphism). */
public abstract class User {
    public static final String MENU_ROOMS = "Comfort Rooms";
    public static final String MENU_HISTORY = "Maintenance History";

    private final String name;
    private final String role;

    protected User(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() { return name; }
    public String getRole() { return role; }

    /** The menu items (features) this user can open. The UI decides how to display them. */
    public abstract List<String> showMenu();

    public boolean canUpdateStatus() { return false; }
    public boolean canViewHistory() { return false; }
    public boolean canReportProblem() { return false; }
}