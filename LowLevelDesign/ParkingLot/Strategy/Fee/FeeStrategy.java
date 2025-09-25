package LowLevelDesign.ParkingLot.Strategy.Fee;

import LowLevelDesign.ParkingLot.Entities.ParkingTicket;

public interface FeeStrategy {
    double calculateFee(ParkingTicket parkingTicket);
}
