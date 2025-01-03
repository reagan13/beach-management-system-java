package beachresort.models;

import java.time.LocalDate;

public class CheckInOut {
    private int id; 
    private int userId; 
    private String customerName; 
    private LocalDate checkInDate; 
    private LocalDate checkOutDate; 
    private String roomNumber;
    private String checkInType; 
    private String status; 

    // Constructor with all fields including id and userId
    public CheckInOut(int id, int userId, String customerName, LocalDate checkInDate, LocalDate checkOutDate, 
                      String roomNumber, String checkInType, String status) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomNumber = roomNumber;
        this.checkInType = checkInType;
        this.status = status;
    }

    // Constructor without id
    public CheckInOut(int userId, String customerName, LocalDate checkInDate, LocalDate checkOutDate, 
                      String roomNumber, String checkInType, String status) {
        this.userId = userId;
        this.customerName = customerName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomNumber = roomNumber;
        this.checkInType = checkInType;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "CheckInOut{" +
                "id=" + id +
                ", userId=" + userId +
                ", customerName='" + customerName + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", roomNumber='" + roomNumber + '\'' +
                ", checkInType='" + checkInType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}