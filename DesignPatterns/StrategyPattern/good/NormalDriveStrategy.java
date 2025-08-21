package StrategyPattern.good;

class NormalDriveStrategy implements DriveStrategy {
    public void drive(Stats s) {
        double speed = s.baseSpeedKph;
        double burn = speed * 0.005;                 // 0.5% burn
        s.fuelLiters = Math.max(0, s.fuelLiters - burn);
        s.tirePressurePsi = Math.max(28, s.tirePressurePsi - 0.2);
        System.out.printf("[Normal] speed=%.1f kph, fuel=%.1f L, psi=%.1f%n", speed, s.fuelLiters, s.tirePressurePsi);
    }
}
