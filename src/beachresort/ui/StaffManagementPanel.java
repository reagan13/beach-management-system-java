package beachresort.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class StaffManagementPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel tableModel;

    public StaffManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Table Setup
        String[] columnNames = {
            "ID", "Name", "Position", 
            "Department", "Contact", "Status"
        };

        Object[][] data = {
            {"S001", "John Doe", "Manager", "Administration", "1234567890", "Active"},
            {"S002", "Jane Smith", "Receptionist", "Front Desk", "9876543210", "Active"}
        };

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);
        
        // Styling
        customizeTable();

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Search and Action Panels
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(createSearchPanel(), BorderLayout.WEST);
        topPanel.add(createActionPanel(), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void customizeTable() {
        // Enhanced Table Styling
        staffTable.setRowHeight(45);
        staffTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Show grid lines and set colors
        staffTable.setShowGrid(true);
        staffTable.setGridColor(Color.LIGHT_GRAY);
        
        // Set intercell spacing
        staffTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Selection styling
        staffTable.setSelectionBackground(SystemColor.textHighlight);
        staffTable.setSelectionForeground(SystemColor.textHighlightText);

        // Header Styling
        JTableHeader header = staffTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchField.setText("Search staff...");
        searchField.setForeground(Color.GRAY);
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search staff...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search staff...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch(searchField.getText()));

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addStaffBtn = new JButton("Add Staff");
        JButton editStaffBtn = new JButton("Edit Staff");
        JButton deleteStaffBtn = new JButton("Delete Staff");

        addStaffBtn.addActionListener(e -> showAddStaffDialog());
        editStaffBtn.addActionListener(e -> editSelectedStaff());
        deleteStaffBtn.addActionListener(e -> deleteSelectedStaff());

        panel.add(addStaffBtn);
        panel.add(editStaffBtn);
        panel.add(deleteStaffBtn);

        return panel;
    }

    private void performSearch(String searchText) {
        if (searchText.equals("Search staff...")) {
            searchText = "";
        }
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        staffTable.setRowSorter(sorter);
        
        // Basic search across all columns
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
    }

    private void showAddStaffDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Staff");
        dialog.setSize(400, 500);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        String[] labels = {
            "Staff ID:", "Name:", "Position:", 
            "Department:", "Contact:", "Email:", "Salary:"
        };

        for (String label : labels) {
            formPanel.add(new JLabel(label));
            formPanel.add(new JTextField());
        }

        JButton saveButton = new JButton("Save Staff");
        saveButton.addActionListener(e -> {
            // Save staff logic
            dialog.dispose();
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(saveButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void editSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a staff member to edit",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Populate dialog with existing staff details
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Staff Member");
        dialog.setSize(400, 500);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Populate form with existing data
        String[] labels = {
            "Staff ID:", "Name:", "Position:", 
            "Department:", "Contact:", "Email:", "Salary:"
        };

        for (String label : labels) {
            formPanel.add(new JLabel(label));
            JTextField field = new JTextField();
            
            // Pre-populate if possible
            if (label.equals("Staff ID:")) {
                field.setText(tableModel.getValueAt(selectedRow, 0).toString());
                field.setEditable(false);
            }
            
            formPanel.add(field);
        }

        JButton updateButton = new JButton("Update Staff");
        updateButton.addActionListener(e -> {
            // Update staff logic
            dialog.dispose();
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(updateButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
private void deleteSelectedStaff() {
    int selectedRow = staffTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, 
            "Please select a staff member to deactivate",
            "No Selection",
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // Check if staff is already inactive
    String currentStatus = tableModel.getValueAt(selectedRow, 5).toString();
    if (currentStatus.equals("Inactive")) {
        JOptionPane.showMessageDialog(this, 
            "This staff member is already inactive",
            "Cannot Deactivate",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Confirmation Dialog with system-style option pane
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Are you sure you want to deactivate this staff member?", 
        "Confirm Deactivation", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.QUESTION_MESSAGE
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        // Change status to Inactive instead of removing the row
        tableModel.setValueAt("Inactive", selectedRow, 5);
        
        // Optional: Highlight or visually indicate the inactive status
        staffTable.repaint();
        
        // Show confirmation message
        JOptionPane.showMessageDialog(
            this, 
            "Staff member has been deactivated", 
            "Deactivation Successful", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
    // Additional utility methods for staff management

    private void validateStaffInput(JTextField[] fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "All fields must be filled",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
    }

    private void generateStaffReport() {
        // Create a comprehensive staff report
        int totalStaff = tableModel.getRowCount();
        int activeStaff = 0;
        
        // Count active staff
        for (int i = 0; i < totalStaff; i++) {
            if (tableModel.getValueAt(i, 5).toString().equals("Active")) {
                activeStaff++;
            }
        }

        // Create report dialog
        JDialog reportDialog = new JDialog();
        reportDialog.setTitle("Staff Management Report");
        reportDialog.setSize(400, 300);
        reportDialog.setModal(true);
        reportDialog.setLocationRelativeTo(this);

        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Report Labels
        JLabel totalStaffLabel = new JLabel("Total Staff: " + totalStaff);
        JLabel activeStaffLabel = new JLabel("Active Staff: " + activeStaff);
        JLabel inactiveStaffLabel = new JLabel("Inactive Staff: " + (totalStaff - activeStaff));

        // Styling
        Font reportFont = new Font("Arial", Font.BOLD, 14);
        totalStaffLabel.setFont(reportFont);
        activeStaffLabel.setFont(reportFont);
        inactiveStaffLabel.setFont(reportFont);

        // Add to panel
        reportPanel.add(totalStaffLabel);
        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(activeStaffLabel);
        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(inactiveStaffLabel);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> reportDialog.dispose());
        reportPanel.add(Box.createVerticalStrut(20));
        reportPanel.add(closeButton);

        reportDialog.add(reportPanel);
        reportDialog.setVisible(true);
    }

    // Optional: Method to export staff data
    private void exportStaffData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Staff Data");
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                // Implement export logic (CSV, Excel, etc.)
                JOptionPane.showMessageDialog(
                    this, 
                    "Staff data exported successfully", 
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Error exporting staff data", 
                    "Export Failed", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Optional: Method to import staff data
    private void importStaffData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Staff Data");
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                // Implement import logic (CSV, Excel, etc.)
                JOptionPane.showMessageDialog(
                    this, 
                    "Staff data imported successfully", 
                    "Import Complete", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Error importing staff data", 
                    "Import Failed", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}