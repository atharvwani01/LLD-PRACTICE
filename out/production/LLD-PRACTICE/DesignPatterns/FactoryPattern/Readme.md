# 🏭 Factory Method Pattern — Notification Story

Once upon a time, we had to send different kinds of notifications:

- **EmailNotification** (SMTP email)
- **SmsNotification** (via SMS gateway)
- **PushNotification** (mobile push)

Each type had its own construction quirks — email needs subject/body, SMS has a character limit, push has a token and payload.

So we wrote one big service with a giant `if/else` chain.

---

## 🌩️ The Trouble Begins

At first it worked… but problems piled up:

1. **If/else jungle**  
   The `NotificationService` checked `"EMAIL"`, `"SMS"`, `"PUSH"`, and manually created the right class. Adding WhatsApp or Slack meant *another branch*.

2. **Tight coupling**  
   The service knew all concrete classes (`new EmailNotification(...)`, `new SmsNotification(...)`). Change a constructor → change the service.

3. **Closed for change**  
   Adding a new type meant editing old, tested code. Risky.

4. **Duplication**  
   Every place we needed notifications, we repeated the same if/else logic.

**Example (bad):**
```java
class NotificationService {
    void send(String type, String to, String msg) {
        if ("EMAIL".equalsIgnoreCase(type)) {
            EmailNotification n = new EmailNotification(to);
            n.setSubject("Alert");
            n.setBody(msg);
            n.send();
        } else if ("SMS".equalsIgnoreCase(type)) {
            SmsNotification n = new SmsNotification(to);
            n.setMsg(msg);
            n.send();
        } else if ("PUSH".equalsIgnoreCase(type)) {
            PushNotification n = new PushNotification(to);
            n.setPayload(Map.of("title","Alert","body",msg));
            n.send();
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
```

💡 The Insight
Object creation was the messy part.
We realized: “Let’s push creation into factories.”

The client shouldn’t know which class to instantiate. It should just ask a factory.

🛠️ The Refactor — Enter Factory Method
We introduced a common interface:

```java
interface Notification {
    void send(String msg);
}
```

Concrete products:
```java
class EmailNotification implements Notification { ... }
class SmsNotification implements Notification { ... }
class PushNotification implements Notification { ... }
```
An abstract creator:
```java
abstract class NotificationFactory {
    abstract Notification create(String toOrToken);

    public void notify(String toOrToken, String msg) {
        Notification n = create(toOrToken);
        n.send(msg);
    }
}
```
Concrete factories:
```java
class EmailFactory extends NotificationFactory {
    Notification create(String to) { return new EmailNotification(to); }
}
class SmsFactory extends NotificationFactory {
    Notification create(String to) { return new SmsNotification(to); }
}
class PushFactory extends NotificationFactory {
    Notification create(String token) { return new PushNotification(token); }
}
```
✨ The Payoff
No if/else in client
Creation logic is centralized in factories.

Open for extension
Add a new SlackFactory + SlackNotification without touching old code.

Decoupled
Client code depends only on Notification and NotificationFactory.

Reusable & testable
You can inject fake factories for testing.

Example (good main):

```java
public class MainFactoryMethod {
    public static void main(String[] args) {
        Map<String, NotificationFactory> factories = Map.of(
            "EMAIL", new EmailFactory(),
            "SMS",   new SmsFactory(),
            "PUSH",  new PushFactory()
        );

        factories.get("EMAIL").notify("user@example.com", "Hello!");
        factories.get("SMS").notify("9998887777", "Hi!");
        factories.get("PUSH").notify("devtoken-123", "Ping!");
    }
}
```
🧠 How to Remember (story in my head)
Before: Every client had to decide which class to new up. If/else everywhere.

After: Clients don’t care — they just ask a factory, and the right product is built.

Mental model: Separate “what to build” from “how to build it.”

🗺️ Quick Visual
```nginx
         Notification (interface)
       /          |           \
EmailNotif   SmsNotif    PushNotif
     ^            ^           ^
     |            |           |
 EmailFactory  SmsFactory  PushFactory
        \        |        /
       (client asks a factory)
       
```
✅ Moral of the Story
Whenever you see:

if/else chains deciding which class to instantiate,

scattered new calls for different product types,

or client code tightly coupled to concrete classes…

👉 Use the Factory Method pattern.