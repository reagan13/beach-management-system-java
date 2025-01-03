package beachresort.models;

import java.time.LocalDate;

public class Absence {
    private String absenceId;
    private String staffId; // Reference to the staff member
    private LocalDate date; // Date of absence
    private String reason; // Reason for absence
    private String excuseLetter; // Excuse letter or documentation

    // Constructor
    public Absence(String absenceId, String staffId, LocalDate date, String reason, String excuseLetter) {
        this.absenceId = absenceId;
        this.staffId = staffId;
        this.date = date;
        this.reason = reason;
        this.excuseLetter = excuseLetter;
    }

    // Getters and Setters
    public String getAbsenceId() {
        return absenceId;
    }

    public void setAbsenceId(String absenceId) {
        this.absenceId = absenceId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public LocalDate getDate() {
        return date;
 }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExcuseLetter() {
        return excuseLetter;
    }

    public void setExcuseLetter(String excuseLetter) {
        this.excuseLetter = excuseLetter;
    }

    // toString method for easy printing
    @Override
    public String toString() {
        return "Absence{" +
                "absenceId='" + absenceId + '\'' +
                ", staffId='" + staffId + '\'' +
                ", date=" + date +
                ", reason='" + reason + '\'' +
                ", excuseLetter='" + excuseLetter + '\'' +
                '}';
    }
}