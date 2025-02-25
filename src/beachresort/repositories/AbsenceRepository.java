package beachresort.repositories;

import beachresort.models.Absence;
import beachresort.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbsenceRepository {
    private Connection connection;

    public AbsenceRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createAbsenceTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAbsenceTableIfNotExists() {
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS absences (" +
                        "   absence_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "   user_id INT NOT NULL," +
            "   leave_type VARCHAR(50) NOT NULL," +
            "   start_date DATE NOT NULL," +
            "   end_date DATE NOT NULL," +
            "   status ENUM('Pending', 'Approved', 'Rejected') NOT NULL," +
            "   reason TEXT," +
            "   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")";
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
            System.out.println("Absences table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating absences table: " + e.getMessage());
        }
    }

    // Validate staff user
    private boolean validateStaffUser (int staffId) throws SQLException {
        String query = "SELECT role FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "staff".equalsIgnoreCase(rs.getString("role"));
                }
            }
        }
        return false;
    }

    public boolean addAbsence(Absence absence) {
        try {
            // Validate staff user
            if (!validateStaffUser (absence.getUserId())) {
                System.err.println("Invalid staff user");
                return false;
            }

            String query = "INSERT INTO absences " +
                           "(user_id, leave_type, start_date, end_date, status, reason) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, absence.getUserId());
                pstmt.setString(2, absence.getLeaveType());
                pstmt.setDate(3, absence.getStartDate());
                pstmt.setDate(4, absence.getEndDate());
                pstmt.setString(5, absence.getStatus());
                pstmt.setString(6, absence.getReason());
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding absence: " + e.getMessage());
            return false;
        }
    }

    public List<Absence> getAbsencesByUserId(int userId) {
        List<Absence> absences = new ArrayList<>();
        try {
            // Validate staff user
            if (!validateStaffUser(userId)) {
                System.err.println("Invalid staff user");
                return absences;
            }

            String query = "SELECT * FROM absences WHERE user_id = ? ORDER BY start_date DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Absence absence = new Absence(
                                rs.getInt("absence_id"),
                                rs.getInt("user_id"),
                                rs.getString("leave_type"),
                                rs.getDate("start_date"),
                                rs.getDate("end_date"),
                                rs.getString("status"),
                                rs.getString("reason"),
                                rs.getTimestamp("created_at"),
                                rs.getTimestamp("updated_at"));
                        absences.add(absence);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving absences: " + e.getMessage());
        }
        return absences;
    }
    
    
    
    public Absence getAbsenceById(int absenceId) {
        String query = "SELECT * FROM absences WHERE absence_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, absenceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Absence(
                            rs.getInt("absence_id"),
                            rs.getInt("user_id"),
                            rs.getString("leave_type"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getString("status"),
                            rs.getString("reason"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving absence by ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<Absence> getAllLeaveRequests() {
        List<Absence> absences = new ArrayList<>();
        String query = "SELECT * FROM absences ORDER BY start_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Absence absence = new Absence(
                        rs.getInt("absence_id"),
                        rs.getInt("user_id"),
                        rs.getString("leave_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status"),
                        rs.getString("reason"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"));
                absences.add(absence);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all leave requests: " + e.getMessage());
        }
        return absences;
    }

    

    public boolean updateAbsenceStatus(int absenceId, String newStatus) {
        try {
            String query = "UPDATE absences SET status = ? WHERE absence_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, absenceId);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating absence status: " + e.getMessage());
            return false;
        }
    }

    public List<Absence> getAllPendingAbsences() {
        List<Absence> pendingAbsences = new ArrayList<>();
        try {
            String query = "SELECT * FROM absences WHERE status = 'Pending' ORDER BY start_date";
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Absence absence = new Absence(
                        rs.getInt("absence_id"),
                        rs.getInt("user_id"),
                        rs.getString("leave_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status"),
                        rs.getString("reason"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    pendingAbsences.add(absence);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving pending absences: " + e.getMessage());
        }
        return pendingAbsences;
    }
}