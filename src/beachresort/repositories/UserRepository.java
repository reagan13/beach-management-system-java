package beachresort.repositories;

import beachresort.models.User;
import beachresort.database.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

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
                "role VARCHAR(20) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public boolean createUser(User user) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
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

            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                System.err.println("Role cannot be null or empty");
                return false;
            }

            // Prepare statement
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

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

    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            null,  // email removed
                            null   // full name removed
                    );
                 
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Integer validateUser(String username, String password, String role) {
        String query = "SELECT id FROM users WHERE username = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Return user ID if found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during user validation: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if no user matches
    }




}