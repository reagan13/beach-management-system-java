package beachresort.ui;

import javax.swing.*;
import java.awt.*;

public class TaskManagementPanel extends JPanel {
    public TaskManagementPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Task Management"));

        // Sample task list
        String[] tasks = {"Task 1: Clean Room 101", "Task 2: Prepare Breakfast", "Task 3: Check Inventory"};

        JList<String> taskList = new JList<>(tasks);
        JScrollPane scrollPane = new JScrollPane(taskList);
        add(scrollPane, BorderLayout.CENTER);

        // Add a button to mark tasks as complete
        JButton completeTaskButton = new JButton("Mark as Complete");
        completeTaskButton.addActionListener(e -> {
            // Logic to mark the selected task as complete
            String selectedTask = taskList.getSelectedValue();
            if (selectedTask != null) {
                JOptionPane.showMessageDialog(this, selectedTask + " marked as complete.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a task to complete.");
            }
        });
        add(completeTaskButton, BorderLayout.SOUTH);
    }
}