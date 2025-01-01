package beachresort.ui;

import javax.swing.*;
import java.awt.*;

public class OverviewPanel extends JPanel {
    public OverviewPanel() {
        setLayout(new BorderLayout());

        JLabel overviewLabel = new JLabel("Overview", SwingConstants.CENTER);
        overviewLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(overviewLabel, BorderLayout.NORTH);

        JTextArea overviewTextArea = new JTextArea();
        overviewTextArea.setEditable(false);
        overviewTextArea.setText("Welcome to the Owner Dashboard!\n\n"
                + "Here you can manage bookings, rooms, and staff.");
        add(new JScrollPane(overviewTextArea), BorderLayout.CENTER);
    }
}