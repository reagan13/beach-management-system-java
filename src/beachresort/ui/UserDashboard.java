package beachresort.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserDashboard extends JFrame {
    private String username;
    private String role;

    public UserDashboard(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();
    }

    private void initComponents() {
        setTitle("User Dashboard - Beach Resort Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add Tabs
        tabbedPane.addTab("Available Rooms", createRoomsPanel());
        tabbedPane.addTab("Available Facilities", createFacilitiesPanel());
        tabbedPane.addTab("My Bookings", createMyBookingsPanel());
        tabbedPane.addTab("Book Room", createBookRoomPanel());
        tabbedPane.addTab("Book Facility", createBookFacilityPanel());

        // Add user info panel
        JPanel headerPanel = createHeaderPanel();

        // Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + username, SwingConstants.LEFT);
        JLabel roleLabel = new JLabel("Role: " + role, SwingConstants.RIGHT);
        
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        roleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(roleLabel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createRoomsPanel() {
        JPanel roomsPanel = new JPanel(new BorderLayout());
        
        // Table of Available Rooms
        String[] columnNames = {"Room Number", "Type", "Capacity", "Price", "Status"};
        Object[][] data = {
            {"101", "Standard", "2", "₱1,000/night", "Available"},
            {"102", "Deluxe", "4", "₱2,500/night", "Available"},
            {"103", "Suite", "6", "₱5,000/night", "Available"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable roomTable = new JTable(model);
        
        roomsPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        
        return roomsPanel;
    }

    private JPanel createFacilitiesPanel() {
        JPanel facilitiesPanel = new JPanel(new BorderLayout());
        
        // Table of Available Facilities
        String[] columnNames = {"Facility", "Description", "Price", "Status"};
        Object[][] data = {
            {"Swimming Pool", "Outdoor Pool", "₱500/hour", "Available"},
            {"Gym", "Fitness Center", "₱300/hour", "Available"},
            {"Spa", "Relaxation Services", "₱1,500/session", "Available"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable facilitiesTable = new JTable(model);
        
        facilitiesPanel.add(new JScrollPane(facilitiesTable), BorderLayout.CENTER);
        
        return facilitiesPanel;
    }

    private JPanel createMyBookingsPanel() {
        JPanel bookingsPanel = new JPanel(new BorderLayout());
        
        // Table of User's Bookings
        String[] columnNames = {"Booking ID", "Type", "Details", "Date", "Total Cost", "Status", "Action"};
        Object[][] data = {
            {"B001", "Room", "Deluxe Room 102", "2023-06-15", "₱2,500", "Confirmed", "Cancel Request"},
            {"B002", "Facility", "Spa Session", "2023-06-20", "₱1,500", "Confirmed", "Cancel Request"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable bookingsTable = new JTable(model);
        
        bookingsPanel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        
        return bookingsPanel;
    }

    private JPanel createBookRoomPanel() {
        JPanel bookRoomPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        bookRoomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Room Selection
        bookRoomPanel.add(new JLabel("Select Room Type:"));
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{
            "Standard (₱1,000/night)", 
            "Deluxe (₱2,500/night)", 
            "Suite (₱5,000/night)"
        });
        bookRoomPanel.add(roomTypeCombo);

        // Check-in Date
        bookRoomPanel.add(new JLabel("Check-in Date:"));
        JTextField checkInDateField = new JTextField();
        bookRoomPanel.add(checkInDateField);

        // Check-out Date
        bookRoomPanel.add(new JLabel("Check-out Date:"));
        JTextField checkOutDateField = new JTextField();
        bookRoomPanel.add(checkOutDateField);

        // Number of Guests
        bookRoomPanel.add(new JLabel("Number of Guests:"));
        JSpinner guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
        bookRoomPanel.add(guestsSpinner);

        // Special Requests
        bookRoomPanel.add(new JLabel("Special Requests:"));
        JTextArea specialRequestsArea = new JTextArea(3, 20);
        bookRoomPanel.add(new JScrollPane(specialRequestsArea));

        // Book Button
        JButton bookButton = new JButton("Book Room");
        bookRoomPanel.add(bookButton);

        return bookRoomPanel;
    }

    private JPanel createBookFacilityPanel() {
        JPanel bookFacilityPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        bookFacilityPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Facility Selection
        bookFacilityPanel.add(new JLabel("Select Facility:"));
        JComboBox<String> facilityCombo = new JComboBox<>(new String[]{
            "Swimming Pool (₱500/hour)", 
            "Gym (₱300/hour)", 
            "Spa (₱1,500/session)"
        });
        bookFacilityPanel.add(facilityCombo);

        // Booking Date
        bookFacilityPanel.add(new JLabel("Booking Date:"));
        JTextField bookingDateField = new JTextField();
        bookFacilityPanel.add(bookingDateField);

        // Time Slot
        bookFacilityPanel.add(new JLabel("Time Slot:"));
        JTextField timeSlotField = new JTextField();
        bookFacilityPanel.add(timeSlotField);

        // Number of People
        bookFacilityPanel.add(new JLabel("Number of People:"));
        JSpinner peopleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        bookFacilityPanel.add(peopleSpinner);

        // Special Requests
        bookFacilityPanel.add(new JLabel("Special Requests:"));
        JTextArea facilityRequestsArea = new JTextArea(3, 20);
        bookFacilityPanel.add(new JScrollPane(facilityRequestsArea));

        // Book Button
        JButton bookFacilityButton = new JButton("Book Facility");
        bookFacilityPanel.add(bookFacilityButton);

        return bookFacilityPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserDashboard dashboard = new UserDashboard("John Doe", "Customer");
            dashboard.setVisible(true);
        });
    }
}