package LowLevelDesign.CarRental.Strategy;

public class CreditCard implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Credit Card payment of amount " + amount + " done !");
        return true;
    }
}
