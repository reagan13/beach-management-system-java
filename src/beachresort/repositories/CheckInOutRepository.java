package beachresort.repositories;

import beachresort.models.CheckInOut;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class CheckInOutRepository {
    private Connection connection;

    public CheckInOutRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createCheckInOutTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create check-in/check-out table if it does not exist
    private void createCheckInOutTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS check_in_out (" +
                "   id INT AUTO_INCREMENT PRIMARY KEY," +
                "   user_id INT NOT NULL," +
                "   customer_name VARCHAR(36) NOT NULL," +
                "   check_in_date DATE NOT NULL," +
                "   check_out_date DATE NOT NULL," +
                "   room_number VARCHAR(10) NOT NULL," +
                "   check_in_type VARCHAR(20) NOT NULL," +
                "   status VARCHAR(20) NOT NULL" +
                ");";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Validate User ID
    public boolean isValidCustomerUser(int userId) {
        String query = "SELECT COUNT(*) FROM users WHERE id = ? AND role = 'CUSTOMER'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
        }
        return false;
    }

    // Add a new check-in/check-out record
    public boolean addCheckInOut(CheckInOut checkInOut) {
        // Validate user first
        if (!isValidCustomerUser(checkInOut.getUserId())) {
            System.err.println("Invalid user ID or user is not a customer");
            return false;
        }

        String query = "INSERT INTO check_in_out (user_id, customer_name, check_in_date, check_out_date, room_number, check_in_type, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, checkInOut.getUserId());
            pstmt.setString(2, checkInOut.getCustomerName());
            pstmt.setDate(3, java.sql.Date.valueOf(checkInOut.getCheckInDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(checkInOut.getCheckOutDate()));
            pstmt.setString(5, checkInOut.getRoomNumber());
            pstmt.setString(6, checkInOut.getCheckInType());
            pstmt.setString(7, checkInOut.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding check-in/check-out record: " + e.getMessage());
            return false;
        }
    }

    // Update the status of a check-in/check-out record
    public boolean updateCheckInOut(CheckInOut checkInOut) {
        // Validate user first
        if (!isValidCustomerUser(checkInOut.getUserId())) {
            System.err.println("Invalid user ID or user is not a customer");
            return false;
        }

        // First, check the current status of the check-in/check-out record
        String statusQuery = "SELECT status FROM check_in_out WHERE id = ?";
        try (PreparedStatement statusPstmt = connection.prepareStatement(statusQuery)) {
            statusPstmt.setInt(1, checkInOut.getId());
            ResultSet rs = statusPstmt.executeQuery();

            if (rs.next()) {
                String currentStatus = rs.getString("status");
                if ("OUT".equals(currentStatus)) {
                    JOptionPane.showMessageDialog(null, "This check-in record has already been checked out.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving check-in/check-out status: " + e.getMessage());
            return false;
        }

        // Proceed to update the status if it is not already "OUT"
        String query = "UPDATE check_in_out SET status = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, checkInOut.getStatus());
            pstmt.setInt(2, checkInOut.getUserId());
            pstmt.setInt(3, checkInOut.getId());
            int rowsAffected = pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Checked out successfully.");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating check-in/check-out status: " + e.getMessage());
            return false;
        }
    }

    // Retrieve a check-in/check-out record by ID
    public CheckInOut getCheckInOutById(int id) {
        String query = "SELECT * FROM check_in_out WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new CheckInOut(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("customer_name"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getString("room_number"),
                        rs.getString("check_in_type"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving check-in/check-out record: " + e.getMessage());
        }
        return null; // Return null if no record is found
    }

    // Retrieve all check-in/check-out records
    public List<CheckInOut> getAllCheckInOuts() {
        List<CheckInOut> checkInOuts = new ArrayList<>();
        String query = "SELECT * FROM check_in_out";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                CheckInOut checkInOut = new CheckInOut(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("customer_name"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getString("room_number"),
                        rs.getString("check_in_type"),
                        rs.getString("status")
                );
                checkInOuts.add(checkInOut);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all check-in/check-out records: " + e.getMessage());
        }
        return checkInOuts; // Return the list of records
    }

    // New method to get check-in/out records by user ID
    public List<CheckInOut> getCheckInOutsByUserId(int userId) {
        List<CheckInOut> checkInOuts = new ArrayList<>();
        String query = "SELECT * FROM check_in_out WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CheckInOut checkInOut = new CheckInOut(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("customer_name"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getString("room_number"),
                        rs.getString("check_in_type"),
                        rs.getString("status")
                );
                checkInOuts.add(checkInOut);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving check-in/out records by user ID: " + e.getMessage());
        }
        return checkInOuts; // Return the list of records for the specified user
    }
}