package LowLevelDesign.ParkingLot.Strategy.Fee;

import LowLevelDesign.ParkingLot.Entities.ParkingTicket;
import LowLevelDesign.ParkingLot.Vehicles.VehicleSize;

import java.util.Map;

public class VehicleBasedFeeStrategy implements FeeStrategy {
    public static final Map<VehicleSize, Double> HOURLY_RATES = Map.of(
            VehicleSize.SMALL, 10.0,
            VehicleSize.MEDIUM, 20.0,
            VehicleSize.LARGE, 30.0
    );
    @Override
    public double calculateFee(ParkingTicket parkingTicket) {
        long duration = parkingTicket.getExitTimeStamp() - parkingTicket.getEntryTimestamp();
        long hours = (duration / (1000 * 60 * 60)) + 1;
        return hours * HOURLY_RATES.get(parkingTicket.getVehicle().getVehicleSize());
    }
}
