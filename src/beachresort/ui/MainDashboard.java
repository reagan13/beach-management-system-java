// ui/MainDashboard.java
package beachresort.ui;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    private String username;
    private String userRole;

    public MainDashboard(String username, String userRole) {
        this.username = username;
        this.userRole = userRole;
        initComponents();
    }

    private void initComponents() {
        setTitle("Beach Resort Management System - Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with CardLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Navigation Panel
        JPanel navigationPanel = createNavigationPanel();
        mainPanel.add(navigationPanel, BorderLayout.WEST);

        // Content Panel (will switch between different views)
        JPanel contentPanel = new JPanel(new CardLayout());
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Top Information Panel
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        add(mainPanel);
    }

    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setPreferredSize(new Dimension(200, getHeight()));
        navigationPanel.setBorder(BorderFactory.createEtchedBorder());

        // Navigation Buttons
        String[] navItems = {"Dashboard", "Rooms", "Bookings", "Guests", "Reports"};
        for (String item : navItems) {
            JButton button = new JButton(item);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            navigationPanel.add(button);
            navigationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return navigationPanel;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // User Information
        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (" + userRole + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        return topPanel;
    }

    private void logout() {
        // Close current dashboard
        dispose();
        
        // Open login frame again
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}