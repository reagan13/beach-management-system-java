package beachresort.repositories;

import beachresort.database.DatabaseConnection;

import beachresort.models.Owner;
import beachresort.models.User;
import beachresort.models.User.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OwnerRepository {
    private Connection connection;
    

    public OwnerRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createStaffTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    private void createStaffTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS owner (" +
                "   owner_id INT AUTO_INCREMENT PRIMARY KEY," +
                "   user_id VARCHAR(50)," +
                "   businessName VARCHAR(50)," +
                "   licenseNumber VARCHAR(50)," +
                "   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Staff table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating staff table: " + e.getMessage());
        }
    }
    public void updateOwner(String businessName, String licenseNumber, int userId) throws SQLException {
        String updateQuery = "UPDATE owner SET businessName = ?, licenseNumber = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, businessName);
            pstmt.setString(2, licenseNumber);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
            System.out.println("Owner updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating owner: " + e.getMessage());
            throw e;
        }

    }

    
    
    
    // Method to retrieve an owner by user ID
    public Owner getOwnerByUserId(int userId) {
        String query = "SELECT o.owner_id, o.businessName, o.licenseNumber, u.username, u.password, u.email, u.full_name, u.address, u.contact_number "
                +
                "FROM owner o " +
                "JOIN users u ON o.user_id = u.id " +
                "WHERE u.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId); // Use userId as an integer
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Owner(
                            rs.getInt("owner_id"), // ownerId from the owner table
                            userId, // userId passed as a parameter
                            rs.getString("username"), // username from the user table
                            rs.getString("password"), // password from the user table
                            rs.getString("email"), // email from the user table
                            rs.getString("full_name"), // fullName from the user table
                            rs.getString("address"), // address from the user table
                            rs.getString("contact_number"), // contact number from the user table
                            rs.getString("businessName"), // businessName from the owner table
                            rs.getString("licenseNumber")
                         
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving owner by user ID: " + e.getMessage());
        }
        return null; // Return null if no owner found
    }

    
    

    // Close the connection when done
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

   
    

  
}