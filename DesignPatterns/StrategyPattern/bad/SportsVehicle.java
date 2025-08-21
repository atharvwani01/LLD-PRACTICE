package StrategyPattern.bad;

public class SportsVehicle extends Vehicle {
    @Override
    public void drive() {

        // "Fast"(v1) for sports, Drive logic(some random logic)
        double speed = baseSpeed * 1.6;
        double aeroDrag = 0.03 * speed;
        speed -= aeroDrag;
        double burn  = 0.01 * speed;
        fuelLitres = Math.max(0, fuelLitres - burn);
        tirePressurePsi = Math.max(30, tirePressurePsi - 0.5); // more stress
        System.out.printf("[Sports] speed=%.1f kph, fuel=%.1f L, psi=%.1f%n", speed, fuelLitres, tirePressurePsi);
    }
}
