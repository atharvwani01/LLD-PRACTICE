package FactoryPattern.good;

class SmsFactory extends NotificationFactory {
    @Override
    Notification createNotification(String to) {
        return new SmsNotification(to);
    }
}
