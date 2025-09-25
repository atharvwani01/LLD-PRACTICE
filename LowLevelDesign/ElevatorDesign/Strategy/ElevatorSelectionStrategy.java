package LowLevelDesign.ElevatorDesign.Strategy;


import LowLevelDesign.ElevatorDesign.Entities.Elevator;
import LowLevelDesign.ElevatorDesign.Entities.Request;

import java.util.List;
import java.util.Optional;

public interface ElevatorSelectionStrategy {
    Optional<Elevator> selectElevator(List<Elevator> elevators, Request request);
}
