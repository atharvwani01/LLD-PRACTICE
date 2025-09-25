package LowLevelDesign.ParkingLot.Entities;

import LowLevelDesign.ParkingLot.Vehicles.Vehicle;
import LowLevelDesign.ParkingLot.Vehicles.VehicleSize;

public class ParkingSpot {

    private final String spotName;
    private boolean available;
    private Vehicle parkedVehicle;
    private VehicleSize spotSize;


    public ParkingSpot(String spotName, VehicleSize spotSize) {
        this.spotName = spotName;
        this.spotSize = spotSize;
        this.available = true;
        this.parkedVehicle = null;
    }

    public String getSpotName() {
        return this.spotName;
    }

    public VehicleSize getSpotSize() {
        return this.spotSize;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public synchronized void parkVehicle(Vehicle vehicle){
        if(this.available){
            this.parkedVehicle = vehicle;
            this.available = false;
        }
        else{
            throw new IllegalArgumentException("Parking Spot is parked !");
        }
    }

    public synchronized void unparkVehicle(Vehicle vehicle){
        if(this.available){
            throw new IllegalArgumentException("Parking Spot is already unparked !");
        }
        else{
            this.available = true;
        }
    }

    public boolean canFitVehicle(Vehicle vehicle){
        if (!this.isAvailable()){
            return false;
        }

        switch (vehicle.getVehicleSize()){
            case SMALL:
                return spotSize.equals(VehicleSize.SMALL);
            case MEDIUM:
                return spotSize.equals(VehicleSize.MEDIUM);
            case LARGE:
                return spotSize.equals(VehicleSize.LARGE);
            default:
                return false;
        }

    }
}
