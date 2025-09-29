package LowLevelDesign.CarRental.Entities;

import LowLevelDesign.CarRental.Strategy.CreditCard;
import LowLevelDesign.CarRental.Strategy.PaymentStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CarRentalSystem {

    private static CarRentalSystem instance;
    private final Map<String, Car> cars;
    private final Map<String, Reservation> reservations;
    private final PaymentStrategy paymentStrategy;

    private CarRentalSystem() {
        cars = new ConcurrentHashMap<>();
        reservations = new ConcurrentHashMap<>();
        paymentStrategy = new CreditCard();
    }

    public static synchronized CarRentalSystem getInstance() {
        if(instance == null){
            instance = new CarRentalSystem();
        }
        return instance;
    }

    public void addCar(Car car) {
        cars.put(car.getLicensePlate(), car);
    }

    public void removeCar(Car car) {
        cars.remove(car.getLicensePlate());
    }

    public List<Car> searchCars(String make, String model, LocalDate startDate, LocalDate endDate) {
        List<Car> availableCars = new ArrayList<>();
        for(Car car : cars.values()){
            if(car.getMake().equalsIgnoreCase(make) && car.getModel().equalsIgnoreCase(model) && car.isAvailable()){
                if(isCarAvailable(car, startDate, endDate)){
                    availableCars.add(car);
                }
            }
        }
        return availableCars;
    }

    private boolean isCarAvailable(Car car, LocalDate startDate, LocalDate endDate){
        for(Reservation reservation : reservations.values()){
            if(reservation.getCar().equals(car)){
                if (startDate.isBefore(reservation.getEndDate()) && endDate.isAfter(reservation.getStartDate())) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized Reservation makeReservation(Customer customer, Car car, LocalDate startDate, LocalDate endDate) {
        if(isCarAvailable(car, startDate, endDate)){
            String reservationId = UUID.randomUUID().toString();
            Reservation reservation = new Reservation(reservationId, customer, car, startDate, endDate);
            reservations.put(reservationId, reservation);
            car.setAvailable(false);
            return reservation;
        }
        return null;
    }

    public synchronized void cancelReservation(String reservationId) {
        Reservation reservation = reservations.remove(reservationId);
        if (reservation != null) {
            reservation.getCar().setAvailable(true);
        }
    }

    public boolean processPayment(Reservation reservation) {
        return paymentStrategy.processPayment(reservation.getTotalPrice());
    }

}
