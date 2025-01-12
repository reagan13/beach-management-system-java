package beachresort.ui;

import beachresort.models.Staff; // Assuming you have a Staff model
import beachresort.models.Person; // Assuming you have a User model
import beachresort.repositories.StaffRepository; // Assuming you have a StaffRepository

import javax.swing.*;
import java.awt.*;

public class TaskManagementPanel extends JPanel {
    private StaffRepository staffRepository;

    public TaskManagementPanel(Person person) {
        staffRepository = new StaffRepository(); // Initialize the repository
        setLayout(new GridLayout(0, 2, 10, 10)); // Use GridLayout with 2 columns and gaps

        setBorder(BorderFactory.createTitledBorder("Task Details"));

        // Fetch staff details using the user ID
        Staff staff = staffRepository.getStaffByUserId(person.getId());
        if (staff != null) {
            // Create labels for each detail
            JLabel staffIdLabel = new JLabel("Staff ID: ");
            JLabel staffIdValue = new JLabel(String.valueOf(staff.getStaffId()));
            
            JLabel positionLabel = new JLabel("Position: ");
            JLabel positionValue = new JLabel(staff.getPosition());
            
            JLabel statusLabel = new JLabel("Status: ");
            JLabel statusValue = new JLabel(staff.getStatus());
            
            JLabel taskLabel = new JLabel("Task: ");
            JLabel taskValue = new JLabel(staff.getTask());

            // Add labels to the panel
            add(staffIdLabel);
            add(staffIdValue);
            add(positionLabel);
            add(positionValue);
            add(statusLabel);
            add(statusValue);
            add(taskLabel);
            add(taskValue);
        } else {
            JLabel errorLabel = new JLabel("No staff details found for the user.");
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(errorLabel);
            add(new JLabel()); // Empty label to maintain grid structure
        }

    }
}