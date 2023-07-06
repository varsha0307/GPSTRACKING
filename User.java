import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class User extends JFrame {
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
    public User() {
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
                insertUser();
            }
        });

        btnModify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyUser();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        btnDisplay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUsers();
            }
        });

        setTitle("User Management");
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    // Method to insert a new user
    private void insertUser() {
        try (Connection conn = getConnection()) {
            String id = getId();
            String name = getName();

            // Check if ID and name are not null
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter ID and name.");
                return;
            }

            // Insert the new user
            String insertQuery = "INSERT INTO users (id, name) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, id);
                insertStmt.setString(2, name);
                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "User inserted successfully.");
                    displayUsers(); // Refresh the table after insertion
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error inserting user: " + e.getMessage());
        }
    }

    // Method to modify an existing user
    private void modifyUser() {
        try (Connection conn = getConnection()) {
            String id = getId();
            String name = getName();

            // Check if ID and name are not null
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter ID and name.");
                return;
            }

            // Modify the existing user
            String updateQuery = "UPDATE users SET name = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, name);
                updateStmt.setString(2, id);
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "User modified successfully.");
                    displayUsers(); // Refresh the table after modification
                } else {
                    JOptionPane.showMessageDialog(this, "User ID not found.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error modifying user: " + e.getMessage());
        }
    }

    // Method to delete an existing user
    private void deleteUser() {
        try (Connection conn = getConnection()) {
            String id = getId();

            // Delete the existing user
            String deleteQuery = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, id);
                int rowsDeleted = deleteStmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully.");
                    displayUsers(); // Refresh the table after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "User ID not found.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }

    // Method to display all users
    private void displayUsers() {
        try (Connection conn = getConnection()) {
            String selectQuery = "SELECT * FROM users";
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
            JOptionPane.showMessageDialog(this, "Error displaying users: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new User();
            }
        });
    }
}
