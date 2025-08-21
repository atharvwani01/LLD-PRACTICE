package FactoryPattern.bad;

import java.util.Map;

public class NotificationService {
    void send(String type, String to, String msg) {
        if("EMAIL".equalsIgnoreCase(type)){
            EmailNotification emailNotification = new EmailNotification(to);
            emailNotification.setBody(msg);
            emailNotification.setSubject("Sample Subject");
            emailNotification.send();
        }
        else if("SMS".equalsIgnoreCase(type)){
            SmsNotification smsNotification = new SmsNotification(to);
            smsNotification.setMessage(msg);
            smsNotification.send();
        }
        else if("PUSH".equalsIgnoreCase(type)){
            PushNotification pushNotification = new PushNotification(to);
            pushNotification.setPayload(Map.of("Title", "Sample Title", "Message", msg));
            pushNotification.send();
        }
        else{
            System.out.println("Invalid Notification Type");
        }
    }
}
