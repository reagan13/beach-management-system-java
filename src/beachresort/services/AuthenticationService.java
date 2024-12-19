package beachresort.services;

import beachresort.models.User;
import beachresort.repositories.UserRepository;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthenticationService {
    private UserRepository userRepository;

    public AuthenticationService() throws SQLException {
        this.userRepository = new UserRepository();
    }

    public boolean registerUser(String username, String password, String role) {
        return registerUser(username, password, role, null, null);
    }

    public boolean registerUser(String username, String password, String role, String email, String fullName) {
        // Validate inputs
        if (!isValidUsername(username)) {
            return false;
        }

        if (!isValidPassword(password)) {
            return false;
        }

        // Check if username already exists
        if (userRepository.usernameExists(username)) {
            return false;
        }

        // Create user object
        User newUser = new User(0, username, password, role, email, fullName);
        
        // Attempt to create user
        return userRepository.createUser(newUser);
    }

    public boolean authenticateUser(String username, String password, String role) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Simple password and role check
            return user.getPassword().equals(password) && 
                   user.getRole().equals(role);
        }
        
        return false;
    }

    public String getUserRole(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.map(User::getRole).orElse(null);
    }

    // Username validation
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        // Username must be:
        // - 3-50 characters long
        // - Contain only alphanumeric characters and underscores
        // - Start with a letter
        return Pattern.matches("^[a-zA-Z][a-zA-Z0-9_]{2,49}$", username);
    }

    // Password validation
    public boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }

        // Simple password validation
        // - At least 6 characters long
        return password.length() >= 6;
    }

    // Additional utility methods
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Verify current password
            if (user.getPassword().equals(oldPassword)) {
                // Update password
                user.setPassword(newPassword);
                
                // Save updated user
                return userRepository.updateUser(user);
            }
        }
        
        return false;
    }
}