package FactoryPattern.bad;

class EmailNotification {
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
    void send() {
        System.out.println("Sending email to " + to + " with subject " + subject + " and body " + body);
    }
}