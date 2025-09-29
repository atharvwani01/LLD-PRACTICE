package LowLevelDesign.ElevatorDesign.Entities;

import LowLevelDesign.ElevatorDesign.Enums.Direction;
import LowLevelDesign.ElevatorDesign.Observer.ElevatorObserver;
import LowLevelDesign.ElevatorDesign.State.ElevatorState;
import LowLevelDesign.ElevatorDesign.State.IdleState;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Elevator implements Runnable{

    private final int id;
    private AtomicInteger currentFloor;
    private ElevatorState state;
    private volatile boolean isRunning;

    private final TreeSet<Integer> upRequests;
    private final TreeSet<Integer> downRequests;

    private final List<ElevatorObserver> observers = new ArrayList<>();

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = new AtomicInteger(1);
        this.upRequests = new TreeSet<>();
        this.downRequests = new TreeSet<>((a,b) -> b - a);
        this.state = new IdleState();
    }

    public void addObserver(ElevatorObserver elevatorObserver){
        observers.add(elevatorObserver);
        elevatorObserver.update(this);
    }

    public void notifyObservers() {
        for (ElevatorObserver observer : observers) {
            observer.update(this);
        }
    }


    public void setState(ElevatorState elevatorState){
        this.state = elevatorState;
        notifyObservers();
    }

    public void move() {
        state.move(this);
    }

    // --- Request Handling ---
    public synchronized void addRequest(Request request) {
        System.out.println("Elevator " + id + " processing: " + request);
        state.addRequest(this, request);
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }
    public int getCurrentFloor() {
        return currentFloor.get();
    }

    public void setCurrentFloor(int floor) {
        this.currentFloor.set(floor);
        notifyObservers(); // Notify observers on floor change
    }

    public Direction getDirection() {
        return state.getDirection();
    }
    public TreeSet<Integer> getUpRequests() {
        return upRequests;
    }
    public TreeSet<Integer> getDownRequests() {
        return downRequests;
    }
    public boolean isRunning() {
        return isRunning;
    }
    public void stopElevator() {
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            System.out.println("Elevator " + id + " running - Current State: " +
                    state.getClass().getSimpleName() +
                    " - Up Requests: " + upRequests.size() +
                    " - Down Requests: " + downRequests.size());
            move();
            try {
                Thread.sleep(1000); // Simulate movement time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isRunning = false;
            }
        }
    }
}