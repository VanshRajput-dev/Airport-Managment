public class Flight {
    private int flightId;
    private String flightName;
    private String source;
    private String destination;

    public Flight(int flightId, String flightName, String source, String destination) {
        this.flightId = flightId;
        this.flightName = flightName;
        this.source = source;
        this.destination = destination;
    }

    public int getFlightId() { return flightId; }
    public String getFlightName() { return flightName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }

    @Override
    public String toString() {
        return flightId + " - " + flightName;
    }
}
