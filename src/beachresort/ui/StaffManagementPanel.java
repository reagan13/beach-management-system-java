package beachresort.ui;

import beachresort.models.Absence;
import beachresort.models.Staff;
import beachresort.repositories.StaffRepository;
import beachresort.repositories.AbsenceRepository;

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
    private AbsenceRepository absenceRepository;
    private JTabbedPane tabbedPane;

    public StaffManagementPanel() {
        // Initialize repository
        staffRepository = new StaffRepository();
        absenceRepository = new AbsenceRepository();

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
        String[] columnNames = {"Staff ID", "Name", "Position", "Phone", "Email", "Status"};
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
        String[] leaveColumnNames = {"Absence ID", "Staff ID", "Leave Type", "Start Date", "End Date", "Status", "Reason"};
        DefaultTableModel leaveTableModel = new DefaultTableModel(leaveColumnNames, 0);
        JTable leaveTable = new JTable(leaveTableModel);
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single selection
        

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

        // Load leave requests into the table
        loadLeaveRequests(leaveTableModel);

        // Action for Approve button
        approveButton.addActionListener(e -> {
            int selectedRow = leaveTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Please select a leave request to approve.");
                return;
            }
            int absenceId = (int) leaveTableModel.getValueAt(selectedRow, 0); // Get Absence ID
            // Call method to approve the absence
            if (absenceRepository.updateAbsenceStatus(absenceId, "Approved")) {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Leave request approved successfully.");
                loadLeaveRequests(leaveTableModel); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Failed to approve leave request.");
            }
        });

        // Action for Reject button
        rejectButton.addActionListener(e -> {
            int selectedRow = leaveTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Please select a leave request to reject.");
                return;
            }
            int absenceId = (int) leaveTableModel.getValueAt(selectedRow, 0); // Get Absence ID
            // Call method to reject the absence
            if (absenceRepository.updateAbsenceStatus(absenceId, "Rejected")) {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Leave request rejected successfully.");
                loadLeaveRequests(leaveTableModel); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Failed to reject leave request.");
            }
        });

        // Action for View Details button
        viewDetailsButton.addActionListener(e -> {
            int selectedRow = leaveTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Please select a leave request to view details.");
                return;
            }
            int absenceId = (int) leaveTableModel.getValueAt(selectedRow, 0); 
            // Fetch and display details for the selected absence
            Absence absence = absenceRepository.getAbsenceById(absenceId); 
            if (absence != null) {
                String details = String.format("Absence ID: %d\nStaff ID: %d\nLeave Type: %s\nStart Date: %s\nEnd Date: %s\nStatus: %s\nReason: %s",
                        absence.getAbsenceId(), absence.getUserId(), absence.getLeaveType(),
                        absence.getStartDate(), absence.getEndDate(), absence.getStatus(), absence.getReason());
                JOptionPane.showMessageDialog(leaveRequestsPanel, details, "Leave Request Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(leaveRequestsPanel, "Failed to retrieve absence details.");
            }
        });

        return leaveRequestsPanel;
    }

private void loadLeaveRequests(DefaultTableModel leaveTableModel) {
    // Clear existing rows
    leaveTableModel.setRowCount(0);

    // Fetch leave requests from the repository
    List<Absence> absences = absenceRepository.getAllLeaveRequests(); // Assuming this method exists to fetch all leave requests
    for (Absence absence : absences) {
        leaveTableModel.addRow(new Object[] {
                absence.getAbsenceId(),
                absence.getUserId(),
                absence.getLeaveType(),
                absence.getStartDate(),
                absence.getEndDate(),
                absence.getStatus(),
                absence.getReason()
        });
    }
}



    private void loadStaff() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch staff from repository
        List<Staff> staffList = staffRepository.getAllStaff();
        
        // Populate table
        for (Staff staff : staffList) {
            Object[] rowData = {
                staff.getStaffId(),
                staff.getFullName(),
                staff.getPosition(),
                staff.getContactNumber(),
                staff.getEmail(),
                staff.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }





    private void addStaff(ActionEvent e) {
        JDialog addStaffDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Staff", true);
        addStaffDialog.setSize(500, 500);
        addStaffDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Fields
        JComboBox<String> staffIdCombo = createStaffIdComboBox(panel, "Staff ID:");
        JTextField nameField = createLabeledTextField(panel, "Full Name:");
        JTextField phoneField = createLabeledTextField(panel, "Phone Number:");
        JTextField emailField = createLabeledTextField(panel, "Email:");
        JComboBox<String> positionCombo = createPositionComboBox(panel, "Position:");
        JTextField userIdField = createLabeledTextField(panel, "User  ID:");
        JComboBox<String> statusCombo = createStatusComboBox(panel, "Status:");

        // Create a text area for tasks
        JTextArea taskArea = new JTextArea(5, 20); // 5 rows, 20 columns
        taskArea.setLineWrap(true);
        taskArea.setWrapStyleWord(true);
        JScrollPane taskScrollPane = new JScrollPane(taskArea);

        // Add the text area to the panel with a label
        panel.add(new JLabel("Task:"));
        panel.add(taskScrollPane);


        // Add ActionListener to staffIdCombo to populate fields when a staff ID is selected
        staffIdCombo.addActionListener(event -> {
            int selectedStaffId = Integer.parseInt((String) staffIdCombo.getSelectedItem());
            Staff staff = staffRepository.getStaffByStaffId(selectedStaffId); // Assuming this method exists

            if (staff != null) {
                nameField.setText(staff.getFullName());
                phoneField.setText(staff.getContactNumber());
                emailField.setText(staff.getEmail());
                userIdField.setText(String.valueOf(staff.getId()));
                positionCombo.setSelectedItem(staff.getPosition());
                statusCombo.setSelectedItem(staff.getStatus());
                taskArea.setText(staff.getTask()); // Populate the task area
            } else {
                // Clear fields if no staff is found
                nameField.setText("");
                phoneField.setText("");
                emailField.setText("");
                userIdField.setText("");
                positionCombo.setSelectedItem("Unassigned");
                statusCombo.setSelectedItem("Active");
                taskArea.setText(""); // Clear the task area
            }
        });


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            // // Validate inputs
            if (validateInputs(nameField, phoneField, emailField, userIdField)) {
                try {
                    // Logic to save the updated staff information
                   String selectedItem = staffIdCombo.getSelectedItem().toString();
                    int staffId = Integer.parseInt(selectedItem);
                   
                    // Save the updated staff information to the repository
                    
                    boolean success =   staffRepository.updateStaff( positionCombo.getSelectedItem().toString(),
                        statusCombo.getSelectedItem().toString(),
                        taskArea.getText(),
                        staffId); 
                    if (success) {

                        JOptionPane.showMessageDialog(addStaffDialog, "Staff updated successfully.");
                        addStaffDialog.dispose();
                    }
                    else {
                        JOptionPane.showMessageDialog(addStaffDialog, "Staff not updated successfully.");
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
        String userIdString = staffTable.getValueAt(selectedRow, 0).toString();
        Integer userId = Integer.parseInt(userIdString);

        // Fetch existing staff
        Staff existingStaff = staffRepository.getStaffByStaffId(userId);
        if (existingStaff == null) {
            JOptionPane.showMessageDialog(this, "Staff not found");
            return;
        }

        JDialog editStaffDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Staff", true);
        editStaffDialog.setSize(400, 400);
        editStaffDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

       // Input Fields

        JTextField staffField = createLabeledTextField(panel, "Staff Id:");
        staffField.setText(String.valueOf(existingStaff.getStaffId()));
        staffField.setEditable(false);

        JTextField nameField = createLabeledTextField(panel, "Full Name:");
        nameField.setText(existingStaff.getFullName());
        nameField.setEditable(false);

        JTextField phoneField = createLabeledTextField(panel, "Phone Number:");
        phoneField.setText(existingStaff.getContactNumber());
        phoneField.setEditable(false);

        JTextField emailField = createLabeledTextField(panel, "Email:");
        emailField.setText(existingStaff.getEmail());
        emailField.setEditable(false);

        JComboBox<String> positionCombo = createPositionComboBox(panel, "Position:");
        positionCombo.setSelectedItem(existingStaff.getPosition());
        positionCombo.setEditable(true);

        JTextField userIdField = createLabeledTextField(panel, "User ID:");
        userIdField.setText(String.valueOf(existingStaff.getId()));
        userIdField.setEditable(false);

        JComboBox<String> statusCombo = createStatusComboBox(panel, "Status:");
        statusCombo.setSelectedItem(existingStaff.getStatus());

        // Create a text area for tasks
        JTextArea taskArea = new JTextArea(5, 20); // 5 rows, 20 columns
        taskArea.setLineWrap(true);
        taskArea.setWrapStyleWord(true);
        taskArea.setText(existingStaff.getTask()); // Set the existing task
        JScrollPane taskScrollPane = new JScrollPane(taskArea);

        // Add the text area to the panel with a label
        panel.add(new JLabel("Task:"));
        panel.add(taskScrollPane);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
             // // Validate inputs
            if (validateInputs(nameField, phoneField, emailField, userIdField)) {
                try {
                    // Logic to save the updated staff information
                   
                    // Save the updated staff information to the repository
                    staffRepository.updateStaff( positionCombo.getSelectedItem().toString(),
                            statusCombo.getSelectedItem().toString(),
                                userIdField.getText(),
                        existingStaff.getStaffId());

                   // Show success message
                    JOptionPane.showMessageDialog(editStaffDialog, "Staff updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    editStaffDialog.dispose();
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
            JOptionPane.showMessageDialog(this, "Please select a staff member to edit");
            return;
        }

        // Get user ID of selected staff
        String userIdString = staffTable.getValueAt(selectedRow, 0).toString();
        Integer userId = Integer.parseInt(userIdString);

        // Fetch existing staff
        Staff existingStaff = staffRepository.getStaffByStaffId(userId);
        if (existingStaff == null) {
            JOptionPane.showMessageDialog(this, "Staff not found");
            return;
        }


        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this staff member?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (staffRepository.deleteStaff(existingStaff.getStaffId())) {
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
        textField.setEditable(false);
        panel.add(textField);
        return textField;
    }


    private JComboBox<String> createPositionComboBox(JPanel panel, String label) {
        panel.add(new JLabel(label));
        String[] positions = {"Unassigned","Manager", "Receptionist", "Housekeeping", "Maintenance"};
        JComboBox<String> comboBox = new JComboBox<>(positions);
        panel.add(comboBox);
        return comboBox;
    }

    private JComboBox<String> createStatusComboBox(JPanel panel, String label) {
        panel.add(new JLabel(label));
        String[] statuses = { "Active", "Inactive"};
        JComboBox<String> comboBox = new JComboBox<>(statuses);
        panel.add(comboBox);
        return comboBox;
    }

    private JComboBox<String> createStaffIdComboBox(JPanel panel, String label) {
        panel.add(new JLabel(label));
        JComboBox<String> comboBox = new JComboBox<>();

        // Populate the combo box with staff IDs
        List<Integer> staffIds = staffRepository.getAllStaffIds();
        for (Integer id : staffIds) {
            comboBox.addItem(String.valueOf(id)); // Convert Integer to String and add to combo box
        }

        panel.add(comboBox);
        return comboBox;
    }


    
    
}