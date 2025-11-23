package LowLevelDesign.DoctorAppointment;


import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

//enums
enum Specialization{
    CARDIOLOGIST, DERMATOLOGIST, ORTHOPEDIC, GENERAL_PHYSICIAN
}

//exceptions
class BookingNotFoundException extends RuntimeException{
    public BookingNotFoundException(String message){
        super(message);
    }
}
class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(String message){
        super(message);
    }
}
class DoctorNotFoundException extends RuntimeException{
    public DoctorNotFoundException(String message){
        super(message);
    }
}

//models
class Doctor{
    String id;
    String name;
    Specialization specialization;
    @Setter
    Map<String, Boolean> availability = new HashMap<>();
    double rating;

    public Doctor(String name, Specialization specialization, double rating){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.specialization = specialization;
        this.rating = rating;
    }
}

class Patient{
    String id;
    @Setter
    String name;
    public Patient(String name){
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}

class Booking{
    String id;
    String patientId;
    String doctorId;
    String slot;

    public Booking(String patientId, String doctorId, String slot){
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.slot = slot;
    }
}

//Repositories
class DoctorRepository{
    private Map<String, Doctor> doctorMap = new HashMap<>();

    public void save(Doctor doctor){
        doctorMap.put(doctor.id, doctor);
    }
    public Doctor findDoctorById(String id){
        return doctorMap.get(id);
    }
    public List<Doctor> findBySpecialization(Specialization specialization){
        return doctorMap.values().stream().filter(doctor -> doctor.specialization == specialization).collect(Collectors.toList());
    }
}

class PatientRepository{
    private Map<String, Patient> patientMap = new HashMap<>();

    public void save(Patient patient){
        patientMap.put(patient.id, patient);
    }
    public Patient findPatientById(String id){
        return patientMap.get(id);
    }
}

class BookingRepository{
    private Map<String, Booking> bookingMap = new HashMap<>();
    private Map<String, Queue<String>> waitList = new HashMap<>();

    public void save(Booking booking){
        bookingMap.put(booking.id, booking);
    }
    public Booking findBookingById(String id){
        return bookingMap.get(id);
    }
    public void delete(Booking booking){
        bookingMap.remove(booking.id);
    }
    public List<Booking> findByDoctorId(String doctorId){
        return bookingMap.values().stream().filter(booking -> Objects.equals(booking.doctorId, doctorId)).collect(Collectors.toList());
    }
    public List<Booking> findByPatientId(String patientId){
        return bookingMap.values().stream().filter(booking -> Objects.equals(booking.patientId, patientId)).collect(Collectors.toList());
    }
    public void addToWaitList(String doctorSlotKey, String patientId){
        waitList.putIfAbsent(doctorSlotKey, new LinkedList<>());
        waitList.get(doctorSlotKey).offer(patientId);
    }
    public String popFromWaitList(String doctorSlotKey){
        Queue<String> queue = waitList.get(doctorSlotKey);
        return (queue != null) ? queue.poll() : null;
    }
}


//Strategies

@AllArgsConstructor
class DoctorSlot{
    Doctor doctor;
    String slot;
}

interface SlotRankStrategy{
    public List<DoctorSlot> rank(List<DoctorSlot> slots);
}

class StartTimeStrategy implements SlotRankStrategy{
    @Override
    public List<DoctorSlot> rank(List<DoctorSlot> slots) {
        slots.sort(Comparator.comparing(doctorSlot -> {
            String timeString = doctorSlot.slot;
            return LocalTime.parse(timeString);
        }));
        return slots;
    }
}

class RatingBasedStrategy implements SlotRankStrategy{
    @Override
    public List<DoctorSlot> rank(List<DoctorSlot> slots) {
        slots.sort((a, b) -> Double.compare(b.doctor.rating, a.doctor.rating));
        return slots;
    }
}

//Services
@AllArgsConstructor
class PatientService{
    private final PatientRepository repo;

    public Patient register(String name){
        Patient patient = new Patient(name);
        repo.save(patient);
        return patient;
    }
    public Patient findById(String id){
        Patient patient = repo.findPatientById(id);
        if (patient == null)
            throw new PatientNotFoundException("Patient Not Found");
        return patient;
    }
}

@AllArgsConstructor
class DoctorService{
    private final DoctorRepository repo;

    public Doctor register(String name, Specialization specialization, double rating){
        Doctor doctor = new Doctor(name, specialization, rating);
        repo.save(doctor);
        return doctor;
    }
    public void declareAvailability(String doctorId, List<String> slots){
        Doctor doctor = repo.findDoctorById(doctorId);
        if (doctor == null)
            throw new DoctorNotFoundException("Doctor Not Found");
        doctor.setAvailability(slots.stream().collect(Collectors.toMap(slot -> slot, slot -> Boolean.TRUE)));
    }
}

@AllArgsConstructor
class BookingService{
    private final BookingRepository bookingRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    public List<DoctorSlot> search(Specialization specialization, SlotRankStrategy strategy){
        List<Doctor> doctors = doctorRepo.findBySpecialization(specialization);
        List<DoctorSlot> doctorSlots =  doctors.stream().flatMap(doctor -> doctor.availability.keySet().stream().map(slot -> new DoctorSlot(doctor, slot))).collect(Collectors.toList());
        return strategy.rank(doctorSlots);
    }

    public Booking book(String patientId, String doctorId, String slot){
        Doctor doctor = doctorRepo.findDoctorById(doctorId);
        Map<String, Boolean> availability = doctor.availability;

        if (!availability.containsKey(slot)){
            throw new RuntimeException("Invalid Slot ! ");
        }

        for (Booking b : bookingRepo.findByPatientId(patientId)){
            if (Objects.equals(b.slot, slot)) {
                throw new RuntimeException("Patient Already has a booking !");
            }
        }

        if(availability.get(slot)){
            Booking booking = new Booking(patientId, doctorId, slot);
            bookingRepo.save(booking);
            availability.put(slot, false);
            System.out.println("\n" + "Booking confirmed " + patientRepo.findPatientById(patientId).name + " and the assigned slot is " + slot + " with doctor " + doctorRepo.findDoctorById(doctorId).name + " ! ");
            return booking;
        }
        else{
            String key = doctorId + "-" + slot;
            bookingRepo.addToWaitList(key, patientId);
            System.out.println("\nSlot already booked, added to waitlist !");
        }

        return null;
    }

    public void cancel(String id){
        Booking booking = bookingRepo.findBookingById(id);
        if (booking == null)
            throw new BookingNotFoundException("Booking not found");

        Doctor doctor = doctorRepo.findDoctorById(booking.doctorId);
        doctor.availability.put(booking.slot, true);
        bookingRepo.delete(booking);

        System.out.println("Booking cancelled !");

        String key = doctor.id + "-" + booking.slot;
        String nextPatient = bookingRepo.popFromWaitList(key);
        if (nextPatient != null){
            book(nextPatient, doctor.id, booking.slot);
        }
    }

    public List<Booking> viewBookingsByDoctor(String doctorId){
        return bookingRepo.findByDoctorId(doctorId);
    }
    public List<Booking> viewBookingsByPatient(String patientId){
        return bookingRepo.findByPatientId(patientId);
    }
}

public class Solution {
    public static void main(String[] args) {
        DoctorRepository doctorRepository = new DoctorRepository();
        PatientRepository patientRepository = new PatientRepository();
        BookingRepository bookingRepository = new BookingRepository();

        DoctorService doctorService = new DoctorService(doctorRepository);
        PatientService patientService = new PatientService(patientRepository);
        BookingService bookingService = new BookingService(bookingRepository, patientRepository, doctorRepository);

        SlotRankStrategy slotRankStrategy = new RatingBasedStrategy();

        Doctor curious = doctorService.register("Curious", Specialization.CARDIOLOGIST, 4.5);
        Doctor dreadful = doctorService.register("Dreadful", Specialization.CARDIOLOGIST, 3.8);
        Doctor daring = doctorService.register("Daring", Specialization .DERMATOLOGIST, 4.2);

        doctorService.declareAvailability(curious.id, List.of("9:30", "12:30", "16:00"));
        doctorService.declareAvailability(dreadful.id, List.of("12:30", "13:00"));


        Patient p1 = patientService.register( "Atharv");
        Patient p2 = patientService.register("Ayushi");

        System.out.println("Available Cardiologist slots:");
        List<DoctorSlot> slots = bookingService.search(Specialization.CARDIOLOGIST, slotRankStrategy);
        for (DoctorSlot slot : slots) {
            System.out.println(slot.doctor.name + " - " + slot.slot);
        }

        Booking b1 = bookingService.book(p1.id, curious.id, "12:30");

        System.out.println("\nDoctor Curious bookings:");
        for (Booking b : bookingService.viewBookingsByDoctor(curious.id)) {
            System.out.println("Booking: Patient ID " + patientService.findById(b.patientId).name);
        }

        try {
            Booking b2 = bookingService.book(p2.id, curious.id, "12:30");
        } catch (Exception e) {
            System.out.println("\nPatient 2 waitlisted: " + e.getMessage());
        }

        bookingService.cancel(b1.id);

        System.out.println("\nDoctor Curious bookings:");
        for (Booking b : bookingService.viewBookingsByDoctor(curious.id)) {
            System.out.println("Booking: Patient ID " + patientService.findById(b.patientId).name);
        }

    }
}
