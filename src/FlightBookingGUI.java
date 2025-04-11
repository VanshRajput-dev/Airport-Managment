import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FlightBookingGUI {
    private DatabaseHandler dbHandler;
    private JFrame frame;
    private JTextField flightNameField, sourceField, destinationField;
    private JTextField passengerNameField, passportField, contactField, emailField;
    private JComboBox<String> flightDropdown;
    private JTable table;
    private DefaultTableModel tableModel;

    public FlightBookingGUI() {
        dbHandler = new DatabaseHandler();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Flight Booking System");
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        frame.add(createFlightPanel());
        frame.add(createPassengerPanel());
        frame.add(createTablePanel());
        frame.add(createAdvancedQueriesPanel());

        frame.setVisible(true);
        updateFlightDropdown();
    }

    private JPanel createFlightPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Flight Name:"));
        flightNameField = new JTextField();
        panel.add(flightNameField);

        panel.add(new JLabel("Source:"));
        sourceField = new JTextField();
        panel.add(sourceField);

        panel.add(new JLabel("Destination:"));
        destinationField = new JTextField();
        panel.add(destinationField);

        JButton addFlightButton = new JButton("Add Flight");
        addFlightButton.addActionListener(e -> addFlight());
        panel.add(addFlightButton);

        return panel;
    }

    private JPanel createPassengerPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Passenger Name:"));
        passengerNameField = new JTextField();
        panel.add(passengerNameField);

        panel.add(new JLabel("Passport Number:"));
        passportField = new JTextField();
        panel.add(passportField);

        panel.add(new JLabel("Contact Number:"));
        contactField = new JTextField();
        panel.add(contactField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Select Flight:"));
        flightDropdown = new JComboBox<>();
        panel.add(flightDropdown);

        JButton addPassengerButton = new JButton("Add Passenger");
        addPassengerButton.addActionListener(e -> addPassenger());
        panel.add(addPassengerButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton viewFlightsButton = new JButton("View All Flights");
        JButton viewPassengersButton = new JButton("View All Passengers");
        JButton viewPassengerFlightsButton = new JButton("View Passengers with Flights");

        viewFlightsButton.addActionListener(e -> showFlights());
        viewPassengersButton.addActionListener(e -> showPassengers());
        viewPassengerFlightsButton.addActionListener(e -> showPassengersWithFlights());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewFlightsButton);
        buttonPanel.add(viewPassengersButton);
        buttonPanel.add(viewPassengerFlightsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAdvancedQueriesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));

        JButton unionQueryButton = new JButton("Show UNION");
        unionQueryButton.addActionListener(e -> showUnionExample());
        panel.add(unionQueryButton);
    
       
        JPanel cursorPanel = new JPanel();
        JComboBox<String> flightSelector = new JComboBox<>();
        JButton viewPassengersByFlightButton = new JButton("View Passengers (Cursor)");
     
        List<String[]> flights = dbHandler.getAllFlights();
        for (String[] flight : flights) {
            flightSelector.addItem(flight[0] + " - " + flight[1]);
        }
    
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
    
        cursorPanel.add(flightSelector);
        cursorPanel.add(viewPassengersByFlightButton);
        panel.add(cursorPanel);
    
        return panel;
    }

    private void updateFlightDropdown() {
        flightDropdown.removeAllItems();
        List<String[]> flights = dbHandler.getAllFlights();
        for (String[] flight : flights) {
            if (flight.length >= 2) {
                String flightInfo = flight[0] + " - " + flight[1];
                flightDropdown.addItem(flightInfo);
            }
        }
    }

    private void addFlight() {
        String flightName = flightNameField.getText().trim();
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();

        if (flightName.isEmpty() || source.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All flight fields are required!");
            return;
        }

        dbHandler.addFlight(flightName, source, destination);
        updateFlightDropdown();
        JOptionPane.showMessageDialog(frame, "Flight added successfully");

        flightNameField.setText("");
        sourceField.setText("");
        destinationField.setText("");
    }

    private void showPassengersByFlight(int flightId) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Name", "Passport", "Contact", "Email"});
    
        List<String[]> passengers = dbHandler.getPassengersByFlightId(flightId);
        for (String[] passenger : passengers) {
            tableModel.addRow(passenger);
        }
    }
    

    private void addPassenger() {
        String name = passengerNameField.getText().trim();
        String passport = passportField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String selectedFlight = (String) flightDropdown.getSelectedItem();

        if (name.isEmpty() || passport.isEmpty() || contact.isEmpty() || email.isEmpty() || selectedFlight == null) {
            JOptionPane.showMessageDialog(frame, "Please fill all passenger fields and select a flight.");
            return;
        }

        try {
            int flightId = Integer.parseInt(selectedFlight.split(" - ")[0].trim());
            dbHandler.addPassenger(name, passport, contact, email, flightId);
            JOptionPane.showMessageDialog(frame, "Passenger added successfully!");

            passengerNameField.setText("");
            passportField.setText("");
            contactField.setText("");
            emailField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid flight selection.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error adding passenger: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showFlights() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Flight ID", "Name", "Source", "Destination"});
        List<String[]> flights = dbHandler.getAllFlights();
        for (String[] flight : flights) {
            tableModel.addRow(flight);
        }
    }

    private void showPassengers() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Name", "Passport", "Contact", "Email", "Flight ID"});
        List<String[]> passengers = dbHandler.getAllPassengers();
        for (String[] passenger : passengers) {
            tableModel.addRow(passenger);
        }
    }

    private void showPassengersWithFlights() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{
                "Passenger ID", "Name", "Passport", "Contact", "Email",
                "Flight ID", "Flight Name", "Source", "Destination"
        });
        List<String[]> passengers = dbHandler.getPassengersWithFlights();
        for (String[] passenger : passengers) {
            tableModel.addRow(passenger);
        }
    }



    
    private void showUnionExample() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Name/Flight", "Email/Source"});
        List<String[]> result = dbHandler.getUnionExample();
        for (String[] row : result) {
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlightBookingGUI::new);
    }
}
