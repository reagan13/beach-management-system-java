package beachresort.repositories;

import beachresort.models.Owner;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class OwnerRepository {
    private Connection connection;

    public OwnerRepository() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    // Authenticate Owner
    public Owner authenticateOwner(String username, String password) {
        try {
            String query = "SELECT * FROM owners WHERE username = ? AND password = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Owner(
                    rs.getInt("owner_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("contact_number"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Owner getOwnerByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOwnerByUsername'");
    }
}
