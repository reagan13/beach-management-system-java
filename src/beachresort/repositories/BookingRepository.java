package beachresort.repositories;

import beachresort.models.Booking;
import beachresort.models.RoomAuditLog;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private Connection connection;
    private RoomAuditLogRepository auditLogRepository; // Reference to the audit log repository

    public BookingRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            this.auditLogRepository = new RoomAuditLogRepository(); // Initialize the audit log repository
            createBookingsTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create bookings table if not exists
    private void createBookingsTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS bookings (" +
                "   booking_id INT AUTO_INCREMENT PRIMARY KEY," +
                "    room_number VARCHAR(10) NOT NULL," +
                "    customer_name VARCHAR(36) NOT NULL," +
                "    check_in_date DATE NOT NULL," +
                "    check_out_date DATE NOT NULL," +
                "    number_of_guests INT NOT NULL," +
                "    total_price DECIMAL(10, 2) NOT NULL," +
                "    status VARCHAR(20) NOT NULL" +
                ");";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Booking getBookingById(int bookingId) {
        String query = "SELECT * FROM bookings WHERE booking_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("booking_id"),
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
        return null; // Return null if no booking is found
    }

    public boolean addBooking(Booking booking, String performedBy) {
        if (!booking.validate()) {
            System.err.println("Invalid booking data");
            return false;
        }

        String query = "INSERT INTO bookings ( room_number, customer_name, check_in_date, check_out_date, number_of_guests, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, booking.getRoomNumber());
            pstmt.setString(2, booking.getCustomerName());
            pstmt.setDate(3, java.sql.Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(booking.getCheckOutDate()));
            pstmt.setInt(5, booking.getNumberOfGuests());
            pstmt.setDouble(6, booking.getTotalPrice());
            pstmt.setString(7, booking.getStatus());
            pstmt.executeUpdate();

            // Create an audit log entry for the booking creation
            AuditLog auditLog = new AuditLog(
                
                booking.getRoomNumber(),
                "ADD", // Action type
                null, // Old details (none for new booking)
                booking.toString(), // New details (the booking details)
                performedBy // The user who performed the action
            );

            // Log the action in the audit log
            auditLogRepository.logAction(auditLog);
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
                    rs.getInt("booking_id"),
                    rs.getString("room_number"),
                    rs.getString("customer_name"),
                    rs.getDate("check_in_date").toLocalDate(),
                    rs.getDate("check_out_date").toLocalDate(),
                    rs.getInt("number_of_guests"),
                    rs.getDouble("total_price"),
                    rs.getString("status")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean updateBooking(Booking booking, String performedBy) {
        String query = "UPDATE bookings SET room_number = ?, customer_name = ?, check_in_date = ?, check_out_date = ?, number_of_guests = ?, total_price = ?, status = ? WHERE booking_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, booking.getRoomNumber());
            pstmt.setString(2, booking.getCustomerName());
            pstmt.setDate(3, java.sql.Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(booking.getCheckOutDate()));
            pstmt.setInt(5, booking.getNumberOfGuests());
            pstmt.setDouble(6, booking.getTotalPrice());
            pstmt.setString(7, booking.getStatus());
           
            pstmt.executeUpdate();

            // Create an audit log entry for the booking update
            RoomAuditLog auditLog = new RoomAuditLog(
                booking.getRoomNumber(),
                "UPDATE", // Action type
                booking.toString(), // Old details (previous state)
                booking.toString(), // New details (the updated booking details)
                performedBy // The user who performed the action
            );

            // Log the action in the audit log
            auditLogRepository.logRoomAction(auditLog);
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBooking(String bookingId, String performedBy) {
        String query = "DELETE FROM bookings WHERE booking_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, bookingId);
            pstmt.executeUpdate();

            // Create an audit log entry for the booking deletion
            RoomAuditLog auditLog = new RoomAuditLog(
                null, // Room number is not applicable for deletion
                "DELETE", // Action type
                null, // Old details (not applicable for deletion)
                "Booking with ID: " + bookingId + " has been deleted.", // New details (log the deletion)
                performedBy // The user who performed the action
            );

            // Log the action in the audit log
            auditLogRepository.logRoomAction(auditLog);
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }
}