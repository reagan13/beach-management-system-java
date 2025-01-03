package beachresort.models;

import java.sql.Timestamp;

public class Staff {
    private int staffId;
    private String name;
    private String phoneNumber;
    private String email;
    private String position;
    private String userId;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor
    public Staff(String name, String phoneNumber, String email, 
                 String position, String userId,
                 String status, Timestamp createdAt, Timestamp updatedAt) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.position = position;
        this.userId = userId;

        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Validate method
    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            return false;
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        if (position == null || position.trim().isEmpty()) {
            return false;
        }
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
   
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}