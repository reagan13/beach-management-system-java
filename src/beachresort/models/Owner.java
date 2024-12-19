package beachresort.models;

public class Owner {
    private int ownerId;
    private String username;
    private String fullName;
    private String email;
    private String contactNumber;
    private String role;

    // Constructor
    public Owner(int ownerId, String username, String fullName, String email, String contactNumber, String role) {
        this.ownerId = ownerId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.role = role;
    }

    // Getters and Setters
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}