package LowLevelDesign.CarRental_AtharvWani;

//Register users and cars in the system
//User should be able to search for available vehicles based on names and date range
//User can reserve cars in the date range
//Users can pay through UPI, Credit Card, Debit Card for the associated cost of vehicle

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

enum ReservationStatus{
    NOT_BOOKED, BOOKED
}
enum PayMethods{
    UPI, CREDIT_CARD, DEBIT_CARD
}
interface PaymentStrategy{
    void pay(Reservation reservation);
}
class UPI implements PaymentStrategy{
    @Override
    public void pay(Reservation reservation) {
        System.out.println("Paying using UPI " + reservation.totalCost + "rs");
    }
}
class CreditCard implements PaymentStrategy{
    @Override
    public void pay(Reservation reservation) {
        System.out.println("Paying using Credit Card" + reservation.totalCost + "rs");
    }
}
class DebitCard implements PaymentStrategy{
    @Override
    public void pay(Reservation reservation) {
        System.out.println("Paying using Debit Card" + reservation.totalCost + "rs");
    }
}
class PaymentFactory{
    public static PaymentStrategy create(PayMethods payMethods){
        return switch(payMethods){
            case UPI -> new UPI();
            case DEBIT_CARD -> new DebitCard();
            case CREDIT_CARD -> new CreditCard();
        };
    }
}
class Notification{
    String id;
    String message;
    public Notification(String message){
        this.id = UUID.randomUUID().toString();
        this.message = message;
    }
}
interface NotificationObserver{
    void notify(Notification notification);
}
class User implements NotificationObserver{
    String id;
    String name;
    String email;
    PaymentStrategy paymentStrategy;
    List<Notification> notifications = new ArrayList<>(); // ✅ FIXED (initialized)

    public User(String name,String email){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }
    public void setPaymentStrategy(PayMethods payMethod){
        this.paymentStrategy = PaymentFactory.create(payMethod);
    }

    @Override
    public void notify(Notification notification){
        notifications.add(notification);
        System.out.println("🔔 " + name + ": " + notification.message);
    }
}
class Vehicle{
    String id;
    String model;
    int charge;
    String vehicleNo;
    ReservationStatus reservationStatus;
    public Vehicle(String model,String vehicleNo,int charge){
        this.id = UUID.randomUUID().toString();
        this.model = model;
        this.charge = charge;
        this.vehicleNo = vehicleNo;
        this.reservationStatus = ReservationStatus.NOT_BOOKED;
    }
    @Override
    public String toString(){
        return "[" + this.model + ", " + this.vehicleNo + ", " + this.reservationStatus + "]";
    }
}

class Reservation{
    String id;
    User user;
    Vehicle vehicle;
    LocalDate startTime;
    LocalDate endTime;
    double totalCost;
    public Reservation(User user,Vehicle vehicle,LocalDate start,LocalDate end){
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.vehicle = vehicle;
        this.startTime = start;
        this.endTime = end;
        this.totalCost = ChronoUnit.DAYS.between(start, end) * vehicle.charge;
    }
}
class CarRentalSystem{
    private static volatile CarRentalSystem instance;
    Map<String, Vehicle> vehicles = new HashMap<>();
    Map<String, User> users = new HashMap<>();
    Map<String, Reservation> reservations = new HashMap<>();

    private CarRentalSystem(){}

    public synchronized static CarRentalSystem getInstance(){
        if(instance == null)
            instance = new CarRentalSystem();
        return instance;
    }
    public void addVehicle(Vehicle vehicle){
        System.out.println("Vehicle Added -> ["+vehicle.model+"] with Vehicle No : "+vehicle.vehicleNo);
        vehicles.put(vehicle.id,vehicle);
    }
    public void addUsers(User user){
        System.out.println("User Added -> ["+user.name+"] with Email : "+user.email);
        users.put(user.id,user);
    }

    public Reservation reserve(User user,Vehicle vehicle,LocalDate start,LocalDate end){
        if(vehicle.reservationStatus == ReservationStatus.BOOKED){
            System.out.println("❌ Vehicle already booked!");
            return null;
        }
        vehicle.reservationStatus = ReservationStatus.BOOKED;
        Notification n = new Notification("User ["+user.name+"] your car "+vehicle.model+" is booked!");
        user.notify(n); // ✅ FIXED — only notify this user, not all
        Reservation r = new Reservation(user,vehicle,start,end);
        reservations.put(r.id,r);
        return r;
    }

    private boolean isAvailableInDates(Vehicle vehicle, LocalDate searchStart, LocalDate searchEnd) {
        return reservations.values().stream()
                .filter(r -> r.vehicle.id.equals(vehicle.id)) // only reservations for this vehicle
                .allMatch(r -> searchEnd.isBefore(r.startTime) || searchStart.isAfter(r.endTime));
    }


    public List<Vehicle> searchVehicle(String name, LocalDate searchStart, LocalDate searchEnd){
        return vehicles.values().stream()
                .filter(vehicle -> vehicle.model.equalsIgnoreCase(name) && vehicle.reservationStatus == ReservationStatus.NOT_BOOKED)
                .filter(vehicle -> isAvailableInDates(vehicle, searchStart, searchEnd))
                .collect(Collectors.toList());
    }

}
public class Solution{
    public static void main(String[] args){
        CarRentalSystem carRentalSystem = CarRentalSystem.getInstance();
        User useratharv = new User("Atharv","atharvwani01@gmail.com");
        User userayushi = new User("Ayushi","ayushiwani035@gmail.com");
        Vehicle vehicleinnova = new Vehicle("INNOVA","MH 04 AF 3435",130);
        Vehicle vehiclescorpio = new Vehicle("SCORPIO","MH 04 GN 0988",190);

        carRentalSystem.addUsers(useratharv);
        carRentalSystem.addVehicle(vehicleinnova);
        carRentalSystem.addUsers(userayushi);
        carRentalSystem.addVehicle(vehiclescorpio);

        System.out.println("Searching ....");
        List<Vehicle> searchList = carRentalSystem.searchVehicle("INNOVA", LocalDate.now(), LocalDate.now().plusDays(3));
        for (Vehicle vehicle: searchList){
            System.out.println(vehicle.toString());
        }

        Reservation reservation = carRentalSystem.reserve(useratharv, vehicleinnova, LocalDate.now(), LocalDate.now().plusDays(3));

        useratharv.setPaymentStrategy(PayMethods.UPI);
        useratharv.paymentStrategy.pay(reservation);

    }
}
