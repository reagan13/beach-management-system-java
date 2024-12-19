package beachresort.ui;

import beachresort.models.Room;
import beachresort.repositories.RoomRepository;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoomRepository roomRepository;

    public RoomManagementPanel() {
        roomRepository = new RoomRepository();
        initComponents();
        setupLayout();
        loadRoomsFromDatabase();
    }

    private void initComponents() {
        // Column Names
        String[] columnNames = {
            "Room Number", "Type", "Capacity", 
            "Price", "Status", "Amenities"
        };

        // Create Table Model
        tableModel = new DefaultTableModel(columnNames, 0) {
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

        // Search and Filter Layout
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

    private void loadRoomsFromDatabase() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch rooms from repository
        List<Room> rooms = roomRepository.getAllRooms();

        // Populate table
        for (Room room : rooms) {
            Object[] rowData = {
                room.getRoomNumber(),
                room.getRoomType(),
                room.getCapacity(),
                String.format("₱%.2f", room.getPricePerNight()),
                room.getStatus(),
                room.getAmenities()
            };
            tableModel.addRow(rowData);
        }
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
        roomNumberField.setEditable(!isEdit); // Disable editing for existing rooms

        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
            "Standard", "Deluxe", "Suite", "Executive"
        });

        JTextField capacityField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Available", "Occupied", "Maintenance"
        });
        JTextField amenitiesField = new JTextField();

        // Add form fields
        addFormField(formPanel, "Room Number:", roomNumberField);
        addFormField(formPanel, "Room Type:", roomTypeCombo);
        addFormField(formPanel, "Capacity:", capacityField);
        addFormField(formPanel, "Price per Night:", priceField);
        addFormField(formPanel, "Status:", statusCombo);
        addFormField(formPanel, "Amenities:", amenitiesField);

        // Pre-populate fields if editing
        if (isEdit) {
            prePopulateEditForm(roomNumberField, roomTypeCombo, capacityField, 
                                priceField, statusCombo, amenitiesField);
        }

        JButton confirmButton = new JButton(isEdit ? "Update Room" : "Add Room");
        confirmButton.addActionListener(e -> {
            if (validateRoomInput(roomNumberField, capacityField, priceField)) {
                if (isEdit) {
                    updateRoomDetails(roomNumberField, roomTypeCombo, capacityField, 
                                      priceField, statusCombo, amenitiesField);
                } else {
                    addNewRoom(roomNumberField, roomTypeCombo, capacityField, 
                               priceField, statusCombo, amenitiesField);
                }
                dialog.dispose();
                loadRoomsFromDatabase(); // Refresh table
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

        // Get room details from selected row
        String roomNumber = tableModel.getValueAt(selectedRow, 0).toString();
        Room room = roomRepository.getRoomByNumber(roomNumber);

        if (room != null) {
            roomNumberField.setText(room.getRoomNumber());
            roomTypeCombo.setSelectedItem(room.getRoomType());
            capacityField.setText(String.valueOf(room.getCapacity()));
            priceField.setText(String.format("%.2f", room.getPricePerNight()));
            statusCombo.setSelectedItem(room.getStatus());
            amenitiesField.setText(room.getAmenities());
        }
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
            double price = Double.parseDouble(priceField.getText().trim().replace("₱", ""));
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

    private void updateRoomDetails(JTextField roomNumberField, 
                                    JComboBox<String> roomTypeCombo, 
                                    JTextField capacityField, 
                                    JTextField priceField, 
                                    JComboBox<String> statusCombo, 
                                    JTextField amenitiesField) {
        // Create Room object from form inputs
        Room updatedRoom = new Room(
            roomNumberField.getText(),
            roomTypeCombo.getSelectedItem().toString(),
            Integer.parseInt(capacityField.getText()),
            Double.parseDouble(priceField.getText().replace("₱", "")),
            statusCombo.getSelectedItem().toString(),
            amenitiesField.getText()
        );

        // Update room in database
        if (roomRepository.updateRoom(updatedRoom)) {
            JOptionPane.showMessageDialog(this, 
                "Room updated successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update room", 
                "Error", 
                                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewRoom(JTextField roomNumberField, 
                             JComboBox<String> roomTypeCombo, 
                             JTextField capacityField, 
                             JTextField priceField, 
                             JComboBox<String> statusCombo, 
                             JTextField amenitiesField) {
        // Create Room object from form inputs
        Room newRoom = new Room(
            roomNumberField.getText(),
            roomTypeCombo.getSelectedItem().toString(),
            Integer.parseInt(capacityField.getText()),
            Double.parseDouble(priceField.getText().replace("₱", "")),
            statusCombo.getSelectedItem().toString(),
            amenitiesField.getText()
        );

        // Add room to database
        if (roomRepository.addRoom(newRoom)) {
            JOptionPane.showMessageDialog(this, 
                "Room added successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to add room", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get room number from selected row
            String roomNumber = tableModel.getValueAt(selectedRow, 0).toString();
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to delete room " + roomNumber + "?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Delete room from database
                if (roomRepository.deleteRoom(roomNumber)) {
                    // Remove from table
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, 
                        "Room deleted successfully", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete room", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a room to delete", 
                "No Room Selected", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void generateRoomReport() {
        // Fetch all rooms from database
        List<Room> rooms = roomRepository.getAllRooms();

        // Calculate room statistics
        int totalRooms = rooms.size();
        int availableRooms = 0;
        int occupiedRooms = 0;
        int maintenanceRooms = 0;
        double totalRoomValue = 0;

        for (Room room : rooms) {
            switch (room.getStatus()) {
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
            totalRoomValue += room.getPricePerNight();
        }

        // Create report dialog
        JDialog reportDialog = new JDialog();
        reportDialog.setTitle("Comprehensive Room Report");
        reportDialog.setSize(400, 350);
        reportDialog.setModal(true);
        reportDialog.setLocationRelativeTo(this);

        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create report labels
        JLabel[] reportLabels = {
            new JLabel(String.format("Total Rooms: %d", totalRooms)),
            new JLabel(String.format("Available Rooms: %d", availableRooms)),
            new JLabel(String.format("Occupied Rooms: %d", occupiedRooms)),
            new JLabel(String.format("Rooms in Maintenance: %d", maintenanceRooms)),
            new JLabel(String.format("Average Room Price: ₱%.2f", totalRoomValue / totalRooms))
        };

        // Style labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        for (JLabel label : reportLabels) {
            label.setFont(labelFont);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            reportPanel.add(label);
            reportPanel.add(Box.createVerticalStrut(10));
        }

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> reportDialog.dispose());

        reportPanel.add(closeButton);

        reportDialog.add(reportPanel);
        reportDialog.setVisible(true);
    }
}