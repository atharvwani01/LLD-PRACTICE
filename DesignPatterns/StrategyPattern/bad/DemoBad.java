package bad;


public class DemoBad {
    public static void main(String[] args) {
        Vehicle s = new SportsVehicle();    // fast (dup logic)
        Vehicle l = new LuxuryVehicle();    // fast (dup logic)
        Vehicle o = new OffRoadVehicle();   // off-road logic
        Vehicle p = new PassengerVehicle(); // normal logic

        s.drive();
        l.drive();
        o.drive();
        p.drive();
    }
}
