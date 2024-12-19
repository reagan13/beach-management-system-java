package beachresort.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class BookingManagementPanel extends JPanel {
    // Dialog Mode Enum
    public enum DialogMode {
        CREATE, EDIT, WALKIN, VIEW, CANCEL
    }

    // Class-level variables
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JPanel buttonPanel;

        public BookingManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
        setupLayout();
        addSampleData();
    }

    private void initializeComponents() {
        // Column Names
        String[] columnNames = {
            "Booking ID", "Guest Name", "Room Type", 
            "Check-In", "Check-Out", "Total Guests", "Status", "Actions"
        };

        // Table Model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return JButton.class;
                return super.getColumnClass(columnIndex);
            }
        };

        // Initialize Table
        bookingsTable = new JTable(tableModel);
        customizeTable();

        // Search Field
        searchField = new JTextField(20);
    }

    private void customizeTable() {
        // Table customization logic
        bookingsTable.setRowHeight(30);
        bookingsTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer for action column
        bookingsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        bookingsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void setupLayout() {
        add(createSearchPanel(), BorderLayout.NORTH);
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void addSampleData() {
        Object[][] sampleBookings = {
            {"B001", "John Doe", "Standard Room", "2023-07-15", "2023-07-20", 2, "Confirmed", "View"},
            {"B002", "Jane Smith", "Deluxe Room", "2023-08-01", "2023-08-05", 1, "Pending", "View"},
            {"B003", "Mike Johnson", "Suite", "2023-09-10", "2023-09-15", 3, "Confirmed", "View"}
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
            ((JTextField)fields[0]).setText(tableModel.getValueAt(selectedRow, 1).toString());
        }

        if (fields[3] instanceof JComboBox) {
            ((JComboBox<?>)fields[3]).setSelectedItem(tableModel.getValueAt(selectedRow, 2));
        }

        if (fields[4] instanceof JTextField) {
            ((JTextField)fields[4]).setText(tableModel.getValueAt(selectedRow, 3).toString());
        }

        if (fields[5] instanceof JTextField) {
            ((JTextField)fields[5]).setText(tableModel.getValueAt(selectedRow, 4).toString());
        }

        if (fields[6] instanceof JTextField) {
            ((JTextField)fields[6]).setText(tableModel.getValueAt(selectedRow, 5).toString());
        }

        // Disable fields for view and cancel modes
        if (mode == DialogMode.VIEW || mode == DialogMode.CANCEL) {
            for (JComponent field : fields) {
                if (field instanceof JTextField) {
                    ((JTextField)field).setEditable(false);
                } else if (field instanceof JComboBox) {
                    ((JComboBox<?>)field).setEnabled(false);
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
            case WALKIN: return "Create Booking";
            case EDIT: return "Update Booking";
            case VIEW: return "Close";
            case CANCEL: return "Confirm Cancel";
            default: return "Submit";
        }
    }

    private boolean validateBookingForm(JPanel formPanel) {
        // Implement form validation logic
        // Check for empty required fields, date validity, etc.
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
        // Logic to create a new booking
        // Generate booking ID, add to table, etc.
    }

    private void updateExistingBooking(JPanel formPanel) {
        // Logic to update an existing booking
    }

    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Booking canceled successfully");
        }
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
        if (row == -1) return;

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

    // Main method for testing
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