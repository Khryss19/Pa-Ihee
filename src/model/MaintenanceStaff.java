package model;

import java.util.List;

/** Staff can view statuses, update them with a reason, and review the history. */
public class MaintenanceStaff extends User {
    public MaintenanceStaff(String name) { super(name, "Maintenance Staff"); }

    @Override public List<String> showMenu() { return List.of(MENU_ROOMS, MENU_HISTORY); }
    @Override public boolean canUpdateStatus() { return true; }
    @Override public boolean canViewHistory() { return true; }
}
