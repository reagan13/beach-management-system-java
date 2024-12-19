package beachresort.repositories;

import beachresort.models.User;
import beachresort.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private Connection connection;

    public UserRepository() throws SQLException {
        connection = DatabaseConnection.getConnection();
        createUserTableIfNotExists();
    }

    public void createUserTableIfNotExists() {
        try {
        Statement stmt = connection.createStatement();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "email VARCHAR(100), " +
                "full_name VARCHAR(100), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        stmt.execute(createTableSQL);
        
        // Create unique index on username
        String createIndexSQL = "CREATE UNIQUE INDEX IF NOT EXISTS idx_username ON users(username)";
        stmt.execute(createIndexSQL);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    public boolean usernameExists(String username) {
        try {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUser(User user) {
        try {
            String query = "INSERT INTO users (username, password, role, email, full_name) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getRole());
                pstmt.setString(4, user.getEmail());
                pstmt.setString(5, user.getFullName());
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("email"),
                            rs.getString("full_name")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();


        }
                return null;

    }
        public boolean updateUser(User user) {
        try {
            String query = "UPDATE users SET " +
                           "password = ?, " +
                           "role = ?, " +
                           "email = ?, " +
                           "full_name = ? " +
                           "WHERE username = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, user.getPassword());
                pstmt.setString(2, user.getRole());
                pstmt.setString(3, user.getEmail());
                pstmt.setString(4, user.getFullName());
                pstmt.setString(5, user.getUsername());
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String username) {
        try {
            String query = "DELETE FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String query = "SELECT * FROM users";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("full_name")
                    );
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        try {
            String query = "SELECT * FROM users WHERE role = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, role);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("email"),
                            rs.getString("full_name")
                        );
                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int getUserCount() {
        try {
            String query = "SELECT COUNT(*) FROM users";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getUserCountByRole(String role) {
        try {
            String query = "SELECT COUNT(*) FROM users WHERE role = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, role);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
