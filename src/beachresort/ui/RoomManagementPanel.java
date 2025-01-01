package beachresort.ui;

import beachresort.models.Room;
import beachresort.repositories.RoomRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private RoomRepository roomRepository;

    public RoomManagementPanel() {
        // Initialize repository
        roomRepository = new RoomRepository();

        // Set layout
        setLayout(new BorderLayout());

        // Title
        JLabel roomLabel = new JLabel("Room Management", SwingConstants.CENTER);
        roomLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(roomLabel, BorderLayout.NORTH);

        // Table Model Setup
        String[] columnNames = {"Room Number", "Room Type", "Capacity", "Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        roomsTable = new JTable(tableModel);
        
        // Configure table selection
        roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addRoomButton = new JButton("Add Room");
        JButton editRoomButton = new JButton("Edit Room");
        JButton deleteRoomButton = new JButton("Delete Room");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addRoomButton);
        buttonPanel.add(editRoomButton);
        buttonPanel.add(deleteRoomButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add Action Listeners
        addRoomButton.addActionListener(this::addRoom);
        editRoomButton.addActionListener(this::editRoom);
        deleteRoomButton.addActionListener(this::deleteRoom);
        refreshButton.addActionListener(e -> loadRooms());

        // Initial load of rooms
        loadRooms();
    }

    private void loadRooms() {
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
                String.format("$%.2f", room.getPricePerNight()),
                room.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void addRoom(ActionEvent e) {
        JDialog addRoomDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New Room", true);
        addRoomDialog.setSize(400, 300);
        addRoomDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Fields
        JTextField roomNumberField = createLabeledTextField(panel, "Room Number:");
        JComboBox<String> roomTypeCombo = createRoomTypeComboBox(panel, "Room Type:");
        JTextField capacityField = createLabeledTextField(panel, "Capacity:");
        JTextField priceField = createLabeledTextField(panel, "Price per Night:");
        JComboBox<String> statusCombo = createStatusComboBox(panel, "Status:");

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            // Validate inputs
            if (!validateInputs(roomNumberField, capacityField, priceField)) {
                return;
            }

            try {
                // Create Room object
                Room newRoom = new Room(
                    roomNumberField.getText(),
                    (String)roomTypeCombo.getSelectedItem(),
                    Integer.parseInt(capacityField.getText()),
                    Double.parseDouble(priceField.getText()),
                    (String)statusCombo.getSelectedItem()
                );

                // Add to repository
                if (roomRepository.addRoom(newRoom)) {
                    loadRooms(); // Refresh table
                    JOptionPane.showMessageDialog(addRoomDialog, "Room Added Successfully!");
                    addRoomDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addRoomDialog, "Failed to add room. Room number might already exist.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addRoomDialog, "Invalid numeric input for capacity or price");
            }
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> addRoomDialog.dispose());
        panel.add(cancelButton);

        addRoomDialog.add(panel);
        addRoomDialog.setVisible(true);
    }

   // Enhanced edit room method
   private void editRoom(ActionEvent e) {
       // Get selected row
       int selectedRow = roomsTable.getSelectedRow();
       if (selectedRow == -1) {
           JOptionPane.showMessageDialog(this, "Please select a room to edit");
           return;
       }

       // Get room number of selected row
       String roomNumber = (String) tableModel.getValueAt(selectedRow, 0);

       // Fetch room from repository
       Room existingRoom = roomRepository.getRoomByNumber(roomNumber);
       if (existingRoom == null) {
           JOptionPane.showMessageDialog(this, "Room not found");
           return;
       }

       // Create edit dialog
       JDialog editRoomDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Room", true);
       editRoomDialog.setSize(400, 300);
       editRoomDialog.setLocationRelativeTo(this);

       JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
       panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

       // Prepare input fields
       JTextField roomNumberField = createLabeledTextField(panel, "Room Number:");
       roomNumberField.setText(existingRoom.getRoomNumber());
       roomNumberField.setEditable(false); // Room number cannot be changed

       JComboBox<String> roomTypeCombo = createRoomTypeComboBox(panel, "Room Type:");
       roomTypeCombo.setSelectedItem(existingRoom.getRoomType());

       JTextField capacityField = createLabeledTextField(panel, "Capacity:");
       capacityField.setText(String.valueOf(existingRoom.getCapacity()));

       JTextField priceField = createLabeledTextField(panel, "Price per Night:");
       priceField.setText(String.valueOf(existingRoom.getPricePerNight()));

       JComboBox<String> statusCombo = createStatusComboBox(panel, "Status:");
       statusCombo.setSelectedItem(existingRoom.getStatus());

       // Save button
       JButton saveButton = new JButton("Save Changes");
       saveButton.addActionListener(saveEvent -> {
           // Validate inputs
           if (!validateInputs(roomNumberField, capacityField, priceField)) {
               return;
           }

           try {
               // Create updated Room object
               Room updatedRoom = new Room(
                       roomNumberField.getText(),
                       (String) roomTypeCombo.getSelectedItem(),
                       Integer.parseInt(capacityField.getText()),
                       Double.parseDouble(priceField.getText()),
                       (String) statusCombo.getSelectedItem());

               // Update in repository
               if (roomRepository.updateRoom(updatedRoom)) {
                   loadRooms(); // Refresh table
                   JOptionPane.showMessageDialog(editRoomDialog, "Room Updated Successfully!");
                   editRoomDialog.dispose();
               } else {
                   JOptionPane.showMessageDialog(editRoomDialog, "Failed to update room");
               }
           } catch (NumberFormatException ex) {
               JOptionPane.showMessageDialog(editRoomDialog, "Invalid numeric input");
           }
       });
       panel.add(saveButton);

       // Cancel button
       JButton cancelButton = new JButton("Cancel");
       cancelButton.addActionListener(cancelEvent -> editRoomDialog.dispose());
       panel.add(cancelButton);

       editRoomDialog.add(panel);
       editRoomDialog.setVisible(true);
   }

    
    private void deleteRoom(ActionEvent e) {
        // Get selected row
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete");
            return;
        }

        // Get room number of selected row
        String roomNumber = (String)tableModel.getValueAt(selectedRow, 0);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete room " + roomNumber + "?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete from repository
            if (roomRepository.deleteRoom(roomNumber)) {
                loadRooms(); // Refresh table
                JOptionPane.showMessageDialog(this, "Room deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room");
            }
        }
    }

    // Utility methods
    private JTextField createLabeledTextField(JPanel panel, String label) {
        panel.add(new JLabel(label));
        JTextField textField = new JTextField();
        panel.add(textField);
        return textField;
    }

        private JComboBox<String> createRoomTypeComboBox(JPanel panel, String label) {
        panel.add(new JLabel(label));
        String[] roomTypes = {"Standard", "Deluxe", "Suite", "Family"};
        JComboBox<String> comboBox = new JComboBox<>(roomTypes);
        panel.add(comboBox);
        return comboBox;
    }

    private JComboBox<String> createStatusComboBox(JPanel panel, String label) {
        panel.add(new JLabel(label));
        String[] statuses = {"Available", "Occupied", "Maintenance"};
        JComboBox<String> comboBox = new JComboBox<>(statuses);
        panel.add(comboBox);
        return comboBox;
    }

    private boolean validateInputs(JTextField roomNumberField, 
                                   JTextField capacityField, 
                                   JTextField priceField) {
        // Room Number validation
        if (roomNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room Number cannot be empty");
            return false;
        }

        // Capacity validation
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "Capacity must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid capacity input");
            return false;
        }

        // Price validation
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price input");
            return false;
        }

        return true;
    }

   
}