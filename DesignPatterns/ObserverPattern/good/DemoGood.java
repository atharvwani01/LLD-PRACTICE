package ObserverPattern.good;

public class DemoGood {
    public static void main(String[] args) {
        Cab cab = new Cab();
        cab.addObserver(new DriverApp());
        cab.addObserver(new RiderApp());
        cab.addObserver(new AnalyticsService());

        cab.setStatus("Not Free");
    }
}
