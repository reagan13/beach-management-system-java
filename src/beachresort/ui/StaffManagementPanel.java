package beachresort.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StaffManagementPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel tableModel;

    public StaffManagementPanel() {
        setLayout(new BorderLayout());

        JLabel staffLabel = new JLabel("Staff Management", SwingConstants.CENTER);
        staffLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(staffLabel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {
            "Staff ID", 
            "Full Name", 
            "Email", 
            "Phone", 
            "Role", 
            "Hire Date", 
            "Status"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        staffTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(staffTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons for managing staff
        JPanel buttonPanel = new JPanel();
        JButton addStaffButton = new JButton("Add Staff");
        JButton editStaffButton = new JButton("Edit Staff");
        JButton deactivateStaffButton = new JButton("Deactivate Staff");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addStaffButton);
        buttonPanel.add(editStaffButton);
        buttonPanel.add(deactivateStaffButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addStaffButton.addActionListener(this::addStaff);
        editStaffButton.addActionListener(this::editStaff);
        deactivateStaffButton.addActionListener(this::deactivateStaff);
        refreshButton.addActionListener(this::refreshStaff);
    }

    private void addStaff(ActionEvent e) {
        JDialog addStaffDialog = new JDialog(
            (Frame)SwingUtilities.getWindowAncestor(this), 
            "Add New Staff Member", 
            true
        );
        addStaffDialog.setSize(400, 500);
        addStaffDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Staff Details Input Fields
        panel.add(new JLabel("Full Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Phone Number:"));
        JTextField phoneField = new JTextField();
        panel.add(phoneField);

        panel.add(new JLabel("Role:"));
        String[] roles = {
            "Reception", 
            "Housekeeping", 
            "Management", 
            "Maintenance", 
            "Security"
        };
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        panel.add(roleCombo);

        panel.add(new JLabel("Hire Date:"));
        JTextField hireDateField = new JTextField("YYYY-MM-DD");
        panel.add(hireDateField);

        panel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Status:"));
        String[] statuses = {"Active", "Probation", "Inactive"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        panel.add(statusCombo);

        // Save and Cancel Buttons
        JButton saveButton = new JButton("Save Staff");
        saveButton.addActionListener(saveEvent -> {
            // Validate inputs
            if (validateStaffInput(nameField, emailField, phoneField, usernameField, passwordField)) {
                // Add to table
                Object[] newStaff = {
                    generateStaffID(), // Auto-generate Staff ID
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    roleCombo.getSelectedItem(),
                    hireDateField.getText(),
                    statusCombo.getSelectedItem()
                };
                
                tableModel.addRow(newStaff);
                
                JOptionPane.showMessageDialog(
                    addStaffDialog, 
                    "Staff Member Added Successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                addStaffDialog.dispose();
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
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to edit");
            return;
        }

        // Similar to addStaff dialog, but pre-fill with existing data
        JDialog editStaffDialog = new JDialog(
            (Frame)SwingUtilities.getWindowAncestor(this), 
            "Edit Staff Member", 
            true
        );
        editStaffDialog.setSize(400, 500);
        editStaffDialog.setLocationRelativeTo(this);

        // Implement edit logic similar to add staff
        JOptionPane.showMessageDialog(this, "Edit Staff functionality not fully implemented");
    }

    private void deactivateStaff(ActionEvent e) {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to deactivate");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to deactivate this staff member?", 
            "Confirm Deactivation", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Change status to "Inactive"
            tableModel.setValueAt("Inactive", selectedRow, 6);
            JOptionPane.showMessageDialog(this, "Staff member deactivated successfully");
        }
    }

    private void refreshStaff(ActionEvent e) {
        // TODO: Implement actual refresh logic from database
        JOptionPane.showMessageDialog(this, "Refreshing staff list...");
    }

    // Utility method to validate staff input
    private boolean validateStaffInput(
        JTextField nameField, 
        JTextField emailField, 
        JTextField phoneField, 
        JTextField usernameField, 
        JPasswordField passwordField
    ) {
        // Basic validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty");
            return false;
        }

        if (!emailField.getText().contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email address");
            return false;
        }

        if (phoneField.getText().trim().length() < 10) {
            JOptionPane.showMessageDialog(this, "Invalid phone number");
            return false;
        }

        if (usernameField.getText().trim().length() < 4) {
            JOptionPane.showMessageDialog(this, "Username must be at least 4 characters");
            return false;
        }
                if (passwordField.getPassword().length < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters");
            return false;
        }

        return true; // All validations passed
    }

    // Utility method to generate a unique Staff ID (for demonstration purposes)
    private String generateStaffID() {
        // In a real application, this would be generated by the database
        return "STAFF" + (tableModel.getRowCount() + 1); // Simple ID generation
    }
}