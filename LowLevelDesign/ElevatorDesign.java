import java.util.*;
import java.util.concurrent.*;

enum Direction { UP, DOWN }

class Request {
    Direction direction;
    int floor;
    public Request(Direction direction, int floor) {
        this.direction = direction;
        this.floor = floor;
    }
}

interface ElevatorState {
    void move(Request request, Elevator elevator);
}

class MovingUpState implements ElevatorState {
    @Override
    public void move(Request request, Elevator elevator) {
        int current = elevator.currFloor;
        int destination = request.floor;
        elevator.currentDirection = Direction.UP;

        System.out.println("🚀 " + elevator.name + " moving UP from " + current + " to " + destination);
        while (current < destination) {
            try { Thread.sleep(1000); } catch (InterruptedException e) { return; }
            elevator.currFloor = ++current;
            System.out.println(elevator.name + " at floor " + current);
        }
        System.out.println("✅ " + elevator.name + " reached " + destination);
    }
}

class MovingDownState implements ElevatorState {
    @Override
    public void move(Request request, Elevator elevator) {
        int current = elevator.currFloor;
        int destination = request.floor;
        elevator.currentDirection = Direction.DOWN;

        System.out.println("⬇️ " + elevator.name + " moving DOWN from " + current + " to " + destination);
        while (current > destination) {
            try { Thread.sleep(1000); } catch (InterruptedException e) { return; }
            elevator.currFloor = --current;
            System.out.println(elevator.name + " at floor " + current);
        }
        System.out.println("✅ " + elevator.name + " reached " + destination);
    }
}

// ======================================
// STRATEGY PATTERN (REQUEST ASSIGNMENT)
// ======================================
interface RequestAssignmentStrategy {
    void assign(Elevator elevator, Request request);
}

class IdleAssignmentStrategy implements RequestAssignmentStrategy {
    @Override
    public void assign(Elevator elevator, Request request) {
        if (request.floor > elevator.currFloor) {
            elevator.currentDirection = Direction.UP;
            elevator.upQueue.offer(request);
        } else {
            elevator.currentDirection = Direction.DOWN;
            elevator.downQueue.offer(request);
        }
        System.out.println("📨 " + elevator.name + " (IDLE) assigned to floor " + request.floor);
        elevator.notifyElevator();
    }
}

class UpAssignmentStrategy implements RequestAssignmentStrategy {
    @Override
    public void assign(Elevator elevator, Request request) {
        if (request.floor >= elevator.currFloor) {
            elevator.upQueue.offer(request);
            System.out.println("📥 " + elevator.name + " added mid-trip UP request → " + request.floor);
        } else {
            System.out.println("❌ " + elevator.name + " ignored DOWN request " + request.floor);
        }
    }
}

class DownAssignmentStrategy implements RequestAssignmentStrategy {
    @Override
    public void assign(Elevator elevator, Request request) {
        if (request.floor <= elevator.currFloor) {
            elevator.downQueue.offer(request);
            System.out.println("📥 " + elevator.name + " added mid-trip DOWN request → " + request.floor);
        } else {
            System.out.println("❌ " + elevator.name + " ignored UP request " + request.floor);
        }
    }
}

class Elevator implements Runnable {
    String name;
    int currFloor = 0;
    Direction currentDirection = null;
    ElevatorState elevatorState;
    boolean busy = false;

    final PriorityBlockingQueue<Request> upQueue =
            new PriorityBlockingQueue<>(10, Comparator.comparingInt(r -> r.floor));
    final PriorityBlockingQueue<Request> downQueue =
            new PriorityBlockingQueue<>(10, (r1, r2) -> Integer.compare(r2.floor, r1.floor));

    public Elevator(String name) { this.name = name; }

    public synchronized boolean isIdle() {
        return !busy && upQueue.isEmpty() && downQueue.isEmpty();
    }

    public synchronized void assignRequest(Request request) {
        busy = true;

        RequestAssignmentStrategy strategy;
        if (currentDirection == null)
            strategy = new IdleAssignmentStrategy();
        else if (currentDirection == Direction.UP)
            strategy = new UpAssignmentStrategy();
        else 
            strategy = new DownAssignmentStrategy();

        strategy.assign(this, request);
    }

    public synchronized void notifyElevator() {
        notify();
    }

    @Override
    public void run() {
        System.out.println("▶️ " + name + " started");
        while (true) {
            synchronized (this) {
                while (upQueue.isEmpty() && downQueue.isEmpty()) {
                    busy = false;
                    currentDirection = null;
                    try { wait(); } catch (InterruptedException e) { return; }
                }
            }

            // Process requests by direction
            if (currentDirection == Direction.UP) {
                while (!upQueue.isEmpty()) {
                    Request next = upQueue.poll();
                    elevatorState = new MovingUpState();
                    System.out.println("🟢 " + name + " serving floor " + next.floor + " (UP)");
                    elevatorState.move(next, this);
                }
                if (!downQueue.isEmpty()) currentDirection = Direction.DOWN;
            }

            if (currentDirection == Direction.DOWN) {
                while (!downQueue.isEmpty()) {
                    Request next = downQueue.poll();
                    elevatorState = new MovingDownState();
                    System.out.println("🟢 " + name + " serving floor " + next.floor + " (DOWN)");
                    elevatorState.move(next, this);
                }
                if (!upQueue.isEmpty()) currentDirection = Direction.UP;
            }
        }
    }
}

class ElevatorSystem {
    private static ElevatorSystem instance;
    private final List<Elevator> elevators = new ArrayList<>();
    private final Queue<Request> pendingRequests = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private ElevatorSystem() {}

    public static synchronized ElevatorSystem getInstance() {
        if (instance == null) instance = new ElevatorSystem();
        return instance;
    }

    public void addElevator(Elevator elevator) {
        elevators.add(elevator);
        executor.submit(elevator);
    }

    public void addRequest(Request request) {
        pendingRequests.add(request);
        processRequests();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    private synchronized void processRequests() {
        while (!pendingRequests.isEmpty()) {
            Request req = pendingRequests.poll();
            Elevator best = findBestElevator(req);

            if (best != null) best.assignRequest(req);
            else {
                System.out.println("⚠️ No elevator available for " + req.floor + " — requeued");
                pendingRequests.add(req);
                break;
            }
        }
    }

    private Elevator findBestElevator(Request req) {
        Elevator best = null;
        int minScore = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            if (!canTakeRequest(e, req)) continue;
            int score = Math.abs(e.currFloor - req.floor);
            score += e.upQueue.size() + e.downQueue.size(); // penalty by queue load
            if (score < minScore) {
                minScore = score;
                best = e;
            }
        }
        return best;
    }

    private boolean canTakeRequest(Elevator e, Request req) {
        if (e.isIdle()) return true;
        if (e.currentDirection == Direction.UP)
            return req.floor >= e.currFloor;
        if (e.currentDirection == Direction.DOWN)
            return req.floor <= e.currFloor;
        return false;
    }
}

public class Solution {
    public static void main(String[] args) throws InterruptedException {
        ElevatorSystem system = ElevatorSystem.getInstance();

        system.addElevator(new Elevator("Elevator A"));
        system.addElevator(new Elevator("Elevator B"));
        system.addElevator(new Elevator("Elevator C"));

        system.addRequest(new Request(Direction.UP, 8));
        system.addRequest(new Request(Direction.UP, 2));
        system.addRequest(new Request(Direction.UP, 5));
        system.addRequest(new Request(Direction.UP, 3));
        system.addRequest(new Request(Direction.UP, 1));
        system.addRequest(new Request(Direction.UP, 9));
        system.addRequest(new Request(Direction.UP, 7));
        system.addRequest(new Request(Direction.UP, 4));

        Thread.sleep(30000);
        System.exit(0);
    }
}
