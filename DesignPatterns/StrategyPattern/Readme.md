# 🚗 Strategy Pattern Story — Vehicles Edition

Once upon a time, we had a few vehicles in our codebase:

- **SportsVehicle** (fast driving)
- **OffRoadVehicle** (rough terrain driving)
- **PassengerVehicle** (normal driving)
- **LuxuryVehicle** (also fast driving)

Each one had its own `drive()` method, and life felt simple.

---

## 🌩️ The Trouble Begins

At first it looked fine, but cracks started to show:

1. **Code duplication**  
   Sports and Luxury both wanted “fast drive.” We just… copied the same 4–5 lines of logic.  
   Then we realized if “fast drive” ever changes (say, add logging), we’d have to edit it in *every place we pasted it*.

2. **Rigid hierarchy**  
   One day we said: “What if a PassengerVehicle (like a Tesla Plaid) should also drive fast?”  
   Our only choices were:
    - make a `FastPassengerVehicle` subclass, or
    - override `drive()` and paste the “fast” logic again.  
      Both felt ugly.

3. **Closed for change**  
   Every time we wanted a new mode (eco mode, snow mode, etc.), we had to crack open old classes and edit them. That’s fragile.

4. **Single Responsibility broken**  
   Our `Vehicle` subclasses were doing two jobs:
    - being a *type* of vehicle
    - knowing *how to drive*  
      Classes were bloated and tests were messy.

It became obvious: **type and behavior are different things.**

---

## 💡 The Insight

Driving style is not tied to the *kind* of vehicle.  
It’s a behavior. A strategy.

So why not separate **what the vehicle is** from **how it drives**?

---

## 🛠️ The Refactor — Enter Strategy Pattern

We introduced an interface:

```java
interface DriveStrategy {
    void drive(Stats s);
}
```

Then we created small classes for each driving style:

NormalDriveStrategy

SportsDriveStrategy

OffRoadDriveStrategy

EcoDriveStrategy

Each one had its own tiny logic: calculate speed, adjust fuel, tweak tire PSI.

Now our vehicles just compose a strategy:
```java
class Vehicle {
    private DriveStrategy strategy;
    private final Stats stats = new Stats();

    public Vehicle(DriveStrategy strategy) {
        this.strategy = strategy;
    }

    public void drive() {
        strategy.drive(stats);
    }

    // optional: runtime swap
    public void setDriveStrategy(DriveStrategy s) {
        this.strategy = s;
    }
}
```

And types became clean:

```java
class SportsVehicle extends Vehicle {
    public SportsVehicle() { super(new SportsDriveStrategy()); }
}

class PassengerVehicle extends Vehicle {
    public PassengerVehicle() { super(new NormalDriveStrategy()); }
}

class LuxuryVehicle extends Vehicle {
    public LuxuryVehicle() { super(new SportsDriveStrategy()); } // reuse
}
```
✨ The Payoff
No more duplication
Sports & Luxury both point to SportsDriveStrategy. “Fast drive” logic exists once.

No rigid hierarchy
Want a fast passenger car? Just:

```java
passenger.setDriveStrategy(new SportsDriveStrategy());
```
No subclass explosion.

Open for extension, closed for modification
Adding Eco mode? Just create EcoDriveStrategy. No need to edit existing vehicles.

SRP respected
Vehicle = type.
Strategy = behavior.
Classes shrank, testing became easy.

Runtime flexibility
You can flip modes per instance at runtime. That’s runtime polymorphism in action.

🧠 How to Remember (the “story in my head”)
Before: Every subclass knew how to drive. Behaviors were welded into the hierarchy.

After: Vehicles don’t drive themselves; they ask a strategy to drive.

Key mental model: Type vs. behavior are separate concerns.

🗺️ Quick Visual
```java
Vehicle ---- has-a ----> DriveStrategy
   |                           |
   |                           |
SportsVehicle         SportsDriveStrategy
OffRoadVehicle        OffRoadDriveStrategy
PassengerVehicle      NormalDriveStrategy
LuxuryVehicle         (reuses SportsDriveStrategy)
```


✅ Moral of the Story
Whenever we see:

repeated logic across subclasses,

subclass explosion just to tweak behavior,

or classes that mix type + behavior in one place…

👉 Extract the behavior into a Strategy.