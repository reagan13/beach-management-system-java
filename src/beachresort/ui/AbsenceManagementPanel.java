package beachresort.ui;

import beachresort.models.Absence;
import beachresort.models.Person;
import beachresort.repositories.StaffRepository;
import beachresort.repositories.AbsenceRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AbsenceManagementPanel extends JPanel {
    private AbsenceRepository absenceRepository;
    private StaffRepository staffRepository;
    private JTable absenceTable;
    private DefaultTableModel tableModel;

    public AbsenceManagementPanel(Person person) {
        absenceRepository = new AbsenceRepository();
        staffRepository = new StaffRepository();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Absence Management"));

        // Form to request absence
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input Fields
        JComboBox<String> leaveTypeCombo = new JComboBox<>(new String[] { "Sick Leave", "Vacation", "Personal Leave" });
        JTextField startDateField = new JTextField(10); // 10 columns
        JTextField endDateField = new JTextField(10); // 10 columns
        JTextField absenceReasonField = new JTextField(20); // 20 columns
        JButton requestAbsenceButton = new JButton("Request Absence");

        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Leave Type:"), gbc);

        gbc.gridx = 1;
        formPanel.add(leaveTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        formPanel.add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        formPanel.add(endDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Reason for Absence:"), gbc);

        gbc.gridx = 1;
        formPanel.add(absenceReasonField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(requestAbsenceButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Create the table to display absences
        String[] columnNames = { "Absence ID", "Leave Type", "Start Date", "End Date", "Status", "Reason" };
        tableModel = new DefaultTableModel(columnNames, 0);
        absenceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(absenceTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load existing absences for the user
        loadAbsences(person.getId());

        // Action for requesting absence
        requestAbsenceButton.addActionListener(e -> {
            String leaveType = (String) leaveTypeCombo.getSelectedItem();
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();
            String reason = absenceReasonField.getText().trim();

            if (leaveType.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            try {
                // Parse dates using LocalDate
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(startDateStr, formatter);
                LocalDate endDate = LocalDate.parse(endDateStr, formatter);

                // Convert LocalDate to java.sql.Date
                Date sqlStartDate = Date.valueOf(startDate);
                Date sqlEndDate = Date.valueOf(endDate);

                // Get the staff ID int staffId = staffRepository.getStaffByUser Id(user.getId()).getStaffID();

                // Create Absence object
                Absence absence = new Absence(
                        person.getId(),
                        leaveType,
                        sqlStartDate,
                        sqlEndDate,
                        "Pending", // Default status
                        reason);

                // Add absence to the repository
                if (absenceRepository.addAbsence(absence)) {
                    JOptionPane.showMessageDialog(this, "Absence requested successfully.");
                    // Clear fields after successful submission
                    leaveTypeCombo.setSelectedIndex(0);
                    startDateField.setText("");
                    endDateField.setText("");
                    absenceReasonField.setText("");
                    // Reload the absence list
                    loadAbsences(person.getId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to request absence. Please try again.");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error requesting absence: " + ex.getMessage());
            }
        });
    }

    
    private void loadAbsences(int userId) {
        // Clear the existing table data
        tableModel.setRowCount(0);
        // Fetch absences for the user
        List<Absence> absences = absenceRepository.getAbsencesByUserId(userId);
        for (Absence absence : absences) {
            tableModel.addRow(new Object[]{
                absence.getAbsenceId(),
                absence.getLeaveType(),
                absence.getStartDate(),
                absence.getEndDate(),
                absence.getStatus(),
                absence.getReason()
            });
        }
    }
}