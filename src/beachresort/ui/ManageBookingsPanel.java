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
    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;

    public ManageBookingsPanel() {
        bookingRepository = new BookingRepository();
        roomRepository = new RoomRepository();
        setLayout(new BorderLayout());

        JLabel bookingsLabel = new JLabel("Manage Bookings", SwingConstants.CENTER);
        bookingsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(bookingsLabel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"Booking ID", "Customer Name", "Room Number", "Check-in Date", "Check-out Date", "Status"};
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
                booking.getBookingID(),
                booking.getCustomerName(),
                booking.getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void addBooking(ActionEvent e) {
        JDialog addBookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Booking", true);
        addBookingDialog.setSize(400, 600);
        addBookingDialog.setLocationRelativeTo(this);

        // Use GridBagLayout for more control over component placement
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 1: User ID Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("User ID:"), gbc);

        gbc.gridx = 1;
        JTextField userIdField = new JTextField();
        panel.add(userIdField, gbc);

        // Row 2: Customer Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1;
        JTextField customerNameField = new JTextField();
        panel.add(customerNameField, gbc);

        // Row 3: Room Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Room Type:"), gbc);

        gbc.gridx = 1;
        String[] roomTypes = { "Standard", "Deluxe", "Suite" };
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        panel.add(roomTypeCombo, gbc);

        // Row 4: Available Rooms
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Available Rooms:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> availableRoomsCombo = new JComboBox<>();
        panel.add(availableRoomsCombo, gbc);

        // Row 5: Room Capacity
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Room Capacity:"), gbc);

        gbc.gridx = 1;
        JTextField capacityField = new JTextField();
        capacityField.setEditable(false);
        panel.add(capacityField, gbc);

        // Row 6: Room Price
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Room Price:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField();
        priceField.setEditable(false);
        panel.add(priceField, gbc);

        // Room Type Listener
        roomTypeCombo.addActionListener(actionEvent -> {
            String selectedRoomType = (String) roomTypeCombo.getSelectedItem();
            updateAvailableRooms(selectedRoomType, availableRoomsCombo);
        });

        // Available Rooms Listener
        availableRoomsCombo.addActionListener(actionEvent -> {
            String selectedRoomNumber = (String) availableRoomsCombo.getSelectedItem();
            if (selectedRoomNumber != null) {
                Room selectedRoom = roomRepository.getRoomByNumber(selectedRoomNumber);
                if (selectedRoom != null) {
                    capacityField.setText(String.valueOf(selectedRoom.getCapacity()));
                    priceField.setText(String.valueOf(selectedRoom.getPricePerNight()));
                }
            }
        });

        // Row 7: Check-in Date
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Check-in Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        JTextField checkInField = new JTextField();
        panel.add(checkInField, gbc);

        // Row 8: Check-out Date
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Check-out Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        JTextField checkOutField = new JTextField();
        panel.add(checkOutField, gbc);

        // Row 9: Status
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        String[] statuses = {"Pending", "Cancelled"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        panel.add(statusCombo, gbc);

        // Row 10: Save Button ```java
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            // Validate User ID
            int userId;
            try {
                userId = Integer.parseInt(userIdField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addBookingDialog, "Invalid User ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if user is a valid customer
            if (!bookingRepository.isValidCustomerUser (userId)) {
                JOptionPane.showMessageDialog(addBookingDialog, "Invalid User ID or User is not a Customer", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected room number and date range
            String selectedRoomNumber = (String) availableRoomsCombo.getSelectedItem();
            LocalDate newStartDate;
            LocalDate newEndDate;

            try {
                newStartDate = LocalDate.parse(checkInField.getText());
                newEndDate = LocalDate.parse(checkOutField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addBookingDialog, "Invalid date format. Please use the format yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for overlapping bookings
            if (checkForOverlappingBookings(selectedRoomNumber, newStartDate, newEndDate)) {
                JOptionPane.showMessageDialog(addBookingDialog, "There is already a booking for this room on the selected dates.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Booking newBooking = new Booking(
                          userId,
                    selectedRoomNumber,
                    customerNameField.getText(),
                    newStartDate,
                    newEndDate,
                    1, // Placeholder for number of guests
                    Double.parseDouble(priceField.getText()),
                    statusCombo.getSelectedItem().toString()
              
                );

                boolean isSuccess = bookingRepository.addBooking(newBooking, "User ");
                if (isSuccess) {
                    roomRepository.updateRoomStatusBasedOnCurrent(selectedRoomNumber, statusCombo.getSelectedItem().toString());
                    JOptionPane.showMessageDialog(addBookingDialog, "Booking Added Successfully!");
                    addBookingDialog.dispose();
                }
                loadBookings();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addBookingDialog, "Error adding booking: " + ex.getMessage());
            }
        });
        panel.add(saveButton, gbc);

        // Row 11: Cancel Button
        gbc.gridy = 10;
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> addBookingDialog.dispose());
        panel.add(cancelButton, gbc);

        addBookingDialog.add(panel);
        addBookingDialog.setVisible(true);
    }

    private void updateAvailableRooms(String roomType, JComboBox<String> availableRoomsCombo) {
        availableRoomsCombo.removeAllItems();
        List<String> availableRooms = roomRepository.getAvailableRoomsByType(roomType);
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

        int bookingId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Booking booking = bookingRepository.getBookingById(bookingId);

        JDialog editBookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Booking", true);
        editBookingDialog.setSize(400, 350);
        editBookingDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add User ID field
        panel.add(new JLabel("User  ID:"));
        JTextField userIdField = new JTextField(String.valueOf(booking.getUserId()));
        panel.add(userIdField);

        panel.add(new JLabel("Customer Name:"));
        JTextField customerNameField = new JTextField(booking.getCustomerName());
        panel.add(customerNameField);

        panel.add(new JLabel("Room Number:"));
        JTextField roomNumber = new JTextField(booking.getRoomNumber());
        roomNumber.setEditable(false);
        panel.add(roomNumber);

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
                // Validate User ID
                int userId;
                try {
                    userId = Integer.parseInt(userIdField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editBookingDialog, "Invalid User ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if user is a valid customer
                if (!bookingRepository.isValidCustomerUser (userId)) {
                    JOptionPane.showMessageDialog(editBookingDialog, "Invalid User ID or User is not a Customer", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update the booking object
                booking.setBookingId(bookingId);
                booking.setUserId(userId);
                booking.setCustomerName(customerNameField.getText());
                booking.setCheckInDate(LocalDate.parse(checkInField.getText()));
                booking.setCheckOutDate(LocalDate.parse(checkOutField.getText()));
                booking.setStatus(statusCombo.getSelectedItem().toString());

                // Save the updated booking using the repository
                bookingRepository.updateBooking(booking, "User ");
                loadBookings();

                roomRepository.updateRoomStatusBasedOnCurrent(booking.getRoomNumber(), statusCombo.getSelectedItem().toString());
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
        int bookingId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Booking booking = bookingRepository.getBookingById(bookingId);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this booking?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            roomRepository.updateRoomStatusBasedOnCurrent(booking.getRoomNumber(), "Cancelled");
            bookingRepository.deleteBooking(bookingId, "User ");
            loadBookings();
            JOptionPane.showMessageDialog(this, "Booking deleted successfully");
        }
    }

    private void refreshBookings(ActionEvent e) {
        loadBookings();
        JOptionPane.showMessageDialog(this, "Bookings refreshed successfully.");
    }

    private boolean checkForOverlappingBookings(String roomNumber, LocalDate newStartDate, LocalDate newEndDate) {
        List<Booking> existingBookings = getExistingBookingsForRoom(roomNumber);

        for (Booking booking : existingBookings) {
            LocalDate existingStartDate = booking.getCheckInDate();
            LocalDate existingEndDate = booking.getCheckOutDate();

            if ((newStartDate.isEqual(existingStartDate) || newStartDate.isAfter(existingStartDate)) &&
                    (newStartDate.isBefore(existingEndDate) || newStartDate.isEqual(existingEndDate))) {
                return true;
            }

            if ((newEndDate.isEqual(existingStartDate) || newEndDate.isBefore(existingEndDate)) &&
                    (newEndDate.isAfter(existingStartDate) || newEndDate.isEqual(existingStartDate))) {
                return true;
            }

            if (newStartDate.isBefore(existingStartDate) && newEndDate.isAfter(existingEndDate)) {
                return true;
            }
        }

        return false;
    }

    public List<Booking> getExistingBookingsForRoom(String roomNumber) {
        List<Booking> existingBookings = new ArrayList<>();
        List<Booking> allBookings = bookingRepository.getAllBookings();

        for (Booking booking : allBookings) {
            if (booking.getRoomNumber().equals(roomNumber )) {
                existingBookings.add(booking);
            }
        }

        return existingBookings;
    }
}