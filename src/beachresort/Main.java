// Main.java
package beachresort;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import beachresort.database.DatabaseConnection;
import beachresort.ui.LoginFrame;

public class Main {
  public static void main(String[] args) {
    try {
        // Detailed logging
        System.out.println("Starting Beach Resort Management System");
        
        // Test database connection
        if (DatabaseConnection.testConnection()) {
            // Launch application
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        } else {
            // Show error dialog if connection fails
            JOptionPane.showMessageDialog(null, 
                "Unable to connect to database. Please check XAMPP services.", 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Fatal error during application startup: " + e.getMessage(), 
            "Startup Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
}