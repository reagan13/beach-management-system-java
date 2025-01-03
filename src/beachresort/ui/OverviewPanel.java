package beachresort.ui;

import beachresort.repositories.*;
import beachresort.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OverviewPanel extends JPanel {
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private StaffRepository staffRepository;
    private PaymentRepository paymentRepository;

    // Labels to update on refresh
    private JLabel totalRoomsLabel;
    private JLabel availableRoomsLabel;
    private JLabel occupiedRoomsLabel;
    private JLabel totalStaffLabel;
    private JLabel totalBookingsLabel;
    private JLabel totalRevenueLabel;

    private JPanel dashboardPanel;

    public OverviewPanel() {
    
            // Initialize repositories
        roomRepository = new RoomRepository();
        bookingRepository = new BookingRepository();
        staffRepository = new StaffRepository();
        paymentRepository = new PaymentRepository();

        setLayout(new BorderLayout());

        // Title and Refresh Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel overviewLabel = new JLabel("Resort Dashboard", SwingConstants.CENTER);
        overviewLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(overviewLabel, BorderLayout.CENTER);

        // Refresh Button
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> refreshDashboard());
        titlePanel.add(refreshButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Create main dashboard panel
        dashboardPanel = createDashboardPanel();
        add(new JScrollPane(dashboardPanel), BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        dashboardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create labels for each analytics card
        totalRoomsLabel = new JLabel(getRoomCount(), SwingConstants.CENTER);
        availableRoomsLabel = new JLabel(getAvailableRoomCount(), SwingConstants.CENTER);
        occupiedRoomsLabel = new JLabel(getOccupiedRoomCount(), SwingConstants.CENTER);
        totalStaffLabel = new JLabel(getStaffCount(), SwingConstants.CENTER);
        totalBookingsLabel = new JLabel(getBookingCount(), SwingConstants.CENTER);
        totalRevenueLabel = new JLabel(getTotalRevenue(), SwingConstants.CENTER);

        // Styling for labels
        Font valueFont = new Font("Arial", Font.BOLD, 24);
        Color valueColor = Color.BLUE;

        totalRoomsLabel.setFont(valueFont);
        totalRoomsLabel.setForeground(valueColor);

        availableRoomsLabel.setFont(valueFont);
        availableRoomsLabel.setForeground(valueColor);

        occupiedRoomsLabel.setFont(valueFont);
        occupiedRoomsLabel.setForeground(valueColor);

        totalStaffLabel.setFont(valueFont);
        totalStaffLabel.setForeground(valueColor);

        totalBookingsLabel.setFont(valueFont);
        totalBookingsLabel.setForeground(valueColor);

        totalRevenueLabel.setFont(valueFont);
        totalRevenueLabel.setForeground(valueColor);

        // Analytics Cards
        dashboardPanel.add(createAnalyticsCard("Total Rooms", totalRoomsLabel));
        dashboardPanel.add(createAnalyticsCard("Available Rooms", availableRoomsLabel));
        dashboardPanel.add(createAnalyticsCard("Occupied Rooms", occupiedRoomsLabel));
        dashboardPanel.add(createAnalyticsCard("Total Staff", totalStaffLabel));
        dashboardPanel.add(createAnalyticsCard("Total Bookings", totalBookingsLabel));
        dashboardPanel.add(createAnalyticsCard("Total Revenue", totalRevenueLabel));

        return dashboardPanel;
    }

    private JPanel createAnalyticsCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
    // Analytics Methods

      // Refresh method
      private void refreshDashboard() {
          // Update each label with fresh data
          totalRoomsLabel.setText(getRoomCount());
          availableRoomsLabel.setText(getAvailableRoomCount());
          occupiedRoomsLabel.setText(getOccupiedRoomCount());
          totalStaffLabel.setText(getStaffCount());
          totalBookingsLabel.setText(getBookingCount());
          totalRevenueLabel.setText(getTotalRevenue());

          // Optional: Show a brief notification
          JOptionPane.showMessageDialog(this, "Dashboard Refreshed", "Refresh", JOptionPane.INFORMATION_MESSAGE);
      }

    
    private String getRoomCount() {
        try {
            String query = "SELECT COUNT(*) FROM rooms";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                
                return rs.next() ? rs.getString(1) : "0";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private String getAvailableRoomCount() {
        try {
            String query = "SELECT COUNT(*) FROM rooms WHERE status = 'Available'";
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    ResultSet rs = pstmt.executeQuery()) {

                return rs.next() ? rs.getString(1) : "0";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
    
    

    private String getOccupiedRoomCount() {
        try {
            String query = "SELECT COUNT(*) FROM rooms WHERE status = 'Occupied'";
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    ResultSet rs = pstmt.executeQuery()) {

                return rs.next() ? rs.getString(1) : "0";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
   

    private String getStaffCount() {
        try {
            String query = "SELECT COUNT(*) FROM staff";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                
                return rs.next() ? rs.getString(1) : "0";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private String getBookingCount() {
        try {
            String query = "SELECT COUNT(*) FROM bookings";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                
                return rs.next() ? rs.getString(1) : "0";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private String getTotalRevenue() {
        try {
            String query = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'Completed'";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                
                return rs.next() ? String.format("₱%.2f", rs.getDouble(1)) : "₱0.00";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}