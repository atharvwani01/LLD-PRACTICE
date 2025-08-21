package FactoryPattern.good;

class EmailNotification implements Notification{
    private final String to;
    private String subject;
    private String body;
    EmailNotification(String to){
        this.to = to;
    }
    void setSubject(String subject){
        this.subject = subject;
    }
    void setBody(String body){
        this.body = body;
    }
    public void send(String body) {
        System.out.println("Sending email to " + to + " with subject " + subject + " and body " + body);
    }
}