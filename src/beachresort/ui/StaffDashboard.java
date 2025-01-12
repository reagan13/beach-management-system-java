package beachresort.ui;

import javax.swing.*;
import beachresort.models.Person;
import java.awt.*;
import java.sql.SQLException;

public class StaffDashboard extends JFrame {
    private JPanel mainPanel;

    public StaffDashboard(Person person) throws SQLException {
        // Frame setup
        setTitle("Staff Management System");
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
        mainPanel.add(new StaffDetailsPanel(person), "Staff Details");
        mainPanel.add(new TaskManagementPanel(person), "Task Management");
        mainPanel.add(new AbsenceManagementPanel(person), "Absence Management");

        add(mainPanel, BorderLayout.CENTER);

        // Set initial view
        showPanel("Staff Details");
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

        JLabel titleLabel = new JLabel("Welcome, Staff");
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
        JButton staffDetailsButton = createStyledButton("Staff Details");
        JButton taskManagementButton = createStyledButton("Task Management");
        JButton absenceManagementButton = createStyledButton("Absence Management");
        JButton logoutButton = createStyledButton("Logout");

        // Add Navigation Buttons
        navigationPanel.add(staffDetailsButton, gbc);
        navigationPanel.add(taskManagementButton, gbc);
        navigationPanel.add(absenceManagementButton, gbc);
        navigationPanel.add(logoutButton, gbc);

        // Add Action Listeners
        staffDetailsButton.addActionListener(e -> showPanel("Staff Details"));
        taskManagementButton.addActionListener(e -> showPanel("Task Management"));
        absenceManagementButton.addActionListener(e -> showPanel("Absence Management"));
        logoutButton.addActionListener(e -> handleLogout());

        // Combine Panels
        sidebarPanel.add(titlePanel, BorderLayout.NORTH);
        sidebarPanel.add(navigationPanel, BorderLayout.CENTER);

        return sidebarPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setMinimumSize(new Dimension(200, 40));
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
}