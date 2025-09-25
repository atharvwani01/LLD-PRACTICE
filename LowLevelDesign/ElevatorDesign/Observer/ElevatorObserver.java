package LowLevelDesign.ElevatorDesign.Observer;

import LowLevelDesign.ElevatorDesign.Entities.Elevator;

public interface ElevatorObserver {
    void update(Elevator elevator);
}
