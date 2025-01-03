package beachresort.ui;

import beachresort.models.User;
import beachresort.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private AuthenticationService authService;

    public LoginFrame() {
        // Frame setup
        setTitle("Beach Resort Management System");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize authentication service
        try {
            authService = new AuthenticationService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to initialize authentication: " + e.getMessage(), 
                "Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Create and set up the content
        createLoginUI();
    }

    private void createLoginUI() {
        // Main panel with centered layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Title
        JLabel titleLabel = new JLabel("Beach Resort Management System", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Username Label and Field
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(usernameField, gbc);

        // Password Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(passwordField, gbc);

        // Role Label and Combo Box
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        String[] roles = {"CUSTOMER", "STAFF", "OWNER"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(roleComboBox, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(250, 35));
        loginButton.addActionListener(this::performLogin);
        mainPanel.add(loginButton, gbc);

        // Register Link
        gbc.gridy = 5;
        JLabel registerLink = new JLabel("Don't have an account? Register here");
        registerLink.setForeground(Color.BLUE);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openRegisterDialog();
            }
        });
        registerLink.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(registerLink, gbc);

        // Add main panel to frame
        add(mainPanel);
    }

    private void performLogin(ActionEvent e) {
        // Validate input fields
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username cannot be empty",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Password cannot be empty",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            // Authenticate user and get User object
            Optional<User> userOpt = authService.authenticateUser (username, password, role);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Successful login
                JOptionPane.showMessageDialog(this,
                        "Login Successful! User: " + user.getUsername(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Open main application window based on role
                openMainWindow(role, user);

                // Close login form
                dispose();
            } else {
                // Failed login
                JOptionPane.showMessageDialog(this,
                        "Invalid username, password, or role",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMainWindow(String role, User user) {
        try {
            switch (role) {
                case "CUSTOMER":
                    new CustomerDashboard(user).setVisible(true);
                    break;
                case "STAFF":
                case "OWNER":
                    new OwnerDashboard().setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unsupported role");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error opening dashboard: " + e.getMessage(),
                    "Dashboard Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(this);
        registerDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}