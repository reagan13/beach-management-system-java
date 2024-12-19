package beachresort.ui;

import beachresort.models.Booking;
import beachresort.repositories.BookingRepository;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class BookingManagementPanel extends JPanel {
    // Dialog Mode Enum
    public enum DialogMode {
        CREATE, EDIT, WALKIN, VIEW, CANCEL
    }

    // Class-level variables
    // UI Components
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JPanel buttonPanel;

    // Database Repository
    private BookingRepository bookingRepository;

    public BookingManagementPanel() {
        // Initialize repository
        bookingRepository = new BookingRepository();

        setLayout(new BorderLayout(10, 10));
        initializeComponents();
        setupLayout();
        loadBookingsFromDatabase();
    }

    private void initializeComponents() {
        // Column Names
        String[] columnNames = {
                "Booking ID", "Guest Name", "Room Type",
                "Check-In", "Check-Out", "Total Guests", "Status"
        };

        // Table Model
        tableModel = new DefaultTableModel(columnNames, 0);
        bookingsTable = new JTable(tableModel);
        
          
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable(searchField.getText());
            }
        });
    }

    // private void customizeTable() {
    //     // Table customization logic
    //     bookingsTable.setRowHeight(30);
    //     bookingsTable.getTableHeader().setReorderingAllowed(false);

    //     // Custom renderer for action column
    //     bookingsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
    //     bookingsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));
    // }


      private void setupLayout() {
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // Table in Scroll Pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Create Buttons
        JButton addButton = createButton("Add Booking", e -> showBookingDialog(DialogMode.CREATE));
        JButton editButton = createButton("Edit Booking", e -> showBookingDialog(DialogMode.EDIT));
        JButton cancelButton = createButton("Cancel Booking", e -> cancelBooking());
        JButton refreshButton = createButton("Refresh", e -> loadBookingsFromDatabase());

        // Add Buttons to Panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBookingsFromDatabase() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch bookings from database
        List<Booking> bookings = bookingRepository.getAllBookings();
        
        // Populate table
        for (Booking booking : bookings) {
            Object[] row = {
                booking.getBookingId(),
                booking.getGuestName(),
                booking.getRoomType(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalGuests(),
                booking.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addSampleData() {
        Object[][] sampleBookings = {
                { "B001", "John Doe", "Standard Room", "2023-07-15", "2023-07-20", 2, "Confirmed", "View" },
                { "B002", "Jane Smith", "Deluxe Room", "2023-08-01", "2023-08-05", 1, "Pending", "View" },
                { "B003", "Mike Johnson", "Suite", "2023-09-10", "2023-09-15", 3, "Confirmed", "View" }
        };

        for (Object[] booking : sampleBookings) {
            tableModel.addRow(booking);
        }
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search Bookings:");

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable(searchField.getText());
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        return searchPanel;
    }
     private void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookingsTable.setRowSorter(sorter);

        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton[] buttons = {
                createButton("Add New Booking", e -> showBookingDialog(DialogMode.CREATE)),
                createButton("Walk-in Booking", e -> showBookingDialog(DialogMode.WALKIN)),
                createButton("Modify Booking", e -> handleModifyBooking()),
                createButton("View Details", e -> handleViewBooking()),
                createButton("Cancel Booking", e -> handleCancelBooking())
        };

        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        return buttonPanel;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setPreferredSize(new Dimension(150, 35));
        return button;
    }

    private void handleModifyBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow != -1) {
            showBookingDialog(DialogMode.EDIT);
        } else {
            showErrorMessage("Please select a booking to modify");
        }
    }

    private void handleViewBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow != -1) {
            showBookingDialog(DialogMode.VIEW);
        } else {
            showErrorMessage("Please select a booking to view");
        }
    }

    private void handleCancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow != -1) {
            showBookingDialog(DialogMode.CANCEL);
        } else {
            showErrorMessage("Please select a booking to cancel");
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

   private void showBookingDialog(DialogMode mode) {
        JDialog dialog = new JDialog();
        dialog.setTitle(getDialogTitle(mode));
        dialog.setSize(500, 600);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Components
        JPanel formPanel = createFormPanel(mode);
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createDialogButtonPanel(dialog, mode, formPanel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    private String getDialogTitle(DialogMode mode) {
        switch (mode) {
            case CREATE: return "New Booking";
            case EDIT: return "Modify Booking";
            case WALKIN: return "Walk-in Booking";
            case VIEW: return "View Booking Details";
            case CANCEL: return "Cancel Booking";
            default: return "Booking Management";
        }
    }
  private JPanel createFormPanel(DialogMode mode) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {
            "Guest Name*", "Contact Number*", "Email*", 
            "Room Type*", "Check-In Date*", "Check-Out Date*", 
            "Total Guests*", "Special Requests"
        };

        JComponent[] fields = new JComponent[labels.length];
        
        // Room Type Combo Box
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
            "Standard Room", "Deluxe Room", "Suite", "Ocean View Room"
        });
        fields[3] = roomTypeCombo;

        // Create text fields
        for (int i = 0; i < labels.length; i++) {
            if (i != 3) {  // Skip room type combo
                fields[i] = new JTextField(20);
            }
        }

        // Populate fields based on mode
        populateFieldsBasedOnMode(mode, fields);

        // Add components to form panel
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        return formPanel;
    }

    private void populateFieldsBasedOnMode(DialogMode mode, JComponent[] fields) {
        int selectedRow = bookingsTable.getSelectedRow();

        // Only populate if a row is selected and mode requires it
        if (selectedRow == -1 ||
                (mode != DialogMode.EDIT && mode != DialogMode.VIEW && mode != DialogMode.CANCEL)) {
            return;
        }

        // Populate fields from selected row
        if (fields[0] instanceof JTextField) {
            ((JTextField) fields[0]).setText(tableModel.getValueAt(selectedRow, 1).toString());
        }

        if (fields[1] instanceof JTextField) {
            ((JTextField) fields[1]).setText(tableModel.getValueAt(selectedRow, 6).toString()); // Contact Number
        }

        if (fields[2] instanceof JTextField) {
            ((JTextField) fields[2]).setText(""); // Email (you might want to add this to your table model)
        }

        if (fields[3] instanceof JComboBox) {
            ((JComboBox<?>) fields[3]).setSelectedItem(tableModel.getValueAt(selectedRow, 2));
        }

        if (fields[4] instanceof JTextField) {
            ((JTextField) fields[4]).setText(tableModel.getValueAt(selectedRow, 3).toString());
        }

        if (fields[5] instanceof JTextField) {
            ((JTextField) fields[5]).setText(tableModel.getValueAt(selectedRow, 4).toString());
        }

        if (fields[6] instanceof JTextField) {
            ((JTextField) fields[6]).setText(tableModel.getValueAt(selectedRow, 5).toString());
        }

        // Disable fields for view and cancel modes
        if (mode == DialogMode.VIEW || mode == DialogMode.CANCEL) {
            for (JComponent field : fields) {
                if (field instanceof JTextField) {
                    ((JTextField) field).setEditable(false);
                } else if (field instanceof JComboBox) {
                    ((JComboBox<?>) field).setEnabled(false);
                }
            }
        }
    }
    
    private JPanel createDialogButtonPanel(JDialog dialog, DialogMode mode, JPanel formPanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton primaryButton = new JButton(getPrimaryButtonText(mode));
        JButton cancelButton = new JButton("Cancel");

        primaryButton.addActionListener(e -> {
            if (validateBookingForm(formPanel)) {
                processBookingAction(mode, formPanel);
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(primaryButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private String getPrimaryButtonText(DialogMode mode) {
        switch (mode) {
            case CREATE:
            case WALKIN:
                return "Create Booking";
            case EDIT:
                return "Update Booking";
            case VIEW:
                return "Close";
            case CANCEL:
                return "Confirm Cancel";
            default:
                return "Submit";
        }
    }

   private boolean validateBookingForm(JPanel formPanel) {
        Component[] components = formPanel.getComponents();
        
        // Validate Guest Name
        JTextField guestNameField = (JTextField) components[1];
        if (guestNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Guest Name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Contact Number
        JTextField contactField = (JTextField) components[3];
        if (!contactField.getText().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Invalid Contact Number. Use 10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Email
        JTextField emailField = (JTextField) components[5];
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid Email Format", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Room Type
        JComboBox<?> roomTypeCombo = (JComboBox<?>) components[7];
        if (roomTypeCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a Room Type", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Dates
        JTextField checkInField = (JTextField) components[9];
        JTextField checkOutField = (JTextField) components[11];
        try {
            LocalDate checkIn = LocalDate.parse(checkInField.getText());
            LocalDate checkOut = LocalDate.parse(checkOutField.getText());

            if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
                JOptionPane.showMessageDialog(this, "Invalid Date Range", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format. Use YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Total Guests
        JTextField guestsField = (JTextField) components[13];
        try {
            int guests = Integer.parseInt(guestsField.getText());
            if (guests <= 0 || guests > 10) {
                JOptionPane.showMessageDialog(this, "Total Guests must be between 1 and 10", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Number of Guests", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    private void processBookingAction(DialogMode mode, JPanel formPanel) {
        switch (mode) {
            case CREATE:
            case WALKIN:
                createNewBooking(formPanel);
                break;
            case EDIT:
                updateExistingBooking(formPanel);
                break;
            case CANCEL:
                cancelBooking();
                break;
        }
    }

    private void createNewBooking(JPanel formPanel) {
        Component[] components = formPanel.getComponents();

        // Extract values from form components
        String guestName = ((JTextField) components[1]).getText();
        String contactNumber = ((JTextField) components[3]).getText();
        String email = ((JTextField) components[5]).getText();
        String roomType = ((JComboBox<?>) components[7]).getSelectedItem().toString();
        String checkInDate = ((JTextField) components[9]).getText();
        String checkOutDate = ((JTextField) components[11]).getText();
        int totalGuests = Integer.parseInt(((JTextField) components[13]).getText());

        // Generate unique booking ID
        String bookingId = generateBookingId();

        // Create Booking object
        Booking newBooking = new Booking(
                bookingId,
                guestName,
                roomType,
                LocalDate.parse(checkInDate),
                LocalDate.parse(checkOutDate),
                totalGuests,
                "Confirmed",
                contactNumber,
                email);

        // Save to database
        if (bookingRepository.addBooking(newBooking)) {
            loadBookingsFromDatabase();
            JOptionPane.showMessageDialog(this, "Booking created successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create booking", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateExistingBooking(JPanel formPanel) {
        // Similar to createNewBooking, but use updateBooking method
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1)
            return;

        String bookingId = tableModel.getValueAt(selectedRow, 0).toString();

        // Collect form data (similar to createNewBooking)
        String guestName = getFieldValue(formPanel, 0);
        String contactNumber = getFieldValue(formPanel, 1);
        String email = getFieldValue(formPanel, 2);
        String roomType = ((JComboBox<?>) getFormComponent(formPanel, 3)).getSelectedItem().toString();
        String checkInDate = getFieldValue(formPanel, 4);
        String checkOutDate = getFieldValue(formPanel, 5);
        String totalGuests = getFieldValue(formPanel, 6);

        // Validate input
        if (!validateBookingDetails(guestName, contactNumber, email, checkInDate, checkOutDate, totalGuests)) {
            return;
        }

        // Create Booking object
        Booking updatedBooking = new Booking(
                bookingId,
                guestName,
                roomType,
                LocalDate.parse(checkInDate),
                LocalDate.parse(checkOutDate),
                Integer.parseInt(totalGuests),
                "Confirmed",
                contactNumber,
                email);

        // Update in database
        if (bookingRepository.updateBooking(updatedBooking)) {
            // Refresh table
            loadBookingsFromDatabase();
            JOptionPane.showMessageDialog(this, "Booking updated successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update booking", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel");
            return;
        }

        String bookingId = tableModel.getValueAt(selectedRow, 0).toString();

        // Cancel booking in database
        if (bookingRepository.cancelBooking(bookingId)) {
            // Refresh table
            loadBookingsFromDatabase();
            JOptionPane.showMessageDialog(this, "Booking canceled successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to cancel booking", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to generate unique booking ID
    private String generateBookingId() {
        return "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Utility methods to get form component values
    private String getFieldValue(JPanel formPanel, int index) {
        Component[] components = formPanel.getComponents();
        if (components[index * 2 + 1] instanceof JTextField) {
            return ((JTextField) components[index * 2 + 1]).getText();
        }
        return "";
    }

    private Component getFormComponent(JPanel formPanel, int index) {
        Component[] components = formPanel.getComponents();
        return components[index * 2 + 1];
    }

    // Custom Button Renderer for Action Column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            setText((value == null) ? "View" : value.toString());

            // Styling
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }

            return this;
        }
    }

    // Custom Button Editor for Action Column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {

            label = (value == null) ? "View" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button click
                showBookingDetailsDialog(bookingsTable.getSelectedRow());
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

    // Booking Details Dialog
    private void showBookingDetailsDialog(int row) {
        if (row == -1)
            return;

        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Booking Details");
        detailsDialog.setSize(400, 500);
        detailsDialog.setModal(true);
        detailsDialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {
                "Booking ID:", "Guest Name:", "Room Type:",
                "Check-In Date:", "Check-Out Date:",
                "Total Guests:", "Status:"
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            detailsPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            JLabel valueLabel = new JLabel(tableModel.getValueAt(row, i).toString());
            detailsPanel.add(valueLabel, gbc);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());

        detailsPanel.add(closeButton, gbc);

        detailsDialog.add(new JScrollPane(detailsPanel));
        detailsDialog.setVisible(true);
    }

    // Utility Methods for Date Handling
    private boolean isValidDate(String dateString) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Advanced Validation Method
    private boolean validateBookingDetails(
            String guestName,
            String contactNumber,
            String email,
            String checkInDate,
            String checkOutDate,
            String totalGuests) {

        // Validate guest name
        if (guestName == null || guestName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Guest name cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate contact number
        if (contactNumber == null || !contactNumber.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid contact number. Use 10 digits.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email format",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate dates
        if (!isValidDate(checkInDate) || !isValidDate(checkOutDate)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use YYYY-MM-DD",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate total guests
        try {
            int guests = Integer.parseInt(totalGuests);
            if (guests <= 0 || guests > 10) {
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

        return true;
    }

  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Booking Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new BookingManagementPanel());
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}