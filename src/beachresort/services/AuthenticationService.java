package beachresort.services;

import beachresort.models.Person;
import beachresort.models.Person.PersonRole; 
import beachresort.models.Owner; // Import Owner class
import beachresort.models.Staff; // Import Staff class
import beachresort.models.Customer; // Import Customer class
import beachresort.repositories.PersonRepository;

import java.sql.Date;
import java.sql.SQLException;

public class AuthenticationService {
    private PersonRepository personRepository;

    public AuthenticationService() throws SQLException {
        this.personRepository = new PersonRepository();
    }

    public Person authenticateUser (String username, String password, String role) throws SQLException {
        // Validate inputs
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Role: " + role);
        
        if (!isValidUsername(username) || isNullOrEmpty(password) || isNullOrEmpty(role)) {
            System.out.println("Invalid input");
            return null; // Return null for invalid inputs
        }

        // Use repository to validate user and return User object
        Person person = personRepository.findByUsername(username);
        
        if (person != null && person.getPassword().equals(password) && person.getRole().name().equals(role)) {
            return person; // Return the authenticated user
        }
        
        return null; // Return null if authentication fails
    }

    // Validates that the username meets minimum requirements
    public boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    // Utility method to check for null or empty strings
    private boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean registerUser (String username, String password, String role, String email, String fullName,
                            String address, String contactNumber) {
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
        if (personRepository.usernameExists(username)) {
            System.err.println("Username already exists: " + username);
            return false;
        }

        // Create user object based on role
        Person newUser ;
        switch (PersonRole.valueOf(role.toUpperCase())) {
            case OWNER:
               
                newUser  = new Owner(
                        username,
                        password,
                        email,
                        fullName,
                        address,
                        contactNumber
                );

                System.out.println("Owner created");
                break;
            case CUSTOMER:
                
                newUser  = new Customer(
                        username,
                        password,
                        email,
                        fullName,
                        address,
                        contactNumber
                );
                System.out.println("Customer created");
                break;
            case STAFF:
                newUser  = new Staff(
                        username,
                        password,
                        email,
                        fullName,
                        address,
                        contactNumber
                      
                );
                System.out.println("Staff created");
                break;
            default:
            System.out.println("Invalid role");
                System.err.println("Invalid role: " + role);
                return false;
        }

        // Attempt to create user
        boolean created = personRepository.createUser(newUser);

        
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