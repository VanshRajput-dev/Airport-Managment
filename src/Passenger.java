public class Passenger {
    private String name;
    private int age;
    private String flightNumber;

    public Passenger(String name, int age, String flightNumber) {
        this.name = name;
        this.age = age;
        this.flightNumber = flightNumber;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getFlightNumber() { return flightNumber; }

    @Override
    public String toString() {
        return "Passenger: " + name + " (Age: " + age + "), Flight: " + flightNumber;
    }
}
