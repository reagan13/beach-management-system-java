package beachresort.repositories;

import beachresort.database.DatabaseConnection;
import beachresort.models.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


    


public class BookingRepository {
    private static final Logger LOGGER = Logger.getLogger(BookingRepository.class.getName());

    // Constructor to create table when repository is instantiated
    public BookingRepository() {
        createBookingsTableIfNotExists();
    }

    // Method to create bookings table
    private void createBookingsTableIfNotExists() {
  
StringBuilder sqlBuilder = new StringBuilder();
sqlBuilder.append("CREATE TABLE IF NOT EXISTS bookings (")
    .append("booking_id VARCHAR(50) PRIMARY KEY,")
    .append("guest_name VARCHAR(100) NOT NULL,")
    .append("room_type VARCHAR(50) NOT NULL,")
    .append("check_in_date DATE NOT NULL,")
    .append("check_out_date DATE NOT NULL,")
    .append("total_guests INT NOT NULL,")
    .append("status VARCHAR(20) DEFAULT 'Confirmed',")
    .append("contact_number VARCHAR(20),")
    .append("email VARCHAR(100)")
    .append(")");
String createTableSQL = sqlBuilder.toString();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            System.out.println("Bookings table created or already exists.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating bookings table", e);
            e.printStackTrace();
        }
    }
  
    public boolean addBooking(Booking booking) {
        String query = "INSERT INTO bookings " +
            "(booking_id, guest_name, room_type, check_in_date, check_out_date, " +
            "total_guests, status, contact_number, email) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, booking.getBookingId());
            pstmt.setString(2, booking.getGuestName());
            pstmt.setString(3, booking.getRoomType());
            pstmt.setDate(4, Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(5, Date.valueOf(booking.getCheckOutDate()));
            pstmt.setInt(6, booking.getTotalGuests());
            pstmt.setString(7, booking.getStatus());
            pstmt.setString(8, booking.getContactNumber());
            pstmt.setString(9, booking.getEmail());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBooking(Booking booking) {
        String query = "UPDATE bookings SET " +
            "guest_name = ?, room_type = ?, check_in_date = ?, " +
            "check_out_date = ?, total_guests = ?, status = ?, " +
            "contact_number = ?, email = ? " +
            "WHERE booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, booking.getGuestName());
            pstmt.setString(2, booking.getRoomType());
            pstmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            pstmt.setInt(5, booking.getTotalGuests());
            pstmt.setString(6, booking.getStatus());
            pstmt.setString(7, booking.getContactNumber());
            pstmt.setString(8, booking.getEmail());
            pstmt.setString(9, booking.getBookingId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelBooking(String bookingId) {
        String query = "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Booking getBookingById(String bookingId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBookingById'");
    }
    
    // Example method with extra error handling
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getString("booking_id"),
                    rs.getString("guest_name"),
                    rs.getString("room_type"),
                    rs.getDate("check_in_date").toLocalDate(),
                    rs.getDate("check_out_date").toLocalDate(),
                    rs.getInt("total_guests"),
                    rs.getString("status"),
                    rs.getString("contact_number"),
                    rs.getString("email")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            // Check if table exists, if not, create it
            if (e.getErrorCode() == 1146) { // MySQL error code for "table doesn't exist"
                createBookingsTableIfNotExists();
                // Optionally, retry the query
                return getAllBookings();
            }
            
            LOGGER.log(Level.SEVERE, "Error fetching bookings", e);
            e.printStackTrace();
        }

        return bookings;
    }

    // Additional method to verify table structure
    public void verifyTableStructure() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Check table columns
            try (ResultSet columns = metaData.getColumns(null, null, "bookings", null)) {
                boolean hasBookingId = false;
                boolean hasGuestName = false;
                
              while (columns.next()) {
    String columnName = columns.getString("COLUMN_NAME");
    if ("booking_id".equals(columnName)) {
        hasBookingId = true;
    } else if ("guest_name".equals(columnName)) {
        hasGuestName = true;
    }
}
                
                if (!hasBookingId || !hasGuestName) {
                    System.out.println("Table structure is incomplete. Recreating table...");
                    recreateBookingsTable();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verifying table structure", e);
        }
    }

    // Method to completely recreate the table if needed
    private void recreateBookingsTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Drop existing table
            stmt.execute("DROP TABLE IF EXISTS bookings");
            
            // Recreate table
            createBookingsTableIfNotExists();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error recreating bookings table", e);
        }
    }

}





  
