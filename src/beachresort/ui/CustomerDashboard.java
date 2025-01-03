package beachresort.ui;

import beachresort.models.User;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {
    private JPanel mainContent;
    private User currentUser ; // Change from Owner to User

    public CustomerDashboard(User user) {
        this.currentUser  = user; // Initialize currentUser  with the passed User object
        setTitle("Customer Dashboard");
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
        mainContent.add(new CustomerBookingPanel(currentUser.getId()), BorderLayout.CENTER);
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
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
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

        String[] menuItems = {"My Bookings"};

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

    private void updateMainContent(String section) {
        mainContent.removeAll();
        
        switch (section) {
            // case "Overview":
            //     mainContent.add(new CustomerOverviewPanel(currentUser ), BorderLayout.CENTER);
            //     break;
            case "My Bookings":
                mainContent.add(new CustomerBookingPanel(currentUser.getId()), BorderLayout.CENTER);
                break;
        }
        
        mainContent.revalidate();
        mainContent.repaint();
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         new CustomerDashboard().setVisible(true);
    //     });
    // }
}