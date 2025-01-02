package beachresort.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    private int id; // Unique identifier for the audit log entry
    private String transactionType; // Type of transaction (e.g., Booking, Room)
    private int transactionID; // ID of the transaction associated with this log
    private String actionType; // Action performed (e.g., ADD, EDIT, DELETE)
    private String oldDetails; // Previous state/details before the action
    private String newDetails; // New state/details after the action
    private LocalDateTime actionTimestamp; // Timestamp of when the action was performed
    private String performedBy; // User who performed the action

    // Constructor
    public AuditLog(int transactionID, String transactionType ,String actionType, String oldDetails, String newDetails, String performedBy) {
        this.transactionID = transactionID; // Set the transaction ID
        this.transactionType=transactionType;
        this.actionType = actionType;
        this.oldDetails = oldDetails;
        this.newDetails = newDetails;
        this.actionTimestamp = LocalDateTime.now(); // Set the current timestamp
        this.performedBy=performedBy;
    }

    // Detailed toString method for comprehensive logging
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "AUDIT LOG: [Transaction ID: %d] %s Action\n" +
                "Transaction Type: %s\n" +
                "Timestamp: %s\n" +
                "Performed By: %s\n" +
                "Old Details: %s\n" +
                "New Details: %s",
                transactionID,
                actionType,
                transactionType,
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
            "Transaction ID %d: %s - %s by %s at %s",
            transactionID,
            actionType,
            actionType.equals("ADD") ? "Created" : 
            actionType.equals("EDIT") ? "Updated" : 
            "Deleted",
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

    public int getTransactionID() {
        return transactionID; // Getter for transaction ID
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID; // Setter for transaction ID
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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