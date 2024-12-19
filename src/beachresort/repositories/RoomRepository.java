package beachresort.repositories;

import beachresort.database.DatabaseConnection;
import beachresort.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomRepository {
    private static final Logger LOGGER = Logger.getLogger(RoomRepository.class.getName());

    // Constructor to create table when repository is instantiated
    public RoomRepository() {
        createRoomsTableIfNotExists();
    }

    // Method to create rooms table
    private void createRoomsTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS rooms (" +
                "room_number VARCHAR(10) PRIMARY KEY, " +
                "room_type VARCHAR(50) NOT NULL, " +
                "capacity INT NOT NULL, " +
                "price_per_night DECIMAL(10,2) NOT NULL, " +
                "status VARCHAR(20) DEFAULT 'Available', " +
                "amenities TEXT" +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            System.out.println("Rooms table created or already exists.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating rooms table", e);
        }
    }

    // Add a new room
    public boolean addRoom(Room room) {
        String query = "INSERT INTO rooms " +
                "(room_number, room_type, capacity, price_per_night, status, amenities) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setDouble(4, room.getPricePerNight());
            pstmt.setString(5, room.getStatus());
            pstmt.setString(6, room.getAmenities());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding room", e);
            return false;
        }
    }

    // Update existing room
    public boolean updateRoom(Room room) {
        String query = "UPDATE rooms SET " +
                "room_type = ?, capacity = ?, price_per_night = ?, " +
                "status = ?, amenities = ? " +
                "WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, room.getRoomType());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setString(4, room.getStatus());
            pstmt.setString(5, room.getAmenities());
            pstmt.setString(6, room.getRoomNumber());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating room", e);
            return false;
        }
    }

    // Delete a room
    public boolean deleteRoom(String roomNumber) {
        String query = "DELETE FROM rooms WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting room", e);
            return false;
        }
    }

    // Get all rooms
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_night"),
                    rs.getString("status"),
                    rs.getString("amenities")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching rooms", e);
        }

        return rooms;
    }

    // Get room by room number
    public Room getRoomByNumber(String roomNumber) {
        String query = "SELECT * FROM rooms WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getInt("capacity"),
                        rs.getDouble("price_per_night"),
                        rs.getString("status"),
                        rs.getString("amenities")
                    );
                }
            }
        } catch (SQLException e) {
                       LOGGER.log(Level.SEVERE, "Error fetching room", e);
        }
        return null; // Return null if room not found
    }
}