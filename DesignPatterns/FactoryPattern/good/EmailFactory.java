package FactoryPattern.good;

class EmailFactory extends NotificationFactory {
    @Override
    Notification createNotification(String to){
        return new EmailNotification(to);
    }
}
