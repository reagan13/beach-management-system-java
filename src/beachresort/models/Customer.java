package beachresort.models;

public class Customer extends User {
    private String preferredAccommodationType; // Customer's preferred type of accommodation
    private int numberOfVisits;                 // Number of visits made by the customer

    // Constructor for existing customers (with ID)
    public Customer(int id, String username, String password, String email, String fullName, String address, String contactNumber, String preferredAccommodationType, int numberOfVisits) {
        super(id, username, password, email, fullName, address, contactNumber); // Call the parent constructor
        this.preferredAccommodationType = preferredAccommodationType;
        this.numberOfVisits = numberOfVisits;
    }

    // Constructor for new customers (without ID)
    public Customer(String username, String password, String email, String fullName, String address,
            String contactNumber, String preferredAccommodationType) {
        super(username, password, email, fullName, address, contactNumber); // Call the parent constructor
        this.preferredAccommodationType = preferredAccommodationType;
        this.numberOfVisits = 0; // Default number of visits for new customers
    }

    public Customer(String username, String password, String email, String fullName, String address, String contactNumber) {
        super(username, password, email, fullName, address, contactNumber); // Call the parent constructor
    }
    // Getter and Setter for preferredAccommodationType
    public String getPreferredAccommodationType() {
        return preferredAccommodationType;
    }

    public void setPreferredAccommodationType(String preferredAccommodationType) {
        this.preferredAccommodationType = preferredAccommodationType;
    }

    // Getter and Setter for numberOfVisits
    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    // Method to increment the number of visits
    public void incrementVisits() {
        this.numberOfVisits++;
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER; // Return the specific role for Customer
    }

    @Override
    public String displayInfo() {
        return String.format("Customer ID: %d, Username: %s, Email: %s, Full Name: %s, Address: %s, Contact Number: %s, Preferred Accommodation Type: %s, Number of Visits: %d, Role: %s",
                getId(), getUsername(), getEmail(), getFullName(), getAddress(), getContactNumber(), preferredAccommodationType, numberOfVisits, getRole());
    }
}