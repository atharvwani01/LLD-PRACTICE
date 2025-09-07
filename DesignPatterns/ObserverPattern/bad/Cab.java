package ObserverPattern.bad;

public class Cab {
    private String status = "Free";
    void setStatus(String status) {
        this.status = status;
        //notify the interested parties manually
        notifyDrivers();
        notifyRiders();
    }
    void notifyDrivers(){
        System.out.println("DriverAPP: The Cab Status is " + status);
    }
    void notifyRiders(){
        System.out.println("RiderAPP: The Cab Status is " + status);
    }
}
