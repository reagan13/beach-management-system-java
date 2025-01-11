package beachresort.models;

import java.util.Date;

public class Staff extends User {
    private int staffId;     // Unique identifier for the staff member
    private String position; // Position of the staff member (e.g., Manager, Receptionist)
    private String status;   // Employment status (e.g., Active, Inactive, Terminated)
   
    // Constructor for existing staff (with ID)
    public Staff(int id, int staffId, String username, String password, String email, String fullName, String address,
            String contactNumber, String position,String status) {
        super(id, username, password, email, fullName, address, contactNumber); // Call the parent constructor
        this.staffId = staffId; // Corresponds to staff_id in the database
        this.position = position; // Corresponds to position in the database
        this.status = status; // Corresponds to status in the database
    }


    // Constructor for new staff (without ID)
    public Staff(String username, String password, String email, String fullName, String address, String contactNumber,
            String position,  String status) {
        super(username, password, email, fullName, address, contactNumber); // Call the parent constructor
        this.position = position;
    
        this.status = status;
    }



    public Staff(String username, String password, String email, String fullName, String address,
            String contactNumber) {
        super(username, password, email, fullName, address, contactNumber); // Call the parent constructor
    }
   
    
    
    // Getter and Setter for position

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public UserRole getRole() {
        return UserRole.STAFF; // Return the specific role for Staff
    }

    @Override
    public String toString() {
        return String.format("Staff ID: %d, User ID: %d, Username: %s, Email: %s, Full Name: %s, Address: %s, Position: %s, Status: %s, Role: %s",
                staffId, getId(), getUsername(), getEmail(), getFullName(), getAddress(), position, status, getRole());
    }
}