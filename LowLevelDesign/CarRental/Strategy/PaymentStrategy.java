package LowLevelDesign.CarRental.Strategy;

public interface PaymentStrategy {
    boolean processPayment(double amount);
}
