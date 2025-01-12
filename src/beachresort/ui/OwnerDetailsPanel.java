package beachresort.ui;

import beachresort.models.Owner;
import beachresort.models.Person;
import beachresort.repositories.OwnerRepository;
import beachresort.repositories.PersonRepository;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class OwnerDetailsPanel extends JPanel {
    private Owner owner; // The owner object to display and update
    private OwnerRepository ownerRepository; // Repository to handle database operations
    private Person person; // The owner object to display and update
    private PersonRepository personRepository; // Repository to handle database operations

    // Text fields for owner details
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField addressField;
    private JTextField contactNumberField;
    private JTextField businessNameField;
    private JTextField licenseNumberField;

    public OwnerDetailsPanel(Person person) throws SQLException {
        this.person = person;
        this.ownerRepository = new OwnerRepository();
        this.personRepository = new PersonRepository();

        setLayout(new GridLayout(8, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Owner Details"));

             
        if(owner == null){
            owner = new Owner(person.getId(),person.getUsername(), person.getPassword(), person.getEmail(), person.getFullName(), person.getAddress(), person.getContactNumber(), "","");
        } else {
            owner.setBusinessName("");
            owner.setLicenseNumber("");
        }

    


        // Initialize text fields with current owner details
        usernameField = new JTextField(person.getUsername());
        emailField = new JTextField(person.getEmail());
        fullNameField = new JTextField(person.getFullName());
        addressField = new JTextField(person.getAddress());
        contactNumberField = new JTextField(person.getContactNumber());
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
        owner = ownerRepository.getOwnerByUserId(person.getId());
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
                    person.setUsername(username);
                    person.setEmail(email);
                    person.setFullName(fullName);
                    person.setAddress(address);
                    person.setContactNumber(contactNumber);
                    owner.setBusinessName(businessName);
                    owner.setLicenseNumber(licenseNumber);

                    personRepository.updateUser(person);
                    ownerRepository.updateOwner(owner.getBusinessName(), owner.getLicenseNumber(), person.getId());

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