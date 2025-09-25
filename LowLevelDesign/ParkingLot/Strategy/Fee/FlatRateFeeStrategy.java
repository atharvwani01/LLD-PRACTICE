package LowLevelDesign.ParkingLot.Strategy.Fee;

import LowLevelDesign.ParkingLot.Entities.ParkingTicket;

public class FlatRateFeeStrategy implements FeeStrategy {

    private static final double FEE_PER_HOUR = 10.0;

    @Override
    public double calculateFee(ParkingTicket parkingTicket) {
        long duration = parkingTicket.getExitTimeStamp() - parkingTicket.getEntryTimestamp();
        long hours = (duration / (1000 * 60 * 60)) + 1;
        return FEE_PER_HOUR * hours;
    }
}
