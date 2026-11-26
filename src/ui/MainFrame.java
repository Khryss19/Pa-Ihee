package ui;

import model.ComfortRoom;
import model.IssueReport;
import model.User;
import service.MainSystem;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

/**
 * The phone-sized window. It owns all screens and switches between them with a CardLayout.
 * The screens never touch each other directly; they ask this class (through Navigator) to move.
 */
public class MainFrame extends JFrame implements Navigator {

    private final MainSystem system;
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);
    private final Map<String, Screen> screens = new HashMap<>();

    private final StudentDashboardView dashboardView;
    private final FloorDetailView floorDetailView;
    private final ReportIssueView reportIssueView;
    private final ReportSubmittedView reportSubmittedView;
    private final MaintenanceQueueView queueView;
    private final MaintenanceLogsView logsView;
    private final MaintenanceHistoryView historyView;

    public MainFrame(MainSystem system) {
        super("Pa-Ihee");
        this.system = system;

        dashboardView = new StudentDashboardView(this, system);
        floorDetailView = new FloorDetailView(this, system);
        reportIssueView = new ReportIssueView(this, system);
        reportSubmittedView = new ReportSubmittedView(this, system);
        queueView = new MaintenanceQueueView(this, system);
        logsView = new MaintenanceLogsView(this, system);
        historyView = new MaintenanceHistoryView(this, system);

        register(LOGIN, new LoginView(this, system));
        register(DASHBOARD, dashboardView);
        register(FLOOR_DETAIL, floorDetailView);
        register(REPORT_FORM, reportIssueView);
        register(REPORT_DONE, reportSubmittedView);
        register(QUEUE, queueView);
        register(OVERVIEW, new FacilityOverviewView(this, system));
        register(LOGS, logsView);
        register(HISTORY, historyView);
        register(SIGNUP, new SignUpView(this, system));


        container.setPreferredSize(new Dimension(Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT));
        setContentPane(container);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        showScreen(LOGIN);
    }

    private void register(String name, Screen screen) {
        screens.put(name, screen);
        container.add(screen, name);
    }

    @Override
    public void showScreen(String name) {
        screens.get(name).refresh();
        cards.show(container, name);
    }

    @Override
    public void goHome() {
        // The user's own permission methods decide the first screen (the menu changes per user type).
        User user = system.getCurrentUser();
        if (user.canUpdateStatus()) {
            showScreen(QUEUE);          // maintenance staff
        } else if (user.canViewHistory()) {
            showScreen(OVERVIEW);       // administrator
        } else {
            showScreen(DASHBOARD);      // student, faculty / employee
        }
    }

    @Override
    public void logout() {
        system.logout();
        dashboardView.resetFilter();
        queueView.resetFilter();
        logsView.resetFilter();
        historyView.resetFilter();
        showScreen(LOGIN);
    }

    @Override
    public void showFloorDetail(int floor) {
        floorDetailView.setFloor(floor);
        showScreen(FLOOR_DETAIL);
    }

    @Override
    public void showReportForm(ComfortRoom room) {
        reportIssueView.prepare(room);
        showScreen(REPORT_FORM);
    }

    @Override
    public void showReportSubmitted(IssueReport report) {
        reportSubmittedView.setReport(report);
        showScreen(REPORT_DONE);
    }
}
