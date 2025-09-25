package LowLevelDesign.ParkingLot.Entities;

import LowLevelDesign.ParkingLot.Strategy.Fee.FeeStrategy;
import LowLevelDesign.ParkingLot.Strategy.Fee.FlatRateFeeStrategy;
import LowLevelDesign.ParkingLot.Strategy.Parking.BestFitStrategy;
import LowLevelDesign.ParkingLot.Strategy.Parking.ParkingStrategy;
import LowLevelDesign.ParkingLot.Vehicles.Vehicle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {

    private static ParkingLot instance;
    private final List<ParkingFloor> parkingFloors = new ArrayList<>();
    private final Map<String, ParkingTicket> activeParkingTickets;
    private FeeStrategy feeStrategy;
    private ParkingStrategy parkingStrategy;

    private ParkingLot() {
        this.feeStrategy = new FlatRateFeeStrategy();
        this.parkingStrategy = new BestFitStrategy();
        this.activeParkingTickets = new ConcurrentHashMap<>();
    }

    public static synchronized ParkingLot getInstance(){
        if(instance == null){
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addFloor(ParkingFloor parkingFloor){
        parkingFloors.add(parkingFloor);
    }

    public void setFeeStrategy (FeeStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
    }

    public void setParkingStrategy(ParkingStrategy parkingStrategy) {
        this.parkingStrategy = parkingStrategy;
    }

    public Optional<ParkingTicket> parkVehicle(Vehicle vehicle){
        Optional<ParkingSpot> availableSpot = parkingStrategy.findSpot(parkingFloors, vehicle);
        if(availableSpot.isPresent()){
            ParkingSpot parkingSpot = availableSpot.get();
            parkingSpot.parkVehicle(vehicle);
            ParkingTicket parkingTicket = new ParkingTicket(vehicle, parkingSpot);
            activeParkingTickets.put(vehicle.getVehicleNo(), parkingTicket);
            System.out.printf("%s parked at %s. Ticket: %s\n", vehicle.getVehicleNo(), parkingSpot.getSpotName(), parkingTicket.getTicketId());
            return Optional.of(parkingTicket);
        }
        return Optional.empty();
    }

    public Optional<Double> unParkVehicle(Vehicle vehicle){
        ParkingTicket parkingTicket = activeParkingTickets.remove(vehicle.getVehicleNo());
        if (parkingTicket == null) {
            System.out.println("Ticket not found");
            return Optional.empty();
        }
        parkingTicket.setExitTimeStamp();
        parkingTicket.getParkingSpot().unparkVehicle(vehicle);
        activeParkingTickets.remove(parkingTicket.getTicketId());
        Double parkingFee = feeStrategy.calculateFee(parkingTicket);

        return Optional.of(parkingFee);
    }


}
