import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private Connection conn;

    public DatabaseHandler() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:airport.db");
            // Enable foreign key constraint support
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement stmt = conn.createStatement()) {
            String flightTable = "CREATE TABLE IF NOT EXISTS flights ("
                    + "flightId INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "flightName TEXT, source TEXT, destination TEXT, "
                    + "passenger_count INTEGER DEFAULT 0)";
            
            String passengerTable = "CREATE TABLE IF NOT EXISTS passengers ("
                    + "passengerId INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT, passportNumber TEXT UNIQUE, contactNumber TEXT, "
                    + "email TEXT, flightId INTEGER, "
                    + "FOREIGN KEY(flightId) REFERENCES flights(flightId))";

            stmt.execute(flightTable);
            stmt.execute(passengerTable);
            createTriggers();
            createView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTriggers() {
        try (Statement stmt = conn.createStatement()) {
            String incrementTrigger = "CREATE TRIGGER IF NOT EXISTS update_passenger_count_after_insert "
                    + "AFTER INSERT ON passengers "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE flights SET passenger_count = passenger_count + 1 "
                    + "WHERE flightId = NEW.flightId; "
                    + "END;";

            String decrementTrigger = "CREATE TRIGGER IF NOT EXISTS update_passenger_count_after_delete "
                    + "AFTER DELETE ON passengers "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE flights SET passenger_count = passenger_count - 1 "
                    + "WHERE flightId = OLD.flightId; "
                    + "END;";

            stmt.execute(incrementTrigger);
            stmt.execute(decrementTrigger);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createView() {
        try (Statement stmt = conn.createStatement()) {
            String view = "CREATE VIEW IF NOT EXISTS flight_passenger_view AS "
                    + "SELECT p.passengerId, p.name, p.passportNumber, p.contactNumber, p.email, "
                    + "f.flightId, f.flightName, f.source, f.destination "
                    + "FROM passengers p "
                    + "JOIN flights f ON p.flightId = f.flightId;";
            stmt.execute(view);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFlight(String name, String source, String destination) {
        String sql = "INSERT INTO flights (flightName, source, destination) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, source);
            pstmt.setString(3, destination);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPassenger(String name, String passport, String contact, String email, int flightId) {
        String sql = "INSERT INTO passengers (name, passportNumber, contactNumber, email, flightId) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, passport);
            pstmt.setString(3, contact);
            pstmt.setString(4, email);
            pstmt.setInt(5, flightId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This is used by the flight dropdown
    public List<String[]> getAllFlights() {
        List<String[]> flights = new ArrayList<>();
        String sql = "SELECT flightId, flightName FROM flights";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                flights.add(new String[]{
                        String.valueOf(rs.getInt("flightId")),
                        rs.getString("flightName")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public List<String[]> getAllPassengers() {
        List<String[]> passengers = new ArrayList<>();
        String sql = "SELECT * FROM passengers";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                passengers.add(new String[]{
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

    public List<String[]> getPassengersWithFlights() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT * FROM flight_passenger_view";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{
                        String.valueOf(rs.getInt("passengerId")),
                        rs.getString("name"),
                        rs.getString("passportNumber"),
                        rs.getString("contactNumber"),
                        rs.getString("email"),
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

    public List<String[]> getPassengersByFlightId(int flightId) {
        List<String[]> passengers = new ArrayList<>();
        String sql = "SELECT name, passportNumber, contactNumber, email FROM passengers WHERE flightId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flightId);
            ResultSet rs = pstmt.executeQuery(); 
    
            while (rs.next()) {
                passengers.add(new String[]{
                    rs.getString("name"),
                    rs.getString("passportNumber"),
                    rs.getString("contactNumber"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return passengers;
    }
    

    public List<String[]> getFlightPassengerCounts() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT flightId, flightName, passenger_count FROM flights";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{
                    String.valueOf(rs.getInt("flightId")),
                    rs.getString("flightName"),
                    String.valueOf(rs.getInt("passenger_count"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    



    public List<String[]> getUnionExample() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT name, email FROM passengers "
                   + "UNION "
                   + "SELECT flightName, source FROM flights";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{rs.getString(1), rs.getString(2)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
