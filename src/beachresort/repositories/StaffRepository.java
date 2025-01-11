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
    private UserRepository userRepository; // Reference to UserRepository

    public StaffRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            this.userRepository = new UserRepository(); // Initialize UserRepository
            createStaffTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createStaffTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS staff (" +
                "   staff_id INT AUTO_INCREMENT PRIMARY KEY," +
                "   user_id VARCHAR(50)," + 
                "   position ENUM('Manager', 'Receptionist', 'Housekeeping', 'Maintenance') NOT NULL," +
                "   status ENUM('Active', 'Inactive','Terminated') NOT NULL," +
                "   add_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," + // Add date column
                "   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Staff table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating staff table: " + e.getMessage());
        }
    }

    public boolean addStaff(Staff staff) {
        try {
            // Validate user exists
            if (!userRepository.usernameExists(staff.getUsername())) {
                System.err.println("Invalid user ID");
                return false;
            }

            // Check if staff already exists
            if (staffExists(staff.getUsername())) {
                System.err.println("Staff with this user ID already exists");
                return false;
            }

            String query = "INSERT INTO staff " +
                    "(user_id, position, status, add_date) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, staff.getUsername());
                pstmt.setString(2, staff.getPosition());
                pstmt.setString(3, staff.getStatus());
                pstmt.setTimestamp(4, new java.sql.Timestamp(staff.getAddDate().getTime())); // Set add date

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding staff: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStaff(Staff staff) {
        String query = "UPDATE staff SET " +
                "position = ?, status = ? " +
                "WHERE staff_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, staff.getPosition());
            pstmt.setString(2, staff.getStatus());
            pstmt.setString(3, staff.getUsername());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
            return false;
        }
    }

    public Staff getStaffByStaffId(int staffId) {
        String query = "SELECT s.staff_id, s.position, s.status, s.add_date, u.username, u.password, u.email, u.full_name, u.address, u.contact_number "
                +
                "FROM staff s " +
                "JOIN user u ON s.user_id = u.id " +
                "WHERE s.staff_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, staffId); // Use staffId as an integer
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Staff(
                            rs.getInt("id"), // id from the user table (assuming this is the primary key for the User class)
                            rs.getInt("staff_id"), // staffId from the staff table
                            rs.getString("username"), // username from the user table
                            rs.getString("password"), // password from the user table
                            rs.getString("email"), // email from the user table
                            rs.getString("full_name"), // fullName from the user table
                            rs.getString("address"), // address from the user table
                            rs.getString("contact_number"), // contact number from the user table
                            rs.getString("position"), // position from the staff table
                            rs.getTimestamp("add_date"), // addDate from the staff table
                            rs.getString("status") // status from the staff table
                    );

                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff by staff ID: " + e.getMessage());
        }
        return null;
    }


    public boolean deleteStaff(String userId) {
        String query = "DELETE FROM staff WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting staff: " + e.getMessage());
            return false;
        }
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT s.staff_id, s.user_id, u.username, u.password, u.email, u.full_name, u.address, u.contact_number, s.position, s.add_date, s.status "
                +
                "FROM staff s " +
                "JOIN user u ON s.user_id = u.id"; // Join to get user details

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                staffList.add(new Staff(
                        rs.getInt("id"), // id from the user table
                        rs.getInt("staff_id"), // staffId from the staff table
                        rs.getString("username"), // username from the user table
                        rs.getString("password"), // password from the user table
                        rs.getString("email"), // email from the user table
                        rs.getString("full_name"), // fullName from the user table
                        rs.getString("address"), // address from the user table
                        rs.getString("contact_number"), // contact number from the user table
                        rs.getString("position"), // position from the staff table
                        rs.getTimestamp("add_date"), // addDate from the staff table
                        rs.getString("status") // status from the staff table
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all staff: " + e.getMessage());
        }
        return staffList;
    }


    private boolean staffExists(String userId) {
        String query = "SELECT COUNT(*) FROM staff WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if staff exists: " + e.getMessage());
        }
        return false;
    }
}