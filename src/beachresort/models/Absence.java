package beachresort.models;

import java.sql.Date;
import java.sql.Timestamp;

public class Absence {
    private int absenceId;
    private int userId;
    private String leaveType;
    private Date startDate;
    private Date endDate;
    private String status; 
    private String reason;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    
    // Constructor
    public Absence(int userId, String leaveType, Date startDate, 
            Date endDate, String status, String reason) {
        this.userId = userId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.reason = reason;
    }
    
    // Constructor
    public Absence(int userId, String leaveType, Date startDate, 
                   Date endDate, String status, String reason, 
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
    
 
       // Constructor
    public Absence(int absenceId,int userId, String leaveType, Date startDate, 
                   Date endDate, String status, String reason, 
            Timestamp createdAt, Timestamp updatedAt) {
        this.absenceId = absenceId;
        this.userId = userId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
       // Constructor
    public Absence(int absenceId,int userId, String leaveType, Date startDate, 
                   Date endDate, String status, String reason 
        ) {
        this.absenceId = absenceId;
        this.userId = userId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.reason = reason;
    }

    // Getters and Setters

    public int getAbsenceId() {
        return absenceId;
    }

    public void setAbsenceId(int absenceId) {
        this.absenceId = absenceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLeaveType() {
        return leaveType;
    }
    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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