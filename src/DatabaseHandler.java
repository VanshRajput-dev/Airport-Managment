import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the Flight Booking System
 */
public class DatabaseHandler {
    private Connection conn;

    /**
     * Constructor initializes database connection and creates tables if they don't exist
     */
    public DatabaseHandler() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:airport.db");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates database tables if they don't exist
     */
    private void createTables() {
        try (Statement stmt = conn.createStatement()) {
            String flightTable = "CREATE TABLE IF NOT EXISTS flights ("
                    + "flightId INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "flightName TEXT, source TEXT, destination TEXT, "
                    + "capacity INTEGER DEFAULT 100, "
                    + "passenger_count INTEGER DEFAULT 0, "
                    + "available_seats INTEGER DEFAULT 100)";

            String passengerTable = "CREATE TABLE IF NOT EXISTS passengers ("
                    + "passengerId INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT, passportNumber TEXT UNIQUE, contactNumber TEXT UNIQUE, "
                    + "email TEXT UNIQUE, flightId INTEGER, "
                    + "FOREIGN KEY(flightId) REFERENCES flights(flightId))";

            stmt.execute(flightTable);
            stmt.execute(passengerTable);

            createTriggers();
            createView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates triggers to maintain consistency between tables
     */
    private void createTriggers() {
        try (Statement stmt = conn.createStatement()) {
            String incrementTrigger = "CREATE TRIGGER IF NOT EXISTS update_passenger_count_after_insert "
                    + "AFTER INSERT ON passengers "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE flights SET passenger_count = passenger_count + 1, "
                    + "available_seats = capacity - passenger_count - 1 "
                    + "WHERE flightId = NEW.flightId; "
                    + "END;";

            String decrementTrigger = "CREATE TRIGGER IF NOT EXISTS update_passenger_count_after_delete "
                    + "AFTER DELETE ON passengers "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE flights SET passenger_count = passenger_count - 1, "
                    + "available_seats = capacity - passenger_count + 1 "
                    + "WHERE flightId = OLD.flightId; "
                    + "END;";

            stmt.execute(incrementTrigger);
            stmt.execute(decrementTrigger);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates view to join flight and passenger data
     */
    private void createView() {
        try (Statement stmt = conn.createStatement()) {
            String view = "CREATE VIEW IF NOT EXISTS flight_passenger_view AS "
                    + "SELECT p.passengerId, p.name, p.passportNumber, p.contactNumber, p.email, "
                    + "f.flightId, f.flightName, f.source, f.destination, f.available_seats "
                    + "FROM passengers p "
                    + "JOIN flights f ON p.flightId = f.flightId";
            stmt.execute(view);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new flight to the database
     */
    public void addFlight(String name, String source, String destination, int capacity) {
        int finalCapacity = Math.max(capacity, 100); // Ensure minimum 100 seats
        
        String sql = "INSERT INTO flights (flightName, source, destination, capacity, passenger_count, available_seats) "
                   + "VALUES (?, ?, ?, ?, 0, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, source);
            pstmt.setString(3, destination);
            pstmt.setInt(4, finalCapacity);
            pstmt.setInt(5, finalCapacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a passenger to the database
     */
    public boolean addPassenger(String name, String passport, String contact, String email, int flightId) {
        if (!hasAvailableSeats(flightId)) {
            return false;
        }
        
        try {
            conn.setAutoCommit(false);
            
            if (passportExists(passport) || contactExists(contact) || emailExists(email)) {
                conn.rollback();
                return false;
            }

            String sql = "INSERT INTO passengers (name, passportNumber, contactNumber, email, flightId) "
                       + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, passport);
                pstmt.setString(3, contact);
                pstmt.setString(4, email);
                pstmt.setInt(5, flightId);
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets all flights from the database
     */
    public List<String[]> getAllFlights() {
        List<String[]> flights = new ArrayList<>();
        String sql = "SELECT flightId, flightName, source, destination, capacity, passenger_count, available_seats "
                   + "FROM flights ORDER BY flightId";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                flights.add(new String[] {
                    String.valueOf(rs.getInt("flightId")),
                    rs.getString("flightName"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    String.valueOf(rs.getInt("capacity")),
                    String.valueOf(rs.getInt("passenger_count")),
                    String.valueOf(rs.getInt("available_seats"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    /**
     * Gets all passengers from the database
     */
    public List<String[]> getAllPassengers() {
        List<String[]> passengers = new ArrayList<>();
        String sql = "SELECT passengerId, name, passportNumber, contactNumber, email, flightId "
                   + "FROM passengers ORDER BY passengerId";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                passengers.add(new String[] {
                    String.valueOf(rs.getInt("passengerId")),
                    rs.getString("name"),
                    rs.getString("passportNumber"),
                    rs.getString("contactNumber"),
                    rs.getString("email"),
                    String.valueOf(rs.getInt("flightId"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengers;
    }

    /**
     * Gets all passengers with their flight details
     */
    public List<String[]> getPassengersWithFlights() {
        List<String[]> passengers = new ArrayList<>();
        String sql = "SELECT p.passengerId, p.name, p.passportNumber, p.contactNumber, p.email, "
                   + "f.flightId, f.flightName, f.source, f.destination, f.available_seats "
                   + "FROM passengers p JOIN flights f ON p.flightId = f.flightId "
                   + "ORDER BY p.passengerId";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                passengers.add(new String[] {
                    String.valueOf(rs.getInt("passengerId")),
                    rs.getString("name"),
                    rs.getString("passportNumber"),
                    rs.getString("contactNumber"),
                    rs.getString("email"),
                    String.valueOf(rs.getInt("flightId")),
                    rs.getString("flightName"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    String.valueOf(rs.getInt("available_seats"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengers;
    }

   
    public List<String[]> getPassengersByFlightId(int flightId) {
        List<String[]> passengers = new ArrayList<>();
        String sql = "SELECT name, passportNumber, contactNumber, email "
                   + "FROM passengers WHERE flightId = ? ORDER BY name";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flightId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    passengers.add(new String[] {
                        rs.getString("name"),
                        rs.getString("passportNumber"),
                        rs.getString("contactNumber"),
                        rs.getString("email")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengers;
    }
 
    public List<String[]> getUnionExample() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT flightId, flightName, source, destination FROM flights WHERE capacity > 50 "
                   + "UNION "
                   + "SELECT flightId, flightName, source, destination FROM flights WHERE available_seats < 20";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[] {
                    String.valueOf(rs.getInt("flightId")),
                    rs.getString("flightName"),
                    rs.getString("source"),
                    rs.getString("destination")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Helper methods
    public boolean passportExists(String passport) {
        return checkExists("passportNumber", passport);
    }

    public boolean contactExists(String contact) {
        return checkExists("contactNumber", contact);
    }

    public boolean emailExists(String email) {
        return checkExists("email", email);
    }

    public boolean flightExists(int flightId) {
        return checkExists("flightId", String.valueOf(flightId));
    }

    public boolean hasAvailableSeats(int flightId) {
        String sql = "SELECT available_seats FROM flights WHERE flightId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("available_seats") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkExists(String column, String value) {
        String sql = "SELECT 1 FROM passengers WHERE " + column + " = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}