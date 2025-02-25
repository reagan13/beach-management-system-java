package beachresort.ui;

import beachresort.models.Customer;
import beachresort.models.Person;
import beachresort.repositories.CustomerRepository;
import beachresort.repositories.PersonRepository;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CustomerOverviewPanel extends JPanel {
    private Customer customer; 
    private CustomerRepository customerRepository; 
    private Person person; 
    private PersonRepository personRepository;

    // Text fields for customer details
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField addressField;
    private JTextField contactNumberField;
    private JTextField preferredAccommodationField;
    private JTextField numberVisitsField;

    public CustomerOverviewPanel(Person person) throws SQLException {
        this.person = person;
        this.customerRepository = new CustomerRepository();
        this.personRepository = new PersonRepository();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize customer object
        customer = customerRepository.getCustomerByUserId(person.getId());
        if (customer == null) {
            customer = new Customer(person.getId(), person.getUsername(), person.getPassword(), person.getEmail(), person.getFullName(), person.getAddress(), person.getContactNumber(), "", 0);
        }

        // Initialize text fields with current customer details
        usernameField = new JTextField(person.getUsername());
        usernameField.setEditable(false); 
        emailField = new JTextField(person.getEmail());
        fullNameField = new JTextField(person.getFullName());
        addressField = new JTextField(person.getAddress());
        contactNumberField = new JTextField(person.getContactNumber());
        preferredAccommodationField = new JTextField(customer.getPreferredAccommodationType());
        numberVisitsField = new JTextField(String.valueOf(customer.getNumberOfVisits()));

        // Create the first group panel for user details
        JPanel userDetailsPanel = createUserDetailsPanel();
        userDetailsPanel.setBorder(BorderFactory.createTitledBorder("User  Details"));
        add(userDetailsPanel);

        // Create the second group panel for customer details
        JPanel customerDetailsPanel = createCustomerDetailsPanel();
        customerDetailsPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        add(customerDetailsPanel);

        // Add update button
        JButton updateButton = new JButton("Update Customer");
        updateButton.addActionListener(e -> updateCustomer());
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createRigidArea(new Dimension(0, 10))); 
        add(updateButton);
    }

    private JPanel createUserDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Add components to the user details panel
        addComponent(panel, gbc, 0, 0, "Username:", usernameField);
        addComponent(panel, gbc, 0, 1, "Email:", emailField);
        addComponent(panel, gbc, 0, 2, "Full Name:", fullNameField);
        addComponent(panel, gbc, 0, 3, "Address:", addressField);
        addComponent(panel, gbc, 0, 4, "Contact Number:", contactNumberField);

        return panel;
    }

    private JPanel createCustomerDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Add components to the customer details panel
        addComponent(panel, gbc, 0, 0, "Preferred Accommodation Type:", preferredAccommodationField);
        addComponent(panel, gbc, 0, 1, "Number of Visits:", numberVisitsField);

        return panel;
    }

    private void addComponent(JPanel panel, GridBagConstraints gbc, int x, int y, String labelText, JTextField textField) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER; 
        panel.add(textField, gbc);
    }

    private void updateCustomer() {
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
                    String preferredAccommodation = preferredAccommodationField.getText().trim();
                    int numberVisits = Integer.parseInt(numberVisitsField.getText().trim());

                    // Validate inputs
                    if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() ||
                            address.isEmpty() || contactNumber.isEmpty() ||
                            preferredAccommodation.isEmpty()) {
                        JOptionPane.showMessageDialog(CustomerOverviewPanel.this,
                                "All fields must be filled out.", "Validation Error",
                                JOptionPane.WARNING_MESSAGE);
                        return null;
                    }

                    // Update the user and customer
                    person.setUsername(username);
                    person.setEmail(email);
                    person.setFullName(fullName);
                    person.setAddress(address);
                    person.setContactNumber(contactNumber);
                    customer.setPreferredAccommodationType(preferredAccommodation);
                    customer.setNumberOfVisits(numberVisits);

                    personRepository.updateUser(person);
                    customerRepository.updateCustomer(person.getId(), numberVisits, preferredAccommodation);

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(CustomerOverviewPanel.this,
                            "Error updating customer details: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }

            @Override
            protected void done() {
                // Show success message on completion
                JOptionPane.showMessageDialog(CustomerOverviewPanel.this,
                        "Customer details updated successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }
}