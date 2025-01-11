package beachresort.models;

public abstract class User {
    private int id;                // Unique identifier for the user
    private String username;       // Username for login
    private String password;       // Password for login (consider hashing for security)
    private String email;          // User's email address
    private String fullName;       // User's full name
    private String address;         // User's address
    private String contactNumber;   // User's contact number

    public enum UserRole {
        OWNER,
        CUSTOMER,
        STAFF
    }

    // Constructor for existing users (with ID)
    public User(int id, String username, String password, String email, String fullName, String address, String contactNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    // Constructor for new users (without ID)
    public User(String username, String password, String email, String fullName, String address, String contactNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // Abstract method to get the role of the user
    public abstract UserRole getRole();

    // Method to display user information (can be overridden in subclasses)
    public String displayInfo() {
        return String.format("User  ID: %d, Username: %s, Email: %s, Full Name: %s, Address: %s, Contact Number: %s, Role: %s",
                id, username, email, fullName, address, contactNumber, getRole());
    }

    // Additional method to check if the user is an admin (example)
    public boolean isAdmin() {
        return getRole() == UserRole.OWNER; 
    }
}