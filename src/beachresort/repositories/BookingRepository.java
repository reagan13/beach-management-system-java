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
        String query = "SELECT * FROM bookings WHERE bookingID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("bookingID"),
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
    String query = "UPDATE bookings SET customer_name = ?, check_in_date = ?, check_out_date = ?, status = ? WHERE bookingID = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, booking.getCustomerName()); // Set customer name
        pstmt.setDate(2, java.sql.Date.valueOf(booking.getCheckInDate())); // Set check-in date
        pstmt.setDate(3, java.sql.Date.valueOf(booking.getCheckOutDate())); // Set check-out date
        pstmt.setString(4, booking.getStatus()); // Set status
        pstmt.setInt(5, booking.getBookingID()); // Set booking ID for the WHERE clause

        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0; // Return true if at least one row was updated
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
}