package LowLevelDesign.ParkingLot.Entities;

import LowLevelDesign.ParkingLot.Vehicles.Vehicle;
import LowLevelDesign.ParkingLot.Vehicles.VehicleSize;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ParkingFloor {

    private final int floorNumber;
    private final Map<String, ParkingSpot> spots;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ConcurrentHashMap<>();
    }

    public void addSpot(ParkingSpot spot) {
        spots.put(spot.getSpotName(), spot);
    }

    public synchronized Optional<ParkingSpot> findAvailableSpot(Vehicle vehicle) {
        return spots.values().stream()
                .filter(spot -> spot.isAvailable() && spot.canFitVehicle(vehicle))
                .min(Comparator.comparing(ParkingSpot::getSpotSize));
    }

    public void displayAvailability() {
        System.out.println("Floor " + floorNumber + " Availability :");

        Map<VehicleSize, Long> availableCounts = spots.values().stream()
                .filter(ParkingSpot::isAvailable)
                .collect(Collectors.groupingBy(ParkingSpot::getSpotSize, Collectors.counting()));

        System.out.println(availableCounts);
    }


}
