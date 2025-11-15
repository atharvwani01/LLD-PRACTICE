package LowLevelDesign.AmazonLocker;

import java.util.*;
//Locker System 
//Multiple lockers with different sizes
//A locker would have a package in it which will be delivered by the agent.
//Customer whos got the notification after delivery would enter the secret pin and open the locker and collect and collect the package.

enum Size {
    SMALL, MEDIUM, BIG
}

class User {
    String id;
    String name;
    String email;

    public User(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }
}

class Agent {
    String id;
    String name;
    String email;

    public Agent(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }
}

class Package {
    String id;
    User owner;
    Size packageSize;

    public Package(User owner, Size packageSize) {
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.packageSize = packageSize;
    }
}

/* ============================================================
               STATE PATTERN FOR LOCKER PICKUP
   ============================================================ */

interface LockerState {
    void enterOTP(Locker locker, String otp);
    void openLocker(Locker locker);
}

class IdleState implements LockerState {
    @Override
    public void enterOTP(Locker locker, String otp) {
        if (locker.otp != null && locker.otp.equals(otp)) {
            System.out.println("OTP correct! Locker unlocked.");
            locker.setState(new AuthenticatedState());
        } else {
            System.out.println("Incorrect OTP. Locker stays locked.");
        }
    }

    @Override
    public void openLocker(Locker locker) {
        System.out.println("Cannot open. Authenticate first using OTP.");
    }
}

class AuthenticatedState implements LockerState {
    @Override
    public void enterOTP(Locker locker, String otp) {
        System.out.println("Already authenticated. Please open the locker.");
    }

    @Override
    public void openLocker(Locker locker) {
        System.out.println("Locker opened! Package collected.");
        locker.currPackage = null;
        locker.otp = null;

        locker.setState(new IdleState());
        System.out.println("Locker returned to Idle State.");
    }
}

class Locker {
    String id;
    Size size;
    Package currPackage;
    String otp;
    LockerState state;

    public Locker(Size size) {
        this.id = UUID.randomUUID().toString();
        this.size = size;
        this.state = new IdleState();
    }

    public void setState(LockerState state) {
        this.state = state;
    }

    public void enterOTP(String otp) {
        state.enterOTP(this, otp);
    }

    public void openLocker() {
        state.openLocker(this);
    }
}

class LockerBank {
    String id;
    List<Locker> lockers = new ArrayList<>();

    public LockerBank() {
        this.id = UUID.randomUUID().toString();
    }

    public void addLocker(Locker locker) {
        lockers.add(locker);
    }
}

class UserRepository {
    private static volatile UserRepository instance;
    private Map<String, User> users = new HashMap<>();

    private UserRepository() {}

    public synchronized static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    public void save(User user) {
        users.put(user.id, user);
    }
}

class AgentRepository {
    private static volatile AgentRepository instance;
    private Map<String, Agent> agents = new HashMap<>();

    private AgentRepository() {}

    public synchronized static AgentRepository getInstance() {
        if (instance == null) instance = new AgentRepository();
        return instance;
    }

    public void save(Agent agent) {
        agents.put(agent.id, agent);
    }

    public Agent find(String id) {
        return agents.get(id);
    }
}

class PackageRepository {
    private static volatile PackageRepository instance;
    private Map<String, Package> packages = new HashMap<>();

    private PackageRepository() {}

    public synchronized static PackageRepository getInstance() {
        if (instance == null) instance = new PackageRepository();
        return instance;
    }

    public void save(Package pkg) {
        packages.put(pkg.id, pkg);
    }
}

class LockerRepository {
    private static volatile LockerRepository instance;
    Map<String, LockerBank> lockerBanks = new HashMap<>();

    private LockerRepository() {}

    public synchronized static LockerRepository getInstance() {
        if (instance == null) instance = new LockerRepository();
        return instance;
    }

    public void save(LockerBank lockerBank) {
        lockerBanks.put(lockerBank.id, lockerBank);
    }

    public LockerBank find(String id) {
        return lockerBanks.get(id);
    }
}


interface LockerSelectionStrategy {
    Locker selectLocker(Package pkg, LockerBank lockerBank);
}

class NearestAvailableLockerStrategy implements LockerSelectionStrategy {
    @Override
    public Locker selectLocker(Package pkg, LockerBank lockerBank) {
        for (Locker locker : lockerBank.lockers) {
            if (locker.currPackage == null && locker.size == pkg.packageSize) {
                return locker;
            }
        }
        return null;
    }
}

class FarthestAvailableLockerStrategy implements LockerSelectionStrategy {
    @Override
    public Locker selectLocker(Package pkg, LockerBank lockerBank) {
        for (int i = lockerBank.lockers.size() - 1; i >= 0; i--) {
            Locker locker = lockerBank.lockers.get(i);
            if (locker.currPackage == null && locker.size == pkg.packageSize) {
                return locker;
            }
        }
        return null;
    }
}


interface NotificationObserver {
    void notifyUser(User user, Package pkg, Locker locker);
}

class EmailNotificationService implements NotificationObserver {
    @Override
    public void notifyUser(User user, Package pkg, Locker locker) {
        System.out.println("\n----- EMAIL NOTIFICATION -----");
        System.out.println("Hello " + user.name + ",");
        System.out.println("Your package has been assigned!");
        System.out.println("Locker ID: " + locker.id);
        System.out.println("OTP: " + locker.otp);
        System.out.println("----------------------------------\n");
    }
}


class LockerService {
    private static volatile LockerService instance;
    private LockerRepository lockerRepository = LockerRepository.getInstance();

    private LockerService() {}

    public synchronized static LockerService getInstance() {
        if (instance == null) instance = new LockerService();
        return instance;
    }

    public LockerBank addLockerBank(int size, int pSmall, int pMedium, int pBig) {
        LockerBank bank = new LockerBank();
        for (int i = 0; i < size; i++) {
            if (i < pSmall)
                bank.addLocker(new Locker(Size.SMALL));
            else if (i < pSmall + pMedium)
                bank.addLocker(new Locker(Size.MEDIUM));
            else
                bank.addLocker(new Locker(Size.BIG));
        }
        lockerRepository.save(bank);
        return bank;
    }
}

class UserService {
    private static volatile UserService instance;
    private UserRepository repo = UserRepository.getInstance();

    private UserService() {}

    public synchronized static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    public void addUser(User user) {
        repo.save(user);
    }
}

class AgentService {
    private static volatile AgentService instance;

    private AgentRepository agentRepo = AgentRepository.getInstance();
    private LockerRepository lockerRepo = LockerRepository.getInstance();
    private PackageRepository pkgRepo = PackageRepository.getInstance();

    private List<NotificationObserver> observers = new ArrayList<>();

    private AgentService() {}

    public synchronized static AgentService getInstance() {
        if (instance == null) instance = new AgentService();
        return instance;
    }

    public void registerObserver(NotificationObserver obs) {
        observers.add(obs);
    }

    private void notifyAll(User user, Package pkg, Locker locker) {
        for (NotificationObserver obs : observers) {
            obs.notifyUser(user, pkg, locker);
        }
    }

    public Locker assignPackageToLocker(String agentId,
                                        Package pkg,
                                        String lockerBankId,
                                        LockerSelectionStrategy strategy) {

        Agent agent = agentRepo.find(agentId);
        if (agent == null) throw new RuntimeException("Agent not found!");

        LockerBank bank = lockerRepo.find(lockerBankId);
        if (bank == null) throw new RuntimeException("LockerBank not found!");

        Locker locker = strategy.selectLocker(pkg, bank);
        if (locker == null) throw new RuntimeException("No suitable locker available!");

        locker.currPackage = pkg;
        locker.otp = generateOTP();

        pkgRepo.save(pkg);

        System.out.println(agent.name + " assigned package " + pkg.id + " to locker " + locker.id);
        System.out.println("OTP: " + locker.otp);

        notifyAll(pkg.owner, pkg, locker);

        return locker;
    }

    private String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}

class LockerController {
    private static volatile LockerController instance;
    private LockerService lockerService = LockerService.getInstance();

    private LockerController() {}

    public synchronized static LockerController getInstance() {
        if (instance == null) instance = new LockerController();
        return instance;
    }

    public LockerBank addLockerBank(int size, int s, int m, int b) {
        return lockerService.addLockerBank(size, s, m, b);
    }
}

class UserController {
    private static volatile UserController instance;
    private UserService userService = UserService.getInstance();

    private UserController() {}

    public synchronized static UserController getInstance() {
        if (instance == null) instance = new UserController();
        return instance;
    }

    public void addUser(User u) {
        userService.addUser(u);
    }

    public void pickupPackage(Locker locker, String otp) {
        System.out.println("\nUser entering OTP: " + otp);
        locker.enterOTP(otp);

        System.out.println("Attempting to open locker...");
        locker.openLocker();
    }
}

class AgentController {
    private static volatile AgentController instance;
    private AgentService agentService = AgentService.getInstance();

    private AgentController() {}

    public synchronized static AgentController getInstance() {
        if (instance == null) instance = new AgentController();
        return instance;
    }

    public Locker assignPackage(String agentId,
                                Package pkg,
                                String lockerBankId,
                                LockerSelectionStrategy strategy) {

        return agentService.assignPackageToLocker(agentId, pkg, lockerBankId, strategy);
    }
}

public class Solution {
    public static void main(String[] args) {

        LockerController lockerController = LockerController.getInstance();
        UserController userController = UserController.getInstance();
        AgentController agentController = AgentController.getInstance();

        AgentService.getInstance().registerObserver(new EmailNotificationService());

        LockerBank bank = lockerController.addLockerBank(10, 3, 3, 4);

        User user = new User("Atharv", "atharvwani01@gmail.com");
        userController.addUser(user);

        Agent agent = new Agent("Vinash", "vinash@amazon.com");
        AgentRepository.getInstance().save(agent);

        Package pkg = new Package(user, Size.SMALL);

        Locker assigned = agentController.assignPackage(
                agent.id,
                pkg,
                bank.id,
                new NearestAvailableLockerStrategy()
        );

        System.out.println("\n=== USER PICKUP FLOW ===");
        userController.pickupPackage(assigned, assigned.otp); // correct OTP
    }
}
