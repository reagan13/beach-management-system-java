package beachresort.repositories;

import beachresort.models.Payment;
import beachresort.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    private Connection connection;

    public PaymentRepository() {
        try {
            this.connection = DatabaseConnection.getConnection();
            createPaymentTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPaymentTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS payments (" +
                "payment_id SERIAL PRIMARY KEY, " +
                "user_id VARCHAR(255) NOT NULL, " +
                "payment_type VARCHAR(255), " +
                "amount DECIMAL(10, 2), " +
                "payment_method VARCHAR(255), " +
                "status VARCHAR(255), " +
                "description TEXT, " +
                "payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error creating payments table: " + e.getMessage());
        }
    }
    

     // Validate if user exists in users table
     private boolean validateUser(String userId) throws SQLException {
         String query = "SELECT COUNT(*) FROM users WHERE id = ?";
         try (PreparedStatement pstmt = connection.prepareStatement(query)) {
             pstmt.setString(1, userId);
             try (ResultSet rs = pstmt.executeQuery()) {
                 if (rs.next()) {
                     return rs.getInt(1) > 0;
                 }
             }
         }
         return false;
     }
     
     public boolean addPayment(Payment payment) {
         try {
             // Validate user exists
             if (!validateUser(payment.getUserId())) {
                 System.err.println("User does not exist");
                 return false;
             }

             String query = "INSERT INTO payments " +
                     "(user_id, payment_type, amount, payment_method, status, description, payment_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

             try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                 pstmt.setString(1, payment.getUserId());
                 pstmt.setString(2, payment.getPaymentType());
                 pstmt.setBigDecimal(3, payment.getAmount());
                 pstmt.setString(4, payment.getPaymentMethod());
                 pstmt.setString(5, payment.getStatus());
                 pstmt.setString(6, payment.getDescription());
                 pstmt.setTimestamp(7, payment.getPaymentDate());

                 int rowsAffected = pstmt.executeUpdate();
                 return rowsAffected > 0;
             }
         } catch (SQLException e) {
             System.err.println("Error adding payment: " + e.getMessage());
             return false;
         }
     }

    
    
     public List<Payment> getAllPayments() {
         List<Payment> payments = new ArrayList<>();
         String query = "SELECT * FROM payments ORDER BY payment_date DESC";

         try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

             while (rs.next()) {
                 Payment payment = new Payment(
                         rs.getString("user_id"),
                         rs.getString("payment_type"),
                         rs.getBigDecimal("amount"),
                         rs.getString("payment_method"),
                         rs.getString("status"),
                         rs.getString("description"),
                         rs.getTimestamp("payment_date"));
                 payment.setPaymentId(rs.getInt("payment_id"));
                 payments.add(payment);
             }
         } catch (SQLException e) {
             System.err.println("Error retrieving payments: " + e.getMessage());
         }

         return payments;
     }



    

  
     public List<Payment> getPaymentsByUserId(String userId) {
        // First, validate the user exists
        try {
            if (!validateUser(userId)) {
                System.err.println("User does not exist");
                return new ArrayList<>();
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return new ArrayList<>();
        }

        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE user_id = ? ORDER BY payment_date DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment(
                        rs.getString("user_id"),
                        rs.getString("payment_type"),
                        rs.getBigDecimal("amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"),
                        rs.getString("description"),
                        rs.getTimestamp("payment_date")
                    );
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
        }
        
        return payments;
    }

    public boolean updatePayment(Payment payment) {
        try {
            // Validate user exists
            if (!validateUser(payment.getUserId())) {
                System.err.println("User does not exist");
                return false;
            }

            String query = "UPDATE payments SET " +
                    "user_id = ?, payment_type = ?, amount = ?, " +
                    "payment_method = ?, status = ?, description = ? " +
                    "WHERE payment_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, payment.getUserId());
                pstmt.setString(2, payment.getPaymentType());
                pstmt.setBigDecimal(3, payment.getAmount());
                pstmt.setString(4, payment.getPaymentMethod());
                pstmt.setString(5, payment.getStatus());
                pstmt.setString(6, payment.getDescription());
                pstmt.setInt(7, payment.getPaymentId());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            return false;
        }
    }

    public Payment getPaymentById(int paymentId) {
        String query = "SELECT * FROM payments WHERE payment_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, paymentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Validate user exists
                    if (!validateUser(rs.getString("user_id"))) {
                        System.err.println("Associated user does not exist");
                        return null;
                    }

                    Payment payment = new Payment(
                            rs.getString("user_id"),
                            rs.getString("payment_type"),
                            rs.getBigDecimal("amount"),
                            rs.getString("payment_method"),
                            rs.getString("status"),
                            rs.getString("description"),
                            rs.getTimestamp("payment_date"));
                    payment.setPaymentId(rs.getInt("payment_id"));
                    return payment;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment: " + e.getMessage());
        }

        return null;
    }
    
    
    public boolean deletePayment(int paymentId) {
        // Payment existingPayment = getPaymentById(paymentId);
        // if (existingPayment == null) {
        //     System.err.println("Payment does not exist");
        //     return false;
        // }

        String query = "DELETE FROM payments WHERE payment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, paymentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            return false;
        }
    }

    

    
}