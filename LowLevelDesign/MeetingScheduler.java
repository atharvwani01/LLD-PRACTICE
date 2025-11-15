package LowLevelDesign.MeetingScheduler;

//MeetingScheduler
//Here we have a Participants, TimeSlot, Meeting, MeetingRoom
//Organiser creates a meeting with a timeslot
//Based on the strategy, a room is selected for the meeting 
//We also check if the participant is already occupied at that slot
//Then finally meeting is scheduled and users get notifications/invites for the meeting

import java.time.LocalDateTime;
import java.util.*;

class Participant implements NotificationObserver {
    String id;
    String name;
    String email;

    public Participant(String name, String email) {
        this.id = "P_" + UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    @Override
    public void notify(String message) {
        System.out.println("Notification to " + name + " (" + email + "): " + message);
    }
}

class TimeSlot {
    LocalDateTime startTime;
    LocalDateTime endTime;

    public TimeSlot(LocalDateTime s, LocalDateTime e) {
        this.startTime = s;
        this.endTime = e;
    }

    public boolean overlaps(TimeSlot other) {
        return startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }
}

class Meeting {
    String id;
    TimeSlot slot;
    Participant organiser;
    List<Participant> participants;
    MeetingRoom meetingRoom;

    public Meeting(Participant organiser, TimeSlot slot, List<Participant> participants) {
        this.id = "M_" + UUID.randomUUID().toString();
        this.organiser = organiser;
        this.slot = slot;
        this.participants = participants;
    }
}

class MeetingRoom {
    String id;
    String name;
    int capacity;
    List<Meeting> meetings = new ArrayList<>();

    public MeetingRoom(String name, int capacity) {
        this.id = "R_" + UUID.randomUUID().toString();
        this.name = name;
        this.capacity = capacity;
    }

    public boolean isAvailable(TimeSlot slot) {
        for (Meeting m : meetings) {
            if (m.slot.overlaps(slot)) return false;
        }
        return true;
    }
}

interface NotificationObserver {
    void notify(String message);
}

class ParticipantRepository {
    private static volatile ParticipantRepository instance;
    private Map<String, Participant> map = new HashMap<>();

    private ParticipantRepository() {}

    public static synchronized ParticipantRepository getInstance() {
        if (instance == null) instance = new ParticipantRepository();
        return instance;
    }

    public void save(Participant p) {
        map.put(p.id, p);
    }
}

class MeetingRoomRepository {
    private static volatile MeetingRoomRepository instance;
    private Map<String, MeetingRoom> map = new HashMap<>();

    private MeetingRoomRepository() {}

    public static synchronized MeetingRoomRepository getInstance() {
        if (instance == null) instance = new MeetingRoomRepository();
        return instance;
    }

    public void save(MeetingRoom room) {
        map.put(room.id, room);
    }

    public Collection<MeetingRoom> findAll() {
        return map.values();
    }
}

class MeetingRepository {
    private static volatile MeetingRepository instance;
    private Map<String, Meeting> map = new HashMap<>();

    private MeetingRepository() {}

    public static synchronized MeetingRepository getInstance() {
        if (instance == null) instance = new MeetingRepository();
        return instance;
    }

    public void save(Meeting meeting) {
        map.put(meeting.id, meeting);
    }

    public Collection<Meeting> findAll() {
        return map.values();
    }
}

interface RoomSelectionStrategy {
    MeetingRoom selectRoom(Collection<MeetingRoom> rooms, TimeSlot slot, int requiredCapacity);
}

class FirstFitStrategy implements RoomSelectionStrategy {
    @Override
    public MeetingRoom selectRoom(Collection<MeetingRoom> rooms, TimeSlot slot, int requiredCapacity) {
        for (MeetingRoom r : rooms) {
            if (r.capacity >= requiredCapacity && r.isAvailable(slot)) return r;
        }
        return null;
    }
}

class BestFitStrategy implements RoomSelectionStrategy {
    @Override
    public MeetingRoom selectRoom(Collection<MeetingRoom> rooms, TimeSlot slot, int requiredCapacity) {
        MeetingRoom best = null;

        for (MeetingRoom r : rooms) {
            if (r.capacity >= requiredCapacity && r.isAvailable(slot)) {
                if (best == null || r.capacity < best.capacity) best = r;
            }
        }
        return best;
    }
}

class SchedulerService {
    private static volatile SchedulerService instance;
    private ParticipantService participantService = ParticipantService.getInstance();
    private SchedulerService() {}

    public static synchronized SchedulerService getInstance() {
        if (instance == null) instance = new SchedulerService();
        return instance;
    }

    private MeetingRoomRepository roomRepo = MeetingRoomRepository.getInstance();
    private MeetingRepository meetingRepo = MeetingRepository.getInstance();

    private RoomSelectionStrategy strategy = new FirstFitStrategy();

    public void setStrategy(RoomSelectionStrategy s) {
        this.strategy = s;
    }

    public Meeting createMeeting(
            Participant organiser,
            TimeSlot slot,
            List<Participant> participants,
            int requiredCapacity
    ) {
        System.out.println("Creating meeting...");
        participantService.checkAvailability(organiser, slot);

        for (Participant p : participants) {
            participantService.checkAvailability(p, slot);
        }

        MeetingRoom room = strategy.selectRoom(roomRepo.findAll(), slot, requiredCapacity);
        if (room == null) throw new RuntimeException("No meeting room available!");

        Meeting meeting = new Meeting(organiser, slot, participants);
        meeting.meetingRoom = room;
        room.meetings.add(meeting);

        meetingRepo.save(meeting);

        sendNotifications(meeting);

        return meeting;
    }

    private void sendNotifications(Meeting meeting) {
        String message = "You have been added to meeting: " + meeting.id +
                " in room: " + meeting.meetingRoom.name +
                " from " + meeting.slot.startTime + " to " + meeting.slot.endTime;

        meeting.organiser.notify("Your meeting " + meeting.id + " is confirmed.");

        for (Participant p : meeting.participants) {
            p.notify(message);
        }
    }
}
class SchedulerController {
    private static volatile SchedulerController instance;
    private SchedulerService schedulerService = SchedulerService.getInstance();

    private SchedulerController() {}

    public static synchronized SchedulerController getInstance() {
        if (instance == null) instance = new SchedulerController();
        return instance;
    }

    public void setStrategy(RoomSelectionStrategy s) {
        schedulerService.setStrategy(s);
    }

    public Meeting createMeeting(Participant organiser, TimeSlot slot, List<Participant> participants, int requiredCapacity) {
        return schedulerService.createMeeting(organiser, slot, participants, requiredCapacity);
    }
}

class ParticipantService {
    private static volatile ParticipantService instance;
    private ParticipantRepository repo = ParticipantRepository.getInstance();
    private MeetingRepository meetingRepo = MeetingRepository.getInstance();

    private ParticipantService() {}

    public static synchronized ParticipantService getInstance() {
        if (instance == null) instance = new ParticipantService();
        return instance;
    }

    public void addParticipant(Participant p) {
        repo.save(p);
    }

    public void checkAvailability(Participant p, TimeSlot slot) {
        for (Meeting m : meetingRepo.findAll()) {

            boolean samePerson =
                    m.organiser.id.equals(p.id) ||
                            m.participants.stream().anyMatch(pp -> pp.id.equals(p.id));

            if (samePerson && m.slot.overlaps(slot)) {
                throw new RuntimeException(
                        "Participant " + p.name + " is already in another meeting at this time!"
                );
            }
        }
    }
}


class ParticipantController {
    private static volatile ParticipantController instance;
    private ParticipantService service = ParticipantService.getInstance();

    private ParticipantController() {}

    public static synchronized ParticipantController getInstance() {
        if (instance == null) instance = new ParticipantController();
        return instance;
    }

    public void addParticipant(Participant p) {
        service.addParticipant(p);
    }
}
public class Solution {
    public static void main(String[] args) {

        MeetingRoomRepository rRepo = MeetingRoomRepository.getInstance();
        SchedulerController controller = SchedulerController.getInstance();
        ParticipantController participantController = ParticipantController.getInstance();

        Participant p1 = new Participant("Atharv", "atharv@gmail.com");
        Participant p2 = new Participant("Ayushi", "ayushi@gmail.com");
        Participant p3 = new Participant("Prashant", "prashant@gmail.com");

        participantController.addParticipant(p1);
        participantController.addParticipant(p2);
        participantController.addParticipant(p3);

        rRepo.save(new MeetingRoom("Room-A", 4));
        rRepo.save(new MeetingRoom("Room-B", 10));

        LocalDateTime now = LocalDateTime.now();
        TimeSlot slot = new TimeSlot(now.plusHours(1), now.plusHours(2));

        controller.setStrategy(new BestFitStrategy());

        Meeting m1 = controller.createMeeting(p1, slot, Arrays.asList(p2, p3), 3);
        Meeting m2 = controller.createMeeting(p1, slot, Arrays.asList(p2, p3), 3);

        System.out.println("\nAssigned Room = " + m1.meetingRoom.name);
    }
}
