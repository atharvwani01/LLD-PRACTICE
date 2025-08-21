package StrategyPattern.good;

class Vehicle {
    private DriveStrategy driveStrategy;
    private Stats stats = new Stats();
    public Vehicle(DriveStrategy driveStrategy) {
        this.driveStrategy = driveStrategy;
    }
    public void drive() {
        driveStrategy.drive(stats);
    }
    public void setDriveStrategy(DriveStrategy driveStrategy) {
        this.driveStrategy = driveStrategy;
    }
    public void setStats(Stats stats) {
        this.stats = stats;
    }
}
