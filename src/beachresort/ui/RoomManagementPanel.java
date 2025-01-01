package beachresort.ui;

import beachresort.models.Room;
import beachresort.models.RoomAuditLog;
import beachresort.repositories.RoomRepository;
import beachresort.repositories.RoomAuditLogRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private RoomRepository roomRepository;
    private RoomAuditLogRepository auditLogRepository;


    public RoomManagementPanel() {
        // Initialize repository
        roomRepository = new RoomRepository();
        auditLogRepository = new RoomAuditLogRepository();

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
        JButton auditLogButton = new JButton("View Audit Logs");

        
        buttonPanel.add(addRoomButton);
        buttonPanel.add(editRoomButton);
        buttonPanel.add(deleteRoomButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(auditLogButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add Action Listeners
        addRoomButton.addActionListener(this::addRoom);
        editRoomButton.addActionListener(this::editRoom);
        deleteRoomButton.addActionListener(this::deleteRoom);
        refreshButton.addActionListener(e -> loadRooms());
        auditLogButton.addActionListener(this::showAuditLogs);

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
                    // Create Audit Log
                    RoomAuditLog auditLog = new RoomAuditLog(
                        newRoom.getRoomNumber(),
                        "ADD",
                        "N/A",
                        newRoom.toString(),
                        "Owner"
                    );
                    auditLogRepository.logRoomAction(auditLog);

                    loadRooms(); // Refresh table
                    JOptionPane.showMessageDialog(addRoomDialog, "Room Added Successfully!");
                    addRoomDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addRoomDialog,
                            "Failed to add room. Room number might already exist.");
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
                    // Create Audit Log
                    RoomAuditLog auditLog = new RoomAuditLog(
                        updatedRoom.getRoomNumber(),
                        "EDIT",
                        existingRoom.toString(),
                        updatedRoom.toString(),
                        "Owner"
                    );
                    auditLogRepository.logRoomAction(auditLog);

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
        String roomNumber = (String) tableModel.getValueAt(selectedRow, 0);

        
        // Fetch existing room details before deletion
        Room existingRoom = roomRepository.getRoomByNumber(roomNumber);
        if (existingRoom == null) {
            JOptionPane.showMessageDialog(this, "Room not found");
            return;
        }
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete room " + roomNumber + "?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Attempt to delete from repository
                    boolean deletionSuccessful = roomRepository.deleteRoom(roomNumber);
                
                    if (deletionSuccessful) {
                        // Create comprehensive Audit Log
                        RoomAuditLog auditLog = new RoomAuditLog(
                            roomNumber,
                            "DELETE",
                            existingRoom.toString(),
                            "Room Permanently Removed",
                            "Owner"
                        );
                        auditLogRepository.logRoomAction(auditLog);

                        // Refresh the room list
                        loadRooms();

                        // Show success message
                        JOptionPane.showMessageDialog(
                            this, 
                            "Room " + roomNumber + " has been successfully deleted.", 
                            "Deletion Successful", 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        // Deletion failed
                        JOptionPane.showMessageDialog(
                            this, 
                            "Failed to delete room. Please try again or contact support.", 
                            "Deletion Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception deleteException) {
                    // Log any unexpected errors
                    System.err.println("Unexpected error during room deletion: " + deleteException.getMessage());
                    JOptionPane.showMessageDialog(
                        this, 
                        "An unexpected error occurred while deleting the room.\n" +
                        "Error: " + deleteException.getMessage(), 
                        "Deletion Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
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

    private void showAuditLogs(ActionEvent e) {
    // Create a dialog to display audit logs
    JDialog auditLogDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Room Management Audit Logs", true);
    auditLogDialog.setSize(600, 400);
    auditLogDialog.setLocationRelativeTo(this);

    // Create table model for audit logs
    String[] columnNames = {"Room Number", "Action", "Timestamp", "Performed By"};
    DefaultTableModel auditTableModel = new DefaultTableModel(columnNames, 0);

    // Fetch audit logs
    List<RoomAuditLog> auditLogs = auditLogRepository.getAllAuditLogs();
    
    // Populate table model
    for (RoomAuditLog log : auditLogs) {
        Object[] rowData = {
            log.getRoomNumber(),
            log.getActionType(),
            log.getActionTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            log.getPerformedBy()
        };
        auditTableModel.addRow(rowData);
    }

    // Create table
    JTable auditLogTable = new JTable(auditTableModel);
    auditLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single selection
    JScrollPane scrollPane = new JScrollPane(auditLogTable);

    // Add details button
    JButton detailsButton = new JButton("View Log Details");
    detailsButton.addActionListener(detailEvent -> {
        int selectedRow = auditLogTable.getSelectedRow();
        if (selectedRow != -1) {
            RoomAuditLog selectedLog = auditLogs.get(selectedRow);
            String logDetails = String.format(
                "Room Number: %s\nAction: %s\nTimestamp: %s\nPerformed By: %s\nOld Details: %s\nNew Details: %s",
                selectedLog.getRoomNumber(),
                selectedLog.getActionType(),
                selectedLog.getActionTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                selectedLog.getPerformedBy(),
                selectedLog.getOldDetails() != null ? selectedLog.getOldDetails() : "N/A",
                selectedLog.getNewDetails() != null ? selectedLog.getNewDetails() : "N/A"
            );
            JOptionPane.showMessageDialog(
                auditLogDialog, 
                logDetails, 
                "Room Audit Log Details", 
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(auditLogDialog, "Please select a log entry to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    });

    // Layout
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(detailsButton, BorderLayout.SOUTH);

    auditLogDialog.add(mainPanel);
    auditLogDialog.setVisible(true);
}
}