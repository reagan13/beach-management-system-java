package beachresort.ui;

import beachresort.models.Booking;
import beachresort.models.CheckInOut;
import beachresort.models.Room;
import beachresort.repositories.BookingRepository;
import beachresort.repositories.CheckInOutRepository;
import beachresort.repositories.RoomRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckInOutPanel extends JPanel {
    private JTable checkInOutTable;
    private DefaultTableModel tableModel;
    private BookingRepository bookingRepository; // Repository for managing bookings
    private RoomRepository roomRepository; // Repository for managing rooms
    private CheckInOutRepository checkInOutRepository; // Repository for managing check-ins/outs

    public CheckInOutPanel() {
        setLayout(new BorderLayout());

        // Initialize repositories
        bookingRepository = new BookingRepository();
        roomRepository = new RoomRepository();
        checkInOutRepository = new CheckInOutRepository();

        // Title
        JLabel titleLabel = new JLabel("Check In / Checkout", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
String[] columnNames = {"Check In ID", "User ID", "Customer Name", "Check In Date", "Check Out Date", "Room Number", "Check In Type", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        checkInOutTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(checkInOutTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton checkInByWalkIn = new JButton("Check In by Walk-In");
        JButton checkInByBooking = new JButton("Check In by Booking");
        JButton checkOutButton = new JButton("Check Out");

        // Add action listeners for buttons
        checkInByWalkIn.addActionListener(this::showCheckInDialog);
        checkInByBooking.addActionListener(this::showCheckInByBookingDialog);
        checkOutButton.addActionListener(this::checkOutBooking);

        buttonPanel.add(checkInByWalkIn);
        buttonPanel.add(checkInByBooking);
        buttonPanel.add(checkOutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial check-in/out records
        loadCheckInOuts();
    }

    private void loadCheckInOuts() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch check-in/out records from the repository
        List<CheckInOut> checkInOuts = checkInOutRepository.getAllCheckInOuts();
        for (CheckInOut checkInOut : checkInOuts) {
            Object[] rowData = {
                    checkInOut.getId(),
                    checkInOut.getUserId(),
                    checkInOut.getCustomerName(),
                    checkInOut.getCheckInDate(),
                    checkInOut.getCheckOutDate(),
                    checkInOut.getRoomNumber(),
                    checkInOut.getCheckInType(),
                    checkInOut.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }
    
    
    
    private void showCheckInDialog(ActionEvent e) {
        JDialog addBookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Booking", true);
        addBookingDialog.setSize(400, 500);
        addBookingDialog.setLocationRelativeTo(this);

        // Use GridBagLayout for more control over component placement
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding

        // Row 0: User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("User ID:"), gbc);

        gbc.gridx = 1;
        JTextField userIdField = new JTextField();
      
        panel.add(userIdField, gbc);

        // Row 1: Customer Name
        gbc.gridx = 0; // First column
        gbc.gridy = 1; // First row
        panel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField customerNameField = new JTextField();
        panel.add(customerNameField, gbc);

        // Row 2: Room Type
        gbc.gridx = 0; // First column
        gbc.gridy = 2; // Second row
        panel.add(new JLabel("Room Type:"), gbc);

        gbc.gridx = 1; // Second column
        String[] roomTypes = { "Standard", "Deluxe", "Suite" };
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        panel.add(roomTypeCombo, gbc);

        // Row 3: Available Rooms
        gbc.gridx = 0; // First column
        gbc.gridy = 3; // Third row
        panel.add(new JLabel("Available Rooms:"), gbc);

        gbc.gridx = 1; // Second column
        JComboBox<String> availableRoomsCombo = new JComboBox<>();
        panel.add(availableRoomsCombo, gbc);

        // Row 4: Room Capacity
        gbc.gridx = 0; // First column
        gbc.gridy = 4; // Fourth row
        panel.add(new JLabel("Room Capacity:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField capacityField = new JTextField();
        capacityField.setEditable(false); // Make it read-only
        panel.add(capacityField, gbc);

        // Row 5: Room Price
        gbc.gridx = 0; // First column
        gbc.gridy = 5; // Fifth row
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
        gbc.gridy = 6; // Sixth row
        panel.add(new JLabel("Check-in Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1; // Second column
        LocalDate today = LocalDate.now();
        panel.add(new JLabel(today.toString()), gbc);

        // Row 7: Check-out Date
        gbc.gridx = 0; // First column
        gbc.gridy = 7; // Seventh row
        panel.add(new JLabel("Check-out Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1; // Second column
        LocalDate tomorrow = today.plusDays(1);
        panel.add(new JLabel(tomorrow.toString()), gbc);

        // Row 8: Status
        gbc.gridx = 0; // First column
        gbc.gridy = 8; // Eighth row
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1; // Second column
        String[] statuses = { "Confirmed" };
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        panel.add(statusCombo, gbc);

        // Row 9: Save Button
        gbc.gridx = 0; // First column
        gbc.gridy = 9; // Ninth row
        gbc.gridwidth = 2; // Span both columns
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            // Check if there are available rooms
            if (availableRoomsCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(addBookingDialog, "No available rooms to book.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return; // Prevent booking
            }


            String selectedRoomNumber = (String) availableRoomsCombo.getSelectedItem();

            // Check for overlapping bookings
            if (checkForOverlappingBookings(selectedRoomNumber, today, tomorrow)) {
                JOptionPane.showMessageDialog(addBookingDialog,
                        "There is already a booking for this room on the selected dates.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return; // Prevent booking
            }


            // User ID validation
    int userId;
    try {
        userId = Integer.parseInt(userIdField.getText());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(addBookingDialog, 
            "Invalid User ID. Please enter a valid number.", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validate if user is a customer
            if (!checkInOutRepository.isValidCustomerUser(userId)) {
                JOptionPane.showMessageDialog(addBookingDialog,
                        "Invalid User ID or User is not a Customer",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                // Create a new booking object

                Booking newBooking = new Booking(
                      Integer.parseInt(userIdField.getText()),
                        (String) availableRoomsCombo.getSelectedItem(), // Selected room from available rooms
                        customerNameField.getText(),
                        today,
                        tomorrow,
                        1, // Placeholder for number of guests
                        Double.parseDouble(priceField.getText()), // Use the price from the price field
                        statusCombo.getSelectedItem().toString() // Status
                );
                
                CheckInOut checkInOut = new CheckInOut(
                        Integer.parseInt(userIdField.getText()), // Add user ID
                        customerNameField.getText(),
                        today,
                        tomorrow,
                        (String) availableRoomsCombo.getSelectedItem(),
                        "Walk-In",
                        "Checked In"
                    );

                checkInOutRepository.addCheckInOut(checkInOut); // Save the check-in record

                // Save the booking using the repository
                boolean isSuccess = bookingRepository.addBooking(newBooking, "User    "); // Pass the user who performed the action
                if (isSuccess) {
                    roomRepository.updateRoomStatusBasedOnCurrent((String) availableRoomsCombo.getSelectedItem(),
                            statusCombo.getSelectedItem().toString()); // Update room status to Booked
                    JOptionPane.showMessageDialog(addBookingDialog, "Booking Added Successfully!");
                    addBookingDialog.dispose();
                }
                loadCheckInOuts(); // Refresh the table to show the new booking

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addBookingDialog, "Error adding booking: " + ex.getMessage());
            }
        });
        panel.add(saveButton, gbc);

        // Row 10: Cancel Button
        gbc.gridy = 10; // Tenth row
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> addBookingDialog.dispose());
        panel.add(cancelButton, gbc);

        addBookingDialog.add(panel);
        addBookingDialog.setVisible(true);
    }

    private void showCheckInByBookingDialog(ActionEvent e) {
        JDialog checkInByBookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Check In by Booking", true);
        checkInByBookingDialog.setSize(400, 500);
        checkInByBookingDialog.setLocationRelativeTo(this);

        // Use GridBagLayout for better control over component placement
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding

        // Row 1: Select Booking ID
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        gbc.weightx = 1.0;
        panel.add(new JLabel("Select Booking ID:"), gbc);

        gbc.gridx = 1; // Second column
        JComboBox<Integer> bookingIDComboBox = new JComboBox<>(
                bookingRepository.getAllBookingIDs().toArray(new Integer[0]));
        panel.add(bookingIDComboBox, gbc);

        // Row 2: Room Number
        gbc.gridx = 0; // First column
        gbc.gridy = 2; // Second row
        panel.add(new JLabel("Room Number:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField roomNumberField = new JTextField();
        roomNumberField.setEditable(false); // Make it read-only
        panel.add(roomNumberField, gbc);

        // Row 3: Customer Name
        gbc.gridx = 0; // First column
        gbc.gridy = 3; // Third row
        panel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField customerNameField = new JTextField();
        customerNameField.setEditable(false); // Make it read-only
        panel.add(customerNameField, gbc);

        // Row 4: Check-in Date
        gbc.gridx = 0; // First column
        gbc.gridy = 4; // Fourth row
        panel.add(new JLabel("Check-in Date:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField checkInDateField = new JTextField();
        checkInDateField.setEditable(false); // Make it read-only
        panel.add(checkInDateField, gbc);

        // Row 5: Check-out Date
        gbc.gridx = 0; // First column
        gbc.gridy = 5; // Fifth row
        panel.add(new JLabel("Check-out Date:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField checkOutDateField = new JTextField();
        checkOutDateField.setEditable(false); // Make it read-only
        panel.add(checkOutDateField, gbc);

        
        // Row 6: Number of Guests
        gbc.gridx = 0; // First column
        gbc.gridy = 6; // Sixth row
        panel.add(new JLabel("Number of Guests:"), gbc);

    
        gbc.gridx = 1; // Second column
        JTextField numberOfGuestsField = new JTextField();
        numberOfGuestsField.setEditable(false); // Make it read-only
        panel.add(numberOfGuestsField, gbc);

        // Row 7: User ID
            gbc.gridx = 0;
            gbc.gridy = 7;
            panel.add(new JLabel("User ID:"), gbc);

            gbc.gridx = 1;
            JTextField userIdField = new JTextField();
              userIdField.setEditable(false);
            panel.add(userIdField, gbc);


        // Row 8: Total Price
        gbc.gridx = 0; // First column
        gbc.gridy = 8; // Seventh row
        panel.add(new JLabel("Total Price:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField totalPriceField = new JTextField();
        totalPriceField.setEditable(false); // Make it read-only
        panel.add(totalPriceField, gbc);

        // Add an action listener to populate fields when a booking ID is selected
       bookingIDComboBox.addActionListener(actionEvent -> {
        Integer selectedBookingID = (Integer) bookingIDComboBox.getSelectedItem();
           if (selectedBookingID != null) {
               Booking selectedBooking = bookingRepository.getBookingByID(selectedBookingID);
               if (selectedBooking != null) {
                   roomNumberField.setText(selectedBooking.getRoomNumber());
                   customerNameField.setText(selectedBooking.getCustomerName());
                   checkInDateField.setText(selectedBooking.getCheckInDate().toString());
                   checkOutDateField.setText(selectedBooking.getCheckOutDate().toString());
                   numberOfGuestsField.setText(String.valueOf(selectedBooking.getNumberOfGuests()));
                   totalPriceField.setText(String.valueOf(selectedBooking.getTotalPrice()));

                   // Populate User ID
                   userIdField.setText(String.valueOf(selectedBooking.getUserId()));
               }
           }
    
       });


        // Row 9: Status
        gbc.gridx = 0; // First column
        gbc.gridy = 9; // Eighth row
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1; // Second column
        JTextField statusField = new JTextField("Confirmed");
        statusField.setEditable(false); // Make it read-only
        panel.add(statusField, gbc);

        // Row 10: Save Button
        gbc.gridx = 0; // First column
        gbc.gridy = 10; // Ninth row
        gbc.gridwidth = 2; // Span both columns
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            int userId;
    try {
        userId = Integer.parseInt(userIdField.getText());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(checkInByBookingDialog, 
            "Invalid User ID. Please enter a valid number.", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validate if user is a customer
            if (!checkInOutRepository.isValidCustomerUser(userId)) {
                JOptionPane.showMessageDialog(checkInByBookingDialog,
                        "Invalid User ID or User is not a Customer",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            Integer selectedBookingID = (Integer) bookingIDComboBox.getSelectedItem();
            if (selectedBookingID == null) {
                JOptionPane.showMessageDialog(checkInByBookingDialog, "Please select a booking ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Booking booking = bookingRepository.getBookingByID(selectedBookingID);
            if (booking == null) {
                JOptionPane.showMessageDialog(checkInByBookingDialog, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean isUpdated = bookingRepository.updateBookingStatusToConfirmed(selectedBookingID);
                if (isUpdated) {
                CheckInOut checkInOut = new CheckInOut(
                        Integer.parseInt(userIdField.getText()), // Add user ID
                        booking.getCustomerName(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        roomNumberField.getText(),
                        "Booking",
                        "Checked In"
                    );

                    checkInOutRepository.addCheckInOut(checkInOut); // Save the check-in record
                    JOptionPane.showMessageDialog(checkInByBookingDialog, "Booking status updated to Confirmed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCheckInOuts(); // Refresh the table to show the new booking
                } else {
                    JOptionPane.showMessageDialog(checkInByBookingDialog, "Failed to update booking status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(checkInByBookingDialog, "Check-in successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                checkInByBookingDialog.dispose(); // Close dialog
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(checkInByBookingDialog, "Error during check-in: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(saveButton, gbc);

        // Row 11: Cancel Button
        gbc.gridy = 11; // Tenth row
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> checkInByBookingDialog.dispose());
        panel.add(cancelButton, gbc);

        checkInByBookingDialog.add(panel);
        checkInByBookingDialog.setVisible(true);
}

    
    
private void checkOutBooking(ActionEvent e) {
    int selectedRow = checkInOutTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a check-in record to check out.");
        return;
    }

    int checkInId = (Integer) tableModel.getValueAt(selectedRow, 0);
    int userId = (Integer) tableModel.getValueAt(selectedRow, 1);

    // Validate user
    if (!checkInOutRepository.isValidCustomerUser(userId)) {
        JOptionPane.showMessageDialog(this,
                "Invalid User ID or User is not a Customer",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    String status = (String) tableModel.getValueAt(selectedRow, 7); // Status is now at index 7
    if ("OUT".equals(status)) {
        JOptionPane.showMessageDialog(this, "This check-in record has already been checked out.");
        return;
    }

    CheckInOut checkInOut = checkInOutRepository.getCheckInOutById(checkInId);
    if (checkInOut != null) {
        checkInOut.setStatus("OUT"); // Update status to OUT
        checkInOutRepository.updateCheckInOut(checkInOut); // Save changes to repository

        roomRepository.updateRoomStatus(checkInOut.getRoomNumber(), "Maintenance"); // Update room status to Maintenance
        loadCheckInOuts(); // Refresh the table to show updated status
    } else {
        JOptionPane.showMessageDialog(this, "Error retrieving check-in record.");
    }
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