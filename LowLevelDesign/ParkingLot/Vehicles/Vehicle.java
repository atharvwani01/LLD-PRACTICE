package LowLevelDesign.ParkingLot.Vehicles;

public abstract class Vehicle {

    private final String vehicleNo;
    private final VehicleSize vehicleSize;

    public Vehicle(String vehicleNo, VehicleSize vehicleSize){
        this.vehicleNo = vehicleNo;
        this.vehicleSize = vehicleSize;
    }

    public String getVehicleNo() {
        return this.vehicleNo;
    }

    public VehicleSize getVehicleSize(){
        return this.vehicleSize;
    }
}
