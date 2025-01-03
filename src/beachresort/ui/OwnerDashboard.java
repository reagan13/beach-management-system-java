package beachresort.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OwnerDashboard extends JFrame {
    private JPanel mainPanel;
    private JButton currentActiveButton;

    public OwnerDashboard( ) {
        // Frame setup
        setTitle("Beach Resort Management System");
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
        mainPanel.add(new OverviewPanel(), "Overview");
        mainPanel.add(new CheckInOutPanel(), "Check In / Checkout"); // Add the new panel
        mainPanel.add(new ManageBookingsPanel(), "Manage Bookings");
        mainPanel.add(new RoomManagementPanel(), "Room Management");
        mainPanel.add(new StaffManagementPanel(), "Staff Management");
        mainPanel.add(new PaymentManagementPanel(), "Payment Management"); // Add the new panel
      
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

        JLabel titleLabel = new JLabel("Welcome, Owner");
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
        JButton checkInOutButton = createStyledButton("Check In / Checkout"); // New button
        JButton manageBookingsButton = createStyledButton("Manage Bookings");
        JButton roomManagementButton = createStyledButton("Room Management");
        JButton staffManagementButton = createStyledButton("Staff Management");
        JButton paymentManagementButton = createStyledButton("Payment Management"); // New button
        

        // Add Navigation Buttons
        navigationPanel.add(overviewButton, gbc);
        navigationPanel.add(checkInOutButton, gbc); // Add the new button
        navigationPanel.add(manageBookingsButton, gbc);
        navigationPanel.add(roomManagementButton, gbc);
        navigationPanel.add(staffManagementButton, gbc);
        navigationPanel.add(paymentManagementButton, gbc); // Add the new button
    

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
        manageBookingsButton.addActionListener(e -> showPanel("Manage Bookings"));
        roomManagementButton.addActionListener(e -> showPanel("Room Management"));
        staffManagementButton.addActionListener(e -> showPanel("Staff Management"));
        checkInOutButton.addActionListener(e -> showPanel("Check In / Checkout")); // Action for new button
        paymentManagementButton.addActionListener(e -> showPanel("Payment Management")); // Action for new button
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
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Close current dashboard
            dispose();
            
            // Open login frame
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OwnerDashboard().setVisible(true);
        });
    }
}