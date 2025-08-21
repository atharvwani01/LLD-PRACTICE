package FactoryPattern.good;

import java.util.Map;

public class DemoGood {
    public static void main(String[] args) {
        Map<String, NotificationFactory> factories = Map.of(
                "EMAIL", new EmailFactory(),
                "SMS",   new SmsFactory(),
                "PUSH",  new PushFactory()
        );

        NotificationFactory f1 = factories.get("EMAIL");
        f1.notify("user@example.com", "Hello!");

        factories.get("SMS").notify("9998887777", "Hi!");
        factories.get("PUSH").notify("devtoken-123", "Ping!");
    }
}
