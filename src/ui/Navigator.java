package ui;

import model.ComfortRoom;
import model.IssueReport;

/** What the screens are allowed to ask the main window to do. MainFrame implements it. */
public interface Navigator {

    String LOGIN = "login";
    String DASHBOARD = "dashboard";
    String FLOOR_DETAIL = "floor-detail";
    String REPORT_FORM = "report-form";
    String REPORT_DONE = "report-done";
    String QUEUE = "queue";
    String OVERVIEW = "overview";
    String LOGS = "logs";
    String HISTORY = "history";
    String SIGNUP = "signup";

    /** Shows a screen by name (see the constants above). */
    void showScreen(String name);

    /** Shows the first screen for the logged-in user's role. */
    void goHome();

    void logout();

    void showFloorDetail(int floor);

    /** Opens "Report an Issue" with the given room already selected (may be null). */
    void showReportForm(ComfortRoom room);

    void showReportSubmitted(IssueReport report);
}
