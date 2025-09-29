package DecoratorPattern;

public class Cappuccino implements Coffee {

    @Override
    public String getDescription() {
        return "Cappuccino";
    }

    @Override
    public double getCost() {
        return 10.0;
    }
}
