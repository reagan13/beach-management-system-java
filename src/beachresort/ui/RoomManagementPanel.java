package beachresort.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class RoomManagementPanel extends JPanel {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public RoomManagementPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        // Column Names
        String[] columnNames = {
            "Room Number", "Type", "Capacity", 
            "Price", "Status", "Amenities"
        };

        // Sample Data
        Object[][] data = {
            {"101", "Standard", "2", "$100", "Available", "WiFi, TV"},
            {"202", "Deluxe", "3", "$200", "Occupied", "WiFi, TV, Balcony"},
            {"303", "Suite", "4", "$350", "Maintenance", "WiFi, TV, Kitchen"}
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
        // Enhanced Table Styling
        roomsTable.setRowHeight(45);
        roomsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        roomsTable.setShowGrid(true);
        roomsTable.setIntercellSpacing(new Dimension(0, 0));

        // Header Styling
        JTableHeader header = roomsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search and Filter Panel
        JPanel searchPanel = createSearchPanel();

        // Interactive Buttons
        JPanel buttonPanel = createButtonPanel();

        // Table Scroll Pane
        JScrollPane scrollPane = new JScrollPane(roomsTable);

        // Layout Arrangement
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));

        // Search Field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Placeholder-like effect
        searchField.setText("Search rooms...");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search rooms...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search rooms...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Search Button
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());

        // Filter Combo Boxes
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{
            "All Types", "Standard", "Deluxe", "Suite"
        });
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All Status", "Available", "Occupied", "Maintenance"
        });

        // Search and Filter Layout
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);

        searchPanel.add(filterPanel, BorderLayout.CENTER);

        return searchPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // Buttons
        JButton addRoomBtn = new JButton("Add Room");
        JButton editRoomBtn = new JButton("Edit Room");
        JButton deleteRoomBtn = new JButton("Delete Room");
        JButton reportBtn = new JButton("Room Report");

        // Add Interaction Listeners
        addRoomBtn.addActionListener(e -> showRoomDialog(false));
        editRoomBtn.addActionListener(e -> showRoomDialog(true));
        deleteRoomBtn.addActionListener(e -> deleteSelectedRoom());
        reportBtn.addActionListener(e -> generateRoomReport());

        buttonPanel.add(addRoomBtn);
        buttonPanel.add(editRoomBtn);
        buttonPanel.add(deleteRoomBtn);
        buttonPanel.add(reportBtn);

        return buttonPanel;
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.equals("search rooms...")) {
            searchText = "";
        }
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        roomsTable.setRowSorter(sorter);
        
        // Basic search across all columns
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
    }

    private void showRoomDialog(boolean isEdit) {
        JDialog dialog = new JDialog();
        dialog.setTitle(isEdit ? "Edit Room" : "Add New Room");
        dialog.setSize(450, 550);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Form Fields
        JTextField roomNumberField = new JTextField();
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
            "Standard", "Deluxe", "Suite", "Executive"
        });
        JTextField capacityField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Available", "Occupied", "Maintenance"
        });
        JTextField amenitiesField = new JTextField();

        addFormField(formPanel, "Room Number:", roomNumberField);
        addFormField(formPanel, "Room Type:", roomTypeCombo);
        addFormField(formPanel, "Capacity:", capacityField);
        addFormField(formPanel, "Price per Night:", priceField);
        addFormField(formPanel, "Status:", statusCombo);
        addFormField(formPanel, "Amenities:", amenitiesField);

        // Pre-populate fields if editing
        if (isEdit) {
            prePopulateEditForm(roomNumberField, roomTypeCombo, capacityField, priceField, statusCombo, amenitiesField);
        }

        JButton confirmButton = new JButton(isEdit ? "Update Room" : "Add Room");
        confirmButton.addActionListener(e -> {
            if (validateRoomInput(roomNumberField, capacityField, priceField)) {
                if (isEdit) {
                    updateRoomDetails(roomNumberField, roomTypeCombo, capacityField, priceField, statusCombo, amenitiesField);
                } else { addNewRoom(roomNumberField, roomTypeCombo, capacityField, priceField, statusCombo, amenitiesField);
                }
                dialog.dispose();
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(confirmButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JComponent field) {
        panel.add(new JLabel(label));
        panel.add(field);
    }

    private void prePopulateEditForm(JTextField roomNumberField, 
                                      JComboBox<String> roomTypeCombo, 
                                      JTextField capacityField, 
                                      JTextField priceField, 
                                      JComboBox<String> statusCombo, 
                                      JTextField amenitiesField) {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a room to edit", 
                "No Room Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pre-fill form with selected room's details
        roomNumberField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        roomTypeCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 1).toString());
        capacityField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        priceField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        statusCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
        amenitiesField.setText(tableModel.getValueAt(selectedRow, 5).toString());
    }

    private boolean validateRoomInput(JTextField roomNumberField, 
                                       JTextField capacityField, 
                                       JTextField priceField) {
        // Room Number Validation
        String roomNumber = roomNumberField.getText().trim();
        if (roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Room Number cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Capacity Validation
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0 || capacity > 10) {
                JOptionPane.showMessageDialog(this, 
                    "Capacity must be between 1 and 10", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Capacity must be a valid number", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Price Validation
        try {
            double price = Double.parseDouble(priceField.getText().trim().replace("$", ""));
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Price must be a positive number", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Price must be a valid number", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void generateRoomReport() {
        // Generate a simple room occupancy report
        int totalRooms = tableModel.getRowCount();
        int availableRooms = 0;
        int occupiedRooms = 0;
        int maintenanceRooms = 0;

        for (int i = 0; i < totalRooms; i++) {
            String status = tableModel.getValueAt(i, 4).toString();
            switch (status) {
                case "Available":
                    availableRooms++;
                    break;
                case "Occupied":
                    occupiedRooms++;
                    break;
                case "Maintenance":
                    maintenanceRooms++;
                    break;
            }
        }

        // Create report dialog
        JDialog reportDialog = new JDialog();
        reportDialog.setTitle("Room Occupancy Report");
        reportDialog.setSize(400, 300);
        reportDialog.setModal(true);
        reportDialog.setLocationRelativeTo(this);

        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));

        // Report Details
        JLabel totalRoomsLabel = new JLabel("Total Rooms: " + totalRooms);
        JLabel availableRoomsLabel = new JLabel("Available Rooms: " + availableRooms);
        JLabel occupiedRoomsLabel = new JLabel("Occupied Rooms: " + occupiedRooms);
        JLabel maintenanceRoomsLabel = new JLabel("Rooms in Maintenance: " + maintenanceRooms);

        // Styling labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        totalRoomsLabel.setFont(labelFont);
        availableRoomsLabel.setFont(labelFont);
        occupiedRoomsLabel.setFont(labelFont);
        maintenanceRoomsLabel.setFont(labelFont);

        // Add to panel
        reportPanel.add(Box.createVerticalStrut(20));
        reportPanel.add(totalRoomsLabel);
        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(availableRoomsLabel);
        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(occupiedRoomsLabel);
        reportPanel.add(Box.createVerticalStrut(10));
        reportPanel.add(maintenanceRoomsLabel);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> reportDialog.dispose());

        reportPanel.add(Box.createVerticalStrut(20));
        reportPanel.add(closeButton);
        reportPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        reportDialog.add(reportPanel);
        reportDialog.setVisible(true);
    }

    private void deleteSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a room to delete", 
                "No Room Selected", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateRoomDetails(JTextField roomNumberField, 
                                    JComboBox<String> roomTypeCombo, 
                                    JTextField capacityField, 
                                    JTextField priceField, 
                                    JComboBox<String> statusCombo, 
                                    JTextField amenitiesField) {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.setValueAt(roomNumberField.getText(), selectedRow, 0);
            tableModel.setValueAt(roomTypeCombo.getSelectedItem(), selectedRow, 1);
            tableModel.setValueAt(capacityField.getText(), selectedRow, 2);
            tableModel.setValueAt(priceField.getText(), selectedRow, 3);
            tableModel.setValueAt(statusCombo.getSelectedItem(), selectedRow, 4);
            tableModel.setValueAt(amenitiesField.getText(), selectedRow, 5);
        }
    }

    private void addNewRoom(JTextField roomNumberField, 
                            JComboBox<String> roomTypeCombo, 
                            JTextField capacityField, 
                            JTextField priceField, 
                            JComboBox<String> statusCombo, 
                            JTextField amenitiesField) {
        Object[] newRoom = {
            roomNumberField.getText(),
            roomTypeCombo.getSelectedItem(),
            capacityField.getText(),
            priceField.getText(),
            statusCombo.getSelectedItem(),
            amenitiesField.getText()
        };
        tableModel.addRow(newRoom);
    }
}