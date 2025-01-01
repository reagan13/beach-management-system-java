package beachresort.models;

import java.time.LocalDateTime;

public class BookingAuditLog {
    private String auditId;
    private String bookingId;
    private String actionType; // ADD, EDIT, CANCEL, CONFIRM
    private String oldDetails;
    private String newDetails;
    private LocalDateTime actionTimestamp;
    private String performedBy;

    public BookingAuditLog(String bookingId, String actionType, 
                            String oldDetails, String newDetails, 
            String performedBy) {
        this.bookingId = bookingId;
        this.actionType = actionType;
        this.oldDetails = oldDetails;
        this.newDetails = newDetails;
        this.actionTimestamp = LocalDateTime.now();
        this.performedBy = performedBy;
    }

    // Getters and Setters

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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


    // Comprehensive toString for detailed logging
    @Override
    public String toString() {
        return String.format(
            "BOOKING AUDIT LOG: [Booking %s] %s Action\n" +
            "Timestamp: %s\n" +
            "Performed By: %s\n" +
            "Old Details: %s\n" +
            "New Details: %s",
            bookingId, actionType, actionTimestamp, 
            performedBy, oldDetails, newDetails
        );
    }


}