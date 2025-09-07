package ObserverPattern.good;

public class RiderApp implements Observer {
    @Override
    public void update(String status) {
        System.out.println("Rider App updated with status " + status);
    }
}
