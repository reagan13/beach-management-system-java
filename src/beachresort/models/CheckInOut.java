package beachresort.models;

import java.time.LocalDate;

public class CheckInOut {
    private int id; // Unique identifier for the check-in/check-out record
    private String customerName; // Name of the customer
    private LocalDate checkInDate; // Check-in date
    private LocalDate checkOutDate; // Check-out date
    private String checkInType; // Type of check-in (e.g., "walk-in" or "booking")
    private String status; // Status of the check-in/check-out record (e.g., "Checked In", "Checked Out")

    // Constructor
    public CheckInOut(int id, String customerName, LocalDate checkInDate, LocalDate checkOutDate, String checkInType,
            String status) {
        this.id = id;
        this.customerName = customerName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
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
}