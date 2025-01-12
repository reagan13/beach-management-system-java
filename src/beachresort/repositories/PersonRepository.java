package beachresort.repositories;

import beachresort.models.Person;
import beachresort.models.Person.PersonRole;
import beachresort.repositories.OwnerRepository;
import beachresort.database.DatabaseConnection;

import java.sql.*;

public class PersonRepository {
    private final Connection connection;


    public PersonRepository() throws SQLException {
        connection = DatabaseConnection.getConnection();
        createUserTableIfNotExists();
        
    }

    private void createUserTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "address VARCHAR(255), " + // New address column
                "contact_number VARCHAR(20), " + // New contact number column
                "role VARCHAR(20) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createUser(Person person) {
        String query = "INSERT INTO users (username, password, email, full_name, address, contact_number, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Validate input
            if (person == null) {
                System.err.println("Attempting to create null user");
                return false;
            }

            if (person.getUsername() == null || person.getUsername().trim().isEmpty()) {
                System.err.println("Username cannot be null or empty");
                return false;
            }

            if (person.getPassword() == null || person.getPassword().trim().isEmpty()) {
                System.err.println("Password cannot be null or empty");
                return false;
            }

            if (person.getEmail() == null || person.getEmail().trim().isEmpty()) {
                System.err.println("Email cannot be null or empty");
                return false;
            }

            if (person.getFullName() == null || person.getFullName().trim().isEmpty()) {
                System.err.println("Full name cannot be null or empty");
                return false;
            }

            if (person.getRole() == null) {
                System.err.println("Role cannot be null");
                return false;
            }

            // Prepare statement
            pstmt.setString(1, person.getUsername());
            pstmt.setString(2, person.getPassword());
            pstmt.setString(3, person.getEmail());
            pstmt.setString(4, person.getFullName());
            pstmt.setString(5, person.getAddress()); // Set address
            pstmt.setString(6, person.getContactNumber()); // Set contact number
            pstmt.setString(7, person.getRole().name()); // Assuming PersonRole is an enum

           // Execute and return result
           int rowsAffected = pstmt.executeUpdate();


            // If user is created successfully, check the role and create the corresponding record
            if (rowsAffected > 0) {
                String userId = getLastInsertedUserId(); // Implement this method to retrieve the last inserted user ID

                if (person.getRole() == Person.PersonRole.OWNER) {
                    // Insert owner record
                    String insertOwnerQuery = "INSERT INTO owner (user_id, businessName, licenseNumber) VALUES (?, ?, ?)";
                    try (PreparedStatement ownerPstmt = connection.prepareStatement(insertOwnerQuery)) {
                        ownerPstmt.setString(1, userId);
                        ownerPstmt.setString(2, ""); 
                        ownerPstmt.setString(3, ""); 
                        ownerPstmt.executeUpdate();
                        System.out.println("Owner record added successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error adding owner record: " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                } else if (person.getRole() == Person.PersonRole.CUSTOMER) {
                    // Insert customer record
                    String insertCustomerQuery = "INSERT INTO customer (user_id, numberVisits, preferredAccommodationType) VALUES (?, ?, ?)";
                    try (PreparedStatement customerPstmt = connection.prepareStatement(insertCustomerQuery)) {
                        customerPstmt.setString(1, userId);
                        customerPstmt.setInt(2, 0);
                        customerPstmt.setString(3, ""); 
                        customerPstmt.executeUpdate();
                        System.out.println("Customer record added successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error adding customer record: " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                } else if (person.getRole() == Person.PersonRole.STAFF) {
                    // Insert staff record
                    String insertStaffQuery = "INSERT INTO staff (user_id, position, status, task) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement staffPstmt = connection.prepareStatement(insertStaffQuery)) {
                        staffPstmt.setString(1, userId);
                        staffPstmt.setString(2, "UNASSIGNED");
                        staffPstmt.setString(3, "Active"); 
                        staffPstmt.setString(4, ""); 
    
                        staffPstmt.executeUpdate();
                        System.out.println("Staff record added successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error adding staff record: " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                }
            }

        
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Log specific SQL error
            System.err.println("SQL Error during user creation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to get the last inserted user ID
    private String getLastInsertedUserId() throws SQLException {
        String query = "SELECT LAST_INSERT_ID()"; // MySQL specific
        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        return null;
    }


    
    public boolean updateUser(Person person) {
        String query = "UPDATE users SET username = ?, password = ?, email = ?, full_name = ?, address = ?, contact_number = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Validate input
            if (person == null) {
                System.err.println("Attempting to update null user");
                return false;
            }
            if (person.getUsername() == null || person.getUsername().trim().isEmpty()) {
                System.err.println("Username cannot be null or empty");
                return false;
            }

            if (person.getPassword() == null || person.getPassword().trim().isEmpty()) {
                System.err.println("Password cannot be null or empty");
                return false;
            }

            if (person.getEmail() == null || person.getEmail().trim().isEmpty()) {
                System.err.println("Email cannot be null or empty");
                return false;
            }

            if (person.getFullName() == null || person.getFullName().trim().isEmpty()) {
                System.err.println("Full name cannot be null or empty");
                return false;
            }
            // Prepare statement
            pstmt.setString(1, person.getUsername());
            pstmt.setString(2, person.getPassword());
            pstmt.setString(3, person.getEmail());
            pstmt.setString(4, person.getFullName());
            pstmt.setString(5, person.getAddress()); // Set address
            pstmt.setString(6, person.getContactNumber()); // Set contact number
            pstmt.setInt(7, person.getId()); // Assuming userId is an integer

            // Execute and return result
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Log specific SQL error
            System.err.println("SQL Error during user update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public Person findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
             
            try (ResultSet rs = pstmt.executeQuery()) {
             
                if (rs.next()) {
                    String roleString = rs.getString("role");
                    System.out.println("the role is"+ roleString);;
                    // Create the User object
                    return new Person(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            rs.getString("address"), // Retrieve address
                            rs.getString("contact_number") // Retrieve contact number
                    ) {
                        @Override
                        public PersonRole getRole() {
                            return PersonRole.valueOf(roleString.toUpperCase());
                        }
                    
                    };
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role found in database: " + e.getMessage());
        }
        return null; // Return null if user is not found or an error occurs
    }


    public Integer validateUser(String username, String password) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null; // Return null if user is not found
    }




}