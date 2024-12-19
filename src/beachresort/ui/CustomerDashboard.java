package beachresort.ui;

import beachresort.models.Owner;
import beachresort.repositories.OwnerRepository;
import beachresort.repositories.DashboardStats;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CustomerDashboard extends JFrame {
    private JPanel mainContent;
    private Owner currentOwner;
    private OwnerRepository ownerRepository;
    private DashboardStats dashboardStats;

    public CustomerDashboard(Owner owner) throws SQLException {
        this.currentOwner = owner;
        this.ownerRepository = new OwnerRepository();
        this.dashboardStats = ownerRepository.getDashboardStats();

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
        JLabel titleLabel = new JLabel("Customer Dashboard", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));

        // User and Logout Section
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
     
        
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

        // Dashboard Cards
        JPanel dashboard = new JPanel(new GridLayout(2, 3, 20, 20));

       String[] cardTitles = {
        "Total Bookings", "Rooms Occupied", 
        "Monthly Revenue", "Pending Bookings", 
        "Available Rooms", "Total Staff"
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

        JLabel valueLabel = new JLabel(getCardValue(title), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Dialog", Font.PLAIN, 22));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

   private String getCardValue(String title) {
    switch(title) {
        case "Total Bookings":
            return String.valueOf(dashboardStats.getTotalBookings());
        case "Rooms Occupied":
            return String.valueOf(dashboardStats.getRoomsOccupied());
        case "Monthly Revenue":
            return String.format("$%.2f", dashboardStats.getMonthlyRevenue());
        case "Pending Bookings":
            return String.valueOf(dashboardStats.getPendingBookings());
        case "Available Rooms":
            return String.valueOf(dashboardStats.getAvailableRooms());
        case "Total Staff":
            return String.valueOf(dashboardStats.getTotalStaff());
        default:
            return "0";
    }
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
                contentPanel.add(new CustomerBookingPanel(), BorderLayout.CENTER);
                break;
            case "Rooms":
                contentPanel.add(new CustomerRoomPanel(), BorderLayout.CENTER);
                break;
            case "Staff":
                contentPanel.add(new StaffManagementPanel(), BorderLayout.CENTER);
                break;
            case "Inventory":
                contentPanel.add(new InventoryManagementPanel(), BorderLayout.CENTER);
                break;
           
        }
        
        mainContent.add(contentPanel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

        // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Create a default owner for testing
            Owner testOwner = new Owner(
                1, 
                "admin", 
                "Resort Administrator", 
                "admin@beachresort.com", 
                "1234567890", 
                "Super Admin"
            );
            
            OwnerDashboard dashboard = null;
            try {
                dashboard = new OwnerDashboard(testOwner);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dashboard.setVisible(true);
        });
    }

    // Additional constructor for flexibility
    public CustomerDashboard() throws SQLException {
        this(new Owner(
            1, 
            "admin", 
            "Resort Administrator", 
            "admin@beachresort.com", 
            "1234567890", 
            "Super Admin"
        ));
    }

    // Getter for current owner
    public Owner getCurrentOwner() {
        return currentOwner;
    }

    // Method to refresh dashboard stats
    public void refreshDashboardStats() {
        this.dashboardStats = ownerRepository.getDashboardStats();
        // Optionally, you can call a method to update the dashboard cards
        updateDashboardCards();
    }

    // Helper method to update dashboard cards
    private void updateDashboardCards() {
        // This method would need to be implemented to dynamically update the dashboard cards
        // You might need to keep track of the dashboard panel and its card labels
        Component[] components = mainContent.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel dashboardPanel = (JPanel) comp;
                // Iterate through dashboard cards and update their values
                // This is a simplified example and might need more robust implementation
                for (Component cardComp : dashboardPanel.getComponents()) {
                    if (cardComp instanceof JPanel) {
                        JPanel card = (JPanel) cardComp;
                        // Find the value label and update it
                        for (Component labelComp : card.getComponents()) {
                            if (labelComp instanceof JLabel && 
                                ((JLabel) labelComp).getFont().isBold()) {
                                JLabel titleLabel = (JLabel) labelComp;
                                JLabel valueLabel = findValueLabel(card);
                                if (valueLabel != null) {
                                    valueLabel.setText(getCardValue(titleLabel.getText()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Helper method to find the value label in a card
    private JLabel findValueLabel(JPanel card) {
        for (Component comp : card.getComponents()) {
            if (comp instanceof JLabel && 
                ((JLabel) comp).getFont().getSize() == 22) {
                return (JLabel) comp;
            }
        }
        return null;
    }

    // Method to handle owner profile updates
    public void updateOwnerProfile(Owner updatedOwner) {
        this.currentOwner = updatedOwner;
        // Update the top bar user label
        Component[] topComponents = getContentPane().getComponents();
        for (Component comp : topComponents) {
            if (comp instanceof JPanel && comp.getName() != null && 
                comp.getName().equals("topBar")) {
                updateTopBarUserLabel((JPanel) comp);
                break;
            }
        }
    }

    // Helper method to update top bar user label
    private void updateTopBarUserLabel(JPanel topBar) {
        for (Component comp : topBar.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel userPanel = (JPanel) comp;
                for (Component labelComp : userPanel.getComponents()) {
                    if (labelComp instanceof JLabel && 
                        labelComp.getName() != null && 
                        labelComp.getName().equals("userLabel")) {
                        ((JLabel) labelComp).setText("Logged in as: " + currentOwner.getFullName());
                        break;
                    }
                }
            }
        }
    }
}