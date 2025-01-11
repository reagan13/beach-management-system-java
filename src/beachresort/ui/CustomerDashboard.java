package beachresort.ui;

import beachresort.models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CustomerDashboard extends JFrame {
    private JPanel mainPanel; // Main content panel using CardLayout
    private User currentUser ; // Current user

    public CustomerDashboard(User user) throws SQLException {
        this.currentUser  = user; // Initialize currentUser  with the passed User object
        setTitle("Customer Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set overall layout
        setLayout(new BorderLayout());

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create main content panel
        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(new CustomerOverviewPanel(currentUser ), "Overview");
        mainPanel.add(new CustomerBookingPanel(currentUser.getId()), "My Bookings");
        // Add more panels as needed
        // mainPanel.add(new AnotherPanel(), "Another Panel");

        add(mainPanel, BorderLayout.CENTER);

        // Set initial view
        showPanel("Overview");
    }

    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEtchedBorder());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Welcome, Customer");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Top Panel for Navigation Buttons
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // Navigation Buttons
        JButton overviewButton = createStyledButton("Overview");
        JButton myBookingsButton = createStyledButton("My Bookings");
        // Add more buttons as needed

        // Add Navigation Buttons
        navigationPanel.add(overviewButton, gbc);
        navigationPanel.add(myBookingsButton, gbc);
        // Add more buttons as needed

        // Bottom Panel for Logout Button
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bottomGbc = new GridBagConstraints();
        bottomGbc.gridx = 0;
        bottomGbc.gridy = GridBagConstraints.RELATIVE;
        bottomGbc.fill = GridBagConstraints.HORIZONTAL;
        bottomGbc.insets = new Insets(10, 10, 10, 10);
        bottomGbc.weightx = 1.0;

        // Logout Button
        JButton logoutButton = createStyledButton("Logout");
        bottomPanel.add(logoutButton, bottomGbc);

        // Add Action Listeners
        overviewButton.addActionListener(e -> showPanel("Overview"));
        myBookingsButton.addActionListener(e -> showPanel("My Bookings"));
        // Add more action listeners as needed
        logoutButton.addActionListener(e -> handleLogout());

        // Combine Panels
        sidebarPanel.add(titlePanel, BorderLayout.NORTH);
        sidebarPanel.add(navigationPanel, BorderLayout.CENTER);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);

        return sidebarPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        
        // Consistent button styling
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setMinimumSize(new Dimension(200, 40));
        
        // Remove focus painting
        button.setFocusPainted(false);
        
        return button;
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, panelName);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION
        );
        
        if ( confirm == JOptionPane.YES_OPTION) {
            // Close current dashboard
            dispose();
            
            // Open login frame
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
}