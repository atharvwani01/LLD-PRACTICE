package FactoryPattern.bad;
import java.util.Map;

class PushNotification {
    private final String token;
    private Map<String, Object> payload;

    public PushNotification(String token){
        this.token = token;
    }

    void setPayload(Map<String, Object> payload){
        this.payload = payload;
    }

    void send(){
        System.out.println("Sending push notification "+ token + " with payload " + payload);
    }
}