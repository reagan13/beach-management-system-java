package beachresort.ui;

import beachresort.models.Inventory;
import beachresort.repositories.InventoryRepository;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class InventoryManagementPanel extends JPanel {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private InventoryRepository inventoryRepository;

    // Input Fields
    private JTextField itemNameField;
    private JTextField categoryField;
    private JTextField quantityField;
    private JTextField unitPriceField;
    private JTextField supplierField;
    private JComboBox<String> statusComboBox;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Inventory Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new InventoryManagementPanel());
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public InventoryManagementPanel() {
        inventoryRepository = new InventoryRepository();
        inventoryRepository.initializeInventoryDatabase();
        initComponents();
        setupLayout();
        loadInventoryFromDatabase();
    }

    private void initComponents() {
        // Column Names
        String[] columnNames = {
            "Item ID", "Item Name", "Category", 
            "Quantity", "Unit Price", "Supplier", 
            "Last Restocked", "Status"
        };

        // Create Table Model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        customizeTable();

        // Initialize Input Fields
        itemNameField = new JTextField(15);
        categoryField = new JTextField(15);
        quantityField = new JTextField(10);
        unitPriceField = new JTextField(10);
        supplierField = new JTextField(15);
        
        statusComboBox = new JComboBox<>(new String[]{
            "Active", "Low Stock", "Out of Stock"
        });
    }

    private void customizeTable() {
        inventoryTable.setRowHeight(45);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        inventoryTable.setShowGrid(true);
        inventoryTable.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = inventoryTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));

        // Add selection listener
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = inventoryTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFieldsFromSelectedRow(selectedRow);
                }
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Panel
        JPanel inputPanel = createInputPanel();

        // Search Panel
        JPanel searchPanel = createSearchPanel();

        // Button Panel
        JPanel buttonPanel = createButtonPanel();

        // Table Scroll Pane
        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        // Layout Arrangement
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
private JPanel createInputPanel() {
    JPanel inputPanel = new JPanel(new BorderLayout());
    
    // First row panel
    JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    firstRowPanel.add(new JLabel("Item Name:"));
    firstRowPanel.add(itemNameField);
    
    firstRowPanel.add(new JLabel("Category:"));
    firstRowPanel.add(categoryField);
    
    firstRowPanel.add(new JLabel("Quantity:"));
    firstRowPanel.add(quantityField);
    
    // Second row panel
    JPanel secondRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    secondRowPanel.add(new JLabel("Unit Price:"));
    secondRowPanel.add(unitPriceField);
    
    secondRowPanel.add(new JLabel("Supplier:"));
    secondRowPanel.add(supplierField);
    
    secondRowPanel.add(new JLabel("Status:"));
    secondRowPanel.add(statusComboBox);
    
    // Add rows to the main panel
    inputPanel.add(firstRowPanel, BorderLayout.NORTH);
    inputPanel.add(secondRowPanel, BorderLayout.SOUTH);
    
    return inputPanel;
}
    private void addLabelAndField(JPanel panel, String label, JComponent field, 
                                   int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add Item");
        JButton updateButton = new JButton("Update Item");
        JButton deleteButton = new JButton("Delete Item");
        JButton reportButton = new JButton("Generate Report");

        addButton.addActionListener(e -> addInventoryItem());
        updateButton.addActionListener(e -> updateInventoryItem());
        deleteButton.addActionListener(e -> deleteInventoryItem());
        reportButton.addActionListener(e -> generateInventoryReport());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(reportButton);

        return buttonPanel;
    }

    private void loadInventoryFromDatabase() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch inventory items from repository
        List<Inventory> inventoryItems = inventoryRepository.getAllInventoryItems();

        // Populate table
        for (Inventory item : inventoryItems) {
            Object[] rowData = {
                item.getItemId(),
                item.getItemName(),
                item.getCategory(),
                item.getQuantity(),
                String.format("₱%.2f", item.getUnitPrice()),
                item.getSupplier(),
                item.getLastRestocked(),
                item.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

        private void performSearch() {
        String searchText = searchField.getText().toLowerCase();
        
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch search results
        List<Inventory> searchResults = inventoryRepository.searchInventoryItems(searchText);

        // Populate table with search results
        for (Inventory item : searchResults) {
            Object[] rowData = {
                item.getItemId(),
                item.getItemName(),
                item.getCategory(),
                item.getQuantity(),
                String.format("₱%.2f", item.getUnitPrice()),
                item.getSupplier(),
                item.getLastRestocked(),
                item.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void addInventoryItem() {
        // Validate input fields
        if (!validateInput()) {
            return;
        }

        // Generate unique item ID
        String itemId = inventoryRepository.generateUniqueItemId();

        // Create Inventory object
        Inventory newItem = new Inventory(
            itemId,
            itemNameField.getText().trim(),
            categoryField.getText().trim(),
            Integer.parseInt(quantityField.getText().trim()),
            Double.parseDouble(unitPriceField.getText().trim()),
            supplierField.getText().trim(),
            LocalDate.now(),
            (String) statusComboBox.getSelectedItem()
        );

        // Add to database
        if (inventoryRepository.addInventoryItem(newItem)) {
            JOptionPane.showMessageDialog(this, 
                "Inventory item added successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh table
            loadInventoryFromDatabase();
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to add inventory item", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInventoryItem() {
        // Check if a row is selected
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to update", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate input fields
        if (!validateInput()) {
            return;
        }

        // Get the item ID from the selected row
        String itemId = (String) tableModel.getValueAt(selectedRow, 0);

        // Create Inventory object
        Inventory updatedItem = new Inventory(
            itemId,
            itemNameField.getText().trim(),
            categoryField.getText().trim(),
            Integer.parseInt(quantityField.getText().trim()),
            Double.parseDouble(unitPriceField.getText().trim()),
            supplierField.getText().trim(),
            LocalDate.now(),
            (String) statusComboBox.getSelectedItem()
        );

        // Update in database
        if (inventoryRepository.updateInventoryItem(updatedItem)) {
            JOptionPane.showMessageDialog(this, 
                "Inventory item updated successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh table
            loadInventoryFromDatabase();
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update inventory item", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInventoryItem() {
        // Check if a row is selected
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the item ID from the selected row
        String itemId = (String) tableModel.getValueAt(selectedRow, 0);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete this inventory item?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Delete from database
            if (inventoryRepository.deleteInventoryItem(itemId)) {
                JOptionPane.showMessageDialog(this, 
                    "Inventory item deleted successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh table
                loadInventoryFromDatabase();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete inventory item", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateInventoryReport() {
        try {
            // Get inventory statistics
            InventoryRepository.InventoryStatistics stats = 
                inventoryRepository.getInventoryStatistics();

            // Create report dialog
            JDialog reportDialog = new JDialog();
            reportDialog.setTitle("Inventory Report");
            reportDialog.setSize(400, 500);
            reportDialog.setModal(true);
            reportDialog.setLocationRelativeTo(this);

            // Report Panel
            JPanel reportPanel = new JPanel();
            reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
            reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Report Components
            JLabel[] reportLabels = {
                new JLabel("Inventory Report"),
                new JLabel(String.format("Total Items: %d", stats.getTotalItems())),
                new JLabel(String.format("Total Inventory Value: ₱%.2f", stats.getTotalInventoryValue())),
                new JLabel("Category Breakdown:")
            };

            // Style report labels
            Font titleFont = new Font("Arial", Font.BOLD, 16);
            Font contentFont = new Font("Arial", Font.PLAIN, 14);
            reportLabels[0].setFont(titleFont);
            for (int i = 1; i < reportLabels.length; i++) {
                reportLabels[i].setFont(contentFont);
            }

            // Add labels to report panel
            for (JLabel label : reportLabels) {
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                reportPanel.add(label);
                reportPanel.add(Box.createVerticalStrut(10));
            }

            // Category Breakdown
            for (Map.Entry<String, Integer> entry : stats.getCategoryStats().entrySet()) {
                JLabel categoryLabel = new JLabel(
                    String.format("%s: %d items", entry.getKey(), entry.getValue())
                );
                categoryLabel.setFont(contentFont);
                categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                reportPanel.add(categoryLabel);
                reportPanel.add(Box.createVerticalStrut(5));
            }

            // Low Stock Items
            JLabel lowStockTitle = new JLabel("Low Stock Items:");
            lowStockTitle.setFont(titleFont);
            lowStockTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            reportPanel.add(lowStockTitle);

            List<Inventory> lowStockItems = inventoryRepository.getLowStockItems(10);
            for (Inventory item : lowStockItems) {
                JLabel lowStockLabel = new JLabel(
                    String.format("%s - %d in stock", item.getItemName(), item.getQuantity())
                );
                lowStockLabel.setFont(contentFont);
                lowStockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                reportPanel.add(lowStockLabel);
                reportPanel.add(Box.createVerticalStrut(5));
            }

            // Close Button
            JButton closeButton = new JButton("Close");
                        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.addActionListener(e -> reportDialog.dispose());

            reportPanel.add(closeButton);

            // Add scroll pane if content is too long
            JScrollPane scrollPane = new JScrollPane(reportPanel);
            reportDialog.add(scrollPane);

            reportDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to generate inventory report: " + e.getMessage(), 
                "Report Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFieldsFromSelectedRow(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
            itemNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            categoryField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            quantityField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            
            // Remove currency symbol and parse
            String priceString = tableModel.getValueAt(selectedRow, 4).toString().replace("₱", "");
            unitPriceField.setText(priceString);
            
            supplierField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            statusComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 7).toString());
        }
    }

    private void clearInputFields() {
        itemNameField.setText("");
        categoryField.setText("");
        quantityField.setText("");
        unitPriceField.setText("");
        supplierField.setText("");
        statusComboBox.setSelectedIndex(0);
    }

    private boolean validateInput() {
        // Validate Item Name
        if (itemNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Item Name cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Category
        if (categoryField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Category cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Quantity
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Quantity must be a non-negative number", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Quantity must be a valid number", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Unit Price
        try {
            double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
            if (unitPrice < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Unit Price must be a non-negative number", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Unit Price must be a valid number", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Supplier
        if (supplierField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Supplier cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // Optional: Method to handle low stock alert
    private void checkLowStockItems() {
        List<Inventory> lowStockItems = inventoryRepository.getLowStockItems(10);
        if (!lowStockItems.isEmpty()) {
            StringBuilder alertMessage = new StringBuilder("Low Stock Alert:\n");
            for (Inventory item : lowStockItems) {
                alertMessage.append(String.format("%s: %d in stock\n", 
                    item.getItemName(), item.getQuantity()));
            }

            JOptionPane.showMessageDialog(this, 
                alertMessage.toString(), 
                "Low Stock Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    // Additional utility methods can be added as needed

}