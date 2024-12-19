package beachresort.repositories;

import beachresort.database.DatabaseConnection;
import beachresort.models.Inventory;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryRepository {
    private static final Logger LOGGER = Logger.getLogger(InventoryRepository.class.getName());

    // Create Inventory Table
    public void createInventoryTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS inventory (" +
                "    item_id VARCHAR(10) PRIMARY KEY," +
                "    item_name VARCHAR(100) NOT NULL," +
                "    category VARCHAR(50) NOT NULL," +
                "    quantity INT NOT NULL," +
                "    unit_price DECIMAL(10, 2) NOT NULL," +
                "    supplier VARCHAR(100) NOT NULL," +
                "    last_restocked DATE," +
                "    status VARCHAR(20) NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            LOGGER.info("Inventory table created or already exists");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating inventory table", e);
        }
    }

    // Insert Initial Data
    public void insertInitialInventoryDataIfEmpty() {
        String checkEmptySQL = "SELECT COUNT(*) FROM inventory";
        String insertSampleDataSQL = "INSERT INTO inventory (" +
                "    item_id, item_name, category, quantity, " +
                "    unit_price, supplier, last_restocked, status" +
                ") VALUES " +
                "('I001', 'Bed Sheets', 'Linens', 50, 25.00, 'Linen Suppliers Inc.', '2023-12-01', 'Active')," +
                "('I002', 'Towels', 'Bathroom', 100, 10.50, 'Textile Wholesalers', '2023-11-15', 'Active')," +
                "('I003', 'Cleaning Supplies', 'Maintenance', 30, 50.00, 'Cleaning Solutions Ltd.', '2023-12-10', 'Low Stock')";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement checkStmt = conn.createStatement();
                ResultSet rs = checkStmt.executeQuery(checkEmptySQL)) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (Statement insertStmt = conn.createStatement()) {
                    insertStmt.executeUpdate(insertSampleDataSQL);
                    LOGGER.info("Initial inventory data inserted");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking or inserting inventory data", e);
        }
    }

    // Initialize Database
    public void initializeInventoryDatabase() {
        createInventoryTableIfNotExists();
        insertInitialInventoryDataIfEmpty();
    }

    // Get All Inventory Items
    public List<Inventory> getAllInventoryItems() {
        List<Inventory> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM inventory";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Inventory item = new Inventory(
                        rs.getString("item_id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("supplier"),
                        rs.getDate("last_restocked") != null ? rs.getDate("last_restocked").toLocalDate() : null,
                        rs.getString("status"));
                inventoryList.add(item);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching inventory", e);
        }

        return inventoryList;
    }

    // Generate Unique Item ID
    public String generateUniqueItemId() {
        String query = "SELECT MAX(item_id) AS max_id FROM inventory";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String maxId = rs.getString("max_id");
                if (maxId == null) {
                    return "I001";
                }

                int number = Integer.parseInt(maxId.substring(1)) + 1;
                return String.format("I%03d", number);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating item ID", e);
        }

        return "I001";
    }

    // Add Inventory Item (continued)
    public boolean addInventoryItem(Inventory item) {
        String insertQuery = "INSERT INTO inventory " +
                "(item_id, item_name, category, quantity, unit_price, supplier, last_restocked, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, item.getItemId());
            pstmt.setString(2, item.getItemName());
            pstmt.setString(3, item.getCategory());
            pstmt.setInt(4, item.getQuantity());
            pstmt.setDouble(5, item.getUnitPrice());
            pstmt.setString(6, item.getSupplier());
            pstmt.setDate(7, item.getLastRestocked() != null ? Date.valueOf(item.getLastRestocked()) : null);
            pstmt.setString(8, item.getStatus());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding inventory item", e);
            return false;
        }
    }

    // Update Inventory Item
    public boolean updateInventoryItem(Inventory item) {
        String updateQuery = "UPDATE inventory SET " +
                "item_name = ?, category = ?, quantity = ?, " +
                "unit_price = ?, supplier = ?, last_restocked = ?, status = ? " +
                "WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getCategory());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getUnitPrice());
            pstmt.setString(5, item.getSupplier());
            pstmt.setDate(6, item.getLastRestocked() != null ? Date.valueOf(item.getLastRestocked()) : null);
            pstmt.setString(7, item.getStatus());
            pstmt.setString(8, item.getItemId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating inventory item", e);
            return false;
        }
    }

    // Delete Inventory Item
    public boolean deleteInventoryItem(String itemId) {
        String deleteQuery = "DELETE FROM inventory WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setString(1, itemId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting inventory item", e);
            return false;
        }
    }

    // Search Inventory Items
    public List<Inventory> searchInventoryItems(String searchTerm) {
        List<Inventory> searchResults = new ArrayList<>();
        String searchQuery = "SELECT * FROM inventory WHERE " +
                "item_name LIKE ? OR category LIKE ? OR supplier LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(searchQuery)) {

            String likePattern = "%" + searchTerm + "%";
            pstmt.setString(1, likePattern);
            pstmt.setString(2, likePattern);
            pstmt.setString(3, likePattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory item = new Inventory(
                            rs.getString("item_id"),
                            rs.getString("item_name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getString("supplier"),
                            rs.getDate("last_restocked") != null ? rs.getDate("last_restocked").toLocalDate() : null,
                            rs.getString("status"));
                    searchResults.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching inventory items", e);
        }

        return searchResults;
    }

    // Get Low Stock Items
    public List<Inventory> getLowStockItems(int threshold) {
        List<Inventory> lowStockItems = new ArrayList<>();
        String lowStockQuery = "SELECT * FROM inventory WHERE quantity <= ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(lowStockQuery)) {

            pstmt.setInt(1, threshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory item = new Inventory(
                            rs.getString("item_id"),
                            rs.getString("item_name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getString("supplier"),
                            rs.getDate("last_restocked") != null ? rs.getDate("last_restocked").toLocalDate() : null,
                            rs.getString("status"));
                    lowStockItems.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching low stock items", e);
        }

        return lowStockItems;
    }

    // Get Inventory Statistics
    public InventoryStatistics getInventoryStatistics() {
        InventoryStatistics stats = new InventoryStatistics();

        String totalItemsQuery = "SELECT COUNT(*) AS total_items FROM inventory";
        String totalValueQuery = "SELECT SUM(quantity * unit_price) AS total_value FROM inventory";
        String categoryStatsQuery = "SELECT category, COUNT(*) AS category_count FROM inventory GROUP BY category";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Total Items
            try (ResultSet rs = stmt.executeQuery(totalItemsQuery)) {
                if (rs.next()) {
                    stats.setTotalItems(rs.getInt("total_items"));
                }
            }

            // Total Value
            try (ResultSet rs = stmt.executeQuery(totalValueQuery)) {
                if (rs.next()) {
                    stats.setTotalInventoryValue(rs.getDouble("total_value"));
                }
            }

            // Category Stats
            Map<String, Integer> categoryStats = new HashMap<>();
            try (ResultSet rs = stmt.executeQuery(categoryStatsQuery)) {
                while (rs.next()) {
                    categoryStats.put(
                            rs.getString("category"),
                            rs.getInt("category_count"));
                }
            }
            stats.setCategoryStats(categoryStats);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching inventory statistics", e);
        }

        return stats;
    }

    // Continuing InventoryStatistics class
    public static class InventoryStatistics {
        // Previous code remains the same

        private int totalItems;
        private double totalInventoryValue;
        private Map<String, Integer> categoryStats;

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }


        public double getTotalInventoryValue() {
            return totalInventoryValue;
        }

        public void setTotalInventoryValue(double totalInventoryValue) {
            this.totalInventoryValue = totalInventoryValue;
        }

        public Map<String, Integer> getCategoryStats() {
            return categoryStats;
        }

        public void setCategoryStats(Map<String, Integer> categoryStats) {
            this.categoryStats = categoryStats;
        }
    }
}