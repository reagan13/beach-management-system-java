package beachresort.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    private int id; 
    private String transactionType; 
    private int transactionID;
    private String actionType; 
    private String oldDetails; 
    private String newDetails; 
    private LocalDateTime actionTimestamp; 
    private String performedBy; 

    // Constructor
    public AuditLog(int transactionID, String transactionType ,String actionType, String oldDetails, String newDetails, String performedBy) {
        this.transactionID = transactionID; 
        this.transactionType=transactionType;
        this.actionType = actionType;
        this.oldDetails = oldDetails;
        this.newDetails = newDetails;
        this.actionTimestamp = LocalDateTime.now(); 
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
        return transactionID; 
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID; 
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