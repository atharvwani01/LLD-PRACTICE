package TargetedPractice.Elevator;

import LowLevelDesign.ElevatorDesign.Enums.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

// --- OBSERVER PATTERN: Interface for anyone who wants to listen to Elevator updates ---
interface ElevatorObserver {
    void onStatusUpdate(String message);
}

class ConsoleLogger implements ElevatorObserver {
    @Override
    public void onStatusUpdate(String message) {
        System.out.println("[LOG] " + message);
    }
}

// --- STRATEGY PATTERN: For different Dispatching Logics ---
interface DispatchStrategy {
    Elevator findBestCar(List<Elevator> elevators, int floor);
}

class EnergySavingStrategy implements DispatchStrategy {
    @Override
    public Elevator findBestCar(List<Elevator> elevators, int floor) {
        Elevator best = null;
        int minCost = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            int cost = Math.abs(e.currentFloor - floor);

            // Energy Saving Logic:
            // If a car is IDLE, we give it a "Startup Penalty".
            // It's "cheaper" (energy-wise) to keep a moving car moving than to start a dead motor.
            if (e.direction == Direction.IDLE && cost > 0) {
                cost += 15; // High penalty to avoid waking up idle cars
            }

            // If car is moving AWAY, huge penalty
            if ((e.direction == Direction.UP && floor < e.currentFloor) ||
                    (e.direction == Direction.DOWN && floor > e.currentFloor)) {
                cost += 25;
            }

            if (cost < minCost) {
                minCost = cost;
                best = e;
            }
        }
        return best;
    }
}

class NearestCarStrategy implements DispatchStrategy {
    @Override
    public Elevator findBestCar(List<Elevator> elevators, int floor) {
        Elevator best = null;
        int minCost = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            int distance = Math.abs(e.currentFloor - floor);
            int cost = distance;

            // Penalty if the car is moving away from the request
            if (e.direction != Direction.IDLE) {
                boolean movingAway = (e.direction == Direction.UP && floor < e.currentFloor) ||
                        (e.direction == Direction.DOWN && floor > e.currentFloor);
                if (movingAway) cost += 10;
            }

            if (cost < minCost) {
                minCost = cost;
                best = e;
            }
        }
        return best;
    }
}

class Elevator implements Runnable {
    String id;
    int currentFloor = 0;
    Direction direction = Direction.IDLE;
    TreeSet<Integer> upRequests = new TreeSet<>();
    TreeSet<Integer> downRequests = new TreeSet<>();

    // Observer List
    private List<ElevatorObserver> observers = new ArrayList<>();

    public Elevator(String id) { this.id = id; }

    public void addObserver(ElevatorObserver observer) { observers.add(observer); }

    private void notifyObservers(String message) {
        for (ElevatorObserver o : observers) o.onStatusUpdate(id + ": " + message);
    }

    public synchronized void addRequest(int floor) {
        if (floor > currentFloor) upRequests.add(floor);
        else if (floor < currentFloor) downRequests.add(floor);

        if (direction == Direction.IDLE) {
            direction = (floor > currentFloor) ? Direction.UP : Direction.DOWN;
        }
    }

    // --- YOUR SCAN LOGIC KEPT SAME ---
    private synchronized void processMovement() {
        if (direction == Direction.IDLE) return;

        if (direction == Direction.UP) {
            if (upRequests.contains(currentFloor)) {
                notifyObservers("STOPPING at " + currentFloor);
                upRequests.remove(currentFloor);
            }
            if (!upRequests.tailSet(currentFloor).isEmpty()) {
                currentFloor++;
                notifyObservers("Moving UP to " + currentFloor);
            } else if (!downRequests.isEmpty()) {
                direction = Direction.DOWN;
                notifyObservers("Switching to DOWN");
            } else {
                direction = Direction.IDLE;
                notifyObservers("IDLE at " + currentFloor);
            }
        }
        else if (direction == Direction.DOWN) {
            if (downRequests.contains(currentFloor)) {
                notifyObservers("STOPPING at " + currentFloor);
                downRequests.remove(currentFloor);
            }
            if (!downRequests.headSet(currentFloor).isEmpty()) {
                currentFloor--;
                notifyObservers("Moving DOWN to " + currentFloor);
            } else if (!upRequests.isEmpty()) {
                direction = Direction.UP;
                notifyObservers("Switching to UP");
            } else {
                direction = Direction.IDLE;
                notifyObservers("IDLE at " + currentFloor);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try { Thread.sleep(1000); processMovement(); } catch (Exception e) {}
        }
    }
}

class ElevatorController {
    private List<Elevator> elevators;
    private DispatchStrategy strategy;

    public ElevatorController(List<Elevator> elevators, DispatchStrategy strategy) {
        this.elevators = elevators;
        this.strategy = strategy;
    }

    public void dispatch(int floor) {
        Elevator best = strategy.findBestCar(elevators, floor);
        if (best != null) {
            System.out.println("\n[SYSTEM] Dispatching Floor " + floor + " to " + best.id);
            best.addRequest(floor);
        }
    }
}

public class Solution {
    public static void main(String[] args) throws InterruptedException {
        Elevator e0 = new Elevator("Elevator-0");
        Elevator e1 = new Elevator("Elevator-1");

        // Attach Observers
        ConsoleLogger logger = new ConsoleLogger();
        e0.addObserver(logger);
        e1.addObserver(logger);

        List<Elevator> bank = Arrays.asList(e0, e1);
        ElevatorController controller = new ElevatorController(bank, new EnergySavingStrategy());

        new Thread(e0).start();
        new Thread(e1).start();

        controller.dispatch(5);
        Thread.sleep(2000);
        controller.dispatch(1); // Energy saver will likely pick the car already moving to 5
    }
}
