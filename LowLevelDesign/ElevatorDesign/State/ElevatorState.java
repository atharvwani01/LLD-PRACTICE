package LowLevelDesign.ElevatorDesign.State;


import LowLevelDesign.ElevatorDesign.Entities.Elevator;
import LowLevelDesign.ElevatorDesign.Entities.Request;
import LowLevelDesign.ElevatorDesign.Enums.Direction;

public interface ElevatorState {
    void move(Elevator elevator);
    void addRequest(Elevator elevator, Request request);
    Direction getDirection();
}
