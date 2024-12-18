package beachresort.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffManagementPanel extends JPanel {
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private JTable staffTable;
    private DefaultTableModel tableModel;

    public StaffManagementPanel() {
        setLayout(new BorderLayout());
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

        tableModel = new DefaultTableModel(data, columnNames);
        staffTable = new JTable(tableModel);
        
        // Styling
        staffTable.setRowHeight(40);
        staffTable.getTableHeader().setBackground(ACCENT_COLOR);
        staffTable.getTableHeader().setForeground(Color.WHITE);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(staffTable);
        add(scrollPane, BorderLayout.CENTER);

        // Action Buttons Panel
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.NORTH);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(BACKGROUND_COLOR);

        JButton addStaffBtn = createStyledButton("Add Staff", ACCENT_COLOR);
        JButton editStaffBtn = createStyledButton("Edit Staff", Color.DARK_GRAY);
        JButton deleteStaffBtn = createStyledButton("Delete Staff", Color.RED);

        addStaffBtn.addActionListener(e -> showAddStaffDialog());
        editStaffBtn.addActionListener(e -> editSelectedStaff());
        deleteStaffBtn.addActionListener(e -> deleteSelectedStaff());

        panel.add(addStaffBtn);
        panel.add(editStaffBtn);
        panel.add(deleteStaffBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void showAddStaffDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Staff");
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        String[] labels = {
            "Name:", "Position:", "Department:", 
            "Contact:", "Email:", "Salary:"
        };

        for (String label : labels) {
            dialog.add(new JLabel(label));
            dialog.add(new JTextField());
        }

        JButton saveButton = createStyledButton("Save Staff", ACCENT_COLOR);
        saveButton.addActionListener(e -> {
            // Save staff logic
            dialog.dispose();
        });

        dialog.add(new JLabel()); // Spacer
        dialog.add(saveButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a staff member to edit");
            return;
        }
        // Edit staff logic
    }

    private void deleteSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a staff member to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this staff member?");
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
        }
    }
}