package beachresort.ui;

import javax.swing.*;
import java.awt.*;
import beachresort.models.Person;

public class StaffDetailsPanel extends JPanel {
    public StaffDetailsPanel(Person person) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Staff Details"));

        // Create a panel with GridBagLayout
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Display staff details
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel usernameValue = new JLabel(person.getUsername());
        
        JLabel emailLabel = new JLabel("Email: ");
        JLabel emailValue = new JLabel(person.getEmail());
        
        JLabel fullNameLabel = new JLabel("Full Name: ");
        JLabel fullNameValue = new JLabel(person.getFullName());
        
        JLabel addressLabel = new JLabel("Address: ");
        JLabel addressValue = new JLabel(person.getAddress());
        
        JLabel contactNumberLabel = new JLabel("Contact Number: ");
        JLabel contactNumberValue = new JLabel(person.getContactNumber());

        // Add labels to the details panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        detailsPanel.add(usernameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        detailsPanel.add(emailValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(fullNameLabel, gbc);
        gbc.gridx = 1;
        detailsPanel.add(fullNameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        detailsPanel.add(addressValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(contactNumberLabel, gbc);
        gbc.gridx = 1;
        detailsPanel.add(contactNumberValue, gbc);

        // Add the details panel to the main panel
        add(detailsPanel, BorderLayout.CENTER);

        // Optional: Add some spacing at the bottom
        add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
    }
}