package beachresort.ui;

import beachresort.models.Owner;
import beachresort.models.User;
import beachresort.repositories.OwnerRepository;
import beachresort.repositories.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class OwnerDetailsPanel extends JPanel {
    private Owner owner; // The owner object to display and update
    private OwnerRepository ownerRepository; // Repository to handle database operations
    private User user; // The owner object to display and update
    private UserRepository userRepository; // Repository to handle database operations

    // Text fields for owner details
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField addressField;
    private JTextField contactNumberField;
    private JTextField businessNameField;
    private JTextField licenseNumberField;

    public OwnerDetailsPanel(User user) throws SQLException {
        this.user = user;
        this.ownerRepository = new OwnerRepository();
        this.userRepository = new UserRepository();

        setLayout(new GridLayout(8, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Owner Details"));

             
        if(owner == null){
            owner = new Owner(user.getId(),user.getUsername(), user.getPassword(), user.getEmail(), user.getFullName(), user.getAddress(), user.getContactNumber(), "","");
        } else {
            owner.setBusinessName("");
            owner.setLicenseNumber("");
        }

    


        // Initialize text fields with current owner details
        usernameField = new JTextField(user.getUsername());
        emailField = new JTextField(user.getEmail());
        fullNameField = new JTextField(user.getFullName());
        addressField = new JTextField(user.getAddress());
        contactNumberField = new JTextField(user.getContactNumber());
        businessNameField = new JTextField(owner.getBusinessName());
        licenseNumberField = new JTextField(owner.getLicenseNumber());

        // Add labels and fields to the panel
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Full Name:"));
        add(fullNameField);
        add(new JLabel("Address:"));
        add(addressField);
        add(new JLabel("Contact Number:"));
        add(contactNumberField);
        add(new JLabel("Business Name:"));
        add(businessNameField);
        add(new JLabel("License Number:"));
        add(licenseNumberField);

        // Load owner details
        loadOwnerDetails();

        
        // Add update button
        JButton updateButton = new JButton("Update Owner");
        updateButton.addActionListener(e -> updateOwner());
        add(updateButton);
    }

    private void loadOwnerDetails() {
        owner = ownerRepository.getOwnerByUserId(user.getId());
        if (owner != null) {
            businessNameField.setText(owner.getBusinessName());
            licenseNumberField.setText(owner.getLicenseNumber());
        }
    }
    

    private void updateOwner() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Get updated values from text fields
                    String username = usernameField.getText().trim();
                    String email = emailField.getText().trim();
                    String fullName = fullNameField.getText().trim();
                    String address = addressField.getText().trim();
                    String contactNumber = contactNumberField.getText().trim();
                    String businessName = businessNameField.getText().trim();
                    String licenseNumber = licenseNumberField.getText().trim();

                    // Validate inputs
                    if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() ||
                            address.isEmpty() || contactNumber.isEmpty() ||
                            businessName.isEmpty() || licenseNumber.isEmpty()) {
                        JOptionPane.showMessageDialog(OwnerDetailsPanel.this,
                                "All fields must be filled out.", "Validation Error",
                                JOptionPane.WARNING_MESSAGE);
                        return null;
                    }

                    // Update the user and owner
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setFullName(fullName);
                    user.setAddress(address);
                    user.setContactNumber(contactNumber);
                    owner.setBusinessName(businessName);
                    owner.setLicenseNumber(licenseNumber);

                    userRepository.updateUser(user);
                    ownerRepository.updateOwner(owner.getBusinessName(), owner.getLicenseNumber(), user.getId());

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(OwnerDetailsPanel.this,
                            "Error updating owner details: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }

            @Override
            protected void done() {
                // Show success message on completion
                JOptionPane.showMessageDialog(OwnerDetailsPanel.this,
                        "Owner details updated successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }


    
}