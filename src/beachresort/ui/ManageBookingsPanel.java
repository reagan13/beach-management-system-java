package beachresort.ui;

import beachresort.models.Booking;
import beachresort.models.Room;
import beachresort.repositories.BookingRepository;
import beachresort.repositories.RoomRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ManageBookingsPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private BookingRepository bookingRepository; // Repository for managing bookings
    private RoomRepository roomRepository; // Repository for managing rooms

    public ManageBookingsPanel() {
        bookingRepository = new BookingRepository(); // Initialize the booking repository
        roomRepository = new RoomRepository();
        setLayout(new BorderLayout());

        JLabel bookingsLabel = new JLabel("Manage Bookings", SwingConstants.CENTER);
        bookingsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(bookingsLabel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"Booking ID", "Customer Name", "Room Type", "Check-in Date", "Check-out Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookingsTable = new JTable(tableModel);
        
        // Add scrollpane to table
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons for managing bookings
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Booking");
        JButton editButton = new JButton("Edit Booking");
        JButton deleteButton = new JButton("Delete Booking");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners for buttons
        addButton.addActionListener(this::addBooking);
        editButton.addActionListener(this::editBooking);
        deleteButton.addActionListener(this::deleteBooking);
        refreshButton.addActionListener(this::refreshBookings);

        // Load initial bookings
        loadBookings();
    }

    private void loadBookings() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Fetch bookings from the repository
        List<Booking> bookings = bookingRepository.getAllBookings();
        for (Booking booking : bookings) {
            Object[] rowData = {
                booking.getBookingId(),
                booking.getCustomerName(), // Assuming customer ID is used as the name for simplicity
                booking.getRoomNumber(), // Assuming room number is used as the room type for simplicity
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    
    private void addBooking(ActionEvent e) {
        JDialog addBookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Booking", true);
        addBookingDialog.setSize(400, 500);
        addBookingDialog.setLocationRelativeTo(this);

        // Use GridBagLayout for more control over component placement
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding

        // Row 1: Customer Name
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        panel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField customerNameField = new JTextField();
        panel.add(customerNameField, gbc);

        // Row 2: Room Type
        gbc.gridx = 0; // First column
        gbc.gridy = 1; // Second row
        panel.add(new JLabel("Room Type:"), gbc);

        gbc.gridx = 1; // Second column
        String[] roomTypes = { "Standard", "Deluxe", "Suite" };
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        panel.add(roomTypeCombo, gbc);

        // Row 3: Available Rooms
        gbc.gridx = 0; // First column
        gbc.gridy = 2; // Third row
        panel.add(new JLabel("Available Rooms:"), gbc);

        gbc.gridx = 1; // Second column
        JComboBox<String> availableRoomsCombo = new JComboBox<>();
        panel.add(availableRoomsCombo, gbc);

        // Row 4: Room Capacity
        gbc.gridx = 0; // First column
        gbc.gridy = 3; // Fourth row
        panel.add(new JLabel("Room Capacity:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField capacityField = new JTextField();
        capacityField.setEditable(false); // Make it read-only
        panel.add(capacityField, gbc);

        // Row 5: Room Price
        gbc.gridx = 0; // First column
        gbc.gridy = 4; // Fifth row
        panel.add(new JLabel("Room Price:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField priceField = new JTextField();
        priceField.setEditable(false); // Make it read-only
        panel.add(priceField, gbc);

        // Add an action listener to update available rooms when room type changes
        roomTypeCombo.addActionListener(actionEvent -> {
            String selectedRoomType = (String) roomTypeCombo.getSelectedItem();
            updateAvailableRooms(selectedRoomType, availableRoomsCombo);
        });

        // Add an action listener to update capacity and price when a room is selected
        availableRoomsCombo.addActionListener(actionEvent -> {
            String selectedRoomNumber = (String) availableRoomsCombo.getSelectedItem();
            if (selectedRoomNumber != null) {
                Room selectedRoom = roomRepository.getRoomByNumber(selectedRoomNumber); // Fetch room details
                if (selectedRoom != null) {
                    capacityField.setText(String.valueOf(selectedRoom.getCapacity()));
                    priceField.setText(String.valueOf(selectedRoom.getPricePerNight()));
                }
            }
        });

        // Row 6: Check-in Date
        gbc.gridx = 0; // First column
        gbc.gridy = 5; // Sixth row
        panel.add(new JLabel("Check-in Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1; // Second column
        JTextField checkInField = new JTextField();
        panel.add(checkInField, gbc);

        // Row 7: Check-out Date
        gbc.gridx = 0; // First column
        gbc.gridy = 6; // Seventh row
        panel.add(new JLabel("Check-out Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1; // Second column
        JTextField checkOutField = new JTextField();
        panel.add(checkOutField, gbc);

        // Row 8: Status
        gbc.gridx = 0; // First column
        gbc.gridy = 7; // Eighth row
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1; // Second column
        String[] statuses = { "Confirmed", "Pending", "Cancelled" };
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        panel.add(statusCombo, gbc);

        // Row 9: Save Button
        gbc.gridx = 0; // First column
        gbc.gridy = 8; // Ninth row
        gbc.gridwidth = 2; // Span both columns
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
             // Check if there are available rooms
            if (availableRoomsCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(addBookingDialog, "No available rooms to book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return; // Prevent booking
            }
        
            // Get selected room number and date range
            // Get selected room number and date range
            String selectedRoomNumber = (String) availableRoomsCombo.getSelectedItem();
            LocalDate newStartDate;
            LocalDate newEndDate;
            
            try {
                newStartDate = LocalDate.parse(checkInField.getText());
                newEndDate = LocalDate.parse(checkOutField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addBookingDialog,
                        "Invalid date format . Please use the format yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Prevent booking
            }
            // Check for overlapping bookings
            if (checkForOverlappingBookings(selectedRoomNumber, newStartDate, newEndDate)) {
                JOptionPane.showMessageDialog(addBookingDialog, "There is already a booking for this room on the selected dates.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Prevent booking
            }
        
            try {
                // Create a new booking object
                Booking newBooking = new Booking(
                        
                        (String) availableRoomsCombo.getSelectedItem(), // Selected room from available rooms
                        customerNameField.getText(),
                        LocalDate.parse(checkInField.getText()),
                            LocalDate.parse(checkOutField.getText()),
                        1, // Placeholder for number of guests
                        Double.parseDouble(priceField.getText()), // Use the price from the price field
                        statusCombo.getSelectedItem().toString() // Status
                );

                // Save the booking using the repository
                bookingRepository.addBooking(newBooking, "User    "); // Pass the user who performed the action
                loadBookings(); // Refresh the table to show the new booking

                JOptionPane.showMessageDialog(addBookingDialog, "Booking Added Successfully!");
                addBookingDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addBookingDialog, "Error adding booking: " + ex.getMessage());
            }
        });
        panel.add(saveButton, gbc);

        // Row 10: Cancel Button
        gbc.gridy = 9; // Tenth row
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> addBookingDialog.dispose());
        panel.add(cancelButton, gbc);

        addBookingDialog.add(panel);
        addBookingDialog.setVisible(true);
    }

    private void updateAvailableRooms(String roomType, JComboBox<String> availableRoomsCombo) {
        // Clear existing items
        availableRoomsCombo.removeAllItems();

        // Fetch available rooms from the RoomRepository based on the selected room type
        List<String> availableRooms = roomRepository.getAvailableRoomsByType(roomType); // Implement this method in RoomRepository

        // Add available rooms to the combo box
        if (availableRooms.isEmpty()) {
            availableRoomsCombo.addItem("No available rooms");
        } else {
            for (String room : availableRooms) {
                availableRoomsCombo.addItem(room);
            }
        }
    }
    private void editBooking(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to edit");
            return;
        }

        // Fetch the selected booking details
        int bookingId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Booking booking = bookingRepository.getBookingById(bookingId);

        JDialog editBookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Booking", true);
        editBookingDialog.setSize(400, 300);
        editBookingDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Customer Name:"));
        JTextField customerNameField = new JTextField(booking.getCustomerName());
        panel.add(customerNameField);

        panel.add(new JLabel("Room Type:"));
        String[] roomTypes = {"Standard", "Deluxe", "Suite"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        roomTypeCombo.setSelectedItem(booking.getRoomNumber());
        panel.add(roomTypeCombo);

        panel.add(new JLabel("Check-in Date (yyyy-MM-dd):"));
        JTextField checkInField = new JTextField(booking.getCheckInDate().toString());
        panel.add(checkInField);

        panel.add(new JLabel("Check-out Date (yyyy-MM-dd):"));
        JTextField checkOutField = new JTextField(booking.getCheckOutDate().toString());
        panel.add(checkOutField);

        panel.add(new JLabel("Status:"));
        String[] statuses = {"Confirmed", "Pending", "Cancelled"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(booking.getStatus());
        panel.add(statusCombo);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            try {
                // Update the booking object
                booking.setCustomerName(customerNameField.getText());
                booking.setRoomNumber(roomTypeCombo.getSelectedItem().toString());
                booking.setCheckInDate(LocalDate.parse(checkInField.getText()));
                booking.setCheckOutDate(LocalDate.parse(checkOutField.getText()));
                booking.setStatus(statusCombo.getSelectedItem().toString());

                // Save the updated booking using the repository
                bookingRepository.updateBooking(booking, "User "); // Pass the user who performed the action
                loadBookings(); // Refresh the table to show the updated booking

                JOptionPane.showMessageDialog(editBookingDialog, "Booking Updated Successfully!");
                editBookingDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editBookingDialog, "Error updating booking: " + ex.getMessage());
            }
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> editBookingDialog.dispose());
        panel.add(cancelButton);

        editBookingDialog.add(panel);
        editBookingDialog.setVisible(true);
    }

    private void deleteBooking(ActionEvent e) {
 int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete");
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this booking?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            bookingRepository.deleteBooking(bookingId, "User  "); // Pass the user who performed the action
            loadBookings(); // Refresh the table to reflect the deletion
            JOptionPane.showMessageDialog(this, "Booking deleted successfully");
        }
    }

    private void refreshBookings(ActionEvent e) {
        loadBookings(); // Reload bookings from the repository
        JOptionPane.showMessageDialog(this, "Bookings refreshed successfully.");
    }

    private boolean checkForOverlappingBookings(String roomNumber, LocalDate newStartDate, LocalDate newEndDate) {
        List<Booking> existingBookings = getExistingBookingsForRoom(roomNumber);

        for (Booking booking : existingBookings) {
            LocalDate existingStartDate = booking.getCheckInDate(); // Use getCheckInDate()
            LocalDate existingEndDate = booking.getCheckOutDate(); // Use getCheckOutDate()

            // Check if the new booking overlaps with the existing booking
            if ((newStartDate.isEqual(existingStartDate) || newStartDate.isAfter(existingStartDate)) &&
                    (newStartDate.isBefore(existingEndDate) || newStartDate.isEqual(existingEndDate))) {
                return true; // Overlap found
            }

            if ((newEndDate.isEqual(existingStartDate) || newEndDate.isBefore(existingEndDate)) &&
                    (newEndDate.isAfter(existingStartDate) || newEndDate.isEqual(existingStartDate))) {
                return true; // Overlap found
            }

            // Check if the new booking completely encompasses the existing booking
            if (newStartDate.isBefore(existingStartDate) && newEndDate.isAfter(existingEndDate)) {
                return true; // Overlap found
            }
        }

        return false; // No overlaps found
    }

    public List<Booking> getExistingBookingsForRoom(String roomNumber) {
        List<Booking> existingBookings = new ArrayList<>();
        List<Booking> allBookings = bookingRepository.getAllBookings(); // Retrieve all bookings

        // Filter bookings for the specified room number
        for (Booking booking : allBookings) {
            if (booking.getRoomNumber().equals(roomNumber)) {
                existingBookings.add(booking);
            }
        }

        return existingBookings; // Return the filtered list of bookings for the specified room
    }
}