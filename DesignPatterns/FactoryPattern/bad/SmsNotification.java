package FactoryPattern.bad;


public class SmsNotification {
    private String to;
    private String message;

    SmsNotification(String to){
        this.to = to;
    }

    void setMessage(String message){
        this.message = message;
    }

    void send(){
        System.out.println("Sending SMS to " + to + ": " + message);
    }
}
