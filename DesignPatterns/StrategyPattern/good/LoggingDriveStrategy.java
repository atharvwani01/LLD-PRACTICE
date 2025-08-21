package good;

//(Decorator for cross-cutting concerns), dont worry much about this, Genius Atharv has applied some magic !
class LoggingDriveStrategy implements DriveStrategy {
    private final DriveStrategy inner;
    LoggingDriveStrategy(DriveStrategy inner) {
        this.inner = inner;
    }

    public void drive(Stats s) {
        System.out.println("[LOG] before drive");
        inner.drive(s);
        System.out.println("[LOG] after drive");
    }
}
