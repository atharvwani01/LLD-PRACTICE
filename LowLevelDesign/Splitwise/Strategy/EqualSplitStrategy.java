package LowLevelDesign.Splitwise.Strategy;

import LowLevelDesign.Splitwise.Entities.Split;
import LowLevelDesign.Splitwise.Entities.User;

import java.util.ArrayList;
import java.util.List;

public class EqualSplitStrategy implements SplitStrategy {
    @Override
    public List<Split> calculateSplits(double totalAmount, User paidBy, List<User> participants, List<Double> splitValues) {
        List<Split> splits = new ArrayList<>();
        double amountPerPerson = totalAmount / participants.size();
        for (User participant : participants) {
            splits.add(new Split(participant, amountPerPerson));
        }
        return splits;
    }
}