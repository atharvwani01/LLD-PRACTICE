package LowLevelDesign.BookMyShow_Atharv;

// When user opens the app he sees a  List of Movies...done
// User can also search for movie ...done
// When user click on a movie he sees which all theaters of the movie ...done
// A movie [m1] can be in [t1, t2, t3], [m2] can also be in [t1 t2] ...done
// User books the theater for a movie(Already has some seats in it) and then selects the available seats and gets the booking id ...done
// Seats can be of any type(Bronze, Silver, Gold) ...done
// Once he selects the seats, proceeds to pay via(UPI, CreditCard, DebitCard) ...done

import java.util.*;

enum SeatType{
    BRONZE, SILVER, GOLD
}
enum SeatStatus{
    BOOKED, AVAILABLE
}

interface PricingStrategy{
    double calcPrice();
}

class BronzePricingStrategy implements PricingStrategy{

    @Override
    public double calcPrice() {
        return 100;
    }
}

class SilverPricingStrategy implements PricingStrategy{

    @Override
    public double calcPrice() {
        return 200;
    }
}
class GoldPricingStrategy implements PricingStrategy{

    @Override
    public double calcPrice() {
        return 300;
    }
}


class SeatFactory{
    public static Seat createSeat(SeatType seatType){
        return switch(seatType){
            case BRONZE -> new Seat(seatType, new BronzePricingStrategy());
            case SILVER -> new Seat(seatType, new SilverPricingStrategy());
            case GOLD -> new Seat(seatType, new GoldPricingStrategy());
        };
    }
}
interface PaymentStrategy{
    void pay(double amount);
}
class UPI implements PaymentStrategy{

    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " via UPI");
    }
}
class CreditCard implements PaymentStrategy{

    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " via Credit Card");
    }
}
class DebitCard implements PaymentStrategy{

    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " via Debit Card");
    }
}
class User{
    String id;
    String name;
    String email;
    PaymentStrategy paymentStrategy;
    public User(String name, String email){
        id = "U_" + UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }
    double bookSeats(List<Seat> bookedSeats){
        double totalcost = 0;
        for(Seat seat: bookedSeats){
            synchronized (seat) {
                if (seat.seatStatus == SeatStatus.BOOKED)
                    throw new RuntimeException("Already booked seats !");
                seat.seatStatus = SeatStatus.BOOKED;
                totalcost += seat.pricingStrategy.calcPrice();
            }
        }
        return totalcost;
    }
}

class Seat{
    String id;
    SeatType seatType;
    PricingStrategy pricingStrategy;
    SeatStatus seatStatus;
    public Seat(SeatType seatType, PricingStrategy pricingStrategy){
        this.id = "S_" + UUID.randomUUID().toString();
        this.seatType = seatType;
        this.pricingStrategy = pricingStrategy;
        this.seatStatus = SeatStatus.AVAILABLE;
    }
}
class Movie{
    String id;
    String name;
    int duration;
    List<Seat> seatsOfMovie;

    public Movie(String name, int duration, List<Seat> seatsOfMovie){
        this.id = "M_" + UUID.randomUUID().toString();
        this.name = name;
        this.duration = duration;
        this.seatsOfMovie = seatsOfMovie;
    }
}
class Theater{
    String id;
    String name;
    String location;
    List<Movie> movies = new ArrayList<>();
    public Theater(String name, String location){
        this.id = "T_" + UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
    }
    public void addMovie(Movie movie){
        movies.add(movie);
    }
}
class Booking{
    String id;
    User user;
    Theater theater;
    Movie movie;
    double totalCost;
    public Booking(User user, Theater theater, Movie movie){
        this.id = "B_" + UUID.randomUUID().toString();
        this.user = user;
        this.theater = theater;
        this.movie = movie;
    }
    double getTotalCost(List<Seat> seats){
        return seats.stream().mapToDouble(seat -> seat.pricingStrategy.calcPrice()).sum();
    }

}
class BookMyShowSystem{
    private static volatile BookMyShowSystem instance;
    Map<String, Movie> movies = new HashMap<>();
    Map<String, Theater> theaters = new HashMap<>();
    Map<String, Booking> bookings = new HashMap<>();    //remembering bookings for history
    private BookMyShowSystem(){

    }
    public synchronized static BookMyShowSystem getInstance(){
        if(instance == null)
            instance = new BookMyShowSystem();
        return instance;
    }
    public void addMovie(Movie movie, Theater theater){
        movies.put(movie.id, movie);
        theater.addMovie(movie);
    }
    public void addTheater(Theater theater){
        theaters.put(theater.id, theater);
    }

    public List<Seat> generateSeats(int numSeats){
        List<Seat> seats = new ArrayList<>();
        int partition = numSeats / 3;
        for(int i = 1; i <= partition; i++){
            seats.add(SeatFactory.createSeat(SeatType.BRONZE));
        }
        for(int i = partition + 1; i <= 2 * partition; i++){
            seats.add(SeatFactory.createSeat(SeatType.SILVER));
        }
        for(int i = 2 * partition + 1 ; i <= numSeats; i++){
            seats.add(SeatFactory.createSeat(SeatType.GOLD));
        }
        return seats;
    }

    public Booking bookSeats(User user, List<Seat> seats, Theater theater, Movie movie){
        double cost = user.bookSeats(seats);
        Booking booking = new Booking(user, theater, movie);
        booking.totalCost = cost;
        System.out.println("Booking done for " + user.name + " with email " + user.email);
        System.out.println("Total seats booked : " + seats.size() + " with cost " + booking.totalCost);
        bookings.put(booking.id, booking);
        return booking;
    }

    public List<Movie> searchMovies(String movieName){
        return movies.values().stream().filter(movie -> movie.name.equalsIgnoreCase(movieName)).toList();
    }

    public List<Theater> searchTheaters(List<Movie> movies){
        return theaters.values().stream().filter(theater -> theater.movies.stream().anyMatch(movies::contains)).toList();
    }

}


public class Solution {
    public static void main(String[] args) {
        BookMyShowSystem bookMyShowSystem = BookMyShowSystem.getInstance();
        List<Seat> seatsForbmg = bookMyShowSystem.generateSeats(90);
        List<Seat> seatsForKrissh = bookMyShowSystem.generateSeats(90);
        Movie moviebmg = new Movie("Bhaag Milkha Bhaag", 240, seatsForbmg);
        Movie moviekrissh = new Movie("Krissh", 180, seatsForKrissh);
        Theater theater1 = new Theater("INOX", "Pune");
        Theater theater2 = new Theater("CINEPOLIS", "Pune");
        bookMyShowSystem.addTheater(theater1);
        bookMyShowSystem.addTheater(theater2);

        bookMyShowSystem.addMovie(moviebmg, theater1);
        bookMyShowSystem.addMovie(moviekrissh, theater1);
        bookMyShowSystem.addMovie(moviebmg, theater2);
        bookMyShowSystem.addMovie(moviekrissh, theater2);


        System.out.println("Searching for movies .... ");
        List<Movie> movies = bookMyShowSystem.searchMovies("Bhaag Milkha Bhaag");
        for (Movie movie: movies){
            System.out.println(movie.name);
        }
        System.out.println("Searching for theaters .... ");
        List<Theater> theaters = bookMyShowSystem.searchTheaters(movies);
        for (Theater theater: theaters){
            System.out.println(theater.name);
        }

        User userath = new User("Atharv", "atharvwani01@hmail.com");
        User userayu = new User("Ayushi", "ayushiwani035@gmail.com");

        List<Seat> userathseats = seatsForbmg.subList(0, 3);
        Booking booking1 = bookMyShowSystem.bookSeats(userath, userathseats, theater1, moviebmg);
        userath.paymentStrategy = new UPI();
        userath.paymentStrategy.pay(booking1.totalCost);


        List<Seat> userayuseats = seatsForbmg.subList(3, 9);
        Booking booking2 = bookMyShowSystem.bookSeats(userayu, userayuseats, theater1, moviebmg);
        userayu.paymentStrategy = new CreditCard();
        userayu.paymentStrategy.pay(booking2.totalCost);

        Thread atharvThread = new Thread(() -> {
            try {
                List<Seat> atharvSeats = new ArrayList<>(seatsForbmg.subList(25, 26)); // copy
                Booking bookingAtharv = bookMyShowSystem.bookSeats(userath, atharvSeats, theater1, moviebmg);
                userath.paymentStrategy = new UPI();
                userath.paymentStrategy.pay(bookingAtharv.totalCost);
            } catch (Exception e) {
                System.out.println("Atharv booking failed: " + e.getMessage());
            }
        });

        Thread ayushiThread = new Thread(() -> {
            try {
                List<Seat> ayushiSeats = new ArrayList<>(seatsForbmg.subList(25, 26)); // copy
                Booking bookingAyushi = bookMyShowSystem.bookSeats(userayu, ayushiSeats, theater1, moviebmg);
                userayu.paymentStrategy = new UPI();
                userayu.paymentStrategy.pay(bookingAyushi.totalCost);
            } catch (Exception e) {
                System.out.println("Ayushi booking failed: " + e.getMessage());
            }
        });

        atharvThread.start();
        ayushiThread.start();

    }
}
