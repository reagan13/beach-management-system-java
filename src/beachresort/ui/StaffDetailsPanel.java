package beachresort.ui;

import javax.swing.*;
import beachresort.models.User;

public class StaffDetailsPanel extends JPanel {
    public StaffDetailsPanel(User user) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Staff Details"));

 // Display staff details
        JLabel usernameLabel = new JLabel("Username: " + user.getUsername());
        JLabel emailLabel = new JLabel("Email: " + user.getEmail());
        JLabel fullNameLabel = new JLabel("Full Name: " + user.getFullName());
        JLabel addressLabel = new JLabel("Address: " + user.getAddress());
        JLabel contactNumberLabel = new JLabel("Contact Number: " + user.getContactNumber());

        // Add labels to the panel
        add(usernameLabel);
        add(emailLabel);
        add(fullNameLabel);
        add(addressLabel);
        add(contactNumberLabel);
    }
}