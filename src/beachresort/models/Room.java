package beachresort.models;

import java.sql.Timestamp;

public class Room {
    private int roomID; // New field for room ID
    private String roomNumber;
    private String roomType;
    private int capacity;
    private double pricePerNight;
    private String status;
    private Timestamp createdAt; // New field for created timestamp
    private Timestamp updatedAt; // New field for updated timestamp

    // Constructor
    public Room(int roomID, String roomNumber, String roomType, int capacity, double pricePerNight, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
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
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return false;
        }
        if (roomType == null || roomType.trim().isEmpty()) {
            return false;
        }
        if (capacity <= 0) {
            return false;
        }
        if (pricePerNight <= 0) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomID=" + roomID +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", capacity=" + capacity +
                ", pricePerNight=" + pricePerNight +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}