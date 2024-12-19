package beachresort.ui;

import beachresort.services.AuthenticationService;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton registerButton;
    private AuthenticationService authService;

    public LoginFrame() throws SQLException {
        // Set up the frame
        setTitle("Beach Resort Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize authentication service
        authService = new AuthenticationService();

        // Create components
        initComponents();

        // Create layout
        createLayout();
    }

    private void initComponents() {
        // Username Field
        usernameField = new JTextField(20);
        // usernameField.setPlaceholder("Username");

        // Password Field
        passwordField = new JPasswordField(20);
        // passwordField.setPlaceholder("Password");

        // Role Combo Box
        String[] roles = {"CUSTOMER", "STAFF", "OWNER"};
        roleComboBox = new JComboBox<>(roles);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    performLogin();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        // Register Button
        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openRegisterDialog();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
    }

    private void createLayout() {
        // Use GridBagLayout for flexible positioning
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo or Title (Optional)
        JLabel titleLabel = new JLabel("Beach Resort Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(usernameField, gbc);

        // Password Field
        gbc.gridy = 2;
        add(passwordField, gbc);

        // Role Combo Box
        gbc.gridy = 3;
        add(roleComboBox, gbc);

        // Login Button
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(loginButton, gbc);

        // Register Button
        gbc.gridx = 1;
        add(registerButton, gbc);
    }

    private void performLogin() throws SQLException {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (authService.authenticateUser(username, password, role)) {
            // Successful login
            JOptionPane.showMessageDialog(this, 
                "Login Successful!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open main application window based on role
            openMainWindow(role);
            
            // Close login form
            dispose();
        } else {
            // Failed login
            JOptionPane.showMessageDialog(this, 
                "Invalid username, password, or role", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMainWindow(String role) throws SQLException {
        // Different main windows based on user role
        switch (role) {
            case "CUSTOMER":
                 new CustomerDashboard().setVisible(true);
                break;  
            case "STAFF":
                new OwnerDashboard().setVisible(true);
                break;
            case "OWNER":
                new OwnerDashboard().setVisible(true);
                break;
            default:
                // Default fallback
                JOptionPane.showMessageDialog(this, "Unsupported role");
        }
    }

    private void openRegisterDialog() throws SQLException {
        RegisterDialog registerDialog = new RegisterDialog(this);
        registerDialog.setVisible(true);
    }

    public static void main(String[] args) {
        // Ensure GUI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}