package StrategyPattern.good;

class SportsDriveStrategy implements DriveStrategy {
    public void drive(Stats s) {
        double speed = s.baseSpeedKph * 1.6;
        double aeroDrag = 0.03 * speed;              // simple drag
        speed -= aeroDrag;
        double burn = speed * 0.01;                  // 1% burn
        s.fuelLiters = Math.max(0, s.fuelLiters - burn);
        s.tirePressurePsi = Math.max(30, s.tirePressurePsi - 0.5);
        System.out.printf("[Sports] speed=%.1f kph, fuel=%.1f L, psi=%.1f%n", speed, s.fuelLiters, s.tirePressurePsi);
    }
}
