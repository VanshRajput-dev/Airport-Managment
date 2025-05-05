import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FlightBookingGUI {
    private DatabaseHandler dbHandler;
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JTextField flightNameField, sourceField, destinationField, capacityField;
    private JTextField passengerNameField, passportField, contactField, emailField;
    private JComboBox<String> flightDropdown;
    private JTable flightsTable, passengersTable, viewTable;
    private DefaultTableModel flightsTableModel, passengersTableModel, viewTableModel;
    private JLabel passengerCountLabel;

    public FlightBookingGUI() {
        dbHandler = new DatabaseHandler();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Flight Booking System");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create tabbed pane for better organization
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Add tabs
        tabbedPane.addTab("Flight Management", createFlightPanel());
        tabbedPane.addTab("Passenger Management", createPassengerPanel());
        tabbedPane.addTab("View Data", createViewPanel());
        tabbedPane.addTab("Advanced Features", createAdvancedPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(createStatusBar(), BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        updateFlightDropdown();
        refreshFlightsTable();
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(new Color(240, 240, 240));

        JLabel statusLabel = new JLabel("Status: Ready");
        passengerCountLabel = new JLabel("Passengers: 0");

        statusPanel.add(statusLabel);
        statusPanel.add(new JSeparator(SwingConstants.VERTICAL));
        statusPanel.add(passengerCountLabel);

        return statusPanel;
    }

    private JPanel createFlightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 245, 245));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Flight"));
        formPanel.setBackground(new Color(245, 245, 245));

        // Flight form fields
        flightNameField = createStyledTextField();
        sourceField = createStyledTextField();
        destinationField = createStyledTextField();
        capacityField = createStyledTextField();
        capacityField.setText("100");

        formPanel.add(new JLabel("Flight Name:"));
        formPanel.add(flightNameField);
        formPanel.add(new JLabel("Source:"));
        formPanel.add(sourceField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);

        // Add flight button
        JButton addFlightButton = createStyledButton("Add Flight", new Color(70, 130, 180));
        addFlightButton.addActionListener(e -> addFlight());

        // Table for flights
        flightsTableModel = new DefaultTableModel(new Object[]{"ID", "Flight Name", "Source", "Destination", "Capacity", "Passengers", "Available Seats"}, 0);
        flightsTable = new JTable(flightsTableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(flightsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Current Flights"));

        // Delete button
        JButton deleteFlightButton = createStyledButton("Delete Selected Flight", new Color(220, 80, 80));
        deleteFlightButton.addActionListener(e -> deleteSelectedFlight());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(addFlightButton);
        buttonPanel.add(deleteFlightButton);

        // Layout
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPassengerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 245, 245));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Passenger"));
        formPanel.setBackground(new Color(245, 245, 245));

        // Passenger form fields
        passengerNameField = createStyledTextField();
        passportField = createStyledTextField();
        contactField = createStyledTextField();
        emailField = createStyledTextField();
        flightDropdown = new JComboBox<>();
        flightDropdown.setBackground(Color.WHITE);
        flightDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(new JLabel("Passenger Name:"));
        formPanel.add(passengerNameField);
        formPanel.add(new JLabel("Passport Number:"));
        formPanel.add(passportField);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Select Flight:"));
        formPanel.add(flightDropdown);

        // Add passenger button
        JButton addPassengerButton = createStyledButton("Add Passenger", new Color(70, 130, 180));
        addPassengerButton.addActionListener(e -> addPassenger());

        // Table for passengers
        passengersTableModel = new DefaultTableModel(new Object[]{"Name", "Passport", "Contact", "Email", "Flight ID"}, 0);
        passengersTable = new JTable(passengersTableModel);
        passengersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(passengersTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Current Passengers"));

        // Delete button
        JButton deletePassengerButton = createStyledButton("Delete Selected Passenger", new Color(220, 80, 80));
        deletePassengerButton.addActionListener(e -> deleteSelectedPassenger());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(addPassengerButton);
        buttonPanel.add(deletePassengerButton);

        // Layout
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 245, 245));

        // Create view options
        JPanel viewOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        viewOptionsPanel.setBackground(new Color(245, 245, 245));

        JButton viewAllPassengersBtn = createStyledButton("All Passengers", new Color(100, 149, 237));
        JButton viewPassengersWithFlightsBtn = createStyledButton("Passengers with Flights", new Color(100, 149, 237));
        JButton viewAllFlightsBtn = createStyledButton("All Flights", new Color(100, 149, 237));

        viewAllPassengersBtn.addActionListener(e -> showAllPassengers());
        viewPassengersWithFlightsBtn.addActionListener(e -> showPassengersWithFlights());
        viewAllFlightsBtn.addActionListener(e -> showAllFlights());

        viewOptionsPanel.add(viewAllPassengersBtn);
        viewOptionsPanel.add(viewPassengersWithFlightsBtn);
        viewOptionsPanel.add(viewAllFlightsBtn);

        // Results table
        viewTableModel = new DefaultTableModel();
        viewTable = new JTable(viewTableModel);
        viewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(viewTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("View Data"));

        // Add components
        panel.add(viewOptionsPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdvancedPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 245, 245));

        // Union query section
        JPanel unionPanel = new JPanel(new BorderLayout(10, 10));
        unionPanel.setBorder(BorderFactory.createTitledBorder("Union Query Example"));
        unionPanel.setBackground(new Color(245, 245, 245));

        JButton unionQueryButton = createStyledButton("Show UNION Query Results", new Color(60, 179, 113));
        unionQueryButton.addActionListener(e -> showUnionExample());

        unionPanel.add(unionQueryButton, BorderLayout.NORTH);

        // Cursor query section
        JPanel cursorPanel = new JPanel(new BorderLayout(10, 10));
        cursorPanel.setBorder(BorderFactory.createTitledBorder("Cursor-Based Query"));
        cursorPanel.setBackground(new Color(245, 245, 245));

        JComboBox<String> flightSelector = new JComboBox<>();
        flightSelector.setBackground(Color.WHITE);
        flightSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Populate flight selector
        List<String[]> flights = dbHandler.getAllFlights();
        for (String[] flight : flights) {
            flightSelector.addItem(flight[0] + " - " + flight[1]);
        }

        JButton viewPassengersByFlightButton = createStyledButton("View Passengers by Flight", new Color(60, 179, 113));
        viewPassengersByFlightButton.addActionListener(e -> {
            String selected = (String) flightSelector.getSelectedItem();
            if (selected != null) {
                try {
                    int flightId = Integer.parseInt(selected.split(" - ")[0].trim());
                    showPassengersByFlight(flightId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid flight selected.");
                }
            }
        });

        JPanel cursorControls = new JPanel(new BorderLayout(10, 10));
        cursorControls.add(flightSelector, BorderLayout.CENTER);
        cursorControls.add(viewPassengersByFlightButton, BorderLayout.EAST);

        cursorPanel.add(cursorControls, BorderLayout.NORTH);

        // Layout
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.add(unionPanel);
        featuresPanel.add(Box.createVerticalStrut(15));
        featuresPanel.add(cursorPanel);

        panel.add(featuresPanel, BorderLayout.NORTH);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void updateFlightDropdown() {
        flightDropdown.removeAllItems();
        List<String[]> flights = dbHandler.getAllFlights();
        for (String[] flight : flights) {
            if (flight.length >= 2) {
                String flightInfo = flight[0] + " - " + flight[1] + " (" + flight[6] + " seats available)";
                flightDropdown.addItem(flightInfo);
            }
        }
    }

    private void refreshFlightsTable() {
        flightsTableModel.setRowCount(0);
        List<String[]> flights = dbHandler.getAllFlights();
        for (String[] flight : flights) {
            flightsTableModel.addRow(flight);
        }
    }

    private void refreshPassengersTable() {
        passengersTableModel.setRowCount(0);
        List<String[]> passengers = dbHandler.getAllPassengers();
        for (String[] passenger : passengers) {
            passengersTableModel.addRow(passenger);
        }
    }

    private void addFlight() {
        String flightName = flightNameField.getText().trim();
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();
        String capacityStr = capacityField.getText().trim();

        if (flightName.isEmpty() || source.isEmpty() || destination.isEmpty() || capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All flight fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity < 100) {
                capacity = 100;
                capacityField.setText("100");
                JOptionPane.showMessageDialog(frame, "Capacity set to minimum 100 seats.");
            }

            dbHandler.addFlight(flightName, source, destination, capacity);
            updateFlightDropdown();
            refreshFlightsTable();
            JOptionPane.showMessageDialog(frame, "Flight added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear fields
            flightNameField.setText("");
            sourceField.setText("");
            destinationField.setText("");
            capacityField.setText("100");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Capacity must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a flight to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int flightId = Integer.parseInt(flightsTableModel.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to delete this flight and all its passengers?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Implement flight deletion logic here
            JOptionPane.showMessageDialog(frame, "Flight deletion functionality would be implemented here.");
            refreshFlightsTable();
            updateFlightDropdown();
            refreshPassengersTable();
        }
    }

    private void addPassenger() {
        String name = passengerNameField.getText().trim();
        String passport = passportField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String selectedFlight = (String) flightDropdown.getSelectedItem();

        if (name.isEmpty() || passport.isEmpty() || contact.isEmpty() || email.isEmpty() || selectedFlight == null) {
            JOptionPane.showMessageDialog(frame, "Please fill all passenger fields and select a flight.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (dbHandler.passportExists(passport)) {
                JOptionPane.showMessageDialog(frame, "Passport number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dbHandler.contactExists(contact)) {
                JOptionPane.showMessageDialog(frame, "Contact number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dbHandler.emailExists(email)) {
                JOptionPane.showMessageDialog(frame, "Email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int flightId = Integer.parseInt(selectedFlight.split(" - ")[0].trim());
            boolean success = dbHandler.addPassenger(name, passport, contact, email, flightId);

            if (success) {
                JOptionPane.showMessageDialog(frame, "Passenger added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateFlightDropdown();
                refreshPassengersTable();
                refreshFlightsTable();

                // Clear fields
                passengerNameField.setText("");
                passportField.setText("");
                contactField.setText("");
                emailField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add passenger. No available seats.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error adding passenger: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedPassenger() {
        int selectedRow = passengersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a passenger to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passport = passengersTableModel.getValueAt(selectedRow, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to delete this passenger?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Implement passenger deletion logic here
            JOptionPane.showMessageDialog(frame, "Passenger deletion functionality would be implemented here.");
            refreshPassengersTable();
            updateFlightDropdown();
            refreshFlightsTable();
        }
    }

    private void showAllPassengers() {
        viewTableModel.setRowCount(0);
        viewTableModel.setColumnIdentifiers(new String[]{
            "Passenger ID", "Name", "Passport", "Contact", "Email", "Flight ID"
        });

        List<String[]> passengers = dbHandler.getAllPassengers();
        for (String[] passenger : passengers) {
            viewTableModel.addRow(passenger);
        }
        tabbedPane.setSelectedIndex(2); // Switch to view tab
    }

    private void showPassengersWithFlights() {
        viewTableModel.setRowCount(0);
        viewTableModel.setColumnIdentifiers(new String[]{
            "Passenger ID", "Name", "Passport", "Contact", "Email",
            "Flight ID", "Flight Name", "Source", "Destination", "Available Seats"
        });

        List<String[]> passengers = dbHandler.getPassengersWithFlights();
        for (String[] passenger : passengers) {
            viewTableModel.addRow(passenger);
        }
        tabbedPane.setSelectedIndex(2); // Switch to view tab
    }

    private void showAllFlights() {
        viewTableModel.setRowCount(0);
        viewTableModel.setColumnIdentifiers(new String[]{
            "Flight ID", "Flight Name", "Source", "Destination", 
            "Capacity", "Passengers", "Available Seats"
        });

        List<String[]> flights = dbHandler.getAllFlights();
        for (String[] flight : flights) {
            viewTableModel.addRow(flight);
        }
        tabbedPane.setSelectedIndex(2); // Switch to view tab
    }

    private void showPassengersByFlight(int flightId) {
        viewTableModel.setRowCount(0);
        viewTableModel.setColumnIdentifiers(new String[]{
            "Name", "Passport", "Contact", "Email"
        });

        List<String[]> passengers = dbHandler.getPassengersByFlightId(flightId);
        for (String[] passenger : passengers) {
            viewTableModel.addRow(passenger);
        }
        tabbedPane.setSelectedIndex(2); // Switch to view tab
    }

    private void showUnionExample() {
        viewTableModel.setRowCount(0);
        viewTableModel.setColumnIdentifiers(new String[]{"Flight ID", "Flight Name", "Source", "Destination"});

        List<String[]> result = dbHandler.getUnionExample();
        for (String[] row : result) {
            viewTableModel.addRow(row);
        }
        tabbedPane.setSelectedIndex(2); // Switch to view tab
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new FlightBookingGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}