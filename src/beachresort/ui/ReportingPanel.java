package beachresort.ui;

import javax.swing.*;
import java.awt.*;

public class ReportingPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);

    public ReportingPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setBackground(BACKGROUND_COLOR);
        reportArea.setFont(new Font("Arial", Font.PLAIN, 16));
        reportArea.setText("Select a report type to view details...");

        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(BACKGROUND_COLOR);

        JButton generateReportBtn = createStyledButton("Generate Report", ACCENT_COLOR);
        JButton exportReportBtn = createStyledButton("Export Report", Color.DARK_GRAY);

        generateReportBtn.addActionListener(e -> generateReport());
        exportReportBtn.addActionListener(e -> exportReport());

        panel.add(generateReportBtn);
        panel.add(exportReportBtn);

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

    private void generateReport() {
        // Logic to generate report
        JOptionPane.showMessageDialog(this, "Report generated successfully!");
    }

    private void exportReport() {
        // Logic to export report
        JOptionPane.showMessageDialog(this, "Report exported successfully!");
    }
}