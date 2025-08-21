package StrategyPattern.good;

// src/strategy/SportsVehicle.java
class SportsVehicle extends Vehicle {
    public SportsVehicle() {
        super(new SportsDriveStrategy());
    }
}
