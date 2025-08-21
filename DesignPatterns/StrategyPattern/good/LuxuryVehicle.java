package StrategyPattern.good;

class LuxuryVehicle extends Vehicle {
    public LuxuryVehicle() {
        super(new SportsDriveStrategy());
    }
}
