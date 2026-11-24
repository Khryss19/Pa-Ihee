package paihee.model;

import java.util.List;

/** Students can view statuses and report problems. */
public class Student extends User {
    public Student(String name) { super(name, "Student"); }

    @Override public List<String> showMenu() { return List.of(MENU_ROOMS); }
    @Override public boolean canReportProblem() { return true; }
}
