package beachresort.models;

import java.time.LocalDate;

public class Staff {
    private String staffId;
    private String name;
    private String position;
    private String department;
    private String contactNumber;
    private String email;
    private double salary;
    private LocalDate hireDate;
    private String status; // Active, Inactive

    // Constructor
    public Staff(String staffId, String name, String position, String department, 
                 String contactNumber, String email, double salary, 
                 LocalDate hireDate, String status) {
        this.staffId = staffId;
        this.name = name;
        this.position = position;
        this.department = department;
        this.contactNumber = contactNumber;
        this.email = email;
        this.salary = salary;
        this.hireDate = hireDate;
        this.status = status;
    }

    // Getters and Setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Validation Methods
    public boolean isValidEmail() {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    public boolean isValidContactNumber() {
        String phoneRegex = "^\\d{10}$";
        return contactNumber != null && contactNumber.matches(phoneRegex);
    }

    // toString method for easy printing
    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + staffId + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", department='" + department + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}