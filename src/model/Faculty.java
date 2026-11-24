package paihee.model;

import java.util.List;

/** Faculty and employees can view statuses and report problems. */
public class Faculty extends User {
    public Faculty(String name) { super(name, "Faculty/Employee"); }

    @Override public List<String> showMenu() { return List.of(MENU_ROOMS); }
    @Override public boolean canReportProblem() { return true; }
}
