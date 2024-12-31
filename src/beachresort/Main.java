package beachresort;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import beachresort.database.DatabaseConnection;
import beachresort.repositories.BookingRepository;
import beachresort.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try {
            // Detailed logging
            System.out.println("Starting Beach Resort Management System");
            
            // Test and initialize database connection
            if (DatabaseConnection.testConnection()) {
                // Initialize repositories
                initializeRepositories();
                
                // Launch application
                SwingUtilities.invokeLater(() -> {
                    // Remove SQLException catch, use generic Exception
                    try {
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.setVisible(true);
                    } catch (Exception e) {
                        handleStartupError(e);
                    }
                });
            } else {
                // Show error dialog if connection fails
                showDatabaseConnectionError();
            }
        } catch (Exception e) {
            handleStartupError(e);
        }
    }

    /**
     * Initialize repositories and verify their table structures
     */
    private static void initializeRepositories() {
        try {
            System.out.println("Initializing Repositories...");
            
            // Initialize and verify booking repository
            BookingRepository bookingRepository = new BookingRepository();
            bookingRepository.verifyTableStructure();
            System.out.println("Booking Repository initialized successfully.");
            
        } catch (Exception e) {
            System.err.println("Error initializing repositories: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database repositories: " + e.getMessage(), 
                "Repository Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Show database connection error dialog
     */
    private static void showDatabaseConnectionError() {
        JOptionPane.showMessageDialog(null, 
            "Unable to connect to database. Please check:\n" +
            "1. XAMPP MySQL service is running\n" +
            "2. Database 'beach_resort_db' exists\n" +
            "3. Connection credentials are correct", 
            "Database Connection Error", 
            JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /**
     * Handle any fatal startup errors
     * @param e The exception that occurred during startup
     */
    private static void handleStartupError(Exception e) {
        // Log the full stack trace
        e.printStackTrace();
        
        // Show user-friendly error message
        JOptionPane.showMessageDialog(null, 
            "Fatal error during application startup:\n" + 
            e.getMessage() + 
            "\n\nPlease contact support or check logs.", 
            "Startup Error", 
            JOptionPane.ERROR_MESSAGE);
        
        // Terminate the application
        System.exit(1);
    }
}