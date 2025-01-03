package beachresort.models;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class Payment {
    private int paymentId;
    private String userId;
    private String paymentType; // Booking, Service, Salary, etc.
    private BigDecimal amount;
    private String paymentMethod; // Cash, Credit Card, Bank Transfer
    private String status; // Pending, Completed, Failed
    private String description;
    private Timestamp paymentDate;
    private Timestamp createdAt;

    // Constructor
    public Payment(String userId, String paymentType, BigDecimal amount, 
                   String paymentMethod, String status, String description, 
                   Timestamp paymentDate) {
        this.userId = userId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.description = description;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters (generate for all fields)

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    
    // Validation method
    public boolean validate() {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        if (paymentType == null || paymentType.trim().isEmpty()) {
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}