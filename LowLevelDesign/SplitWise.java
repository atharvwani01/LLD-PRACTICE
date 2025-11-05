package LowLevelDesign.Spliwise_AtharvWani;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class User{
    String id;
    String name;
    String email;
    BalanceSheet balanceSheet;
    public User(String name, String email){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        balanceSheet = new BalanceSheet(this);
    }
}
class Group{
    String id;
    String name;
    List<User> users;
    public Group(String name, List<User> users){
        id = UUID.randomUUID().toString();
        this.name = name;
        this.users = users;
    }
    public void addUser(User user){
        users.add(user);
    }
}
interface SplitStrategy{
    List<Split> split(double amount, User paidBy, List<User> participants, List<Double> splitValues);
}

class EqualSplitStrategy implements SplitStrategy{

    @Override
    public List<Split> split(double amount, User paidBy, List<User> participants, List<Double> splitValues) {
        List<Split> splits = new ArrayList<>();
        double amountPerPerson = amount / participants.size();
        for (User participant : participants) {
            splits.add(new Split(participant, amountPerPerson));
        }
        return splits;
    }
}

class ExactSplitStrategy implements SplitStrategy{

    @Override
    public List<Split> split(double amount, User paidBy, List<User> participants, List<Double> splitValues) {
        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            splits.add(new Split(participants.get(i), splitValues.get(i)));
        }
        return splits;
    }
}

class PercentageSplitStrategy implements SplitStrategy{

    @Override
    public List<Split> split(double amount, User paidBy, List<User> participants, List<Double> splitValues) {
        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            splits.add(new Split(participants.get(i), amount*(splitValues.get(i)/100)));
        }
        return splits;
    }
}

class Expense{
    String id;
    String name;
    double amount;
    User paidBy;
    List<Split> splits;
    SplitStrategy splitStrategy;

    private Expense(ExpenseBuilder expenseBuilder){
        this.id = UUID.randomUUID().toString();
        name = expenseBuilder.name;
        amount = expenseBuilder.amount;
        paidBy = expenseBuilder.paidBy;
        splitStrategy = expenseBuilder.splitStrategy;
        splits = expenseBuilder.splitStrategy.split(expenseBuilder.amount, expenseBuilder.paidBy, expenseBuilder.participants, expenseBuilder.splitValues);
    }

    public static class ExpenseBuilder{
        private String name;
        private double amount;
        private User paidBy;
        private List<User> participants;
        private SplitStrategy splitStrategy;
        private List<Double> splitValues;

        public ExpenseBuilder name(String name){
            this.name = name;
            return this;
        }
        public ExpenseBuilder amount(int amount){
            this.amount = amount;
            return this;
        }
        public ExpenseBuilder paidBy(User paidBy){
            this.paidBy = paidBy;
            return this;
        }
        public ExpenseBuilder splitStrategy(SplitStrategy splitStrategy){
            this.splitStrategy = splitStrategy;
            return this;
        }
        public ExpenseBuilder splitValues(List<Double> splitValues){
            this.splitValues = splitValues;
            return this;
        }
        public ExpenseBuilder participants(List<User> participants){
            this.participants = participants;
            return this;
        }
        public Expense build(){
            return new Expense(this);
        }
    }

}
class Split{
    User user;
    double amount;
    public Split(User user, double amount){
        this.user = user;
        this.amount = amount;
    }
}
class BalanceSheet {
    User owner;
    Map<User, Double> balances = new ConcurrentHashMap<>();

    public BalanceSheet(User owner) {
        this.owner = owner;
    }

    public synchronized void adjustBalance(User otherUser, double amount) {
        if (owner.equals(otherUser)) {
            return;
        }
        balances.merge(otherUser, amount, Double::sum);
    }

    public void showBalances() {
        System.out.println("--- Balance Sheet for " + owner.name + " ---");
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
                System.out.println(otherUser.name + "owes " + owner.name + " $" + amount);
                totalOwedToMe += amount;
            } else if (amount < 0) {
                System.out.println(owner.name + "owes " + otherUser.name + " $" + amount);
                totalIOwe += amount;
            }
        }
        System.out.println("Total Owed to " + owner.name + ": $" + String.format("%.2f", totalOwedToMe));
        System.out.println("Total " + owner.name + " Owes: $" + String.format("%.2f", totalIOwe));
        System.out.println("---------------------------------");
    }

}
class Transaction {
    User from;
    User to;
    double amount;

    public Transaction(User from, User to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return from.name + " should pay " + to.name + " $" + String.format("%.2f", amount);
    }
}
class SplitWiseSystem{
    private static volatile SplitWiseSystem instance;
    Map<String, Group> groups;
    Map<String, User> users;
    private SplitWiseSystem(){
        groups = new HashMap<>();
        users = new HashMap<>();
    }
    public synchronized static SplitWiseSystem getInstance(){
        if(instance == null){
            instance = new SplitWiseSystem();
        }
        return instance;
    }
    public void addGroup(Group group){
        groups.put(group.id, group);
        System.out.println("Added the group " + group.name);
    }
    public void addUser(User user){
        users.put(user.id, user);
        System.out.println("Added the user " + user.name);
    }

    public synchronized void createExpense(Expense expense){
        List<Split> splits = expense.splits;
        for(Split split: splits){
            if(expense.paidBy != split.user){
                expense.paidBy.balanceSheet.adjustBalance(split.user, split.amount);
                split.user.balanceSheet.adjustBalance(expense.paidBy, -split.amount);
            }
        }
        System.out.println("Expense '" + expense.name + "' of amount " + expense.amount + " created.");
    }

    public List<Transaction> simplify(String id){
        List<User> members = groups.get(id).users;
        Map<User, Double> netBalances = new HashMap<>();
        for (User user : members){
            BalanceSheet balanceSheet = user.balanceSheet;
            double balance = 0;
            for(Map.Entry<User, Double> balances : balanceSheet.balances.entrySet()){
                if(members.contains(balances.getKey())){
                    balance += balances.getValue();
                }
            }
            netBalances.put(user, balance);
        }

        List<Map.Entry<User, Double>> creditors = netBalances.entrySet().stream().filter(e -> e.getValue() > 0).collect(Collectors.toList());
        List<Map.Entry<User, Double>> debtors = netBalances.entrySet().stream().filter(e -> e.getValue() < 0).collect(Collectors.toList());

        creditors.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        creditors.sort(Map.Entry.comparingByValue());

        List<Transaction> transactions = new ArrayList<>();
        int i = 0, j = 0;
        while (i < creditors.size() && j < debtors.size()) {
            Map.Entry<User, Double> creditor = creditors.get(i);
            Map.Entry<User, Double> debtor = debtors.get(j);

            double amountToSettle = Math.min(creditor.getValue(), -debtor.getValue());
            transactions.add(new Transaction(debtor.getKey(), creditor.getKey(), amountToSettle));

            creditor.setValue(creditor.getValue() - amountToSettle);
            debtor.setValue(debtor.getValue() + amountToSettle);

            if (Math.abs(creditor.getValue()) < 0.01) i++;
            if (Math.abs(debtor.getValue()) < 0.01) j++;

        }
        return transactions;
    }
}

public class Solution {
    public static void main(String[] args) {
        SplitWiseSystem splitWiseSystem = SplitWiseSystem.getInstance();
        User user1 = new User("Atharv", "atharv@gmail.com");
        User user2 = new User("Ayushi", "ayushi@gmail.com");
        User user3 = new User("Prashant", "prashant@gmail.com");
        User user4 = new User("Vaishali", "vaishali@gmail.com");

        splitWiseSystem.addUser(user1);
        splitWiseSystem.addUser(user2);
        splitWiseSystem.addUser(user3);
        splitWiseSystem.addUser(user4);
        Group group = new Group("TRIP TO DUBAI", List.of(user1, user2, user3, user4));
        splitWiseSystem.addGroup(group);
        splitWiseSystem.createExpense(new Expense.ExpenseBuilder()
                        .name("Coffee")
                        .amount(500)
                        .paidBy(user1)
                        .participants(List.of(user2, user3, user4))
                        .splitStrategy(new ExactSplitStrategy())
                        .splitValues(List.of(100.0, 200.0, 200.0))
                        .build());

        splitWiseSystem.createExpense(new Expense.ExpenseBuilder()
                .name("Tea")
                .amount(1500)
                .paidBy(user2)
                .participants(List.of(user3, user4))
                .splitStrategy(new EqualSplitStrategy())
                .build());

        List<Transaction> simplifiedDebts = splitWiseSystem.simplify(group.id);
        if (simplifiedDebts.isEmpty()) {
            System.out.println("All debts are settled within the group!");
        } else {
            simplifiedDebts.forEach(System.out::println);
        }
        System.out.println();
    }
}
