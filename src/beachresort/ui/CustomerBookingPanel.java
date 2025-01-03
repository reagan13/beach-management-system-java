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
import java.util.List;

public class CustomerBookingPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;
    private int currentUserId; // Track the current user's ID

    public CustomerBookingPanel(int userId) {
        this.currentUserId = userId;
        bookingRepository = new BookingRepository();
        roomRepository = new RoomRepository();
        
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("My Bookings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Booking ID", "Room Number", "Customer Name", "Check-in Date", "Check-out Date", "Total Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookingsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addBookingButton = new JButton("Add Booking");
        JButton editBookingButton = new JButton("Edit Booking");
        JButton deleteBookingButton = new JButton("Cancel Booking");

        addBookingButton.addActionListener(this::addBooking);
        editBookingButton.addActionListener(this::editBooking);
        deleteBookingButton.addActionListener(this::deleteBooking);

        buttonPanel.add(addBookingButton);
        buttonPanel.add(editBookingButton);
        buttonPanel.add(deleteBookingButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load user's bookings
        loadUserBookings();
    }

    private void loadUserBookings() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Fetch bookings for the current user
        List<Booking> bookings = bookingRepository.getBookingsByUserId(currentUserId);
        for (Booking booking : bookings) {
            Object[] rowData = {
                booking.getBookingID(),
                booking.getRoomNumber(),
                booking.getCustomerName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalPrice(),
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
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 0: User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("User  ID:"), gbc);

        gbc.gridx = 1;
        JTextField userIdField = new JTextField(String.valueOf(currentUserId));
        userIdField.setEditable(false); // Make it read-only
        panel.add(userIdField, gbc);

        // Row 1: Customer Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1;
        JTextField customerNameField = new JTextField();
        panel.add(customerNameField, gbc);

        // Row 2: Room Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Room Type:"), gbc);

        gbc.gridx = 1;
        String[] roomTypes = { "Standard", "Deluxe", "Suite" };
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        panel.add(roomTypeCombo, gbc);

        // Row 3: Available Rooms
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Available Rooms:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> availableRoomsCombo = new JComboBox<>();
        panel.add(availableRoomsCombo, gbc);

        // Row 4: Room Capacity ```java
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Number of Guests:"), gbc);

        gbc.gridx = 1;
        JTextField numberOfGuestsField = new JTextField();
        panel.add(numberOfGuestsField, gbc);

        // Row 5: Check-in Date
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Check-in Date:"), gbc);

        gbc.gridx = 1;
        JTextField checkInField = new JTextField();
        panel.add(checkInField, gbc);

        // Row 6: Check-out Date
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Check-out Date:"), gbc);

        gbc.gridx = 1;
        JTextField checkOutField = new JTextField();
        panel.add(checkOutField, gbc);

        // Row 7: Total Price
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Total Price:"), gbc);

        gbc.gridx = 1;
        JTextField totalPriceField = new JTextField();
        totalPriceField.setEditable(false); // Make it read-only
        panel.add(totalPriceField, gbc);

        // Calculate total price based on room type and number of guests
        roomTypeCombo.addActionListener(e1 -> {
            String selectedRoomType = (String) roomTypeCombo.getSelectedItem();
            List<String> availableRooms = roomRepository.getAvailableRoomsByType(selectedRoomType);
            availableRoomsCombo.removeAllItems();
            for (String room : availableRooms) {
                availableRoomsCombo.addItem(room);
            }
        });

        availableRoomsCombo.addActionListener(e1 -> {
            String selectedRoom = (String) availableRoomsCombo.getSelectedItem();
            Room room = roomRepository.getRoomByNumber(selectedRoom);
            if (room != null) {
                totalPriceField.setText(String.valueOf(room.getPricePerNight()));
            }
        });

        // Add Booking Button
        JButton addButton = new JButton("Add Booking");
        addButton.addActionListener(e1 -> {
            // Get selected room number and date range
     
            LocalDate newStartDate;
            LocalDate newEndDate;

            try {
                newStartDate = LocalDate.parse(checkInField.getText());
                newEndDate = LocalDate.parse(checkOutField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addBookingDialog,
                        "Invalid date format. Please use the format yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String customerName = customerNameField.getText();
            String roomNumber = (String) availableRoomsCombo.getSelectedItem();
           
            int numberOfGuests = Integer.parseInt(numberOfGuestsField.getText());
            double totalPrice = Double.parseDouble(totalPriceField.getText());

            Booking newBooking = new Booking(0, currentUserId, roomNumber, customerName, newStartDate, newEndDate, numberOfGuests, totalPrice, "Pending");
            if (bookingRepository.addBooking(newBooking, "User ")) {
                JOptionPane.showMessageDialog(addBookingDialog, "Booking added successfully!");
                loadUserBookings(); // Refresh the bookings table
                addBookingDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(addBookingDialog, "Failed to add booking. Please try again.");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        addBookingDialog.add(panel);
        addBookingDialog.setVisible(true);
    }

    private void editBooking(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            Booking booking = bookingRepository.getBookingById(bookingId);

            if (booking != null) {
                JDialog editBookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Booking", true);
                editBookingDialog.setSize(400, 500);
                editBookingDialog.setLocationRelativeTo(this);

                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(10, 10, 10, 10);

                // Populate fields with existing booking data
                gbc.gridx = 0;
                gbc.gridy = 0;
                panel.add(new JLabel("Booking ID:"), gbc);

                gbc.gridx = 1;
                JTextField bookingIdField = new JTextField(String.valueOf(booking.getBookingID()));
                bookingIdField.setEditable(false);
                panel.add(bookingIdField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                panel.add(new JLabel("Customer Name:"), gbc);

                gbc.gridx = 1;
                JTextField customerNameField = new JTextField(booking.getCustomerName());
                panel.add(customerNameField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                panel.add(new JLabel("Room Number:"), gbc);

                gbc.gridx = 1;
                JTextField roomNumberField = new JTextField(booking.getRoomNumber());
                roomNumberField.setEditable(false); // Make it read-only
                panel.add(roomNumberField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 3;
                panel.add(new JLabel("Check-in Date:"), gbc);

                gbc.gridx = 1;
                 JTextField checkInField = new JTextField(booking.getCheckInDate().toString());
        
                panel.add(checkInField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 4;
                panel.add(new JLabel("Check-out Date:"), gbc);

                gbc.gridx = 1;
                  JTextField checkOutField = new JTextField(booking.getCheckOutDate().toString());
                panel.add(checkOutField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 5;
                panel.add(new JLabel("Total Price:"), gbc);

                gbc.gridx = 1;
                JTextField totalPriceField = new JTextField(String.valueOf(booking.getTotalPrice()));
                totalPriceField.setEditable(false); // Make it read-only
                panel.add(totalPriceField, gbc);

                // Update Booking Button
                JButton updateButton = new JButton("Update Booking");
                updateButton.addActionListener(e1 -> {
                 

                    booking.setCheckInDate(LocalDate.parse(checkInField.getText()));

                    booking.setCheckOutDate(LocalDate.parse(checkOutField.getText()));

                    if (bookingRepository.updateBooking(booking, "User  ")) {
                        JOptionPane.showMessageDialog(editBookingDialog, "Booking updated successfully!");
                        loadUserBookings(); // Refresh the bookings table
                        editBookingDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editBookingDialog, "Failed to update booking. Please try again.");
                    }
                });

                gbc.gridx = 0;
                gbc.gridy = 6;
                gbc.gridwidth = 2;
                panel.add(updateButton, gbc);

                editBookingDialog.add(panel);
                editBookingDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking to edit.");
        }
    }

    private void deleteBooking(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this booking?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                if (bookingRepository.deleteBooking(bookingId, "User  ")) {
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
                    loadUserBookings(); // Refresh the bookings table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel booking. Please try again.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
        }
    }
}