import java.util.ArrayList;
import java.util.List;

public class AirportManagement {
    private List<Airport> airports;

    public AirportManagement() {
        this.airports = new ArrayList<>();
    }

    public void addAirport(Airport airport) {
        airports.add(airport);
    }

    public void removeAirport(int airportId) {
        airports.removeIf(airport -> airport.getAirportId() == airportId);
    }

    public Airport getAirportById(int airportId) {
        for (Airport airport : airports) {
            if (airport.getAirportId() == airportId) {
                return airport;
            }
        }
        return null;
    }

    public List<Airport> getAllAirports() {
        return new ArrayList<>(airports);
    }
}
