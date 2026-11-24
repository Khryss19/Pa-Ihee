package ui;

import model.IssueReport;
import service.MainSystem;
import ui.components.CircleCheck;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Screen 5: confirmation with the new request number. */
public class ReportSubmittedView extends Screen {

    private final JPanel center = new JPanel(new GridBagLayout());
    private IssueReport report;

    public ReportSubmittedView(Navigator nav, MainSystem system) {
        super(nav, system);
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
    }

    public void setReport(IssueReport report) {
        this.report = report;
    }

    @Override
    public void refresh() {
        center.removeAll();
        if (report != null) {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.insets = new Insets(0, 16, 0, 16);
            center.add(buildCard(), c);
        }
        center.revalidate();
        center.repaint();
    }

    private RoundedPanel buildCard() {
        RoundedPanel card = new RoundedPanel(Theme.WHITE, null, 16, 22);
        card.addCentered(new CircleCheck(56));
        card.gap(14);

        card.addCentered(centeredLabel("Report Submitted!", Theme.TITLE, Theme.TEXT));
        card.gap(8);

        card.addCentered(centeredLabel("Thank you for helping keep our comfort", Theme.SMALL, Theme.MUTED));
        card.addCentered(centeredLabel("rooms clean and functional. Your report", Theme.SMALL, Theme.MUTED));
        card.addCentered(centeredLabel("has been sent to maintenance.", Theme.SMALL, Theme.MUTED));
        card.gap(16);

        RoundedPanel info = new RoundedPanel(Theme.INFO_BOX, null, 12, 12);
        info.addCentered(centeredLabel("#" + report.getId(), Theme.BODY_BOLD, Theme.GREEN_FG));
        info.gap(3);
        info.addCentered(centeredLabel(Labels.location(report), Theme.SMALL_BOLD, Theme.TEXT));
        info.gap(3);
        info.addCentered(centeredLabel("Issue: " + report.getIssueType(), Theme.SMALL, Theme.MUTED));
        card.addCentered(info);
        card.gap(18);

        RoundedButton back = new RoundedButton("Back to Dashboard");
        back.addActionListener(e -> nav.showScreen(Navigator.DASHBOARD));
        card.addCentered(back);
        return card;
    }

    private static JLabel centeredLabel(String text, Font font, Color color) {
        JLabel label = Ui.label(text, font, color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
}
