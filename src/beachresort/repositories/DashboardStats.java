package beachresort.repositories;

import beachresort.models.Owner;
import beachresort.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


 public class DashboardStats {
    private int totalBookings;
    private int roomsOccupied;
    private double monthlyRevenue;
    private int pendingBookings;
    private int availableRooms;
    private int totalStaff;
    // Default constructor
   // Default constructor
    public DashboardStats() {
        this.totalBookings = 0;
        this.roomsOccupied = 0;
        this.monthlyRevenue = 0.0;
        this.pendingBookings = 0;
        this.availableRooms = 0;
        this.totalStaff = 0;
    }

    // Comprehensive constructor
    public DashboardStats(int totalBookings, int roomsOccupied, 
                           double monthlyRevenue, int pendingBookings, 
                           int availableRooms, int totalStaff) {
        this.totalBookings = totalBookings;
        this.roomsOccupied = roomsOccupied;
        this.monthlyRevenue = monthlyRevenue;
        this.pendingBookings = pendingBookings;
        this.availableRooms = availableRooms;
        this.totalStaff = totalStaff;
    }

    // Getters and Setters
    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getRoomsOccupied() {
        return roomsOccupied;
    }

    public void setRoomsOccupied(int roomsOccupied) {
        this.roomsOccupied = roomsOccupied;
    }

    public double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public int getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(int pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public int getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

  // Improved mock data method with logging
    public static DashboardStats createMockStats() {
        System.out.println("WARNING: Using mock dashboard statistics due to database query failure.");
        return new DashboardStats(
            45,   // Total Bookings
            20,   // Rooms Occupied
            25000.00, // Monthly Revenue
            5,    // Pending Bookings
            15,   // Available Rooms
            10    // Total Staff
        );
    }

    // Optional toString method for debugging
    @Override
    public String toString() {
        return "DashboardStats{" +
               "totalBookings=" + totalBookings +
               ", roomsOccupied=" + roomsOccupied +
               ", monthlyRevenue=" + monthlyRevenue +
               ", pendingBookings=" + pendingBookings +
                ", availableRooms=" + availableRooms +
                ", totalStaff=" + totalStaff +
               '}';
    }
}