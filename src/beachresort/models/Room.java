package beachresort.models;

public class Room {
    private String roomNumber;
    private String roomType;
    private int capacity;
    private double pricePerNight;
    private String status;

    // Constructor
    public Room(String roomNumber, String roomType, int capacity, double pricePerNight, String status) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    // Getters and Setters
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
                "roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", capacity=" + capacity +
                ", pricePerNight=" + pricePerNight +
                ", status='" + status + '\'' +
                '}';
    }
}