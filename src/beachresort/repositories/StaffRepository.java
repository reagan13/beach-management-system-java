package beachresort.repositories;

import beachresort.database.DatabaseConnection;
import beachresort.models.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaffRepository {
    private static final Logger LOGGER = Logger.getLogger(StaffRepository.class.getName());

      // Constructor to create table when repository is instantiated
    public StaffRepository() {
        createStaffTableIfNotExists();
    }
    // Method to create staff table if it doesn't exist
    public void createStaffTableIfNotExists() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS staff (" +
            "    staff_id VARCHAR(10) PRIMARY KEY," +
            "    name VARCHAR(100) NOT NULL," +
            "    position VARCHAR(50) NOT NULL," +
            "    department VARCHAR(50) NOT NULL," +
            "    contact_number VARCHAR(20) NOT NULL," +
            "    email VARCHAR(100) NOT NULL," +
            "    salary DECIMAL(10, 2) NOT NULL," +
            "    hire_date DATE NOT NULL," +
            "    status VARCHAR(20) NOT NULL DEFAULT 'Active'," +
            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            LOGGER.info("Staff table created or already exists");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating staff table", e);
        }
    }

    // Method to insert initial sample data if table is empty
    public void insertInitialStaffDataIfEmpty() {
        String checkEmptySQL = "SELECT COUNT(*) FROM staff";
        String insertSampleDataSQL = 
            "INSERT INTO staff (" +
            "    staff_id, name, position, department, " +
            "    contact_number, email, salary, hire_date, status" +
            ") VALUES " +
            "('S001', 'John Doe', 'Manager', 'Administration', " +
            " '1234567890', 'john.doe@example.com', 50000.00, '2023-01-15', 'Active')," +
            "('S002', 'Jane Smith', 'Receptionist', 'Front Desk', " +
            " '9876543210', 'jane.smith@example.com', 35000.00, '2023-02-20', 'Active')," +
            "('S003', 'Mike Johnson', 'Maintenance', 'Facilities', " +
            " '5555555555', 'mike.johnson@example.com', 40000.00, '2023-03-10', 'Active')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkEmptySQL)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Table is empty, insert sample data
                try (Statement insertStmt = conn.createStatement()) {
                    insertStmt.executeUpdate(insertSampleDataSQL);
                    LOGGER.info("Initial staff data inserted");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking or inserting staff data", e);
        }
    }
    // Create Staff
    public boolean addStaff(Staff staff) {
        String query = "INSERT INTO staff " +
            "(staff_id, name, position, department, contact_number, email, salary, hire_date, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, staff.getStaffId());
            pstmt.setString(2, staff.getName());
            pstmt.setString(3, staff.getPosition());
            pstmt.setString(4, staff.getDepartment());
            pstmt.setString(5, staff.getContactNumber());
            pstmt.setString(6, staff.getEmail());
            pstmt.setDouble(7, staff.getSalary());
            pstmt.setDate(8, Date.valueOf(staff.getHireDate()));
            pstmt.setString(9, staff.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding staff", e);
            return false;
        }
    }

    // Read All Staff
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT * FROM staff";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching staff", e);
        }

        return staffList;
    }

    // Read Staff by ID
    public Staff getStaffById(String staffId) {
        String query = "SELECT * FROM staff WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, staffId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching staff by ID", e);
        }

        return null;
    }

    // Update Staff
    public boolean updateStaff(Staff staff) {
        String query = "UPDATE staff SET " +
            "name = ?, position = ?, department = ?, " +
            "contact_number = ?, email = ?, salary = ?, " +
            "status = ? WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getPosition());
            pstmt.setString(3, staff.getDepartment());
            pstmt.setString(4, staff.getContactNumber());
            pstmt.setString(5, staff.getEmail());
            pstmt.setDouble(6, staff.getSalary());
            pstmt.setString(7, staff.getStatus());
            pstmt.setString(8, staff.getStaffId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating staff", e);
            return false;
        }
    }

    // Delete/Deactivate Staff
    public boolean deleteStaff(String staffId) {
        String query = "UPDATE staff SET status = 'Inactive' WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, staffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deactivating staff", e);
            return false;
        }
    }

    // Search Staff
    public List<Staff> searchStaff(String searchTerm) {
        List<Staff> searchResults = new ArrayList<>();
        String query = "SELECT * FROM staff WHERE " +
            "LOWER(name) LIKE ? OR LOWER(department) LIKE ? OR LOWER(position) LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    searchResults.add(mapResultSetToStaff(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching staff", e);
        }

        return searchResults;
    }

    // Helper method to map ResultSet to Staff object
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        return new Staff(
            rs.getString("staff_id"),
            rs.getString("name"),
            rs.getString("position"),
            rs.getString("department"),
            rs.getString("contact_number"),
            rs.getString("email"),
            rs.getDouble("salary"),
            rs.getDate("hire_date").toLocalDate(),
            rs.getString("status")
        );
    }

    // Generate Unique Staff ID
    public String generateUniqueStaffId() {
        String query = "SELECT MAX(staff_id) AS max_id FROM staff";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String lastId = rs.getString("max_id");
                if (lastId == null)
                    return "S001";

                int number = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("S%03d", number);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating staff ID", e);
        }

        return "S001";
    }

   public StaffStatistics getStaffStatistics() {
    StaffStatistics stats = new StaffStatistics();
    
    String queryTotal = "SELECT COUNT(*) AS total FROM staff";
    String queryActive = "SELECT COUNT(*) AS active FROM staff WHERE status = 'Active'";
    String queryInactive = "SELECT COUNT(*) AS inactive FROM staff WHERE status = 'Inactive'";
    String queryDepartment = "SELECT department, COUNT(*) AS count FROM staff GROUP BY department";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement()) {

        // Total staff
        try (ResultSet rs = stmt.executeQuery(queryTotal)) {
            if (rs.next()) {
                stats.setTotalStaff(rs.getInt("total"));
            }
        }

        // Active staff
        try (ResultSet rs = stmt.executeQuery(queryActive)) {
            if (rs.next()) {
                stats.setActiveStaff(rs.getInt("active"));
            }
        }

        // Inactive staff
        try (ResultSet rs = stmt.executeQuery(queryInactive)) {
            if (rs.next()) {
                stats.setInactiveStaff(rs.getInt("inactive"));
            }
        }

        // Department statistics
        Map<String, Integer> departmentStats = new HashMap<>();
        try (ResultSet rs = stmt.executeQuery(queryDepartment)) {
            while (rs.next()) {
                departmentStats.put(
                    rs.getString("department"), 
                    rs.getInt("count")
                );
            }
        }
        stats.setDepartmentStats(departmentStats);

        return stats;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error fetching staff statistics", e);
        return new StaffStatistics(); // Return empty statistics
    }
}

// Inner class for Staff Statistics
public static class StaffStatistics {
    private int totalStaff;
    private int activeStaff;
    private int inactiveStaff;
    private Map<String, Integer> departmentStats;

    // Default constructor
    public StaffStatistics() {
        this.totalStaff = 0;
        this.activeStaff = 0;
        this.inactiveStaff = 0;
        this.departmentStats = new HashMap<>();
    }

    // Constructors
    public StaffStatistics(int totalStaff, int activeStaff, int inactiveStaff,
            Map<String, Integer> departmentStats) {
        this.totalStaff = totalStaff;
        this.activeStaff = activeStaff;
        this.inactiveStaff = inactiveStaff;
        this.departmentStats = departmentStats;
    }

    // Getters and Setters
    public int getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    public int getActiveStaff() {
        return activeStaff;
    }

    public void setActiveStaff(int activeStaff) {
        this.activeStaff = activeStaff;
    }

    public int getInactiveStaff() {
        return inactiveStaff;
    }

    public void setInactiveStaff(int inactiveStaff) {
        this.inactiveStaff = inactiveStaff;
    }

    public Map<String, Integer> getDepartmentStats() {
        return departmentStats;
    }

    public void setDepartmentStats(Map<String, Integer> departmentStats) {
        this.departmentStats = departmentStats;
    }

    // Additional utility methods
    public double getActiveStaffPercentage() {
        return totalStaff > 0 ? (double) activeStaff / totalStaff * 100 : 0;
    }

    public String getMostPopulatedDepartment() {
        return departmentStats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}
    // Additional methods in StaffRepository
public List<Staff> getRecentHires(int days) {
    List<Staff> recentHires = new ArrayList<>();
    String query = "SELECT * FROM staff WHERE hire_date >= DATEADD(day, -?, GETDATE())";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, days);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                recentHires.add(mapResultSetToStaff(rs));
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error fetching recent hires", e);
    }

    return recentHires;
}

public Map<String, Double> getDepartmentAverageSalaries() {
    Map<String, Double> departmentSalaries = new HashMap<>();
    String query = "SELECT department, AVG(salary) AS avg_salary FROM staff GROUP BY department";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        while (rs.next()) {
            departmentSalaries.put(
                rs.getString("department"), 
                rs.getDouble("avg_salary")
            );
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error fetching department average salaries", e);
    }

    return departmentSalaries;
}
}