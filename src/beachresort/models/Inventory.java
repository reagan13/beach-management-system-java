package beachresort.models;

import java.time.LocalDate;

public class Inventory {
    private String itemId;
    private String itemName;
    private String category;
    private int quantity;
    private double unitPrice;
    private String supplier;
    private LocalDate lastRestocked;
    private String status; // Active, Low Stock, Out of Stock

    // Constructor
    public Inventory() {}

    public Inventory(String itemId, String itemName, String category, 
                     int quantity, double unitPrice, String supplier, 
                     LocalDate lastRestocked, String status) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
        this.lastRestocked = lastRestocked;
        this.status = status;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getLastRestocked() {
        return lastRestocked;
    }

    public void setLastRestocked(LocalDate lastRestocked) {
        this.lastRestocked = lastRestocked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Calculate total inventory value
    public double getTotalValue() {
        return quantity * unitPrice;
    }
}