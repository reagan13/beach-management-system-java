package beachresort.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RoomAuditLog {
    private int id;
    private String roomNumber;
    private String actionType; // ADD, EDIT, DELETE
    private String oldDetails;
    private String newDetails;
    private LocalDateTime actionTimestamp;
    private String performedBy;

    public RoomAuditLog(String roomNumber, String actionType, String oldDetails, String newDetails, String performedBy) {
        this.roomNumber = roomNumber;
        this.actionType = actionType;
        this.oldDetails = oldDetails;
        this.newDetails = newDetails;
        this.actionTimestamp = LocalDateTime.now();
        this.performedBy = performedBy;
    }

    // Detailed toString method for comprehensive logging
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
            "ROOM AUDIT LOG: [Room %s] %s Action\n" +
            "Timestamp: %s\n" +
            "Performed By: %s\n" +
            "Old Details: %s\n" +
            "New Details: %s",
            roomNumber,
            actionType,
            actionTimestamp.format(formatter),
            performedBy,
            oldDetails,
            newDetails
        );
    }

    // Generate a more readable log entry for display
    public String getFormattedLogEntry() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
            "Room %s: %s - %s by %s at %s",
            roomNumber,
            actionType,
            actionType.equals("ADD") ? "Room Created" : 
            actionType.equals("EDIT") ? "Room Updated" : 
            "Room Deleted",
            performedBy,
            actionTimestamp.format(formatter)
        );
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getOldDetails() {
        return oldDetails;
    }

    public void setOldDetails(String oldDetails) {
        this.oldDetails = oldDetails;
    }

    public String getNewDetails() {
        return newDetails;
    }

    public void setNewDetails(String newDetails) {
        this.newDetails = newDetails;
    }

    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
}