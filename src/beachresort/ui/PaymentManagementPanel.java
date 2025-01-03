package beachresort.ui;

import beachresort.models.Payment;
import beachresort.repositories.PaymentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class PaymentManagementPanel extends JPanel {
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    private PaymentRepository paymentRepository;

    public PaymentManagementPanel() {
        // Initialize repository
        paymentRepository = new PaymentRepository();

        // Set layout
        setLayout(new BorderLayout());

        // Title
        JLabel paymentLabel = new JLabel("Payment Management", SwingConstants.CENTER);
        paymentLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(paymentLabel, BorderLayout.NORTH);

        // Table Model Setup
        String[] columnNames = {"Payment ID", "User ID", "Type", "Amount", "Method", "Status", "Description", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentsTable = new JTable(tableModel);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addPaymentButton = new JButton("Add Payment");
        JButton editPaymentButton = new JButton("Edit Payment");
        JButton deletePaymentButton = new JButton("Delete Payment");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addPaymentButton);
        buttonPanel.add(editPaymentButton);
        buttonPanel.add(deletePaymentButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addPaymentButton.addActionListener(this::addPayment);
        editPaymentButton.addActionListener(this::editPayment);
        deletePaymentButton.addActionListener(this::deletePayment);
        refreshButton.addActionListener(this::loadPayments);

        // Initial load of payments
        loadPayments(null);
    }

    private void loadPayments(ActionEvent event) {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Fetch payments from repository
        List<Payment> payments = paymentRepository.getAllPayments();
        
        // Populate table
        for (Payment payment : payments) {
            tableModel.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getUserId(),
                payment.getPaymentType(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getDescription(),
                payment.getPaymentDate()
            });
        }
    }

    private void addPayment(ActionEvent e) {
        JDialog addPaymentDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New Payment", true);
        addPaymentDialog.setSize(400, 400);
        addPaymentDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Fields
        JTextField userIdField = createLabeledTextField(panel, "User ID:");
        JTextField paymentTypeField = createLabeledTextField(panel, "Payment Type:");
        JTextField amountField = createLabeledTextField(panel, "Amount:");
        
        // Payment Method Combo Box
        panel.add(new JLabel("Payment Method:"));
        String[] paymentMethods = {"Cash", "Credit Card", "Bank Transfer", "Online Payment"};
        JComboBox<String> paymentMethodCombo = new JComboBox<>(paymentMethods);
        panel.add(paymentMethodCombo);

        // Status Combo Box
        panel.add(new JLabel("Status:"));
        String[] statuses = {"Pending", "Completed", "Failed"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        panel.add(statusCombo);

        JTextField descriptionField = createLabeledTextField(panel, "Description:");

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            try {
                // Validate inputs
                if (!validateInputs(userIdField, paymentTypeField, amountField)) {
                    return;
                }

                // Create Payment object
                Payment newPayment = new Payment(
                    userIdField.getText(),
                    paymentTypeField.getText(),
                    new BigDecimal(amountField.getText()),
                    (String) paymentMethodCombo.getSelectedItem(),
                    (String) statusCombo.getSelectedItem(),
                    descriptionField.getText(),
                    new Timestamp(System.currentTimeMillis())
                );

                // Add payment
                if (paymentRepository.addPayment(newPayment)) {
                    JOptionPane.showMessageDialog(addPaymentDialog, "Payment Added Successfully!");
                    loadPayments(null);
                    addPaymentDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addPaymentDialog, 
                        "Failed to add payment. Please check user ID", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addPaymentDialog, 
                    "Invalid amount", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> addPaymentDialog.dispose());
        panel.add(cancelButton);

        addPaymentDialog.add(panel);
        addPaymentDialog.setVisible(true);
    }

    private void editPayment(ActionEvent e) {
        // Get selected row
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment to edit");
            return;
        }

        // Get payment ID of selected payment
        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);

        JDialog editPaymentDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Payment", true);
        editPaymentDialog.setSize(400, 400);
        editPaymentDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Populate existing payment details
        JTextField userIdField = createLabeledTextField(panel, "User ID:");
        userIdField.setText((String) tableModel.getValueAt(selectedRow, 1));
        userIdField.setEditable(false);

        JTextField paymentTypeField = createLabeledTextField(panel, "Payment Type:");
        paymentTypeField.setText((String) tableModel.getValueAt(selectedRow, 2));

        JTextField amountField = createLabeledTextField(panel, "Amount:");
        amountField.setText(tableModel.getValueAt(selectedRow, 3).toString());

        // Payment Method Combo Box
        panel.add(new JLabel("Payment Method:"));
        String[] paymentMethods = {"Cash", "Credit Card", "Bank Transfer", "Online Payment"};
        JComboBox<String> paymentMethodCombo = new JComboBox<>(paymentMethods);
        paymentMethodCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
        panel.add(paymentMethodCombo);

        // Status Combo Box
        panel.add(new JLabel("Status:"));
        String[] statuses = {"Pending", "Completed", "Failed"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 5));
        panel.add(statusCombo);

        JTextField descriptionField = createLabeledTextField(panel, "Description:");
        descriptionField.setText((String) tableModel.getValueAt(selectedRow, 6));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(saveEvent -> {
            try {
                // Validate inputs
                if (!validateInputs(userIdField, paymentTypeField, amountField)) {
                    return;
                }

                // Create updated Payment object
                Payment updatedPayment = new Payment(
                    userIdField.getText(),
                    paymentTypeField.getText(),
                    new BigDecimal(amountField.getText()),
                    (String) paymentMethodCombo.getSelectedItem(),
                    (String) statusCombo.getSelectedItem(),
                    descriptionField.getText(),
                    new Timestamp(System.currentTimeMillis())
                );
                updatedPayment.setPaymentId(paymentId);

                // Update payment in repository
                if (paymentRepository.updatePayment(updatedPayment)) {
                    JOptionPane.showMessageDialog(editPaymentDialog, "Payment Updated Successfully!");
                    loadPayments(null);
                    editPaymentDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editPaymentDialog, 
                        "Failed to update payment", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editPaymentDialog, 
                    "Invalid amount", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelEvent -> editPaymentDialog.dispose());
        panel.add(cancelButton);

        editPaymentDialog.add(panel);
        editPaymentDialog.setVisible(true);
    }

    private void deletePayment(ActionEvent e) {
        // Get selected row
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment to delete");
            return;
        }

        // Get payment ID of selected payment
        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this payment?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (paymentRepository.deletePayment(paymentId)) {
                JOptionPane.showMessageDialog(this, "Payment Deleted Successfully!");
                loadPayments(null);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete payment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs(JTextField userIdField, JTextField paymentTypeField, JTextField amountField) {
        if (userIdField.getText().trim().isEmpty() || 
            paymentTypeField.getText().trim().isEmpty() || 
            amountField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(amountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private JTextField createLabeledTextField(JPanel panel, String label) {
        panel.add(new JLabel(label));
        JTextField textField = new JTextField();
        panel.add(textField);
        return textField;
    }
}