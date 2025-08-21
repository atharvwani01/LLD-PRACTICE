package FactoryPattern.good;
import java.util.Map;

class PushNotification implements Notification {
    private final String token;
    private Map<String, Object> payload;

    public PushNotification(String token){
        this.token = token;
    }

    void setPayload(Map<String, Object> payload){
        this.payload = payload;
    }

    public void send(String token){
        System.out.println("Sending push notification "+ token + " with payload " + payload);
    }
}