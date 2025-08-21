package good;

class OffRoadDriveStrategy implements DriveStrategy {
    public void drive(Stats s) {
        double speed = s.baseSpeedKph * 0.75;        // terrain penalty
        speed = Math.max(30, speed - 5);             // traction loss
        double burn = speed * 0.007;                 // slightly higher burn
        s.fuelLiters = Math.max(0, s.fuelLiters - burn);
        s.tirePressurePsi = Math.max(26, s.tirePressurePsi - 0.3); // air down
        System.out.printf("[OffRoad] speed=%.1f kph, fuel=%.1f L, psi=%.1f%n", speed, s.fuelLiters, s.tirePressurePsi);
    }
}
