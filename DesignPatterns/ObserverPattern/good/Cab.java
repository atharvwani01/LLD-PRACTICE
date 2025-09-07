package ObserverPattern.good;

import java.util.ArrayList;
import java.util.List;

public class Cab implements Subject {
    List<Observer> observerList = new ArrayList<>();
    private String status = "Free";

    void setStatus(String status){
        this.status = status;
        notifyObservers(status);
    }


    @Override
    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(String status) {
        for (Observer observer : observerList) {
            observer.update(status);
        }
    }
}
