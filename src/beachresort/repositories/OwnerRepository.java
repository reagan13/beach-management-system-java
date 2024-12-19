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

    // Get Dashboard Statistics
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
      
          try {
            // Modified query to use generic column names
            String query = "SELECT " +
                "(SELECT COUNT(*) FROM bookings) as total_bookings, " +
                "(SELECT COUNT(*) FROM rooms WHERE room_status = 'Occupied') as rooms_occupied, " +
                "(SELECT COALESCE(SUM(booking_amount), 0) " +
                " FROM bookings " +
                " WHERE MONTH(booking_date) = MONTH(CURRENT_DATE) " +
                " AND YEAR(booking_date) = YEAR(CURRENT_DATE)) as monthly_revenue, " +
                "(SELECT COUNT(*) FROM bookings WHERE booking_status = 'Pending') as pending_bookings, " +
                "(SELECT COUNT(*) FROM rooms WHERE room_status = 'Available') as available_rooms, " +
                "(SELECT COUNT(*) FROM staff) as total_staff";
            
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Safely extract values with default fallbacks
                stats.setTotalBookings(rs.getInt("total_bookings"));
                stats.setRoomsOccupied(rs.getInt("rooms_occupied"));
                stats.setMonthlyRevenue(rs.getDouble("monthly_revenue"));
                stats.setPendingBookings(rs.getInt("pending_bookings"));
                stats.setAvailableRooms(rs.getInt("available_rooms"));
                stats.setTotalStaff(rs.getInt("total_staff"));
                
                // Log the retrieved statistics
                System.out.println("Dashboard Statistics Retrieved Successfully:");
                System.out.println("Total Bookings: " + stats.getTotalBookings());
                System.out.println("Rooms Occupied: " + stats.getRoomsOccupied());
                System.out.println("Monthly Revenue: $" + stats.getMonthlyRevenue());
                System.out.println("Pending Bookings: " + stats.getPendingBookings());
                System.out.println("Available Rooms: " + stats.getAvailableRooms());
                System.out.println("Total Staff: " + stats.getTotalStaff());
            } else {
                // Log when no data is found
                System.out.println("WARNING: No dashboard statistics found. Using mock data.");
                stats = DashboardStats.createMockStats();
            }
        } catch (SQLException e) {
            // Log the full exception
            System.err.println("Error retrieving dashboard statistics:");
            e.printStackTrace();
            
            // Use mock data in case of any database error
            stats = DashboardStats.createMockStats();
        }
        
        return stats;
    
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
