package paihee.model;

import java.util.List;

/** NEW: the school administrator reviews the building status and the maintenance history. */
public class Administrator extends User {
    public Administrator(String name) { super(name, "Administrator"); }

    @Override public List<String> showMenu() { return List.of(MENU_ROOMS, MENU_HISTORY); }
    @Override public boolean canViewHistory() { return true; }
}
