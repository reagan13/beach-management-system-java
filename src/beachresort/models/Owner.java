package beachresort.models;

public class Owner extends User {
    private String businessName;   // Name of the business owned by the owner
    private String licenseNumber;   // License number for the business

      // Constructor for existing owners (with ID)
    public Owner(int id, String username, String password, String email, String fullName, String address, String contactNumber, String businessName, String licenseNumber) {
        super(id, username, password, email, fullName, address, contactNumber); // Call the parent constructor
        this.businessName = businessName;
        this.licenseNumber = licenseNumber;
    }

    // Constructor for new owners (without ID)
    public Owner(String username, String password, String email, String fullName, String address, String contactNumber,
            String businessName, String licenseNumber) {
        super(username, password, email, fullName, address, contactNumber); // Call the parent constructor
        this.businessName = businessName;
        this.licenseNumber = licenseNumber;
    }


     
    public Owner(String username, String password, String email, String fullName, String address, String contactNumber) {
        super(username, password, email, fullName, address, contactNumber); // Call the parent constructor
    }

   // Constructor for existing owners
   public Owner(int id, int userId, String username, String password, String email, String fullName, String address,
           String contactNumber, String businessName, String licenseNumber) {
       super(id, username, password, email, fullName, address, contactNumber); // Call the parent constructor
       this.businessName = businessName;
       this.licenseNumber = licenseNumber;
   }

   public Owner(User user, String businessName, String licenseNumber) {
       super(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getFullName(),
               user.getAddress(), user.getContactNumber());
       this.businessName = businessName;
       this.licenseNumber = licenseNumber;
   }
   


    
    

    // Getters and Setters for businessName and licenseNumber
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Override
    public UserRole getRole() {
        return UserRole.OWNER; // Return the specific role for Owner
    }

    @Override
    public String displayInfo() {
        return String.format("Owner ID: %d, Username: %s, Email: %s, Full Name: %s, Address: %s, Business Name: %s, License Number: %s, Role: %s",
                getId(), getUsername(), getEmail(), getFullName(), getAddress(), businessName, licenseNumber, getRole());
    }
}