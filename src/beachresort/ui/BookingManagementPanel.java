package beachresort.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class BookingManagementPanel extends JPanel {
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
    private JTextField searchField;

    public BookingManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        String[] columnNames = {
            "Booking ID", "Guest Name", "Room Type", 
            "Check-In", "Check-Out", "Total Guests", "Status", "Actions"
        };

        Object[][] data = {
            {"B001", "John Doe", "Deluxe Ocean View", 
             "2023-06-15", "2023-06-20", 2, "Confirmed", "View"},
            {"B002", "Jane Smith", "Standard Room", 
             "2023-06-18", "2023-06-22", 1, "Pending", "View"}
        };

        tableModel = new DefaultTableModel(data, columnNames) {
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

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(createSearchPanel(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
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

        bookingsTable.getColumn("Actions").setCellRenderer(new JButtonRenderer());
        bookingsTable.getColumn("Actions").setCellEditor(new JButtonEditor(new JCheckBox()));
    }

    private void viewBookingDetails(int row) {
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
            "Check-Out Date", "Total Guests", "Total Price", "Status"
        };

        String[] values = {
            tableModel.getValueAt(row, 0).toString(),
            tableModel.getValueAt(row, 1).toString(),
            "123-456-7890",
            "guest@example.com",
            tableModel.getValueAt(row, 2).toString(),
            tableModel.getValueAt(row, 3).toString(),
            tableModel.getValueAt(row, 4).toString(),
            tableModel.getValueAt(row, 5).toString(),
            "$500",
            tableModel.getValueAt(row, 6).toString()
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

        for (int i = 0;i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            fields[i] = new JTextField(20);
            formPanel.add(fields[i], gbc);
        }

        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton(isModify ? "Update" : "Create");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (validateBookingForm(fields)) {
                if (isModify) {
                    updateBookingInTable(fields);
                } else {
                    addNewBookingToTable(fields);
                }
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private boolean validateBookingForm(JTextField[] fields) {
        for (int i = 0; i < 6; i++) {
            if (fields[i].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill all mandatory fields", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void updateBookingInTable(JTextField[] fields) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.setValueAt(fields[0].getText(), selectedRow, 1);
            tableModel.setValueAt(fields[3].getText(), selectedRow, 2);
            tableModel.setValueAt(fields[4].getText(), selectedRow, 3);
            tableModel.setValueAt(fields[5].getText(), selectedRow, 4);
            tableModel.setValueAt(fields[6].getText(), selectedRow, 5);
        }
    }

    private void addNewBookingToTable(JTextField[] fields) {
        Object[] newRow = {
            "B00" + (tableModel.getRowCount() + 1),
            fields[0].getText(),
            fields[3].getText(),
            fields[4].getText(),
            fields[5].getText(),
            fields[6].getText(),
            "Confirmed",
            "View"
        };
        tableModel.addRow(newRow);
    }

    private void cancelBooking(int row) {
        if (row != -1) {
            tableModel.setValueAt("Cancelled", row, 6);
        }
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
            "Guest Name*", "Contact Number*", "ID Type", "ID Number", 
            "Room Type*", "Check-In Date*", "Check-Out Date*", 
            "Total Guests*", "Payment Method"
        };

        JTextField[] fields = new JTextField[labels.length];
        JComboBox<String> idTypeCombo, paymentMethodCombo;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            
            if (labels[i].equals("ID Type")) {
                idTypeCombo = new JComboBox<>(new String[]{"Passport", "Driver's License", "National ID"});
                formPanel.add(idTypeCombo, gbc);
            } else if (labels[i].equals("Payment Method")) {
                paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card"});
                formPanel.add(paymentMethodCombo, gbc);
            } else {
                fields[i] = new JTextField(20);
                formPanel.add(fields[i], gbc);
            }
        }

        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Create Booking");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (validateBookingForm(fields)) {
                addNewBookingToTable(fields);
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            bookingsTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter(searchText));
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        return searchPanel;
    }
}