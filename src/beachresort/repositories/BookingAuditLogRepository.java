package beachresort.repositories;

import beachresort.models.BookingAuditLog;
import beachresort.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingAuditLogRepository {
    private Connection connection;

    public BookingAuditLogRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createBookingAuditLogTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create Booking Audit Log Table
    private void createBookingAuditLogTableIfNotExists() {
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS booking_audit_logs (" +
            "    audit_id VARCHAR(50) PRIMARY KEY," +
            "    booking_id VARCHAR(50) NOT NULL," +
            "    action_type ENUM('ADD', 'EDIT', 'CANCEL', 'CONFIRM', 'DELETE') NOT NULL," +
            "    old_details TEXT," +
            "    new_details TEXT," +
            "    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    performed_by VARCHAR(100)" +
            ")";

        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Booking Audit Logs table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating booking audit logs table: " + e.getMessage());
        }
    }

    // Generate unique audit log ID
    private String generateAuditLogId() {
        return "AUDIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Log Booking Action
    public boolean logBookingAction(BookingAuditLog auditLog) {
        String query = "INSERT INTO booking_audit_logs " +
                       "(audit_id, booking_id, action_type, old_details, new_details, performed_by) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Generate audit log ID if not provided
            String auditId = auditLog.getAuditId() != null 
                ? auditLog.getAuditId() 
                : generateAuditLogId();
            
            pstmt.setString(1, auditId);
            pstmt.setString(2, auditLog.getBookingId());
            pstmt.setString(3, auditLog.getActionType());
            pstmt.setString(4, auditLog.getOldDetails());
            pstmt.setString(5, auditLog.getNewDetails());
            pstmt.setString(6, auditLog.getPerformedBy());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error logging booking action: " + e.getMessage());
            return false;
        }
    }

    // Get Audit Logs for a Specific Booking
    public List<BookingAuditLog> getAuditLogsForBooking(String bookingId) {
        List<BookingAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM booking_audit_logs " +
                       "WHERE booking_id = ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditLogs.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs for booking: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Get All Audit Logs
    public List<BookingAuditLog> getAllAuditLogs() {
        List<BookingAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM booking_audit_logs ORDER BY action_timestamp DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                auditLogs.add(mapResultSetToAuditLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all audit logs: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Get Audit Logs by Action Type
    public List<BookingAuditLog> getAuditLogsByActionType(String actionType) {
        List<BookingAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM booking_audit_logs " +
                       "WHERE action_type = ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, actionType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditLogs.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by action type: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Get Audit Logs Within a Date Range
    public List<BookingAuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<BookingAuditLog> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM booking_audit_logs " +
                       "WHERE action_timestamp BETWEEN ? AND ? " +
                       "ORDER BY action_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditLogs.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by date range: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Count Total Audit Logs
    public int countTotalAuditLogs() {
        String query = "SELECT COUNT(*) FROM booking_audit_logs";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting audit logs: " + e.getMessage());
        }
        
        return 0;
    }

    // Delete Old Audit Logs
    public int deleteOldAuditLogs(LocalDateTime cutoffDate) {
        String query = "DELETE FROM booking_audit_logs WHERE action_timestamp < ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(cutoffDate));
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting old audit logs: " + e.getMessage());
            return -1;
        }
    }

        // Audit Log Summary
    public AuditLogSummary getAuditLogSummary() {
        AuditLogSummary summary = new AuditLogSummary();
        
        String totalLogsQuery = "SELECT COUNT(*) FROM booking_audit_logs";
        String actionTypeQuery = "SELECT action_type, COUNT(*) as count FROM booking_audit_logs GROUP BY action_type";
        
        try {
            // Total Logs
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(totalLogsQuery)) {
                if (rs.next()) {
                    summary.setTotalLogs(rs.getInt(1));
                }
            }
            
            // Action Type Counts
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(actionTypeQuery)) {
                while (rs.next()) {
                    String actionType = rs.getString("action_type");
                    int count = rs.getInt("count");
                    
                    switch (actionType) {
                        case "ADD":
                            summary.setAddCount(count);
                            break;
                        case "EDIT":
                            summary.setEditCount(count);
                            break;
                        case "CANCEL":
                            summary.setCancelCount(count);
                            break;
                        case "CONFIRM":
                            summary.setConfirmCount(count);
                            break;
                        case "DELETE":
                            summary.setDeleteCount(count);
                            break;
                    }
                }
            }
            
            // Most Recent Log
            String recentLogQuery = "SELECT * FROM booking_audit_logs ORDER BY action_timestamp DESC LIMIT 1";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(recentLogQuery)) {
                if (rs.next()) {
                    summary.setMostRecentLog(mapResultSetToAuditLog(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating audit log summary: " + e.getMessage());
        }
        
        return summary;
    }

    // Helper method to map ResultSet to BookingAuditLog object
    private BookingAuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        BookingAuditLog auditLog = new BookingAuditLog(
            rs.getString("booking_id"),
            rs.getString("action_type"),
            rs.getString("old_details"),
            rs.getString("new_details"),
            rs.getString("performed_by")
        );
        
        auditLog.setAuditId(rs.getString("audit_id"));
        auditLog.setActionTimestamp(rs.getTimestamp("action_timestamp").toLocalDateTime());
        
        return auditLog;
    }

    // Search Audit Logs
    public List<BookingAuditLog> searchAuditLogs(AuditLogSearchCriteria criteria) {
        List<BookingAuditLog> auditLogs = new ArrayList<>();
        
        // Build dynamic query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM booking_audit_logs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        
        if (criteria.getBookingId() != null) {
            queryBuilder.append("AND booking_id = ? ");
            params.add(criteria.getBookingId());
        }
        
        if (criteria.getActionType() != null) {
            queryBuilder.append("AND action_type = ? ");
            params.add(criteria.getActionType());
        }
        
        if (criteria.getPerformedBy() != null) {
            queryBuilder.append("AND performed_by LIKE ? ");
            params.add("%" + criteria.getPerformedBy() + "%");
        }
        
        if (criteria.getStartDate() != null) {
            queryBuilder.append("AND action_timestamp >= ? ");
            params.add(Timestamp.valueOf(criteria.getStartDate()));
        }
        
        if (criteria.getEndDate() != null) {
            queryBuilder.append("AND action_timestamp <= ? ");
            params.add(Timestamp.valueOf(criteria.getEndDate()));
        }
        
        queryBuilder.append("ORDER BY action_timestamp DESC");
        
        try (PreparedStatement pstmt = connection.prepareStatement(queryBuilder.toString())) {
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditLogs.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching audit logs: " + e.getMessage());
        }
        
        return auditLogs;
    }

    // Audit Log Summary Inner Class
    public static class AuditLogSummary {
        private int totalLogs;
        private int addCount;
        private int editCount;
        private int cancelCount;
        private int confirmCount;
        private int deleteCount;
        private BookingAuditLog mostRecentLog;

        // Getters and Setters
        public int getTotalLogs() {
            return totalLogs;
        }

        public void setTotalLogs(int totalLogs) {
            this.totalLogs = totalLogs;
        }

        public int getAddCount() {
            return addCount;
        }

        public void setAddCount(int addCount) {
            this.addCount = addCount;
        }

        public int getEditCount() {
            return editCount;
        }

        public void setEditCount(int editCount) {
            this.editCount = editCount;
        }

        public int getCancelCount() {
            return cancelCount;
        }

        public void setCancelCount(int cancelCount) {
            this.cancelCount = cancelCount;
        }

        public int getConfirmCount() {
            return confirmCount;
        }

        public void setConfirmCount(int confirmCount) {
            this.confirmCount = confirmCount;
        }

        public int getDeleteCount() {
            return deleteCount;
        }

        public void setDeleteCount(int deleteCount) {
            this.deleteCount = deleteCount;
        }

        public BookingAuditLog getMostRecentLog() {
            return mostRecentLog;
        }

        public void setMostRecentLog(BookingAuditLog mostRecentLog) {
            this.mostRecentLog = mostRecentLog;
        }
    }

    // Audit Log Search Criteria Inner Class
    public static class AuditLogSearchCriteria {
        private String bookingId;
        private String actionType;
        private String performedBy;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        // Getters and Setters
        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getPerformedBy() {
            return performedBy;
        }

        public void setPerformedBy(String performedBy) {
            this.performedBy = performedBy;
        }

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }
    }
}