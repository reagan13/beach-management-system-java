package beachresort.ui;

import beachresort.models.Staff;
import beachresort.repositories.StaffRepository;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffManagementPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private StaffRepository staffRepository;

    public StaffManagementPanel() {
        // Initialize repository
        staffRepository = new StaffRepository();

        // Setup layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Table Setup
        String[] columnNames = {
            "Staff ID", "Name", "Position", 
            "Department", "Contact", "Email", "Salary", "Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
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
        // topPanel.add(createSearchPanel(), BorderLayout.WEST);
        topPanel.add(createActionPanel(), BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Load initial data
        loadStaffData();
    }

    private void loadStaffData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Fetch staff from repository
        List<Staff> staffList = staffRepository.getAllStaff();
        
        // Populate table
        for (Staff staff : staffList) {
            Object[] rowData = {
                staff.getStaffId(),
                staff.getName(),
                staff.getPosition(),
                staff.getDepartment(),
                staff.getContactNumber(),
                staff.getEmail(),
                staff.getSalary(),
                staff.getStatus()
            };
            tableModel.addRow(rowData);
        }
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

        // Input fields
        JTextField txtName = new JTextField();
        JTextField txtPosition = new JTextField();
        JTextField txtDepartment = new JTextField();
        JTextField txtContactNumber = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtSalary = new JTextField();
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});

        // Add components to form
        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Position:"));
        formPanel.add(txtPosition);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(txtDepartment);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(txtContactNumber);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Salary:"));
        formPanel.add(txtSalary);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);

        JButton saveButton = new JButton("Save Staff");
        saveButton.addActionListener(e -> {
            try {
                // Validate inputs
                if (!validateInputs(txtName, txtPosition, txtDepartment, 
                                     txtContactNumber, txtEmail, txtSalary)) {
                    return;
                }

                // Create Staff object
                Staff newStaff = new Staff(
                    staffRepository.generateUniqueStaffId(), // Generate ID
                    txtName.getText(),
                    txtPosition.getText(),
                    txtDepartment.getText(),
                    txtContactNumber.getText(),
                    txtEmail.getText(),
                    Double.parseDouble(txtSalary.getText()),
                    LocalDate.now(), // Current date as hire date
                    cmbStatus.getSelectedItem().toString()
                );

                // Save to database
                if (staffRepository.addStaff(newStaff)) {
                    JOptionPane.showMessageDialog(dialog, "Staff added successfully!");
                    loadStaffData(); // Refresh table
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add staff", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid salary format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(saveButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private boolean validateInputs(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "All fields must be filled", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
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

        // Get staff ID from selected row
        String staffId = tableModel.getValueAt(selectedRow, 0).toString();
        
        // Fetch full staff details
        Staff selectedStaff = staffRepository.getStaffById(staffId);
        
        if (selectedStaff == null) {
            JOptionPane.showMessageDialog(this, 
                "Could not retrieve staff details",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create edit dialog similar to add dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Staff Member");
        dialog.setSize(400, 500);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Input fields pre-populated
        JTextField txtName = new JTextField(selectedStaff.getName());
        JTextField txtPosition = new JTextField(selectedStaff.getPosition());
        JTextField txtDepartment = new JTextField(selectedStaff.getDepartment());
                // Continuing from previous code
        JTextField txtContactNumber = new JTextField(selectedStaff.getContactNumber());
        JTextField txtEmail = new JTextField(selectedStaff.getEmail());
        JTextField txtSalary = new JTextField(String.valueOf(selectedStaff.getSalary()));
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setSelectedItem(selectedStaff.getStatus());

        // Add components to form
        formPanel.add(new JLabel("Staff ID:"));
        JLabel lblStaffId = new JLabel(staffId);
        formPanel.add(lblStaffId);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Position:"));
        formPanel.add(txtPosition);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(txtDepartment);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(txtContactNumber);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Salary:"));
        formPanel.add(txtSalary);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);

        JButton updateButton = new JButton("Update Staff");
        updateButton.addActionListener(e -> {
            try {
                // Validate inputs
                if (!validateInputs(txtName, txtPosition, txtDepartment, 
                                     txtContactNumber, txtEmail, txtSalary)) {
                    return;
                }

                // Create updated Staff object
                Staff updatedStaff = new Staff(
                    staffId,
                    txtName.getText(),
                    txtPosition.getText(),
                    txtDepartment.getText(),
                    txtContactNumber.getText(),
                    txtEmail.getText(),
                    Double.parseDouble(txtSalary.getText()),
                    selectedStaff.getHireDate(), // Preserve original hire date
                    cmbStatus.getSelectedItem().toString()
                );

                // Update in database
                if (staffRepository.updateStaff(updatedStaff)) {
                    JOptionPane.showMessageDialog(dialog, "Staff updated successfully!");
                    loadStaffData(); // Refresh table
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update staff", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid salary format", "Error", JOptionPane.ERROR_MESSAGE);
            }
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

        // Get staff ID from selected row
        String staffId = tableModel.getValueAt(selectedRow, 0).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 7).toString();

        // Check if staff is already inactive
        if (currentStatus.equals("Inactive")) {
            JOptionPane.showMessageDialog(this, 
                "This staff member is already inactive",
                "Cannot Deactivate",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation Dialog
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to deactivate this staff member?", 
            "Confirm Deactivation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Deactivate staff in database
            if (staffRepository.deleteStaff(staffId)) {
                // Refresh data
                loadStaffData();
                
                JOptionPane.showMessageDialog(
                    this, 
                    "Staff member has been deactivated", 
                    "Deactivation Successful", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, 
                    "Failed to deactivate staff member", 
                    "Deactivation Failed", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
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

    private void performSearch(String searchText) {
        // Clear existing filter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        staffTable.setRowSorter(sorter);
        
        // If search is empty or default text, show all results
        if (searchText.isEmpty() || searchText.equals("Search staff...")) {
            sorter.setRowFilter(null);
            return;
        }

        // Perform case-insensitive search across all columns
        RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter(
            "(?i)" + searchText, 
            0, 1, 2, 3, 4, 5 // Columns to search
        );
        sorter.setRowFilter(filter);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addStaffBtn = new JButton("Add Staff");
        JButton editStaffBtn = new JButton("Edit Staff");
        JButton deleteStaffBtn = new JButton("Deactivate Staff");
        JButton reportBtn = new JButton("Staff Report");

        addStaffBtn.addActionListener(e -> showAddStaffDialog());
        editStaffBtn.addActionListener(e -> editSelectedStaff());
        deleteStaffBtn.addActionListener(e -> deleteSelectedStaff());
        reportBtn.addActionListener(e -> generateStaffReport());

        panel.add(addStaffBtn);
        panel.add(editStaffBtn);
        panel.add(deleteStaffBtn);
        panel.add(reportBtn);

        return panel;
    }

        private void generateStaffReport() {
        // Fetch staff statistics from repository
        StaffRepository.StaffStatistics stats = staffRepository.getStaffStatistics();

        // Create report dialog
        JDialog reportDialog = new JDialog();
        reportDialog.setTitle("Staff Management Report");
        reportDialog.setSize(400, 300);
        reportDialog.setModal(true);
        reportDialog.setLocationRelativeTo(this);

        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create report components
        JLabel titleLabel = new JLabel("Staff Management Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Staff Statistics Labels
        JLabel totalStaffLabel = new JLabel(String.format("Total Staff: %d", stats.getTotalStaff()));
        JLabel activeStaffLabel = new JLabel(String.format("Active Staff: %d", stats.getActiveStaff()));
        JLabel inactiveStaffLabel = new JLabel(String.format("Inactive Staff: %d", stats.getInactiveStaff()));
        
        // Department Breakdown
        JLabel departmentBreakdownLabel = new JLabel("Department Breakdown:");
        departmentBreakdownLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create department breakdown panel
        JPanel departmentPanel = new JPanel();
        departmentPanel.setLayout(new BoxLayout(departmentPanel, BoxLayout.Y_AXIS));
        
        // Add department statistics
        stats.getDepartmentStats().forEach((dept, count) -> {
            departmentPanel.add(new JLabel(String.format("%s: %d staff", dept, count)));
        });

        // Styling
        Font statsFont = new Font("Arial", Font.PLAIN, 12);
        totalStaffLabel.setFont(statsFont);
        activeStaffLabel.setFont(statsFont);
        inactiveStaffLabel.setFont(statsFont);

        // Add components to report panel
        reportPanel.add(titleLabel);
        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(totalStaffLabel);
        reportPanel.add(activeStaffLabel);
        reportPanel.add(inactiveStaffLabel);
        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(departmentBreakdownLabel);
        reportPanel.add(departmentPanel);

        // Export and Close buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton exportButton = new JButton("Export Report");
        JButton closeButton = new JButton("Close");

        exportButton.addActionListener(e -> exportStaffReport(stats));
        closeButton.addActionListener(e -> reportDialog.dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);

        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(buttonPanel);

        reportDialog.add(reportPanel);
        reportDialog.setVisible(true);
    }

    private void exportStaffReport(StaffRepository.StaffStatistics stats) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Staff Report");
        fileChooser.setSelectedFile(new File("staff_report_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                // Write report header
                writer.println("Staff Management Report," + LocalDate.now());
                writer.println();
                
                // Write overall statistics
                writer.println("Total Staff," + stats.getTotalStaff());
                writer.println("Active Staff," + stats.getActiveStaff());
                writer.println("Inactive Staff," + stats.getInactiveStaff());
                writer.println();
                
                // Write department breakdown
                writer.println("Department,Staff Count");
                stats.getDepartmentStats().forEach((dept, count) -> 
                    writer.println(dept + "," + count)
                );

                JOptionPane.showMessageDialog(
                    this, 
                    "Staff report exported successfully to " + fileToSave.getAbsolutePath(), 
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Error exporting staff report: " + ex.getMessage(), 
                    "Export Failed", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Customize table styling method
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

        // Column customization
        TableColumnModel columnModel = staffTable.getColumnModel();
        
        // Set column widths
        columnModel.getColumn(0).setPreferredWidth(50);  // Staff ID
        columnModel.getColumn(1).setPreferredWidth(150); // Name
        columnModel.getColumn(2).setPreferredWidth(100); // Position
        columnModel.getColumn(3).setPreferredWidth(100); // Department
        columnModel.getColumn(4).setPreferredWidth(100); // Contact
        columnModel.getColumn(5).setPreferredWidth(150); // Email
        columnModel.getColumn(6).setPreferredWidth(75);  // Salary
        columnModel.getColumn(7).setPreferredWidth(75);  // Status

        // Align certain columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        columnModel.getColumn(6).setCellRenderer(rightRenderer);
    }
}