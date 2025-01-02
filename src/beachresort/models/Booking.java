package beachresort.models;

import java.time.LocalDate;

public class Booking {
    private int bookingID; // Unique identifier for the booking
    private String roomNumber; // Room number associated with the booking
    private String customerName; // ID of the customer making the booking
    private LocalDate checkInDate; // Check-in date
    private LocalDate checkOutDate; // Check-out date
    private int numberOfGuests; // Number of guests
    private double totalPrice; // Total price for the booking
    private String status; // Status of the booking (e.g., Confirmed, Cancelled)

      // Constructor
      public Booking( String roomNumber, String customerName, LocalDate checkInDate, LocalDate checkOutDate,
              int numberOfGuests, double totalPrice, String status) {
       
          this.roomNumber = roomNumber;
          this.customerName = customerName;
          this.checkInDate = checkInDate;
          this.checkOutDate = checkOutDate;
          this.numberOfGuests = numberOfGuests;
          this.totalPrice = totalPrice;
          this.status = status;
      }
    
    // Constructor
    public Booking(int bookingID, String roomNumber, String customerName, LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests, double totalPrice, String status) {
        this.bookingID = bookingID;
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


    // (Add getters and setters for all attributes)


    // Validation method
    public boolean validate() {
        // Implement validation logic (e.g., check dates, number of guests)
        return true; // Placeholder
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingID='" + bookingID + '\'' +
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