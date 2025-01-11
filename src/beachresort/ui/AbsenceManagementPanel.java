package beachresort.ui;

import javax.swing.*;
import java.awt.*;

public class AbsenceManagementPanel extends JPanel {
    public AbsenceManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Absence Management"));

        // Form to request absence
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        JTextField absenceReasonField = new JTextField();
        JTextField absenceDateField = new JTextField();
        JButton requestAbsenceButton = new JButton("Request Absence");

        formPanel.add(new JLabel("Reason for Absence:"));
        formPanel.add(absenceReasonField);
        formPanel.add(new JLabel("Date of Absence:"));
        formPanel.add(absenceDateField);
        formPanel.add(requestAbsenceButton);

        add(formPanel, BorderLayout.CENTER);

        // Action for requesting absence
        requestAbsenceButton.addActionListener(e -> {
            String reason = absenceReasonField.getText().trim();
            String date = absenceDateField.getText().trim();
            if (reason.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            } else {
                JOptionPane.showMessageDialog(this, "Absence requested for " + date + " with reason: " + reason);
            }
        });
    }
}