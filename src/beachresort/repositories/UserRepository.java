package beachresort.repositories;

import beachresort.models.User;
import beachresort.database.DatabaseConnection;

import java.sql.*;

public class UserRepository {
    private final Connection connection;

    public UserRepository() throws SQLException {
        connection = DatabaseConnection.getConnection();
        createUserTableIfNotExists();
    }

    private void createUserTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "address VARCHAR(255), " + // New address column
                "contact_number VARCHAR(20), " + // New contact number column
                "role VARCHAR(20) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createUser (User user) {
        String query = "INSERT INTO users (username, password, email, full_name, address, contact_number, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Validate input
            if (user == null) {
                System.err.println("Attempting to create null user");
                return false;
            }

            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                System.err.println("Username cannot be null or empty");
                return false;
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                System.err.println("Password cannot be null or empty");
                return false;
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println("Email cannot be null or empty");
                return false;
            }

            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                System.err.println("Full name cannot be null or empty");
                return false;
            }

            if (user.getRole() == null) {
                System.err.println("Role cannot be null");
                return false;
            }

            // Prepare statement
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getAddress()); // Set address
            pstmt.setString(6, user.getContactNumber()); // Set contact number
            pstmt.setString(7, user.getRole().name()); // Assuming UserRole is an enum

            // Execute and return result
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Log specific SQL error
            System.err.println("SQL Error during user creation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public User findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
             
            try (ResultSet rs = pstmt.executeQuery()) {
             
                if (rs.next()) {
                    String roleString = rs.getString("role");
                    System.out.println("the role is"+ roleString);;
                    // Create the User object
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            rs.getString("address"), // Retrieve address
                            rs.getString("contact_number") // Retrieve contact number
                    ) {
                        @Override
                        public UserRole getRole() {
                            return UserRole.valueOf(roleString.toUpperCase());
                        }
                    
                    };
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role found in database: " + e.getMessage());
        }
        return null; // Return null if user is not found or an error occurs
    }


    public Integer validateUser(String username, String password) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null; // Return null if user is not found
    }




}