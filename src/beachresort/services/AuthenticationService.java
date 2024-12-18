// services/AuthenticationService.java
package beachresort.services;

import beachresort.models.User;
import beachresort.repositories.UserRepository;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationService {
    private UserRepository userRepository;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    // Hash password method
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            
            // Convert to hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Login method
    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            return false; // User not found
        }

        // Compare hashed passwords
        String hashedInputPassword = hashPassword(password);
        return user.getPassword().equals(hashedInputPassword);
    }

    // User registration method
    public boolean registerUser(String username, String password, String role) {
        // Check if username already exists
        if (userRepository.findByUsername(username) != null) {
            return false; // Username already taken
        }

        // Create new user with hashed password
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashPassword(password));
        newUser.setRole(role);

        return userRepository.createUser(newUser);
    }

    // Authentication method
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            return null; // User not found
        }

        // Compare hashed passwords
        String hashedInputPassword = hashPassword(password);
        if (user.getPassword().equals(hashedInputPassword)) {
            return user;
        }
        
        return null;
    }
    
    
}