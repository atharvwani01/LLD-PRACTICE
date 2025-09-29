package LowLevelDesign.CarRental.Strategy;

public class UPI implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("UPI payment of amount " + amount + " done !");
        return true;
    }
}
