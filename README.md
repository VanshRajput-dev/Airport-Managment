# Airport Management System

This is a simple **Airport Management System** written in Java using an SQLite database. The system allows you to manage flight details and passenger information.



## Project Structure

airportmanagement/

│

├── bin/

│   ├── Airport.class

│   ├── AirportManagement.class

│   ├── DatabaseHandler.class

│   └── GUI.class

│

├── src/

│   ├── Airport.java

│   ├── AirportManagement.java

│   ├── DatabaseHandler.java

│   └── GUI.java

│

└── sqlite-jdbc-3.46.1.3.jar


## Requirements

- Java 8 or higher
- SQLite JDBC driver (already included in the project: `sqlite-jdbc-3.46.1.3.jar`)
- SQLite installed (optional, if you want to manually inspect the database)

## How to Run

### Step 1: Compile the Program

Open a terminal/command prompt and navigate to the project directory. Then, compile the Java files using the following command:

```bash
javac -cp .:sqlite-jdbc-3.46.1.3.jar src/*.java
```

This will compile the source files in the `src/` directory.

### Step 2: Run the Program

After compiling, you can run the program using the following command:

```bash
java -cp ".;.\bin;.\sqlite-jdbc-3.46.1.3.jar" FlightBookingGUI
```

### Step 3: Interact with the Program

The program provides a menu-driven interface. You can add flights, add passengers, display flight details, and display passengers. Follow the prompts in the terminal to use the system.

## Sample Usage

1. **Add Flight**: You can add a new flight by entering the flight number, destination, and departure time.
2. **Add Passenger**: You can add a new passenger and assign them to an existing flight using the flight ID.
3. **Display Flights**: This option will display all available flights.
4. **Display Passengers**: This option will display all passengers along with their assigned flight numbers.

 

