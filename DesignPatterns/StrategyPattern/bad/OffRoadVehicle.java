package StrategyPattern.bad;

// src/bad/OffRoadVehicle.java
class OffRoadVehicle extends Vehicle {

    //This class is good till now, it has its own logic
    @Override
    void drive() {

        //"Offroad" Drive logic(some random logic)

        // Rough terrain: slower but safer tires, extra traction cost
        double terrainFactor = 0.75;
        double speed = baseSpeed * terrainFactor;
        double tractionLoss = 5;
        speed = Math.max(30, speed - tractionLoss);
        double burn = speed * 0.007;
        fuelLitres = Math.max(0, fuelLitres - burn);
        tirePressurePsi = Math.max(26, tirePressurePsi - 0.3); // airing down
        System.out.printf("[OffRoad] speed=%.1f kph, fuel=%.1f L, psi=%.1f%n", speed, fuelLitres, tirePressurePsi);
    }
}

