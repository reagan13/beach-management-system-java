package beachresort.repositories;

import beachresort.models.AuditLog;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogRepository {
    private Connection connection;

    public AuditLogRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createAuditLogTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create Audit Log Table
    private void createAuditLogTableIfNotExists() {
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS audit_logs (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    transaction_id INT NOT NULL," + // Set transaction ID
            "    transaction_type ENUM('ROOM', 'BOOKING', 'PAYMENT', 'STAFF', 'CHECKIN') NOT NULL," + // Set transaction type
            "    action_type ENUM('ADD', 'EDIT', 'DELETE') NOT NULL," +
            "    old_details TEXT," +
            "    new_details TEXT," +
            "    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    performed_by VARCHAR(100)" +
            ")";

        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Audit Log table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating audit log table: " + e.getMessage());
        }
    }

    // Log Action
    public boolean logAction(AuditLog auditLog) {
        String query = "INSERT INTO audit_logs " +
                       "(transaction_id, transaction_type, action_type, old_details, new_details, performed_by) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, auditLog.getTransactionID()); // Set transaction ID
            pstmt.setString(2, auditLog.getTransactionType()); // Set transaction type
            pstmt.setString(3, auditLog.getActionType()); // Set action type
            pstmt.setString(4, auditLog.getOldDetails()); // Set old details
            pstmt.setString(5, auditLog.getNewDetails()); // Set new details
            pstmt.setString(6, auditLog.getPerformedBy()); // Set performed by

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error logging action: " + e.getMessage());
            return false;
        }
    }

    // Get Audit Logs by Transaction ID
    public List<AuditLog> getAuditLogsByTransactionId(int transactionId) {
        List<AuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs " +
                       "WHERE transaction_id = ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog(
                       rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
                        rs.getString("action_type"),
                        rs.getString("old_details"),
                        rs.getString("new_details"),
                        rs.getString("performed_by")
                    );
                    auditLogs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by transaction ID: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Get All Audit Logs
    public List<AuditLog> getAllAuditLogs() {
        List<AuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                AuditLog log = new AuditLog(
                   rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
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
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs " +
                       "WHERE action_timestamp BETWEEN ? AND ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog(
                        rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
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
    public List<AuditLog> getAuditLogsByActionType(String actionType) {
        List<AuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs " +
                       "WHERE action_type = ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, actionType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog(
                        rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
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
        String query = "SELECT COUNT(*) FROM audit_logs";
        
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