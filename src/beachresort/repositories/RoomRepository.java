package beachresort.repositories;

import beachresort.models.Room;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {
    private Connection connection;

    public RoomRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createRoomsTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createRoomsTableIfNotExists() {
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS rooms (" +
            "   roomID INT AUTO_INCREMENT PRIMARY KEY," +
            "   room_number VARCHAR(10) UNIQUE NOT NULL," +
            "   room_type ENUM('Standard', 'Deluxe', 'Suite', 'Family') NOT NULL," +
            "   capacity INT NOT NULL," +
            "   price_per_night DECIMAL(10, 2) NOT NULL," +
            "   status ENUM('Available', 'Occupied', 'Maintenance') NOT NULL," +
            "   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery )) {
            pstmt.execute();
            System.out.println("Rooms table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating rooms table: " + e.getMessage());
        }
    }

    public boolean addRoom(Room room) {
        if (!room.validate()) {
            System.err.println("Invalid room data");
            return false;
        }

        if (roomExists(room.getRoomNumber())) {
            System.err.println("Room number already exists");
            return false;
        }

        String query = "INSERT INTO rooms (room_number, room_type, capacity, price_per_night, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setDouble(4, room.getPricePerNight());
            pstmt.setString(5, room.getStatus());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRoom(Room room) {
        if (!room.validate()) {
            System.err.println("Invalid room data");
            return false;
        }

        String query = "UPDATE rooms SET room_type = ?, capacity = ?, price_per_night = ?, status = ? WHERE room_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, room.getRoomType());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setString(4, room.getStatus());
            pstmt.setString(5, room.getRoomNumber());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRoom(String roomNumber) {
        String query = "DELETE FROM rooms WHERE room_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_night"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving rooms: " + e.getMessage());
        }
        return rooms;
    }

    public Room getRoomByNumber(String roomNumber) {
        String query = "SELECT * FROM rooms WHERE room_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            rs.getInt("capacity"),
                            rs.getDouble("price_per_night"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving room: " + e.getMessage());
        }
        return null;
    }
    public boolean updateRoomStatus(String roomNumber, String newStatus) {
        String query = "UPDATE rooms SET status = ? WHERE room_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatus); // Set the new status
            pstmt.setString(2, roomNumber); // Set the room number for the WHERE clause

            int rowsAffected = pstmt.executeUpdate(); // Execute the update
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            System.err.println("Error updating room status: " + e.getMessage());
            return false; // Return false if there was an error
        }
    }

    public boolean updateRoomStatusBasedOnCurrent(String roomNumber, String currentStatus ) {
        String newStatus;

        // Determine the new status based on the current status
        switch (currentStatus.toLowerCase()) {
            case "confirmed":
            case "pending":
                newStatus = "Occupied";
                break;
            case "cancelled":
                newStatus = "Available";
                break;
            default:
                newStatus = "Available"; // Default status
                break;
        }
        // Log the new status for debugging
        System.out.println("Updating room status to: " + newStatus);
    
        // Now update the room status in the database
        return updateRoomStatus(roomNumber, newStatus);
    }
    
    public boolean roomExists(String roomNumber) {
        String query = "SELECT COUNT(*) FROM rooms WHERE room_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking room existence: " + e.getMessage());
        }
        return false;
    }
    public List<String> getAvailableRoomsByType(String roomType) {
        List<String> availableRooms = new ArrayList<>();
        System.out.println("Room Type: " + roomType);
        String query = "SELECT room_number FROM rooms WHERE room_type = ? AND status = 'Available'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roomType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    availableRooms.add(rs.getString("room_number"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving available rooms: " + e.getMessage());
        }
        System.out.println("Available Rooms: " + availableRooms);
        return availableRooms; // Return the list of available room numbers
    }

}