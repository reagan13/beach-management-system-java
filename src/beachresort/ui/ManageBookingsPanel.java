package beachresort.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManageBookingsPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    public ManageBookingsPanel() {
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
    }

    private void addBooking(ActionEvent e) {
        JDialog addBookingDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New Booking", true);
        addBookingDialog.setSize(400, 300);
        addBookingDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Customer Name:"));
        JTextField customerNameField = new JTextField();
        panel.add(customerNameField);

        panel.add(new JLabel("Room Type:"));
        String[] roomTypes = {"Standard", "Deluxe", "Suite"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        panel.add(roomTypeCombo);

        panel.add(new JLabel("Check-in Date:"));
        JTextField checkInField = new JTextField();
        panel.add(checkInField);

        panel.add(new JLabel("Check-out Date:"));
        JTextField checkOutField = new JTextField();
        panel.add(checkOutField);

        panel.add(new JLabel("Status:"));
        String[] statuses = {"Confirmed", "Pending", "Cancelled"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        panel.add(statusCombo);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            // TODO: Implement booking saving logic
            Object[] newBooking = {
                "AUTO", // Booking ID
                customerNameField.getText(),
                roomTypeCombo.getSelectedItem(),
                checkInField.getText(),
                checkOutField.getText(),
                statusCombo.getSelectedItem()
            };
            tableModel.addRow(newBooking);
            
            JOptionPane.showMessageDialog(addBookingDialog, "Booking Added Successfully!");
            addBookingDialog.dispose();
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> addBookingDialog.dispose());
        panel.add(cancelButton);

        addBookingDialog.add(panel);
        addBookingDialog.setVisible(true);
    }

    private void editBooking(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to edit");
            return;
        }

        // Similar to addBooking dialog, but pre-fill with existing data
        JOptionPane.showMessageDialog(this, "Edit Booking functionality not fully implemented");
    }

    private void deleteBooking(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this booking?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Booking deleted successfully");
        }
    }

    private void refreshBookings(ActionEvent e) {
        // TODO: Implement actual refresh logic from database
        JOptionPane.showMessageDialog(this, "Refreshing bookings...");
    }
}