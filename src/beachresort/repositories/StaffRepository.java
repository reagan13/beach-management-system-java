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
                "   position ENUM('Manager', 'Receptionist', 'Housekeeping', 'Maintenance','UNASSIGNED') NOT NULL," +
                "   status ENUM('Active', 'Inactive','Terminated') NOT NULL," +
                "   task VARCHAR(250)," +
                "   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Staff table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating staff table: " + e.getMessage());
        }
    }

    public List<Integer> getAllStaffIds() {
        List<Integer> staffIds = new ArrayList<>();
        String query = "SELECT staff_id FROM staff where position = 'UNASSIGNED'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                staffIds.add(rs.getInt("staff_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching staff IDs: " + e.getMessage());
        }

        return staffIds;
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
                    "(user_id, position, status, tas;) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, staff.getUsername());
                pstmt.setString(2, staff.getPosition());
                pstmt.setString(3, staff.getStatus());
                pstmt.setString(4, staff.getTask());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding staff: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStaff(String position, String status, String task,int staffId) {
        String query = "UPDATE staff SET " +
                "position = ?, status = ?, task = ? " +
                "WHERE staff_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, position);
            pstmt.setString(2, status);
            pstmt.setString(3, task);
            pstmt.setInt(4, staffId);
            
            

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
            return false;
        }
    }

    public Staff getStaffByStaffId(int staffId) {
        String query = "SELECT s.staff_id, s.user_id, s.position, s.status,s.task, u.username, u.password, u.email, u.full_name, u.address, u.contact_number "
                +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE s.staff_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Staff(
                            rs.getInt("user_id"),
                            rs.getInt("staff_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            rs.getString("address"),
                            rs.getString("contact_number"),
                            rs.getString("position"),
                            rs.getString("status"),
                            rs.getString("task"));

                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff by staff ID: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteStaff(int userId) {
        String query = "UPDATE staff SET status = 'Terminated' WHERE staff_id = ?";
            
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error terminating staff: " + e.getMessage());
            return false;
        }

    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT s.staff_id, s.user_id, u.username, u.password, u.email, u.full_name, u.address, u.contact_number, s.position,  s.status, s.task "
                +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE s.position != 'UNASSIGNED'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                staffList.add(new Staff(
                        rs.getInt("user_id"),
                        rs.getInt("staff_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("address"),
                        rs.getString("contact_number"),
                        rs.getString("position"),
                        rs.getString("status"),
                        rs.getString("task")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all staff: " + e.getMessage());
        }
        return staffList;
    }

    public Staff getStaffByUserId (int userId) {
        String query = "SELECT s.staff_id, s.user_id, u.username, u.password, u.email, u.full_name, u.address, u.contact_number, s.position, s.status, s.task " +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE s.user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Staff(
                            rs.getInt("user_id"),
                            rs.getInt("staff_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            rs.getString("address"),
                            rs.getString("contact_number"),
                            rs.getString("position"),
                            rs.getString("status"),
                            rs.getString("task"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving staff by user ID: " + e.getMessage());
        }
        return null;
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