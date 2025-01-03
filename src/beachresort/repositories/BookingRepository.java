package beachresort.repositories;

import beachresort.models.Booking;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private Connection connection;

    public BookingRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createBookingsTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create bookings table if not exists
    private void createBookingsTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS bookings (" +
                "   bookingID INT AUTO_INCREMENT PRIMARY KEY," +
                "   user_id INT NOT NULL," +
                "   room_number VARCHAR(10) NOT NULL," +
                "   customer_name VARCHAR(36) NOT NULL," +
                "   check_in_date DATE NOT NULL," +
                "   check_out_date DATE NOT NULL," +
                "   number_of_guests INT NOT NULL," +
                "   total_price DECIMAL(10, 2) NOT NULL," +
                "   status VARCHAR(20) NOT NULL" +
                ");";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     // Validate User ID
     public boolean isValidCustomerUser(int userId) {
         String query = "SELECT COUNT(*) FROM users WHERE id = ? AND role = 'CUSTOMER'";
         try (PreparedStatement pstmt = connection.prepareStatement(query)) {
             pstmt.setInt(1, userId);
             ResultSet rs = pstmt.executeQuery();
             if (rs.next()) {
                 return rs.getInt(1) > 0;
             }
         } catch (SQLException e) {
             System.err.println("Error validating user: " + e.getMessage());
         }
         return false;
     }
    

     
    public Booking getBookingById(int bookingId) {
        String query = "SELECT * FROM bookings WHERE bookingID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("bookingID"),
                        rs.getInt("user_id"),
                        rs.getString("room_number"),
                        rs.getString("customer_name"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getInt("number_of_guests"),
                        rs.getDouble("total_price"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving booking: " + e.getMessage());
        }
        return null; 
    }

    
    // Modify addBooking method to include user validation
    public boolean addBooking(Booking booking, String performedBy) {
        // Validate user first
        if (!isValidCustomerUser(booking.getUserId())) {
            System.err.println("Invalid user ID or user is not a customer");
            return false;
        }


        String query = "INSERT INTO bookings (user_id, room_number, customer_name, check_in_date, check_out_date, number_of_guests, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, booking.getUserId());
            pstmt.setString(2, booking.getRoomNumber());
            pstmt.setString(3, booking.getCustomerName());
            pstmt.setDate(4, java.sql.Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(booking.getCheckOutDate()));
            pstmt.setInt(6, booking.getNumberOfGuests());
            pstmt.setDouble(7, booking.getTotalPrice());
            pstmt.setString(8, booking.getStatus());
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
            return false;
        }
    }
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("bookingID"),
                        rs.getInt("user_id"),
                        rs.getString("room_number"),
                        rs.getString("customer_name"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getInt("number_of_guests"),
                        rs.getDouble("total_price"),
                        rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Integer> getAllBookingIDs() {
        List<Integer> bookingIDs = new ArrayList<>();
        String query = "SELECT bookingID FROM bookings where status = 'Pending'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                bookingIDs.add(rs.getInt("bookingID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingIDs;
    }

    public boolean updateBookingStatusToConfirmed(int bookingID) {
        String query = "UPDATE bookings SET status = ? WHERE bookingID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Confirmed"); // Set the status to "Confirmed"
            pstmt.setInt(2, bookingID); // Set the booking ID for the WHERE clause

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }

    public Booking getBookingByID(int bookingID) {
        Booking booking = null;
        String query = "SELECT * FROM bookings WHERE bookingID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookingID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                booking = new Booking(
                    rs.getInt("bookingID"),
                    rs.getInt("user_id"),
                    rs.getString("room_number"),
                    rs.getString("customer_name"),
                    rs.getDate("check_in_date").toLocalDate(),
                    rs.getDate("check_out_date").toLocalDate(),
                    rs.getInt("number_of_guests"),
                    rs.getDouble("total_price"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booking;
    }
    // Modify updateBooking method to include user validation
    public boolean updateBooking(Booking booking, String performedBy) {
        // Validate user first
        if (!isValidCustomerUser(booking.getUserId())) {
            System.err.println("Invalid user ID or user is not a customer");
            return false;
        }

        String query = "UPDATE bookings SET user_id = ?, customer_name = ?, check_in_date = ?, check_out_date = ?, status = ? WHERE bookingID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, booking.getUserId());
            pstmt.setString(2, booking.getCustomerName());
            pstmt.setDate(3, java.sql.Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(booking.getCheckOutDate()));
            pstmt.setString(5, booking.getStatus());
            pstmt.setInt(6, booking.getBookingID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBooking(int bookingId, String performedBy) {
        String query = "DELETE FROM bookings WHERE bookingID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }
    // Modify getBookingsByUserId to validate user
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();

        // First, validate the user
        if (!isValidCustomerUser(userId)) {
            System.err.println("Invalid user ID or user is not a customer");
            return bookings;
        }

        String query = "SELECT * FROM bookings WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("bookingID"),
                        rs.getInt("user_id"),
                        rs.getString("room_number"),
                        rs.getString("customer_name"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getInt("number_of_guests"),
                        rs.getDouble("total_price"),
                        rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving bookings by user ID: " + e.getMessage());
        }
        return bookings;
    }
    
}