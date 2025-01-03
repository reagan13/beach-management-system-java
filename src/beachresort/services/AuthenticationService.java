package beachresort.services;

import beachresort.models.User;
import beachresort.repositories.UserRepository;

import java.sql.SQLException;
import java.util.Optional;

public class AuthenticationService {
    private UserRepository userRepository;

    public AuthenticationService() throws SQLException {
        this.userRepository = new UserRepository();
    }

    public Optional<User> authenticateUser (String username, String password, String role) {
        // Validate inputs
        
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Role: " + role);
        
        if (!isValidUsername(username) || isNullOrEmpty(password) || isNullOrEmpty(role)) {
            System.out.println("Invalid input");
            return Optional.empty(); // Return empty for invalid inputs
        }

        // Use repository to validate user and return User object
        return userRepository.findByUsername(username)

                .filter(user -> user.getPassword().equals(password) && user.getRole().equals(role));
    }

    // Validates that the username meets minimum requirements
    public boolean isValidUsername(String username) {
        return username != null;
    }

    // Utility method to check for null or empty strings
    private boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean registerUser (String username, String password, String role) {
        // Explicit null checks with detailed logging
        if (username == null) {
            System.err.println("Username is null during registration");
            return false;
        }

        if (password == null) {
            System.err.println("Password is null during registration");
            return false;
        }

        if (role == null) {
            System.err.println("Role is null during registration");
            return false;
        }

        // Basic length checks
        if (username.length() < 6) {
            System.err.println("Username too short: " + username);
            return false;
        }

        if (password.length() < 8) {
            System.err.println("Password too short");
            return false;
        }

        try {
            // Check if username already exists
            if (userRepository.usernameExists(username)) {
                System.err.println("Username already exists: " + username);
                return false;
            }

            // Create user object
            User newUser  = new User(username, password, role);
            
            // Attempt to create user
            boolean created = userRepository.createUser (newUser );
            
            if (!created) {
                System.err.println("Failed to create user in database");
            }
            
            return created;
        } catch (Exception e) {
            // Log the full exception
            System.err.println("Exception during user registration:");
            e.printStackTrace();
            return false;
        }
    }
}