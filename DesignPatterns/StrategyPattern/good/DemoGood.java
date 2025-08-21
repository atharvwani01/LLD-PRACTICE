package good;

// src/strategy/DemoGood.java
public class DemoGood {
    public static void main(String[] args) {
        Vehicle sports = new SportsVehicle();         // sports behavior
        Vehicle off   = new OffRoadVehicle();         // off-road behavior
        Vehicle pass  = new PassengerVehicle();       // normal behavior
        Vehicle lux   = new LuxuryVehicle();          // sports behavior

        sports.drive();
        off.drive();
        pass.drive();
        lux.drive();

        // Runtime swap (no subclass explosion):
        pass.setDriveStrategy(new SportsDriveStrategy());
        pass.drive(); // now "fast" without creating FastPassengerVehicle

        // Cross-cutting logging without touching strategies:
        Vehicle loggedSports = new Vehicle(new LoggingDriveStrategy(new SportsDriveStrategy()));
        loggedSports.drive();
    }
}
