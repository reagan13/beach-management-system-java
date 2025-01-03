package beachresort.repositories;

import beachresort.models.Staff;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {
    private Connection connection;

    public StaffRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createStaffTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createStaffTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS staff (" +
                "   staff_id INT AUTO_INCREMENT PRIMARY KEY," +
                "   user_id VARCHAR(50) UNIQUE NOT NULL," +
                "   name VARCHAR(100) NOT NULL," +
                "   phone_number VARCHAR(15) NOT NULL," +
                "   email VARCHAR(100) NOT NULL UNIQUE," +
                "   position ENUM('Manager', 'Receptionist', 'Housekeeping', 'Maintenance') NOT NULL," +
                "   status ENUM('Active', 'Inactive') NOT NULL," +
                "   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Staff table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating staff table: " + e.getMessage());
        }
    }

    private boolean validateUserForStaff(String userId) throws SQLException {
        String query = "SELECT role FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    return "staff".equalsIgnoreCase(role);
                }
            }
        }
        return false;
    }
    
    public boolean addStaff(Staff staff) {
        try {
            // Validate user exists and has staff role
            if (!validateUserForStaff(staff.getUserId())) {
                System.err.println("Invalid user or user is not a staff");
                return false;
            }

            // Check if staff already exists
            if (staffExists(staff.getUserId())) {
                System.err.println("Staff with this user ID already exists");
                return false;
            }

            // Check for existing email
            if (emailExists(staff.getEmail())) {
                System.err.println("Email already exists");
                return false;
            }

            String query = "INSERT INTO staff " +
                    "(user_id, name, phone_number, email, position, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, staff.getUserId());
                pstmt.setString(2, staff.getName());
                pstmt.setString(3, staff.getPhoneNumber());
                pstmt.setString(4, staff.getEmail());
                pstmt.setString(5, staff.getPosition());
                pstmt.setString(6, staff.getStatus());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding staff: " + e.getMessage());
            return false;
        }
    }
    

    public boolean updateStaff(Staff staff) {
        try {
            // Validate user exists and has staff role
            if (!validateUserForStaff(staff.getUserId())) {
                System.err.println("Invalid user or user is not a staff");
                return false;
            }

            String query = "UPDATE staff SET " +
                    "name = ?, phone_number = ?, email = ?, " +
                    "position = ?, status = ? " +
                    "WHERE user_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, staff.getName());
                pstmt.setString(2, staff.getPhoneNumber());
                pstmt.setString(3, staff.getEmail());
                pstmt.setString(4, staff.getPosition());
                pstmt.setString(5, staff.getStatus());
                pstmt.setString(6, staff.getUserId());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
            return false;
        }
    }
    

    // Check if email already exists
    // Check if email already exists
    private boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM staff WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // Validate user exists in users table
    private boolean validateUserExists(String userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Staff getStaffByUserId(String userId) {
        try {
            // Validate user exists and has staff role
            if (!validateUserForStaff(userId)) {
                System.err.println("Invalid user or user is not a staff");
                return null;
            }

            String query = "SELECT * FROM staff WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Staff(
                            rs.getString("name"),
                            rs.getString("phone_number"),
                            rs.getString("email"),
                            rs.getString("position"),
                            rs.getString("user_id"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff by user ID: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteStaff(String userId) {
        try {
            // Validate user exists and has staff role
            if (!validateUserForStaff(userId)) {
                System.err.println("Invalid user or user is not a staff");
                return false;
            }

            String query = "DELETE FROM staff WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, userId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting staff: " + e.getMessage());
            return false;
        }
    }

    

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT * FROM staff";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Staff staff = new Staff(
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("position"),
                        rs.getString("user_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"));
                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff: " + e.getMessage());
        }
        return staffList;
    }
    
    
    // Check if staff with this user ID already exists
    private boolean staffExists(String userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM staff WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}