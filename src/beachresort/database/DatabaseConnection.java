// DatabaseConnection.java
package beachresort.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // XAMPP Default Configuration
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "beach_resort_db";
    
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + 
        "?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";  // XAMPP default username
    private static final String PASSWORD = "";      // XAMPP default (empty password)

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static boolean testConnection() {
    try {
        System.out.println("Connection URL: " + URL);
        System.out.println("Username: " + USERNAME);
        
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Connection successful!");
            
            // Additional connection details
            System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Database Version: " + conn.getMetaData().getDatabaseProductVersion());
            
            conn.close();
            return true;
        }
    } catch (SQLException e) {
        System.err.println("Connection Error Details:");
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("Message: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}
}