package beachresort.ui;

import beachresort.models.Staff;
import beachresort.repositories.StaffRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.List;

public class StaffManagementPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private StaffRepository staffRepository;
    private JTabbedPane tabbedPane;

    public StaffManagementPanel() {
        // Initialize repository
        staffRepository = new StaffRepository();

        // Set layout
        setLayout(new BorderLayout());

        // Title
        JLabel staffLabel = new JLabel("Staff Management", SwingConstants.CENTER);
        staffLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(staffLabel, BorderLayout.NORTH);

        // Create Tabbed Pane
        tabbedPane = new JTabbedPane();

        // Staff List Tab
        JPanel staffListPanel = createStaffListPanel();
        tabbedPane.addTab("Staff List", staffListPanel);

        // Leave Requests Tab
        JPanel leaveRequestsPanel = createLeaveRequestsPanel();
        tabbedPane.addTab("Leave Requests", leaveRequestsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStaffListPanel() {
        JPanel staffListPanel = new JPanel(new BorderLayout());

        // Table Model Setup
        String[] columnNames = {"User ID", "Name", "Position", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(staffTable);
        staffListPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addStaffButton = new JButton("Add Staff");
        JButton editStaffButton = new JButton("Edit Staff");
        JButton deleteStaffButton = new JButton("Delete Staff");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addStaffButton);
        buttonPanel.add(editStaffButton);
        buttonPanel.add(deleteStaffButton);
        buttonPanel.add(refreshButton);

        staffListPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addStaffButton.addActionListener(this::addStaff);
        editStaffButton.addActionListener(this::editStaff);
        deleteStaffButton.addActionListener(this::deleteStaff);
        refreshButton.addActionListener(e -> loadStaff());

        // Initial load of staff
        loadStaff();

        return staffListPanel;
    }

    private JPanel createLeaveRequestsPanel() {
        JPanel leaveRequestsPanel = new JPanel(new BorderLayout());

        // Leave Requests Table
        String[] leaveColumnNames = {"Staff Name", "Leave Type", "Start Date", "End Date", "Status", "Reason"};
        DefaultTableModel leaveTableModel = new DefaultTableModel(leaveColumnNames, 0);
        JTable leaveTable = new JTable(leaveTableModel);

        JScrollPane scrollPane = new JScrollPane(leaveTable);
        leaveRequestsPanel.add(scrollPane, BorderLayout.CENTER);

        // Leave Request Buttons
        JPanel leaveButtonPanel = new JPanel();
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        JButton viewDetailsButton = new JButton("View Details");

        leaveButtonPanel.add(approveButton);
        leaveButtonPanel.add(rejectButton);
        leaveButtonPanel.add(viewDetailsButton);

        leaveRequestsPanel.add(leaveButtonPanel, BorderLayout.SOUTH);

        // TODO: Implement leave request loading and action methods
        loadLeaveRequests(leaveTableModel);

        return leaveRequestsPanel;
    }

    private void loadStaff() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch staff from repository
        List<Staff> staffList = staffRepository.getAllStaff();
        
        // Populate table
        for (Staff staff : staffList) {
            Object[] rowData = {
                staff.getUserId(),
                staff.getName(),
                staff.getPosition(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void loadLeaveRequests(DefaultTableModel leaveTableModel) {
        // TODO: Implement actual leave request loading from repository
        // This is a placeholder implementation
        leaveTableModel.setRowCount(0);
        // Example data
        Object[][] sampleData = {
            {"John Doe", "Vacation", "2023-07-01", "2023-07-10", "Pending", "Family Trip"},
            {"Jane Smith", "Sick Leave", "2023-07-05", "2023-07-07", "Approved", "Medical Reasons"}
        };

        for (Object[] data : sampleData) {
            leaveTableModel.addRow(data);
        }
    }

    private void addStaff(ActionEvent e) {
        JDialog addStaffDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New Staff", true);
        addStaffDialog.setSize(400, 400);
        addStaffDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Fields
        JTextField nameField = createLabeledTextField(panel, "Full Name:");
        JTextField phoneField = createLabeledTextField(panel, "Phone Number:");
        JTextField emailField = createLabeledTextField(panel, "Email:");
        JComboBox<String> positionCombo = createPositionComboBox(panel, "Position:");
        JTextField userIdField = createLabeledTextField(panel, "User ID:");
        JComboBox<String> statusCombo = createStatusComboBox(panel, "Status:");

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            // Validate inputs
            if (validateInputs(nameField, phoneField, emailField, userIdField)) {
                try {
                    // Create Staff object
                    Staff newStaff = new Staff(
                        nameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        (String) positionCombo.getSelectedItem(),
                        userIdField.getText(),
                        (String) statusCombo.getSelectedItem(),
                        new Timestamp(System.currentTimeMillis()),
                        null
                    );

                    // Attempt to add staff
                    if (staffRepository.addStaff(newStaff)) {
                        JOptionPane.showMessageDialog(addStaffDialog, "Staff Added Successfully!");
                        loadStaff(); // Refresh the staff list
                        addStaffDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(addStaffDialog, 
                            "Failed to add staff. Please check the user ID and email.",
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addStaffDialog, 
                 "Error adding staff: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> addStaffDialog.dispose());
        panel.add(cancelButton);

        addStaffDialog.add(panel);
        addStaffDialog.setVisible(true);
    }

    private void editStaff(ActionEvent e) {
        // Get selected row
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to edit");
            return;
        }

        // Get user ID of selected staff
        String userId = staffTable.getValueAt(selectedRow, 0).toString();

        // Fetch existing staff
        Staff existingStaff = staffRepository.getStaffByUserId(userId);
        if (existingStaff == null) {
            JOptionPane.showMessageDialog(this, "Staff not found");
            return;
        }

        JDialog editStaffDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Staff", true);
        editStaffDialog.setSize(400, 400);
        editStaffDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Fields
        JTextField nameField = createLabeledTextField(panel, "Full Name:");
        nameField.setText(existingStaff.getName());

        JTextField phoneField = createLabeledTextField(panel, "Phone Number:");
        phoneField.setText(existingStaff.getPhoneNumber());

        JTextField emailField = createLabeledTextField(panel, "Email:");
        emailField.setText(existingStaff.getEmail());

        JComboBox<String> positionCombo = createPositionComboBox(panel, "Position:");
        positionCombo.setSelectedItem(existingStaff.getPosition());

        JTextField userIdField = createLabeledTextField(panel, "User  ID:");
        userIdField.setText(existingStaff.getUserId());
        userIdField.setEditable(false);

        JComboBox<String> statusCombo = createStatusComboBox(panel, "Status:");
        statusCombo.setSelectedItem(existingStaff.getStatus());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            // Validate inputs
            if (validateInputs(nameField, phoneField, emailField, userIdField)) {
                try {
                    // Create updated Staff object
                    Staff updatedStaff = new Staff(
                        nameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        (String) positionCombo.getSelectedItem(),
                        userIdField.getText(),
                        (String) statusCombo.getSelectedItem(),
                        existingStaff.getCreatedAt(),
                        new Timestamp(System.currentTimeMillis())
                    );

                    // Attempt to update staff
                    if (staffRepository.updateStaff(updatedStaff)) {
                        JOptionPane.showMessageDialog(editStaffDialog, "Staff Updated Successfully!");
                        loadStaff(); // Refresh the staff list
                        editStaffDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editStaffDialog, 
                            "Failed to update staff. Please check the details.",
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editStaffDialog, 
                        "Error updating staff: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> editStaffDialog.dispose());
        panel.add(cancelButton);

        editStaffDialog.add(panel);
        editStaffDialog.setVisible(true);
    }

    private void deleteStaff(ActionEvent e) {
        // Get selected row
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete");
            return;
        }

        // Get user ID of selected staff
        String userId = staffTable.getValueAt(selectedRow, 0).toString();

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this staff member?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (staffRepository.deleteStaff(userId)) {
                JOptionPane.showMessageDialog(this, "Staff Deleted Successfully!");
                loadStaff(); // Refresh the staff list
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete staff. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs(JTextField nameField, JTextField phoneField, JTextField emailField, JTextField userIdField) {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (userIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "User  ID cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
       
        return true;
    }

    // Utility methods for creating input components
    private JTextField createLabeledTextField(JPanel panel, String label) {
        panel.add(new JLabel(label));
        JTextField textField = new JTextField();
        panel.add(textField);
        return textField;
    }


    private JComboBox<String> createPositionComboBox(JPanel panel, String label) {
        panel.add(new JLabel(label));
        String[] positions = {"Manager", "Receptionist", "Housekeeping", "Maintenance"};
        JComboBox<String> comboBox = new JComboBox<>(positions);
        panel.add(comboBox);
        return comboBox;
    }

    private JComboBox<String> createStatusComboBox(JPanel panel, String label) {
        panel.add(new JLabel(label));
        String[] statuses = {"Active", "Inactive"};
        JComboBox<String> comboBox = new JComboBox<>(statuses);
        panel.add(comboBox);
        return comboBox;
    }
}