package StrategyPattern.good;

class OffRoadVehicle extends Vehicle {
    public OffRoadVehicle() {
        super(new OffRoadDriveStrategy());
    }
}
