package beachresort.ui;

import beachresort.models.User;
import beachresort.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<User.UserRole> roleComboBox; // Use enum for roles
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField addressField;
    private JTextField contactNumberField;
    private AuthenticationService authService;

    public RegisterDialog(JFrame parent) {
        // Call parent constructor with modal settings
        super(parent, "Register New User", true);
        
        // Initialize authentication service
        try {
            authService = new AuthenticationService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Initialization Error: " + e.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Set dialog properties
        setSize(400, 600);
        setLocationRelativeTo(parent);

        // Create components and layout
        createUI();
    }

    private void createUI() {
        // Main panel with grid bag layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("User  Registration", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(confirmPasswordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(emailField, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        fullNameField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(fullNameField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        addressField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(addressField, gbc);

        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(new JLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        contactNumberField = new JTextField(20);
        contactNumberField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(contactNumberField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy =  8;
        mainPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(User .UserRole.values()); // Use enum values
        roleComboBox.setSelectedIndex(0);
        roleComboBox.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(roleComboBox, gbc);

        // Register Button
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(250, 35));
        registerButton.addActionListener(this::performRegistration);
        mainPanel.add(registerButton, gbc);

        // Cancel Button
        gbc.gridy = 10;
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(250, 35));
        cancelButton.addActionListener(e -> dispose());
        mainPanel.add(cancelButton, gbc);

        // Add main panel to dialog
        add(mainPanel);
    }

    private void performRegistration(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String address = addressField.getText();
        String contactNumber = contactNumberField.getText();
        User.UserRole role = (User .UserRole) roleComboBox.getSelectedItem();

        // Basic validation
        if (username.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "Username must be at least 6 characters", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocusInWindow();
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 8 characters", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocusInWindow();
            return;
        }

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match!", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocusInWindow();
            return;
        }

        // Perform registration
        try {
            boolean registrationSuccess = authService.registerUser(username, password, role.name(), email, fullName, address, contactNumber);

            if (registrationSuccess) {
                JOptionPane.showMessageDialog(this, 
                    "Registration Successful!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the dialog
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Registration Failed. Username might already exist.", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Unexpected Error: " + ex.getMessage(), 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}