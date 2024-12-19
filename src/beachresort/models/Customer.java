package beachresort.models;

import java.time.LocalDate;
import java.util.Objects;

public class Customer {
    private int customerId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private LocalDate registrationDate;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String gender;
    private LocalDate dateOfBirth;

    // Full Constructor
    public Customer(
        int customerId, 
        String username, 
        String fullName, 
        String email, 
        String phoneNumber, 
        String status,
        LocalDate registrationDate,
        String address,
        String city,
        String country,
        String postalCode,
        String gender,
        LocalDate dateOfBirth
    ) {
        this.customerId = customerId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.registrationDate = registrationDate;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    // Simplified Constructor
    public Customer(
        int customerId, 
        String username, 
        String fullName, 
        String email, 
        String phoneNumber, 
        String status
    ) {
        this(
            customerId, 
            username, 
            fullName, 
            email, 
            phoneNumber, 
            status, 
            LocalDate.now(),  // Default to current date
            "", 
            "", 
            "", 
            "", 
            "", 
            null
        );
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    // Setters
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Utility Methods
    public int getAge() {
        return dateOfBirth != null 
            ? LocalDate.now().getYear() - dateOfBirth.getYear() 
            : -1;
    }

    public String getFullAddress() {
        return String.format("%s, %s, %s %s", 
            address, city, country, postalCode);
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId &&
               Objects.equals(username, customer.username) &&
               Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, username, email);
    }

    // ToString
    @Override
    public String toString() {
        return "Customer{" +
               "customerId=" + customerId +
               ", username='" + username + '\'' +
               ", fullName='" + fullName + '\'' +
               ", email='" + email + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}