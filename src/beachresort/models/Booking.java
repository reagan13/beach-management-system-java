package beachresort.models;

import java.time.LocalDate;

public class Booking {
    private int bookingID; 
    private int userId; 
    private String roomNumber; 
    private String customerName; 
    private LocalDate checkInDate; 
    private LocalDate checkOutDate; 
    private int numberOfGuests; 
    private double totalPrice; 
    private String status; 

    // Constructor without bookingID and userId
    public Booking(int userId, String roomNumber,String customerName, LocalDate checkInDate, LocalDate checkOutDate,
            int numberOfGuests, double totalPrice, String status) {
           this.userId = userId;
                this.roomNumber = roomNumber;
    
        this.customerName = customerName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Constructor with bookingID and userId
    public Booking(int bookingID, int userId, String roomNumber, String customerName, LocalDate checkInDate, 
                   LocalDate checkOutDate, int numberOfGuests, double totalPrice, String status) {
        this.bookingID = bookingID;
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.customerName = customerName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingID() {
        return bookingID;
    }

    public void setBookingId(int bookingId) {
        this.bookingID = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

  

    @Override
    public String toString() {
        return "Booking{" +
                "bookingID=" + bookingID +
                ", userId=" + userId +
                ", roomNumber='" + roomNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numberOfGuests=" + numberOfGuests +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}