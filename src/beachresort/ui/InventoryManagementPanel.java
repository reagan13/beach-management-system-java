package beachresort.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryManagementPanel extends JPanel {
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryManagementPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Table Setup
        String[] columnNames = {
            "Item ID", "Item Name", "Category", 
            "Quantity", "Unit Price", "Total Value"
        };

        Object[][] data = {
            {"I001", "Bed Sheets", "Linens", 50, 25.00, 1250.00},
            {"I002", "Towels", "Bathroom", 100, 10.00, 1000.00}
        };

        tableModel = new DefaultTableModel(data, columnNames);
        inventoryTable = new JTable(tableModel);
        
        // Styling
        inventoryTable.setRowHeight(40);
        inventoryTable.getTableHeader().setBackground(ACCENT_COLOR);
        inventoryTable.getTableHeader().setForeground(Color.WHITE);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Action Buttons Panel
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.NORTH);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(BACKGROUND_COLOR);

        JButton addItemBtn = createStyledButton("Add Item", ACCENT_COLOR);
        JButton restockBtn = createStyledButton("Restock", Color.DARK_GRAY);
        JButton removeItemBtn = createStyledButton("Remove Item", Color.RED);

        addItemBtn.addActionListener(e -> showAddItemDialog());
        restockBtn.addActionListener(e -> showRestockDialog());
        removeItemBtn.addActionListener(e -> removeSelectedItem());

        panel.add(addItemBtn);
        panel.add(restockBtn);
        panel.add(removeItemBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Inventory Item");
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        String[] labels = {
            "Item Name:", "Category:", 
            "Quantity:", "Unit Price:"
        };

        for (String label : labels) {
            dialog.add(new JLabel(label));
            dialog.add(new JTextField());
        }

        JButton saveButton = createStyledButton ("Save Item", ACCENT_COLOR);
        saveButton.addActionListener(e -> {
            // Save item logic
            dialog.dispose();
        });

        dialog.add(new JLabel()); // Spacer
        dialog.add(saveButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showRestockDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to restock");
            return;
        }
        // Restock logic
    }

    private void removeSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to remove");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove this item?");
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
        }
    }
}