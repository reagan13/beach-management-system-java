package beachresort.ui;

import beachresort.models.User;
import beachresort.services.AuthenticationService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticationService authService;

    public LoginFrame() {
        authService = new AuthenticationService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Beach Resort Management System - Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Title Panel
        JLabel titleLabel = new JLabel("Beach Resort Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Username
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        // Password
        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::performLogin);
        loginPanel.add(loginButton);

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> openRegistrationDialog());
        loginPanel.add(registerButton);

        mainPanel.add(loginPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password", "Login Error");
            return;
        }

        User user = authService.authenticateUser(username, password);
        
        if (user != null) {
            openDashboard(user);
        } else {
            showMessage("Invalid credentials", "Login Failed");
        }
    }

    private void openDashboard(User user) {
        new MainDashboard(user.getUsername(), user.getRole()).setVisible(true);
        dispose();
    }

    private void openRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Registration Fields
        panel.add(new JLabel("Username:"));
        JTextField newUsernameField = new JTextField();
        panel.add(newUsernameField);

        panel.add(new JLabel("Password:"));
        JPasswordField newPasswordField = new JPasswordField();
        panel.add(newPasswordField);

        panel.add(new JLabel("Confirm Password:"));
        JPasswordField confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);

        panel.add(new JLabel("Role:"));
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"CUSTOMER", "STAFF","OWNER"});
        panel.add(roleComboBox);

        // Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String username = newUsernameField.getText().trim();
            String password = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                showMessage("Username and Password cannot be empty", "Registration Error");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showMessage("Passwords do not match", "Registration Error");
                return;
            }

            // Registration
            boolean success = authService.registerUser(username, password, role);
            if (success) {
                showMessage("Registration successful", "Success");
                dialog.dispose();
            } else {
                showMessage("Registration failed", "Error");
            }
        });
        panel.add(submitButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}