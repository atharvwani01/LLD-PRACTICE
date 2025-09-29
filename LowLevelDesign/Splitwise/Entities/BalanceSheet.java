package LowLevelDesign.Splitwise.Entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceSheet {
    private final User owner;
    private final Map<User, Double> balances = new ConcurrentHashMap<>();

    public BalanceSheet(User owner) {
        this.owner = owner;
    }

    public Map<User, Double> getBalances() {
        return balances;
    }

    public synchronized void adjustBalance(User otherUser, double amount) {
        if (owner.equals(otherUser)) {
            return;
        }
        balances.merge(otherUser, amount, Double::sum);
    }

    public void showBalances() {
        System.out.println("--- Balance Sheet for " + owner.getName() + " ---");
        if (balances.isEmpty()) {
            System.out.println("All settled up!");
            return;
        }
        double totalOwedToMe = 0;
        double totalIOwe = 0;

        for (Map.Entry<User, Double> entry : balances.entrySet()) {
            User otherUser = entry.getKey();
            double amount = entry.getValue();

            if(amount > 0) {
                System.out.println(otherUser.getName() + "owes " + owner.getName() + " $" + amount);
                totalOwedToMe += amount;
            } else if (amount < 0) {
                System.out.println(owner.getName() + "owes " + otherUser.getName() + " $" + amount);
                totalIOwe += amount;
            }
        }
        System.out.println("Total Owed to " + owner.getName() + ": $" + String.format("%.2f", totalOwedToMe));
        System.out.println("Total " + owner.getName() + " Owes: $" + String.format("%.2f", totalIOwe));
        System.out.println("---------------------------------");
    }

}
