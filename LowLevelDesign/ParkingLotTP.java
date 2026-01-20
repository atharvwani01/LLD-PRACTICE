package TargetedPractice.ParkingLot;

import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

enum VehicleType { CAR, BIKE, TRUCK }
enum ParkingType { SMALL, MEDIUM, LARGE }

class Vehicle {
    String id;
    String vehicleNo;
    VehicleType vehicleType;

    public Vehicle(String vehicleNo, VehicleType vehicleType) {
        this.id = "V_" + UUID.randomUUID().toString();
        this.vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
    }
}

class VehicleFactory {
    public static Vehicle getVehicle(String vehicleNo, VehicleType vehicleType) {
        return new Vehicle(vehicleNo, vehicleType);
    }
}

class Ticket {
    String id;
    LocalDateTime startTime;
    ParkingSpot spot;
    Vehicle vehicle;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.id = "TICK_" + UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.vehicle = vehicle;
        this.spot = spot;
    }
}

class ParkingSpot {
    String id;
    ParkingType parkingType;
    boolean occupied;

    public ParkingSpot(ParkingType parkingType) {
        this.id = "P_" + UUID.randomUUID().toString();
        this.parkingType = parkingType;
        this.occupied = false;
    }

    public synchronized boolean reserve() {
        if (occupied) return false;
        occupied = true;
        return true;
    }

    public synchronized void release() {
        occupied = false;
    }
}

class ParkingFloor {
    String id;
    Map<String, ParkingSpot> parkingSpots = new ConcurrentHashMap<>();

    public ParkingFloor() {
        this.id = "PF_" + UUID.randomUUID().toString();
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        parkingSpots.put(parkingSpot.id, parkingSpot);
    }

    // Returns the first available compatible spot
    public Optional<ParkingSpot> getAvailableSpot(Vehicle vehicle) {
        return parkingSpots.values().stream()
                .filter(spot -> !spot.occupied && isCompatible(spot.parkingType, vehicle.vehicleType))
                .findFirst();
    }

    private boolean isCompatible(ParkingType spotType, VehicleType vehicleType) {
        return (spotType == ParkingType.LARGE && vehicleType == VehicleType.TRUCK) ||
                (spotType == ParkingType.MEDIUM && vehicleType == VehicleType.CAR) ||
                (spotType == ParkingType.SMALL && vehicleType == VehicleType.BIKE);
    }
}

// COST STRATEGY
interface CostStrategy {
    int calculateCost(Ticket ticket);
}

class HourlyCostStrategy implements CostStrategy {
    @Override
    public int calculateCost(Ticket ticket) {
        long hours = Duration.between(ticket.startTime, LocalDateTime.now()).toHours();
        return (int) Math.max(1, hours) * 50; // 50 per hour, min 50
    }
}

class ParkingLot {
    private static volatile ParkingLot instance;
    private final Map<String, ParkingFloor> parkingFloors = new ConcurrentHashMap<>();
    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();
    @Setter
    private AssignFloorStrategy assignFloorStrategy;
    private CostStrategy costStrategy = new HourlyCostStrategy();

    private ParkingLot() {}

    public static ParkingLot getInstance() {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) instance = new ParkingLot();
            }
        }
        return instance;
    }

    public void addParkingFloor(ParkingFloor parkingFloor) {
        parkingFloors.put(parkingFloor.id, parkingFloor);
    }

    public List<ParkingFloor> getParkingFloors() {
        return new ArrayList<>(parkingFloors.values());
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        ParkingFloor floor = assignFloorStrategy.assign(vehicle, this);
        if (floor == null) {
            System.out.println("No space available for " + vehicle.vehicleType);
            return null;
        }

        Optional<ParkingSpot> spotOpt = floor.getAvailableSpot(vehicle);
        if (spotOpt.isPresent() && spotOpt.get().reserve()) {
            Ticket ticket = new Ticket(vehicle, spotOpt.get());
            activeTickets.put(ticket.id, ticket);
            System.out.println("Parked " + vehicle.vehicleNo + " at spot " + spotOpt.get().id);
            return ticket;
        }
        return null;
    }

    // UNPARK FUNCTIONALITY
    public void unparkVehicle(String ticketId, PaymentStrategy paymentMethod) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            System.out.println("Invalid Ticket ID");
            return;
        }

        int amount = costStrategy.calculateCost(ticket);
        paymentMethod.pay(amount);
        ticket.spot.release();
        System.out.println("Vehicle " + ticket.vehicle.vehicleNo + " unparked from " + ticket.spot.id);
    }
}

interface PaymentStrategy { void pay(int amount); }
class UPI implements PaymentStrategy { public void pay(int amount) { System.out.println("Paid " + amount + " via UPI"); }}
class Cash implements PaymentStrategy { public void pay(int amount) { System.out.println("Paid " + amount + " via Cash"); }}

interface AssignFloorStrategy { ParkingFloor assign(Vehicle vehicle, ParkingLot parkingLot); }

class FarthestFloorAssign implements AssignFloorStrategy {
    @Override
    public ParkingFloor assign(Vehicle vehicle, ParkingLot parkingLot) {
        List<ParkingFloor> floors = parkingLot.getParkingFloors();
        for (int i = floors.size() - 1; i >= 0; i--) {
            if (floors.get(i).getAvailableSpot(vehicle).isPresent()) return floors.get(i);
        }
        return null;
    }
}

public class Solution {
    public static void main(String[] args) throws InterruptedException {
        ParkingLot lot = ParkingLot.getInstance();
        lot.setAssignFloorStrategy(new FarthestFloorAssign());

        ParkingFloor f1 = new ParkingFloor();
        f1.addParkingSpot(new ParkingSpot(ParkingType.SMALL));
        f1.addParkingSpot(new ParkingSpot(ParkingType.MEDIUM));
        lot.addParkingFloor(f1);

        Vehicle car1 = VehicleFactory.getVehicle("KA-01-1234", VehicleType.CAR);
        Vehicle car2 = VehicleFactory.getVehicle("KA-01-1233", VehicleType.CAR);
        Vehicle car3 = VehicleFactory.getVehicle("KA-01-1236", VehicleType.CAR);
        // Park
        Ticket ticket1 = lot.parkVehicle(car1);


        // Simulate some time passing then Unpark
        if (ticket1 != null) {
            lot.unparkVehicle(ticket1.id, new UPI());
        }


        AtomicReference<Ticket> ticket2 = new AtomicReference<>();
        AtomicReference<Ticket> ticket3 = new AtomicReference<>();
        Thread t1 = new Thread(()->{
            ticket2.set(lot.parkVehicle(car2));
        });
        Thread t2 = new Thread(()->{
            ticket3.set(lot.parkVehicle(car3));
        });

        t1.start(); t2.start();
        t1.join();
        t2.join();

        if (ticket2.get() != null) {
            lot.unparkVehicle(ticket2.get().id, new UPI());
        }
        if (ticket3.get() != null) {
            lot.unparkVehicle(ticket3.get().id, new UPI());
        }
    }
}
