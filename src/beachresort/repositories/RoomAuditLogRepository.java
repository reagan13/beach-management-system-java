package beachresort.repositories;

import beachresort.models.RoomAuditLog;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomAuditLogRepository {
    private Connection connection;

    public RoomAuditLogRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createRoomAuditLogTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create Room Audit Log Table
    private void createRoomAuditLogTableIfNotExists() {
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS room_audit_logs (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    room_number VARCHAR(20) NOT NULL," +
            "    action_type ENUM('ADD', 'EDIT', 'DELETE') NOT NULL," +
            "    old_details TEXT," +
            "    new_details TEXT," +
            "    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    performed_by VARCHAR(100)" +
            ")";

        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Room Audit Log table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating room audit log table: " + e.getMessage());
        }
    }

    // Log Room Action
    public boolean logRoomAction(RoomAuditLog auditLog) {
        String query = "INSERT INTO room_audit_logs " +
                       "(room_number, action_type, old_details, new_details, performed_by) " +
                       "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, auditLog.getRoomNumber());
            pstmt.setString(2, auditLog.getActionType());
            pstmt.setString(3, auditLog.getOldDetails());
            pstmt.setString(4, auditLog.getNewDetails());
            pstmt.setString(5, auditLog.getPerformedBy());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error logging room action: " + e.getMessage());
            return false;
        }
    }

    // Get Audit Logs for a Specific Room
    public List<RoomAuditLog> getAuditLogsForRoom(String roomNumber) {
        List<RoomAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM room_audit_logs " +
                       "WHERE room_number = ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RoomAuditLog log = new RoomAuditLog(
                        rs.getString("room_number"),
                        rs.getString("action_type"),
                        rs.getString("old_details"),
                        rs.getString("new_details"),
                        rs.getString("performed_by")
                    );
                    auditLogs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs for room: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Get All Audit Logs
    public List<RoomAuditLog> getAllAuditLogs() {
        List<RoomAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM room_audit_logs ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                RoomAuditLog log = new RoomAuditLog(
                    rs.getString("room_number"),
                    rs.getString("action_type"),
                    rs.getString("old_details"),
                    rs.getString("new_details"),
                    rs.getString("performed_by")
                );
                auditLogs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all audit logs: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Get Audit Logs Within a Date Range
    public List<RoomAuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<RoomAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM room_audit_logs " +
                       "WHERE action_timestamp BETWEEN ? AND ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RoomAuditLog log = new RoomAuditLog(
                        rs.getString("room_number"),
                        rs.getString("action_type"),
                        rs.getString("old_details"),
                        rs.getString("new_details"),
                        rs.getString("performed_by")
                    );
                    auditLogs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by date range: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Get Audit Logs by Action Type
    public List<RoomAuditLog> getAuditLogsByActionType(String actionType) {
        List<RoomAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM room_audit_logs " +
                       "WHERE action_type = ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, actionType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RoomAuditLog log = new RoomAuditLog(
                        rs.getString("room_number"),
                        rs.getString("action_type"),
                        rs.getString("old_details"),
                        rs.getString("new_details"),
                        rs.getString("performed_by")
                    );
                    auditLogs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by action type: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Count Total Audit Logs
    public int countTotalAuditLogs() {
        String query = "SELECT COUNT(*) FROM room_audit_logs";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting audit logs: " + e.getMessage());
        }
        
        return 0;
    }
}