package FactoryPattern.good;

class PushFactory extends NotificationFactory {
    @Override
    Notification createNotification(String token) {
        return new PushNotification(token);
    }
}
