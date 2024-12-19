package beachresort.ui;

import beachresort.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterDialog extends JDialog {
    private PlaceholderTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton cancelButton;
    private AuthenticationService authService;

    public RegisterDialog(JFrame parent) throws SQLException {
        // Call parent constructor with modal settings
        super(parent, "Register New User", true);
        
        // Initialize authentication service
        authService = new AuthenticationService();

        // Set dialog properties
        setSize(450, 500);
        setLocationRelativeTo(parent);

        // Create components
        initComponents();

        // Create layout
        createLayout();
    }

    private void initComponents() {
        // Username Field
        usernameField = new PlaceholderTextField(20);
        usernameField.setPlaceholder("Username");
        
        // Password Fields
        passwordField = new JPasswordField(20);
        passwordField.setEchoChar('*');
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setEchoChar('*');
        
        // Role Combo Box (Exclude OWNER)
        String[] roles = {"CUSTOMER", "STAFF"};
        roleComboBox = new JComboBox<>(roles);

        // Register Button
        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        // Cancel Button
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void createLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Username:"), gbc);
        
        gbc.gridy = 2;
        add(usernameField, gbc);

        // Password
        gbc.gridy = 3;
        add(new JLabel("Password:"), gbc);
        
        gbc.gridy = 4;
        add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = 5;
        add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridy = 6;
        add(confirmPasswordField, gbc);

        // Role
        gbc.gridy = 7;
        add(new JLabel("Role:"), gbc);
        
        gbc.gridy = 8;
        add(roleComboBox, gbc);

        // Buttons
        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(registerButton, gbc);

        gbc.gridx = 1;
        add(cancelButton, gbc);
    }

    private void performRegistration() {
        // Validate input fields
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        // Validate username
        if (!authService.isValidUsername(username)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid username. Must be 3-50 characters, start with a letter, and contain only alphanumeric characters and underscores.", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate password
        if (!authService.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid password. Must be at least 8 characters long and contain uppercase, lowercase, and a digit.", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match!", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Perform registration
        boolean registrationSuccess = authService.registerUser(username, password, role);

        // Handle registration result
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
    }

    // Custom method to add placeholder to text fields
    private class PlaceholderTextField extends JTextField {
        private String placeholder;

        public PlaceholderTextField(int columns) {
            super(columns);
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && placeholder != null) {
                g.setColor(Color.GRAY);
                g.drawString(placeholder, getInsets().left, 
                    g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        }
    }
}