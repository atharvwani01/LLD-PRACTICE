package LowLevelDesign.CarRental.Strategy;

public class DebitCard implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Debit Card payment of amount " + amount + " done !");
        return true;
    }
}
