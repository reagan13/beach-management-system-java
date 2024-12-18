package beachresort.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookingManagementPanel extends JPanel {
    // Neutral Professional Color Palette
    private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);
    private static final Color PRIMARY_COLOR = new Color(55, 65, 81);
    private static final Color SECONDARY_COLOR = new Color(75, 85, 99);
    private static final Color HOVER_COLOR = new Color(99, 102, 241);

    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    public BookingManagementPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);

        // Table setup remains the same
        String[] columnNames = {
            "Booking ID", "Guest Name", "Room", 
            "Check-In", "Check-Out", "Status"
        };

        Object[][] data = {
            {"B001", "John Doe", "101", "2023-06-15", "2023-06-20", "Confirmed"},
            {"B002", "Jane Smith", "202", "2023-06-18", "2023-06-22", "Pending"}
        };

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        customizeTable();
    }

    private void customizeTable() {
        bookingsTable.setRowHeight(40);
        bookingsTable.setSelectionBackground(new Color(230, 232, 240));
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        JTableHeader header = bookingsTable.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private JButton createInteractiveButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover and interaction effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Interactive Buttons
        JButton walkInBookingBtn = createInteractiveButton("Walk-in Booking", PRIMARY_COLOR);
        JButton modifyBookingBtn = createInteractiveButton("Modify Booking", SECONDARY_COLOR);
        JButton cancelBookingBtn = createInteractiveButton("Cancel Booking", Color.RED);

        // Add Interaction Listeners
        walkInBookingBtn.addActionListener(e -> showWalkInBookingDialog());
        modifyBookingBtn.addActionListener(e -> modifySelectedBooking());
        cancelBookingBtn.addActionListener(e -> cancelSelectedBooking());

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(walkInBookingBtn);
        buttonPanel.add(modifyBookingBtn);
        buttonPanel.add(cancelBookingBtn);

        // Table Scroll Pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void showWalkInBookingDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Walk-in Booking");
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Interactive Form Fields
        addFormField(panel, "Guest Name:", new JTextField());
        addFormField(panel, "Contact Number:", new JTextField());
        
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
            "Standard", "Deluxe", "Suite"
        });
        addFormField(panel, "Room Type:", roomTypeCombo);

        addFormField(panel, "Check-In Date:", new JTextField());
        addFormField(panel, "Check-Out Date:", new JTextField());

        JButton confirmButton = createInteractiveButton("Confirm Booking", PRIMARY_COLOR);
        confirmButton.addActionListener(e -> {
            // Booking confirmation logic
            dialog.dispose();
        });

        panel.add(new JLabel()); // Spacer
        panel.add(confirmButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JComponent component) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(jLabel);
        panel.add(component);
    }

    private void modifySelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to modify", 
                "No Booking Selected", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Modify booking logic
        JOptionPane.showMessageDialog(this, 
            "Modify Booking Functionality", 
            "Modify Booking", 
            JOptionPane.PLAIN_MESSAGE);
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to cancel", 
                "No Booking Selected", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this booking?", 
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, 
                "Booking Cancelled Successfully", 
                "Cancellation", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}