import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Location extends JFrame {
    private JTextField tfID;
    private JTextField tfSpeed;
    private JTextField tfPlace;
    private JButton btnInsert;
    private JButton btnModify;
    private JButton btnDelete;
    private JButton btnDisplay;
    private JTable table;
    private DefaultTableModel tableModel;

    // Setter and getter functions for ID
    public void setID(String id) {
        tfID.setText(id);
    }

    public String getID() {
        return tfID.getText();
    }

    // Setter and getter functions for Speed
    public void setSpeed(String speed) {
        tfSpeed.setText(speed);
    }

    public String getSpeed() {
        return tfSpeed.getText();
    }

    // Setter and getter functions for Place
    public void setPlace(String place) {
        tfPlace.setText(place);
    }

    public String getPlace() {
        return tfPlace.getText();
    }

    // Constructor
    public Location() {
        tfID = new JTextField(10);
        tfSpeed = new JTextField(10);
        tfPlace = new JTextField(20);
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
        add(tfID, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Speed:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(tfSpeed, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Place:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(tfPlace, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(btnInsert, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(btnModify, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(btnDelete, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(btnDisplay, gbc);

        // Initialize the table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Speed");
        tableModel.addColumn("Place");

        // Create the JTable using the table model
        table = new JTable(tableModel);

        // Add a selection listener to populate the text fields when a row is selected
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (table.getSelectedRow() != -1) {
                    String id = table.getValueAt(table.getSelectedRow(), 0).toString();
                    String speed = table.getValueAt(table.getSelectedRow(), 1).toString();
                    String place = table.getValueAt(table.getSelectedRow(), 2).toString();
                   setID(id);
                    setSpeed(speed);
                    setPlace(place);
                }
            }
        });

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to the frame
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(scrollPane, gbc);

        // Register action listeners for the buttons
        btnInsert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertLocation();
            }
        });

        btnModify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyLocation();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteLocation();
            }
        });

        btnDisplay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayLocations();
            }
        });

        setTitle("Location Management");
       // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @SuppressWarnings("deprecation")
            @Override
                public void windowClosing(WindowEvent we) {
                    dispose();
                }
            });
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

    // Method to insert a new location
    private void insertLocation() {
        try (Connection conn = getConnection()) {
            String id = getID();
            String speed = getSpeed();
            String place = getPlace();

            // Check if speed and place are not null
            if (speed.isEmpty() || place.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter speed and place.");
                return;
            }

            // Insert the new location
            String insertQuery = "INSERT INTO Location (id, speed, place) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, id);
                insertStmt.setString(2, speed);
                insertStmt.setString(3, place);
                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Location inserted successfully.");
                    displayLocations(); // Refresh the table after insertion
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error inserting location: " + e.getMessage());
        }
    }

    // Method to modify an existing location
    private void modifyLocation() {
        try (Connection conn = getConnection()) {
            String id = getID();
            String speed = getSpeed();
            String place = getPlace();

            // Check if speed and place are not null
            if (speed.isEmpty() || place.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter speed and place.");
                return;
            }

            // Modify the existing location
            String updateQuery = "UPDATE Location SET speed = ?, place = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, speed);
                updateStmt.setString(2, place);
                updateStmt.setString(3, id);
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Location modified successfully.");
                    displayLocations(); // Refresh the table after modification
                } else {
                    JOptionPane.showMessageDialog(this, "Location ID not found.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error modifying location: " + e.getMessage());
        }
    }

    // Method to delete an existing location
    private void deleteLocation() {
        try (Connection conn = getConnection()){
            String id = getID();

            // Delete the existing location
            String deleteQuery = "DELETE FROM Location WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, id);
                int rowsDeleted = deleteStmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Location deleted successfully.");
                    displayLocations(); // Refresh the table after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "Location ID not found.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting location: " + e.getMessage());
        }
    }

    // Method to display all locations
    private void displayLocations() {
        try (Connection conn = getConnection()) {
            String selectQuery = "SELECT * FROM Location";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                ResultSet rs = selectStmt.executeQuery();
                tableModel.setRowCount(0); // Clear the existing rows

                while (rs.next()) {
                    String id = rs.getString("id");
                    String speed = rs.getString("speed");
                    String place = rs.getString("place");
                    tableModel.addRow(new Object[]{id, speed, place});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error displaying locations: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Location();
            }
        });
    }
}
