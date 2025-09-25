package LowLevelDesign.ParkingLot.Strategy.Parking;

import LowLevelDesign.ParkingLot.Entities.ParkingFloor;
import LowLevelDesign.ParkingLot.Entities.ParkingSpot;
import LowLevelDesign.ParkingLot.Vehicles.Vehicle;

import java.util.List;
import java.util.Optional;

public interface ParkingStrategy {
    Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}
