package StrategyPattern.bad;

public class Vehicle {
    protected double baseSpeed = 80;
    protected double tirePressurePsi = 32;
    protected double fuelLitres = 40;

    void drive(){
        // "Normal" Drive logic(some random logic)
        double speed = baseSpeed;
        double burn = speed * 0.005;
        fuelLitres = Math.max(0, fuelLitres - burn);
        tirePressurePsi = Math.max(28, tirePressurePsi - 0.2);
        System.out.printf("[Normal] speed=%.1f kph, fuel=%.1f L, psi=%.1f%n", speed, fuelLitres, tirePressurePsi);
    }
}
