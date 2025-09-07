package ObserverPattern.good;

public class DriverApp implements Observer {
    @Override
    public void update(String status) {
        System.out.println("Driver App updated with status " + status);
    }
}
