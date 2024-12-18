package beachresort.ui;

import javax.swing.*;
import java.awt.*;

public class OwnerDashboard extends JFrame {
    private JPanel mainContent;

    public OwnerDashboard() {
        setTitle("Resort Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top Bar
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Left Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main Content Area
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainContent.add(createDashboard(), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // System Title
        JLabel titleLabel = new JLabel("Resort Management Dashboard", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        // User and Logout Section
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userLabel = new JLabel("Logged in as: Admin");
        userLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION
            );
            if (response == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menuItems = {
            "Dashboard", "Bookings", "Rooms", 
            "Staff", "Inventory", "Reports", "Settings"
        };

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setFont(new Font("Dialog", Font.PLAIN, 14));
            btn.setMaximumSize(new Dimension(230, 40));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> updateMainContent(item));
            
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return sidebar;
    }

    private JPanel createDashboard() {
        JPanel dashboardContainer = new JPanel(new BorderLayout());
        
        // // Dashboard Title
        // JLabel dashboardTitle = new JLabel("Dashboard Overview", SwingConstants.LEFT);
        // dashboardTitle.setFont(new Font("Dialog", Font.BOLD, 18));
        // dashboardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        // dashboardContainer.add(dashboardTitle, BorderLayout.NORTH);

        // Dashboard Cards
        JPanel dashboard = new JPanel(new GridLayout(2, 3, 20, 20));

        String[] cardTitles = {
            "Total Bookings", "Rooms Occupied", 
            "Monthly Revenue", "Pending Bookings", 
            "Available Rooms", "Customer Satisfaction"
        };

        for (String title : cardTitles) {
            dashboard.add(createDashboardCard(title));
        }

        dashboardContainer.add(dashboard, BorderLayout.CENTER);
        return dashboardContainer;
    }

    private JPanel createDashboardCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Dialog", Font.PLAIN, 22));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void updateMainContent(String section) {
        mainContent.removeAll();
        
        JLabel sectionTitle = new JLabel(section, SwingConstants.LEFT);
        sectionTitle.setFont(new Font("Dialog", Font.BOLD, 18));
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(sectionTitle, BorderLayout.NORTH);

        switch (section) {
            case "Dashboard":
                contentPanel.add(createDashboard(), BorderLayout.CENTER);
                break;
            case "Bookings":
                contentPanel.add(new BookingManagementPanel(), BorderLayout.CENTER);
                break;
            case "Rooms":
                contentPanel.add(new RoomManagementPanel(), BorderLayout.CENTER);
                break;
            case "Staff":
                contentPanel.add(new StaffManagementPanel(), BorderLayout.CENTER);
                break;
            case "Inventory":
                contentPanel.add(new InventoryManagementPanel(), BorderLayout.CENTER);
                break;
            case "Reports":
                contentPanel.add(new ReportingPanel(), BorderLayout.CENTER);
                break;
            case "Settings":
                contentPanel.add(new JLabel("Settings Panel"), BorderLayout.CENTER);
                break;
        }
        
        mainContent.add(contentPanel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            OwnerDashboard dashboard = new OwnerDashboard();
            dashboard.setVisible(true);
        });
    }
}