package FactoryPattern.good;


public class SmsNotification implements Notification{
    private String to;
    private String message;

    SmsNotification(String to){
        this.to = to;
    }

    void setMessage(String message){
        this.message = message;
    }

    public void send(String message){
        System.out.println("Sending SMS to " + to + ": " + message);
    }
}
