package FactoryPattern.good;

abstract class NotificationFactory {
    abstract Notification createNotification(String to);
    public void notify(String toOrToken, String msg) {
        Notification n = createNotification(toOrToken);
        n.send(msg);
    }
}
