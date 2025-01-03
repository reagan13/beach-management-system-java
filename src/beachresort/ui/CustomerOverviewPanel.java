package beachresort.ui;

import beachresort.models.User;

import javax.swing.*;
import java.awt.*;

public class CustomerOverviewPanel extends JPanel {
    private User currentUser;

    public CustomerOverviewPanel(User owner) {
        this.currentUser = owner;
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Customer Overview", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Overview content
        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout(new GridLayout(4, 2, 20, 20));

        overviewPanel.add(new JLabel("Name:"));
        overviewPanel.add(new JLabel(currentUser.getFullName()));
        
        overviewPanel.add(new JLabel("Email:"));
        overviewPanel.add(new JLabel(currentUser.getEmail()));
        
        
        overviewPanel.add(new JLabel("Role:"));
        overviewPanel.add(new JLabel(currentUser.getRole()));

        add(overviewPanel, BorderLayout.CENTER);
    }
}