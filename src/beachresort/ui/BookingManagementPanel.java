package beachresort.ui;

import beachresort.models.Booking;
import beachresort.repositories.BookingRepository;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingManagementPanel extends JPanel {
    // ... (previous inner classes for JButtonRenderer and JButtonEditor remain the same)
// Custom Button Renderer
    class JButtonRenderer extends JButton implements TableCellRenderer {
        public JButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, 
            boolean isSelected, boolean hasFocus, 
            int row, int column) {
            
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }


    // Custom Button Editor
    class JButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public JButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(
            JTable table, Object value, 
            boolean isSelected, int row, int column) {
            
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                viewBookingDetails(selectedRow);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private BookingRepository bookingRepository;
    private JTextField searchField;
    private JSpinner dateSpinner;
    private LocalDate selectedDate;
    public BookingManagementPanel() {
        bookingRepository = new BookingRepository();
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }
     public void JDatePicker() {
        setLayout(new BorderLayout());
        
        // Use SpinnerDateModel for date selection
        SpinnerModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        
        // Customize spinner editor
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        
        add(dateSpinner, BorderLayout.CENTER);
        
        // Set default date to today
        selectedDate = LocalDate.now();
    }

    public LocalDate getDate() {
        try {
            java.util.Date spinnerDate = (java.util.Date) dateSpinner.getValue();
            return spinnerDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }
 public void setDate(LocalDate date) {
        if (date != null) {
            java.util.Date utilDate = java.util.Date.from(
                date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            dateSpinner.setValue(utilDate);
            selectedDate = date;
        }
    }
    private void initComponents() {
        String[] columnNames = {
            "Booking ID", "Guest Name", "Room Type", 
            "Check-In", "Check-Out", "Total Guests", "Status", "Actions"
        };

        // Initialize table model with empty data
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only "Actions" column is interactive
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return JButton.class;
                return super.getColumnClass(columnIndex);
            }
        };

        bookingsTable = new JTable(tableModel);
        customizeTable();

        // Load bookings from database
        loadBookingsFromDatabase();

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(createSearchPanel(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void loadBookingsFromDatabase() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch bookings from database
        List<Booking> bookings = bookingRepository.getAllBookings();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Booking booking : bookings) {
            Object[] row = {
                booking.getBookingId(),
                booking.getGuestName(),
                booking.getRoomType(),
                booking.getCheckInDate().format(formatter),
                booking.getCheckOutDate().format(formatter),
                booking.getTotalGuests(),
                booking.getStatus(),
                "View"
            };
            tableModel.addRow(row);
        }
    }

    // Modify existing methods to work with database

    private void showBookingDialog(boolean isModify) {
    JDialog dialog = new JDialog();
    dialog.setTitle(isModify ? "Modify Booking" : "New Booking");
    dialog.setSize(500, 600);
    dialog.setModal(true);
    dialog.setLocationRelativeTo(this);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    String[] labels = {
        "Guest Name*", "Contact Number*", "Email*", 
        "Room Type*", "Check-In Date*", "Check-Out Date*", 
        "Total Guests*", "Special Requests"
    };

    JTextField[] fields = new JTextField[labels.length];
    
    // Predefined room types
    JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
        "Standard Room", 
        "Deluxe Room", 
        "Suite", 
        "Ocean View Room"
    });

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel(labels[i]), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        if (labels[i].equals("Room Type*")) {
            formPanel.add(roomTypeCombo, gbc);
        } else {
            fields[i] = new JTextField(20);
            formPanel.add(fields[i], gbc);
        }
    }

    // If modifying, fetch the ACTUAL booking details from the database
    if (isModify) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the booking ID from the selected row
            String bookingId = tableModel.getValueAt(selectedRow, 0).toString();
            
            // Fetch the complete booking details from the database
            Booking booking = bookingRepository.getBookingById(bookingId);
            
            if (booking != null) {
                // Populate fields with database details, not just table row data
                fields[0].setText(booking.getGuestName());
                fields[1].setText(booking.getContactNumber());
                fields[2].setText(booking.getEmail());
                
                // For room type, set the combo box
                String bookingRoomType = booking.getRoomType();
                for (int i = 0; i < roomTypeCombo.getItemCount(); i++) {
                    if (roomTypeCombo.getItemAt(i).equalsIgnoreCase(bookingRoomType)) {
                        roomTypeCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Format dates for display
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                fields[4].setText(booking.getCheckInDate().format(formatter));
                fields[5].setText(booking.getCheckOutDate().format(formatter));
                
                fields[6].setText(String.valueOf(booking.getTotalGuests()));
                
                
            }
        }
    }

    mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton(isModify ? "Update" : "Create");
    JButton cancelButton = new JButton("Cancel");

    // Final reference to use in lambda
    
    final JComboBox<String> finalRoomTypeCombo = roomTypeCombo;
saveButton.addActionListener(e -> {
    if (validateBookingForm(fields, finalRoomTypeCombo)) {
        try {
            if (isModify) {
                // Update existing booking
                updateBookingInDatabase(fields, finalRoomTypeCombo);
            } else {
                // Add new booking
                addNewBookingToDatabase(fields, finalRoomTypeCombo);
            }
            dialog.dispose();
            loadBookingsFromDatabase(); // Refresh table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, 
                "Error processing booking: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
});

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    dialog.add(mainPanel);
    dialog.setVisible(true);
}

// Helper method to get current booking ID
private String getCurrentBookingId() {
    int selectedRow = bookingsTable.getSelectedRow();
    if (selectedRow != -1) {
        // Convert view row to model row in case of sorting/filtering
        int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);
        
        // Assuming the first column contains the booking ID
        return tableModel.getValueAt(modelRow, 0).toString();
    }
    
    throw new IllegalStateException("No booking selected");
}

// Enhanced date validation method
private boolean isValidDateRange(LocalDate checkIn, LocalDate checkOut) {
    // Check if check-in is before check-out
    if (checkIn.isAfter(checkOut)) {
        JOptionPane.showMessageDialog(this, 
            "Check-in date must be before check-out date", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    // Optional: Check if dates are in the future
    LocalDate today = LocalDate.now();
    if (checkIn.isBefore(today)) {
        JOptionPane.showMessageDialog(this, 
            "Check-in date must be in the future", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    return true;
}

// Updated validation method with more comprehensive checks
private boolean validateBookingForm(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    // Validate text fields are not empty
    for (int i = 0; i < fields.length; i++) {
        if (fields[i] != null) {
            String fieldText = fields[i].getText().trim();
            
            // Skip special requests field
            if (i == 7) continue;
            
            if (fieldText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all required fields", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    // Validate room type selection
    if (roomTypeCombo.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, 
            "Please select a room type", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Validate total guests
    try {
        int totalGuests = Integer.parseInt(fields[6].getText().trim());
        if (totalGuests <= 0 || totalGuests > 10) { // Assuming max 10 guests
            JOptionPane.showMessageDialog(this, 
                "Total guests must be between 1 and 10", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, 
            "Invalid number of guests", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Validate email format
    if (!isValidEmail(fields[2].getText().trim())) {
        JOptionPane.showMessageDialog(this, 
            "Invalid email format", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Validate phone number format
    if (!isValidPhoneNumber(fields[1].getText().trim())) {
        JOptionPane.showMessageDialog(this, 
            "Invalid phone number format", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Validate date range
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkIn = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOut = LocalDate.parse(fields[5].getText().trim(), formatter);
        
        if (!isValidDateRange(checkIn, checkOut)) {
            return false;
        }
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(this, 
            "Invalid date format. Use yyyy-MM-dd", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }

    return true;
}




// Updated method for modifying booking
private void updateBookingInDatabase(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    try {
        // Get the current booking ID
        String bookingId = getCurrentBookingId();
        
        // Retrieve existing booking
        Booking existingBooking = bookingRepository.getBookingById(bookingId);
        
        if (existingBooking == null) {
            throw new IllegalStateException("Booking not found");
        }

        // Parse dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOutDate = LocalDate.parse(fields[5].getText().trim(), formatter);

        // Update booking details
        existingBooking.setGuestName(fields[0].getText().trim());
        existingBooking.setContactNumber(fields[1].getText().trim());
        existingBooking.setEmail(fields[2].getText().trim());
        existingBooking.setRoomType(roomTypeCombo.getSelectedItem().toString());
        existingBooking.setCheckInDate(checkInDate);
        existingBooking.setCheckOutDate(checkOutDate);
        existingBooking.setTotalGuests(Integer.parseInt(fields[6].getText().trim()));
        
        // Optionally update status or keep existing
        // existingBooking.setStatus("Updated");

        // Update in database
        try {
            bookingRepository.updateBooking(existingBooking);
            
            // Show success message
            JOptionPane.showMessageDialog(
                this, 
                "Booking updated successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception dbException) {
            JOptionPane.showMessageDialog(
                this, 
                "Error updating booking in database: " + dbException.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    } catch (DateTimeParseException | NumberFormatException parseException) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid input: " + parseException.getMessage(), 
            "Input Error", 
            JOptionPane.ERROR_MESSAGE
        );
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            this, 
            "Unexpected error: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
}


private void cancelBooking(int row) {
        if (row != -1) {
            String bookingId = tableModel.getValueAt(row, 0).toString();
            
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to cancel this booking?", 
                "Confirm Cancellation", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean cancelled = bookingRepository.cancelBooking(bookingId);
                
                if (cancelled) {
                    // Refresh the table to show updated status
                    loadBookingsFromDatabase();
                    
                    JOptionPane.showMessageDialog(
                        this, 
                        "Booking cancelled successfully", 
                        "Cancellation Successful", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Failed to cancel booking", 
                        "Cancellation Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    private String generateBookingId() {
        // Generate a unique booking ID
        return "B" + System.currentTimeMillis();
    }

  private void showWalkInBookingDialog() {
    JDialog dialog = new JDialog();
    dialog.setTitle("Walk-in Booking");
    dialog.setSize(500, 600);
    dialog.setModal(true);
    dialog.setLocationRelativeTo(this);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    String[] labels = {
        "Guest Name*", "Contact Number*", "Email*", 
        "Room Type*", "Check-In Date*", "Check-Out Date*", 
        "Total Guests*", "Special Requests"
    };

    JTextField[] fields = new JTextField[labels.length];
    
    // Predefined room types
    JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
        "Standard Room", 
        "Deluxe Room", 
        "Suite", 
        "Ocean View Room"
    });

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel(labels[i]), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        if (labels[i].equals("Room Type*")) {
            formPanel.add(roomTypeCombo, gbc);
        } else {
            fields[i] = new JTextField(20);
            formPanel.add(fields[i], gbc);
        }
    }

    mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Create Booking");
    JButton cancelButton = new JButton("Cancel");

    // Final reference for lambda
    final JComboBox<String> finalRoomTypeCombo = roomTypeCombo;

    saveButton.addActionListener(e -> {
        if (validateBookingForm(fields, finalRoomTypeCombo)) {
            addNewBookingToDatabase(fields, finalRoomTypeCombo);
            dialog.dispose();
            loadBookingsFromDatabase(); // Refresh table
        }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    dialog.add(mainPanel);
    dialog.setVisible(true);
}

// Updated method to handle both text fields and room type combo
private void addNewBookingToDatabase(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    try {
        // Generate unique booking ID
        String bookingId = generateUniqueBookingId();

        // Parse dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOutDate = LocalDate.parse(fields[5].getText().trim(), formatter);

        // Create new Booking using the constructor
        Booking newBooking = new Booking(
            bookingId,                                     // bookingId
            fields[0].getText().trim(),                    // guestName
            roomTypeCombo.getSelectedItem().toString(),    // roomType
            checkInDate,                                   // checkInDate
            checkOutDate,                                  // checkOutDate
            Integer.parseInt(fields[6].getText().trim()),  // totalGuests
            "Confirmed",                                   // default status
            fields[1].getText().trim(),                    // contactNumber
            fields[2].getText().trim()                     // email
        );

        // Save to database
        bookingRepository.addBooking(newBooking);
        
        // Show success message
        JOptionPane.showMessageDialog(
            this, 
            "Booking created successfully", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE
        );
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid date format. Please use yyyy-MM-dd", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE
        );
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid number of guests", 
            "Input Error", 
            JOptionPane.ERROR_MESSAGE
        );
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            this, 
            "Error creating booking: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
}

// Updated validation method to include room type combo
private boolean validateBookingForm(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    // Check if required fields are filled
    for (int i = 0; i < fields.length; i++) {
        if (fields[i] != null) {
            // Skip special requests field
            if (i == 7) continue;
            
            if (fields[i].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Please fill in all required fields", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
        }
    }

    // Validate room type
    if (roomTypeCombo.getSelectedItem() == null || 
        roomTypeCombo.getSelectedItem().toString().trim().isEmpty()) {
        JOptionPane.showMessageDialog(
            this, 
            "Please select a room type", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate email format
    if (!isValidEmail(fields[2].getText().trim())) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid email format", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate phone number
    if (!isValidPhoneNumber(fields[1].getText().trim())) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid phone number", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate dates
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOutDate = LocalDate.parse(fields[5].getText().trim(), formatter);
        
        if (checkInDate.isAfter(checkOutDate)) {
            JOptionPane.showMessageDialog(
                this, 
                "Check-in date must be before check-out date", 
                "Date Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid date format. Use yyyy-MM-dd", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate total guests
    try {
        int totalGuests = Integer.parseInt(fields[6].getText().trim());
        if (totalGuests <= 0 || totalGuests > 10) {
            JOptionPane.showMessageDialog(
                this, 
                "Total guests must be between 1 and 10", private void showWalkInBookingDialog() {
    JDialog dialog = new JDialog();
    dialog.setTitle("Walk-in Booking");
    dialog.setSize(500, 600);
    dialog.setModal(true);
    dialog.setLocationRelativeTo(this);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    String[] labels = {
        "Guest Name*", "Contact Number*", "Email*", 
        "Room Type*", "Check-In Date*", "Check-Out Date*", 
        "Total Guests*", "Special Requests"
    };

    JTextField[] fields = new JTextField[labels.length];
    
    // Predefined room types
    JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
        "Standard Room", 
        "Deluxe Room", 
        "Suite", 
        "Ocean View Room"
    });

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel(labels[i]), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        if (labels[i].equals("Room Type*")) {
            formPanel.add(roomTypeCombo, gbc);
        } else {
            fields[i] = new JTextField(20);
            formPanel.add(fields[i], gbc);
        }
    }

    mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Create Booking");
    JButton cancelButton = new JButton("Cancel");

    // Final reference for lambda
    final JComboBox<String> finalRoomTypeCombo = roomTypeCombo;

    saveButton.addActionListener(e -> {
        if (validateBookingForm(fields, finalRoomTypeCombo)) {
            addNewBookingToDatabase(fields, finalRoomTypeCombo);
            dialog.dispose();
            loadBookingsFromDatabase(); // Refresh table
        }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    dialog.add(mainPanel);
    dialog.setVisible(true);
}

// Updated method to handle both text fields and room type combo
private void addNewBookingToDatabase(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    try {
        // Generate unique booking ID
        String bookingId = generateUniqueBookingId();

        // Parse dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOutDate = LocalDate.parse(fields[5].getText().trim(), formatter);

        // Create new Booking using the constructor
        Booking newBooking = new Booking(
            bookingId,                                     // bookingId
            fields[0].getText().trim(),                    // guestName
            roomTypeCombo.getSelectedItem().toString(),    // roomType
            checkInDate,                                   // checkInDate
            checkOutDate,                                  // checkOutDate
            Integer.parseInt(fields[6].getText().trim()),  // totalGuests
            "Confirmed",                                   // default status
            fields[1].getText().trim(),                    // contactNumber
            fields[2].getText().trim()                     // email
        );

        // Save to database
        bookingRepository.addBooking(newBooking);
        
        // Show success message
        JOptionPane.showMessageDialog(
            this, 
            "Booking created successfully", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE
        );
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid date format. Please use yyyy-MM-dd", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE
        );
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid number of guests", 
            "Input Error", 
            JOptionPane.ERROR_MESSAGE
        );
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            this, 
            "Error creating booking: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
}

// Updated validation method to include room type combo
private boolean validateBookingForm(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    // Check if required fields are filled
    for (int i = 0; i < fields.length; i++) {
        if (fields[i] != null) {
            // Skip special requests field
            if (i == 7) continue;
            
            if (fields[i].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Please fill in all required fields", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
        }
    }

    // Validate room type
    if (roomTypeCombo.getSelectedItem() == null || 
        roomTypeCombo.getSelectedItem().toString().trim().isEmpty()) {
        JOptionPane.showMessageDialog(
            this, 
            "Please select a room type", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate email format
    if (!isValidEmail(fields[2].getText().trim())) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid email format", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate phone number
    if (!isValidPhoneNumber(fields[1].getText().trim())) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid phone number", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate dates
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOutDate = LocalDate.parse(fields[5].getText().trim(), formatter);
        
        if (checkInDate.isAfter(checkOutDate)) {
            JOptionPane.showMessageDialog(
                this, 
                "Check-in date must be before check-out date", 
                "Date Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid date format. Use yyyy-MM-dd", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate total guests
    try {
        int totalGuests = Integer.parseInt(fields[6].getText().trim());
        if (totalGuests <= 0 || totalGuests > 10) {
            JOptionPane.showMessageDialog(
                this, 
                "Total guests must be between 1 and 10", private void showWalkInBookingDialog() {
    JDialog dialog = new JDialog();
    dialog.setTitle("Walk-in Booking");
    dialog.setSize(500, 600);
    dialog.setModal(true);
    dialog.setLocationRelativeTo(this);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    String[] labels = {
        "Guest Name*", "Contact Number*", "Email*", 
        "Room Type*", "Check-In Date*", "Check-Out Date*", 
        "Total Guests*", "Special Requests"
    };

    JTextField[] fields = new JTextField[labels.length];
    
    // Predefined room types
    JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
        "Standard Room", 
        "Deluxe Room", 
        "Suite", 
        "Ocean View Room"
    });

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel(labels[i]), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        if (labels[i].equals("Room Type*")) {
            formPanel.add(roomTypeCombo, gbc);
        } else {
            fields[i] = new JTextField(20);
            formPanel.add(fields[i], gbc);
        }
    }

    mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Create Booking");
    JButton cancelButton = new JButton("Cancel");

    // Final reference for lambda
    final JComboBox<String> finalRoomTypeCombo = roomTypeCombo;

    saveButton.addActionListener(e -> {
        if (validateBookingForm(fields, finalRoomTypeCombo)) {
            addNewBookingToDatabase(fields, finalRoomTypeCombo);
            dialog.dispose();
            loadBookingsFromDatabase(); // Refresh table
        }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    dialog.add(mainPanel);
    dialog.setVisible(true);
}

// Updated method to handle both text fields and room type combo
private void addNewBookingToDatabase(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    try {
        // Generate unique booking ID
        String bookingId = generateUniqueBookingId();

        // Parse dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOutDate = LocalDate.parse(fields[5].getText().trim(), formatter);

        // Create new Booking using the constructor
        Booking newBooking = new Booking(
            bookingId,                                     // bookingId
            fields[0].getText().trim(),                    // guestName
            roomTypeCombo.getSelectedItem().toString(),    // roomType
            checkInDate,                                   // checkInDate
            checkOutDate,                                  // checkOutDate
            Integer.parseInt(fields[6].getText().trim()),  // totalGuests
            "Confirmed",                                   // default status
            fields[1].getText().trim(),                    // contactNumber
            fields[2].getText().trim()                     // email
        );

        // Save to database
        bookingRepository.addBooking(newBooking);
        
        // Show success message
        JOptionPane.showMessageDialog(
            this, 
            "Booking created successfully", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE
        );
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid date format. Please use yyyy-MM-dd", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE
        );
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid number of guests", 
            "Input Error", 
            JOptionPane.ERROR_MESSAGE
        );
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            this, 
            "Error creating booking: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
}

// Updated validation method to include room type combo
private boolean validateBookingForm(JTextField[] fields, JComboBox<String> roomTypeCombo) {
    // Check if required fields are filled
    for (int i = 0; i < fields.length; i++) {
        if (fields[i] != null) {
            // Skip special requests field
            if (i == 7) continue;
            
            if (fields[i].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Please fill in all required fields", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
        }
    }

    // Validate room type
    if (roomTypeCombo.getSelectedItem() == null || 
        roomTypeCombo.getSelectedItem().toString().trim().isEmpty()) {
        JOptionPane.showMessageDialog(
            this, 
            "Please select a room type", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate email format
    if (!isValidEmail(fields[2].getText().trim())) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid email format", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate phone number
    if (!isValidPhoneNumber(fields[1].getText().trim())) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid phone number", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate dates
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(fields[4].getText().trim(), formatter);
        LocalDate checkOutDate = LocalDate.parse(fields[5].getText().trim(), formatter);
        
        if (checkInDate.isAfter(checkOutDate)) {
            JOptionPane.showMessageDialog(
                this, 
                "Check-in date must be before check-out date", 
                "Date Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid date format. Use yyyy-MM-dd", 
            "Date Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    // Validate total guests
    try {
        int totalGuests = Integer.parseInt(fields[6].getText().trim());
        if (totalGuests <= 0 || totalGuests > 10) {
            JOptionPane.showMessageDialog(
                this, 
                "Total guests must be between 1 and 10",             JOptionPane.showMessageDialog(
                this, 
                "Total guests must be between 1 and 10", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid number of guests", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }

    return true;
}

// Email validation method
private boolean isValidEmail(String email) {
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    return email.matches(emailRegex);
}

// Phone number validation method
private boolean isValidPhoneNumber(String phoneNumber) {
    // Adjust regex as per your specific phone number format requirements
    String phoneRegex = "^\\+?\\d{10,14}$"; // Example: allows international format
    return phoneNumber.matches(phoneRegex);
}

// Utility method to generate unique booking ID
private String generateUniqueBookingId() {
    // Simple implementation - you might want a more robust method
    return "BK-" + System.currentTimeMillis();
}
    // Modify the viewBookingDetails method to handle both table row click and "View" button
private void viewBookingDetails(int row) {
    if (row == -1) return;

    // Get the booking ID from the selected row
    String bookingId = tableModel.getValueAt(row, 0).toString();
    
    // Fetch the complete booking details from the database
    Booking booking = bookingRepository.getBookingById(bookingId);

    if (booking == null) {
        JOptionPane.showMessageDialog(this, 
            "Unable to fetch booking details", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    JDialog detailDialog = new JDialog();
    detailDialog.setTitle("Booking Details");
    detailDialog.setSize(500, 600);
    detailDialog.setModal(true);
    detailDialog.setLocationRelativeTo(this);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel detailPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    String[] labels = {
        "Booking ID", "Guest Name", "Contact Number", 
        "Email", "Room Type", "Check-In Date", 
        "Check-Out Date", "Total Guests", "Status"
    };

    String[] values = {
        booking.getBookingId(),
        booking.getGuestName(),
        booking.getContactNumber(),
        booking.getEmail(),
        booking.getRoomType(),
        booking.getCheckInDate().toString(),
        booking.getCheckOutDate().toString(),
        String.valueOf(booking.getTotalGuests()),
        booking.getStatus()
    };

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.weightx = 0.3;
        detailPanel.add(new JLabel(labels[i] + ":"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        detailPanel.add(new JLabel(values[i]), gbc);
    }

    mainPanel.add(new JScrollPane(detailPanel), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> detailDialog.dispose());
    buttonPanel.add(closeButton);

    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    detailDialog.add(mainPanel);
    detailDialog.setVisible(true);
}
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            bookingsTable.setRowSorter(sorter);
            
            // Create a row filter that checks multiple columns
            RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).toLowerCase().contains(searchText)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            
            sorter.setRowFilter(filter);
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        return searchPanel;
    }

    private void customizeTable() {
        bookingsTable.setRowHeight(40);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        TableColumnModel columnModel = bookingsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // Booking ID
        columnModel.getColumn(1).setPreferredWidth(150);  // Guest Name
        columnModel.getColumn(2).setPreferredWidth(200);  // Room Type
        columnModel.getColumn(7).setPreferredWidth(50);   // Actions

        JTableHeader header = bookingsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));

        // Custom cell renderer for status column
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, 
                boolean isSelected, boolean hasFocus, 
                int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                switch (status) {
                    case "Confirmed":
                        c.setForeground(Color.GREEN.darker());
                        break;
                    case "Pending":
                        c.setForeground(Color.ORANGE.darker());
                        break;
                    case "Cancelled":
                        c.setForeground(Color.RED.darker());
                        break;
                    default:
                        c.setForeground(table.getForeground());
                }
                
                return c;
            }
        });

           // Set up Actions column
    TableColumn actionColumn = bookingsTable.getColumn("Actions");
    actionColumn.setCellRenderer(new JButtonRenderer());
    actionColumn.setCellEditor(new JButtonEditor(new JCheckBox()));

        
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // New buttons for different booking types
        JButton addNewBookingButton = new JButton("Add New Booking");
        JButton walkInBookingButton = new JButton("Walk-in Booking");
        JButton modifyButton = new JButton("Modify Booking");
        JButton cancelButton = new JButton("Cancel Booking");

        // Styling buttons
        JButton[] buttons = {addNewBookingButton, walkInBookingButton, modifyButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(150, 35));
        }

        // Add New Booking Action
        addNewBookingButton.addActionListener(e -> {
            showBookingDialog(false);
        });

        // Walk-in Booking Action
        walkInBookingButton.addActionListener(e -> {
            showWalkInBookingDialog();
        });

        // Modify Booking Action
        modifyButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow != -1) {
                showBookingDialog(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a booking to modify", 
                    "Selection Error", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        // Cancel Booking Action
        cancelButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow != -1) {
                cancelBooking(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a booking to cancel", 
                    "Selection Error", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add buttons to panel
        buttonPanel.add(addNewBookingButton);
        buttonPanel.add(walkInBookingButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    // Optional: Export bookings to CSV
    private void exportBookingsToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Bookings");
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                // Implement CSV export logic
                List<Booking> bookings = bookingRepository.getAllBookings();
                
                try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                    // Write CSV header
                    writer.println("Booking ID,Guest Name,Room Type,Check-In,Check-Out,Total Guests,Status,Contact Number,Email");
                    
                    // Write booking data
                    for (Booking booking : bookings) {
                        writer.println(String.format("%s,%s,%s,%s,%s,%d,%s,%s,%s",
                            booking.getBookingId(),
                            booking.getGuestName(),
                            booking.getRoomType(),
                            booking.getCheckInDate(),
                            booking.getCheckOutDate(),
                            booking.getTotalGuests(),
                            booking.getStatus(),
                            booking.getContactNumber(),
                            booking.getEmail()
                        ));
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Error exporting bookings: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Optional: Import bookings from CSV
    private void importBookingsFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Bookings");
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                // Skip header line
                String line = reader.readLine();
                
                int successCount = 0;
                int errorCount = 0;
                
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(",");
                    
                    try {
                        Booking booking = new Booking(
                            fields[0],                     // Booking ID
                            fields[1],                     // Guest Name
                            fields[2],                     // Room Type
                            LocalDate.parse(fields[3]),    // Check-In Date
                            LocalDate.parse(fields[4]),    // Check-Out Date
                            Integer.parseInt(fields[5]),   // Total Guests
                            fields[6],                     // Status
                            fields[7],                     // Contact Number
                            fields[8]                      // Email
                        );
                        
                        // Add or update booking
                        if (bookingRepository.addBooking(booking)) {
                            successCount++;
                        } else {
                            errorCount++;
                        }
                    } catch (Exception e) {
                        errorCount++;
                    }
                }
                
                // Refresh table
                loadBookingsFromDatabase();
                
                // Show import summary
                JOptionPane.showMessageDialog(
                    this, 
                    String.format("Import Complete\nSuccessful: %d\nErrors: %d", 
                        successCount, errorCount), 
                    "Import Results", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Error importing bookings: " + e.getMessage(), 
                    "Import Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Add export and import to button panel
    private JPanel createExtendedButtonPanel() {
        JPanel buttonPanel = createButtonPanel();
        
        // Additional buttons for export and import
        JButton exportButton = new JButton("Export Bookings");
        JButton importButton = new JButton("Import Bookings");
        
        // Styling
        exportButton.setFont(new Font("Arial", Font.BOLD, 12));
        importButton.setFont(new Font("Arial", Font.BOLD, 12));
        exportButton.setPreferredSize(new Dimension(150, 35));
        importButton.setPreferredSize(new Dimension(150, 35));
        
        // Add action listeners
        exportButton.addActionListener(e -> exportBookingsToCSV());
        importButton.addActionListener(e -> importBookingsFromCSV());
        
        // Add to panel
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        
        return buttonPanel;
    }

    // Advanced filtering method
    private void showAdvancedFilterDialog() {
        JDialog filterDialog = new JDialog();
        filterDialog.setTitle("Advanced Booking Filter");
        filterDialog.setSize(400, 500);
        filterDialog.setModal(true);
        filterDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Filter components
        JTextField guestNameField = new JTextField(20);
        JComboBox<String> statusCombo = new JComboBox<>(
            new String[]{"All", "Confirmed", "Pending", "Cancelled"}
        );
        JSpinner checkInDatePicker = new JSpinner(new SpinnerDateModel());
        JSpinner checkOutDatePicker = new JSpinner(new SpinnerDateModel());

        // Customize spinner editors
        JSpinner.DateEditor checkInEditor = new JSpinner.DateEditor(checkInDatePicker, "yyyy-MM-dd");
        checkInDatePicker.setEditor(checkInEditor);

        JSpinner.DateEditor checkOutEditor = new JSpinner.DateEditor(checkOutDatePicker, "yyyy-MM-dd");
        checkOutDatePicker.setEditor(checkOutEditor);

        // Add filter components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Guest Name:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(guestNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(statusCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        filterPanel.add(new JLabel("Check-In Date:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(checkInDatePicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        filterPanel.add(new JLabel("Check-Out Date:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(checkOutDatePicker, gbc);

        mainPanel.add(filterPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Apply Filter");
        JButton clearButton = new JButton("Clear Filter");

        applyButton.addActionListener(e -> {
            // Implement filter logic
            applyAdvancedFilter(
                guestNameField.getText(), 
                statusCombo.getSelectedItem().toString(),
                ((java.util.Date) checkInDatePicker.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                ((java.util.Date) checkOutDatePicker.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
            filterDialog.dispose();
        });

        clearButton.addActionListener(e -> {
            // Reset to original view
            bookingsTable.setRowSorter(null);
            filterDialog.dispose();
        });

        buttonPanel.add(applyButton);
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        filterDialog.add(mainPanel);
        filterDialog.setVisible(true);
    }

    private void applyAdvancedFilter(
        String guestName, 
        String status, 
        LocalDate checkInDate, 
        LocalDate checkOutDate
    ) {
        TableRowSorter<DefaultTableModel> sorter = 
            new TableRowSorter<>(tableModel);
        bookingsTable.setRowSorter(sorter);

        List<RowFilter<DefaultTableModel, Object>> filters = 
            new ArrayList<>();

        // Guest Name filter
        if (!guestName.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + guestName, 1));
        }

        // Status filter
        if (!status.equals("All")) {
            filters.add(RowFilter.regexFilter(status, 6));
        }

        // Combine filters
        RowFilter<DefaultTableModel, Object> combinedFilter = 
            RowFilter.andFilter(filters);
        sorter.setRowFilter(combinedFilter);
    }
}