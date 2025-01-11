package beachresort.repositories;

import beachresort.database.DatabaseConnection;
import beachresort.models.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRepository {
    private Connection connection;

    public CustomerRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createCustomerTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCustomerTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS customer (" +
                "   cust_id INT AUTO_INCREMENT PRIMARY KEY," +
                "   user_id INT," + // Assuming user_id is an integer
                "   numberVisits INT DEFAULT 0," +
                "   preferredAccommodationType VARCHAR(50)," +
                "   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Customer table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating customer table: " + e.getMessage());
        }
    }

    public void updateCustomer(int userId, int numberVisits, String preferredAccommodationType) throws SQLException {
        String updateQuery = "UPDATE customer SET numberVisits = ?, preferredAccommodationType = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setInt(1, numberVisits);
            pstmt.setString(2, preferredAccommodationType);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
            System.out.println("Customer updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            throw e;
        }
    }

    // Method to retrieve a customer by user ID
    public Customer getCustomerByUserId(int userId) {
        String query = "SELECT c.cust_id, c.numberVisits, c.preferredAccommodationType, " +
                "u.username, u.password, u.email, u.full_name, u.address, u.contact_number " +
                "FROM customer c " +
                "JOIN users u ON c.user_id = u.id " +
                "WHERE u.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId); // Use userId as an integer
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("cust_id"), // custId from the customer table
                            userId, // userId passed as a parameter
                            rs.getString("username"), // username from the user table
                            rs.getString("password"), // password from the user table
                            rs.getString("email"), // email from the user table
                            rs.getString("full_name"), // fullName from the user table
                            rs.getString("address"), // address from the user table
                            rs.getString("contact_number"), // contact number from the user table
                            rs.getString("preferredAccommodationType"), // preferredAccommodationType from the customer table
                            rs.getInt("numberVisits") // numberVisits from the customer table
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customer by user ID: " + e.getMessage());
        }
        return null; // Return null if no customer found
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