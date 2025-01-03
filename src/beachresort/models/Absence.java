package beachresort.models;

import java.sql.Timestamp;

public class Absence {
    private int absenceId;
    private String userId; 
    private String leaveType;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status; 
    private String reason;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor
    public Absence(String userId, String leaveType, Timestamp startDate, 
                   Timestamp endDate, String status, String reason, 
                   Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public int getAbsenceId() {
        return absenceId;
    }

    public void setAbsenceId(int absenceId) {
        this.absenceId = absenceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLeaveType() {
        return leaveType;
    }



    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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


    
    public boolean validate() {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        if (leaveType == null || leaveType.trim().isEmpty()) {
            return false;
        }
        if (startDate == null || endDate == null) {
            return false;
        }
        if (startDate.after(endDate)) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}