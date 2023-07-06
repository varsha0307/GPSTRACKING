import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;


public class Device extends JFrame {
    private JTextField tfId;
    private JTextField tfName;
    private JButton btnInsert;
    private JButton btnModify;
    private JButton btnDelete;
    private JButton btnDisplay;
    private JTable table;
    private DefaultTableModel tableModel;

    // Setter and getter functions for ID
    public void setId(String id) {
        tfId.setText(id);
    }

    public String getId() {
        return tfId.getText();
    }

    // Setter and getter functions for Name
    public void setName(String name) {
        tfName.setText(name);
    }

    public String getName() {
        return tfName.getText();
    }

    // Constructor
    public Device() {
        tfId = new JTextField(10);
        tfName = new JTextField(20);
        btnInsert = new JButton("Insert");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        // Create the GridBagLayout and GridBagConstraints
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Set padding

        // Add components to the frame using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(tfId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(tfName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(btnInsert, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(btnModify, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(btnDelete, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(btnDisplay, gbc);

        // Initialize the table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");

        // Create the JTable using the table model
        table = new JTable(tableModel);

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to the frame
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(scrollPane, gbc);

        // Register action listeners for the buttons
        btnInsert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertDevice();
            }
        });

        btnModify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyDevice();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteDevice();
            }
        });

        btnDisplay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayDevices();
            }
        });

        setTitle("Device Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    // Method to establish a JDBC connection
    private Connection getConnection() throws SQLException {
    	String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "gattu_niharika";
        String password = "niharika29";
        return DriverManager.getConnection(url, username, password);
    }

    // Method to insert a new device
    private void insertDevice() {
        try (Connection conn = getConnection()) {
            String id = getId();
            String name = getName();

            // Check if ID and name are not null
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter ID and name.");
                return;
            }

            // Insert the new device
            String insertQuery = "INSERT INTO device (id, name) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, id);
                insertStmt.setString(2, name);
                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Device inserted successfully.");
                    displayDevices(); // Refresh the table after insertion
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error inserting device: " + e.getMessage());
        }
    }

    // Method to modify an existing device
    private void modifyDevice() {
        try (Connection conn = getConnection()) {
            String id = getId();
            String name = getName();

            // Check if ID and name are not null
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter ID and name.");
                return;
            }

            // Modify the existing device
            String updateQuery = "UPDATE device SET name = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, name);
                updateStmt.setString(2, id);
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Device modified successfully.");
                    displayDevices(); // Refresh the table after modification
                } else {
                    JOptionPane.showMessageDialog(this, "Device ID not found.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error modifying device: " + e.getMessage());
        }
    }

    // Method to delete an existing device
    private void deleteDevice() {
        try (Connection conn = getConnection()){
            String id = getId();

            // Delete the existing device
            String deleteQuery = "DELETE FROM device WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, id);
                int rowsDeleted = deleteStmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Device deleted successfully.");
                    displayDevices(); // Refresh the table after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "Device ID not found.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting device: " + e.getMessage());
        }
    }

    // Method to display all devices
    private void displayDevices() {
        try (Connection conn = getConnection()) {
            String selectQuery = "SELECT * FROM device";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                ResultSet rs = selectStmt.executeQuery();
                tableModel.setRowCount(0); // Clear the existing rows

                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    tableModel.addRow(new Object[]{id, name});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error displaying devices: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Device();
            }
        });
    }
}
