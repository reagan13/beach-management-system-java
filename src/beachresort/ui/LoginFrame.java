package beachresort.ui;

import beachresort.models.User;
import beachresort.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<User.UserRole> roleComboBox; // Use enum for roles
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
            showErrorDialog("Failed to initialize authentication: " + e.getMessage());
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
        usernameField.setToolTipText("Enter your username");
        mainPanel.add(usernameField, gbc);

        // Password Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setToolTipText("Enter your password");
        mainPanel.add(passwordField, gbc);

        // Role Label and Combo Box
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(User.UserRole.values()); // Use enum values
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
            showWarningDialog("Username cannot be empty");
            return;
        }

        if (passwordField.getPassword().length == 0) {
            showWarningDialog("Password cannot be empty");
            return;
        }

        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            User.UserRole role = (User.UserRole) roleComboBox.getSelectedItem();
           
            // Authenticate user and get User object
            User user = authService.authenticateUser(username, password, role.name());

            if (user != null) {
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
                showErrorDialog("Invalid username, password, or role");
            }
        } catch (Exception ex) {
            showErrorDialog("Error: " + ex.getMessage());
        }
    }
    

    private void openMainWindow(User.UserRole role, User user) {
        try {
            switch (role) {
                case CUSTOMER:
                    new CustomerDashboard(user).setVisible(true);
                    break;
              
                case OWNER:
                    new OwnerDashboard(user).setVisible(true);
                    break;
                default:
                    showErrorDialog("Unsupported role");
            }
        } catch (Exception e) {
            showErrorDialog("Error opening dashboard: " + e.getMessage());
        }
    }

    private void openRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(this);
        registerDialog.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}