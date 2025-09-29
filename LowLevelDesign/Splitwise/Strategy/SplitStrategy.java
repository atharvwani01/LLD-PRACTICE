package LowLevelDesign.Splitwise.Strategy;

import LowLevelDesign.Splitwise.Entities.Split;
import LowLevelDesign.Splitwise.Entities.User;

import java.util.List;

public interface SplitStrategy {
    List<Split> calculateSplits(double totalAmount, User paidBy, List<User> participants, List<Double> splitValues);
}
