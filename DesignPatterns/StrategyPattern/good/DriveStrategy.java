package good;

//Now for each class type or behaviour i define a strategy, Alright, i will do it now
interface DriveStrategy {
    void drive(Stats s);
}

// Minimal mutable state object we pass to strategies
// I will use this for my drive method
class Stats {
    double baseSpeedKph = 80;
    double tirePressurePsi = 32;
    double fuelLiters = 40;
}
