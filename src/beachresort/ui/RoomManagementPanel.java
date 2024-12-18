package beachresort.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomManagementPanel extends JPanel {
    // Neutral Professional Color Palette
    private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);
    private static final Color PRIMARY_COLOR = new Color(55, 65, 81);
    private static final Color SECONDARY_COLOR = new Color(75, 85, 99);
    private static final Color HOVER_COLOR = new Color(99, 102, 241);

    private JTable roomsTable;
    private DefaultTableModel tableModel;

    public RoomManagementPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);

        // Table Columns
        String[] columnNames = {
            "Room Number", "Type", "Capacity", 
            "Price", "Status"
        };

        // Sample Data
        Object[][] data = {
            {"101", "Standard", "2", "$100", "Available"},
            {"202", "Deluxe", "3", "$200", "Occupied"},
            {"303", "Suite", "4", "$350", "Maintenance"}
        };

        // Create Table Model
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomsTable = new JTable(tableModel);
        customizeTable();
    }

    private void customizeTable() {
        // Table Styling
        roomsTable.setRowHeight(40);
        roomsTable.setSelectionBackground(new Color(230, 232, 240));
        roomsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Header Styling
        JTableHeader header = roomsTable.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private JButton createInteractiveButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover and interaction effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Interactive Buttons
        JButton addRoomBtn = createInteractiveButton("Add Room", PRIMARY_COLOR);
        JButton editRoomBtn = createInteractiveButton("Edit Room", SECONDARY_COLOR);
        JButton deleteRoomBtn = createInteractiveButton("Delete Room", Color.RED);

        // Add Interaction Listeners
        addRoomBtn.addActionListener(e -> showAddRoomDialog());
        editRoomBtn.addActionListener(e -> editSelectedRoom());
        deleteRoomBtn.addActionListener(e -> deleteSelectedRoom());

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(addRoomBtn);
        buttonPanel.add(editRoomBtn);
        buttonPanel.add(deleteRoomBtn);

        // Table Scroll Pane
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void showAddRoomDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Room");
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Interactive Form Fields
        addFormField(panel, "Room Number:", new JTextField());
        
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
            "Standard", "Deluxe", "Suite", "Executive"
        });
        addFormField(panel, "Room Type:", roomTypeCombo);

        addFormField(panel, "Capacity:", new JTextField());
        addFormField(panel, "Price per Night:", new JTextField());

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Available", "Occupied", "Maintenance"
        });
        addFormField(panel, "Status:", statusCombo);

        JButton confirmButton = createInteractiveButton("Add Room", PRIMARY_COLOR);
        confirmButton.addActionListener(e -> {
            // Room addition logic
            dialog.dispose();
            JOptionPane.showMessageDialog(this, 
                "Room Added Successfully", 
                "Room Management", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(new JLabel()); // Spacer
        panel.add(confirmButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JComponent component) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(jLabel);
        panel.add(component);
    }

    private void editSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a room to edit", 
                "No Room Selected", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Open dialog with selected room's current details
        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Room");
        editDialog.setSize(400, 500);
        editDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pre-fill with current room details
        String roomNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String roomType = (String) tableModel.getValueAt(selectedRow, 1);
        String capacity = (String) tableModel.getValueAt(selectedRow, 2);
        String price = (String) tableModel.getValueAt(selectedRow, 3);
        String status = (String) tableModel.getValueAt(selectedRow, 4);

        JTextField roomNumberField = new JTextField(roomNumber);
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
            "Standard", "Deluxe", "Suite", "Executive"
        });
        roomTypeCombo.setSelectedItem(roomType);

        JTextField capacityField = new JTextField(capacity);
        JTextField priceField = new JTextField(price);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Available", "Occupied", "Maintenance"
        });
        statusCombo.setSelectedItem(status);

        addFormField(panel, "Room Number:", roomNumberField);
        addFormField(panel, "Room Type:", roomTypeCombo);
        addFormField(panel, "Capacity:", capacityField);
        addFormField(panel, "Price per Night:", priceField);
        addFormField(panel, "Status:", statusCombo);

        JButton confirmButton = createInteractiveButton("Update Room", PRIMARY_COLOR);
        confirmButton.addActionListener(e -> {
            // Update room logic
            tableModel.setValueAt(roomNumberField.getText(), selectedRow, 0);
            tableModel.setValueAt(roomTypeCombo.getSelectedItem(), selectedRow, 1);
            tableModel.setValueAt(capacityField.getText(), selectedRow, 2);
            tableModel.setValueAt(priceField.getText(), selectedRow, 3);
            tableModel.setValueAt(statusCombo.getSelectedItem(), selectedRow, 4);
            editDialog.dispose();
            JOptionPane.showMessageDialog(this, 
                "Room Updated Successfully", 
                "Room Management", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(new JLabel()); // Spacer
        panel.add(confirmButton);

        editDialog.add(panel);
        editDialog.setVisible(true);
    }

    private void deleteSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a room to delete", 
                "No Room Selected", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this room?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, 
                "Room Deleted Successfully", 
                "Room Management", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}