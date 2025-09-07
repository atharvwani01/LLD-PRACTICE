package ObserverPattern.good;

public class AnalyticsService implements Observer {
    @Override
    public void update(String status) {
        System.out.println("Analytics App updated with status " + status);
    }
}
