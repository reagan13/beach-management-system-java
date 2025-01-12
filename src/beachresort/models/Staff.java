package beachresort.models;

import java.util.Date;

public class Staff extends Person {
    private int staffId;     
    private String position; 
    private String status; 
    private String task;
   
    // Constructor for existing staff (with ID)
    public Staff(int id, int staffId, String username, String password, String email, String fullName, String address,
            String contactNumber, String position,String status, String task) {
        super(id, username, password, email, fullName, address, contactNumber); 
        this.staffId = staffId; 
        this.position = position;
        this.status = status; 
        this.task = task;
    }


    // Constructor for new staff (without ID)
    public Staff(String username, String password, String email, String fullName, String address, String contactNumber,
            String position,  String status, String task) {
        super(username, password, email, fullName, address, contactNumber); 
        this.position = position;
        this.status = status;
        this.task = task;
    }



    public Staff(String username, String password, String email, String fullName, String address,
            String contactNumber) {
        super(username, password, email, fullName, address, contactNumber); 
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

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }


    @Override
    public PersonRole getRole() {
        return PersonRole.STAFF; // Return the specific role for Staff
    }

    @Override
    public String toString() {
        return String.format("Staff ID: %d, User ID: %d, Username: %s, Email: %s, Full Name: %s, Address: %s, Position: %s, Status: %s, Role: %s",
                staffId, getId(), getUsername(), getEmail(), getFullName(), getAddress(), position, status, getRole());
    }
}