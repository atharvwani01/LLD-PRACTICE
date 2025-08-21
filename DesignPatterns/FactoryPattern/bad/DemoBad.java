package FactoryPattern.bad;

class DemoBad {
    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();
        notificationService.send("SMS", "a@g.com", "Hello World");
        notificationService.send("EMAIL", "a@g.com", "Hello World");
        notificationService.send("PUSH", "a@g.com", "Hello World");
    }
}
