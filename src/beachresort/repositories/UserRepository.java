package beachresort.repositories;

import beachresort.models.User;
import beachresort.repositories.OwnerRepository;
import beachresort.database.DatabaseConnection;

import java.sql.*;

public class UserRepository {
    private final Connection connection;


    public UserRepository() throws SQLException {
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

    public boolean createUser(User user) {
        String query = "INSERT INTO users (username, password, email, full_name, address, contact_number, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Validate input
            if (user == null) {
                System.err.println("Attempting to create null user");
                return false;
            }

            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                System.err.println("Username cannot be null or empty");
                return false;
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                System.err.println("Password cannot be null or empty");
                return false;
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println("Email cannot be null or empty");
                return false;
            }

            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                System.err.println("Full name cannot be null or empty");
                return false;
            }

            if (user.getRole() == null) {
                System.err.println("Role cannot be null");
                return false;
            }

            // Prepare statement
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getAddress()); // Set address
            pstmt.setString(6, user.getContactNumber()); // Set contact number
            pstmt.setString(7, user.getRole().name()); // Assuming UserRole is an enum

           // Execute and return result
           int rowsAffected = pstmt.executeUpdate();


            // If user is created successfully, check the role and create the corresponding record
            if (rowsAffected > 0) {
                String userId = getLastInsertedUserId(); // Implement this method to retrieve the last inserted user ID

                if (user.getRole() == User.UserRole.OWNER) {
                    // Insert owner record
                    String insertOwnerQuery = "INSERT INTO owner (user_id, businessName, licenseNumber) VALUES (?, ?, ?)";
                    try (PreparedStatement ownerPstmt = connection.prepareStatement(insertOwnerQuery)) {
                        ownerPstmt.setString(1, userId);
                        ownerPstmt.setString(2, ""); // Replace with actual business name
                        ownerPstmt.setString(3, ""); // Replace with actual license number
                        ownerPstmt.executeUpdate();
                        System.out.println("Owner record added successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error adding owner record: " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                } else if (user.getRole() == User.UserRole.CUSTOMER) {
                    // Insert customer record
                    String insertCustomerQuery = "INSERT INTO customer (user_id, numberVisits, preferredAccommodationType) VALUES (?, ?, ?)";
                    try (PreparedStatement customerPstmt = connection.prepareStatement(insertCustomerQuery)) {
                        customerPstmt.setString(1, userId);
                        customerPstmt.setInt(2, 0); // Default number of visits for new customers
                        customerPstmt.setString(3, ""); // Replace with actual preferred accommodation type
                        customerPstmt.executeUpdate();
                        System.out.println("Customer record added successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error adding customer record: " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                } else if (user.getRole() == User.UserRole.STAFF) {
                    // Insert staff record
                    String insertStaffQuery = "INSERT INTO staff (user_id, position, status, add_date) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement staffPstmt = connection.prepareStatement(insertStaffQuery)) {
                        staffPstmt.setString(1, userId);
                        staffPstmt.setString(2, ""); // Replace with actual position
                        staffPstmt.setString(3, "Active"); // Default status for new staff
                        staffPstmt.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Set current date as add date
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


    
    public boolean updateUser(User user) {
        String query = "UPDATE users SET username = ?, password = ?, email = ?, full_name = ?, address = ?, contact_number = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Validate input
            if (user == null) {
                System.err.println("Attempting to update null user");
                return false;
            }
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                System.err.println("Username cannot be null or empty");
                return false;
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                System.err.println("Password cannot be null or empty");
                return false;
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.err.println("Email cannot be null or empty");
                return false;
            }

            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                System.err.println("Full name cannot be null or empty");
                return false;
            }
            // Prepare statement
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getAddress()); // Set address
            pstmt.setString(6, user.getContactNumber()); // Set contact number
            pstmt.setInt(7, user.getId()); // Assuming userId is an integer

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


    public User findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
             
            try (ResultSet rs = pstmt.executeQuery()) {
             
                if (rs.next()) {
                    String roleString = rs.getString("role");
                    System.out.println("the role is"+ roleString);;
                    // Create the User object
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            rs.getString("address"), // Retrieve address
                            rs.getString("contact_number") // Retrieve contact number
                    ) {
                        @Override
                        public UserRole getRole() {
                            return UserRole.valueOf(roleString.toUpperCase());
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