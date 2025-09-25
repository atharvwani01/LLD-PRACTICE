package LowLevelDesign.ParkingLot.Entities;

import LowLevelDesign.ParkingLot.Vehicles.Vehicle;

import java.util.Date;
import java.util.UUID;

public class ParkingTicket {

    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private final long entryTimestamp;
    private long exitTimeStamp;

    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        entryTimestamp = new Date().getTime();
    }

    public long getExitTimeStamp() {
        return this.exitTimeStamp;
    }

    public void setExitTimeStamp() {
        this.exitTimeStamp = new Date().getTime();
    }

    public long getEntryTimestamp() {
        return this.entryTimestamp;
    }

    public ParkingSpot getParkingSpot() {
        return this.parkingSpot;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public String getTicketId() {
        return this.ticketId;
    }
}
