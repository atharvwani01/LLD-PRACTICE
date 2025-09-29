package LowLevelDesign.CoffeeVendingMachine.Factory;

import LowLevelDesign.CoffeeVendingMachine.Decorator.Coffee;
import LowLevelDesign.CoffeeVendingMachine.Entities.Cappuccino;
import LowLevelDesign.CoffeeVendingMachine.Entities.Espresso;
import LowLevelDesign.CoffeeVendingMachine.Entities.Latte;
import LowLevelDesign.CoffeeVendingMachine.Enums.CoffeeType;

public class CoffeeFactory {
    public static Coffee createCoffee(CoffeeType type) {
        switch (type) {
            case ESPRESSO:
                return new Espresso();
            case LATTE:
                return new Latte();
            case CAPPUCCINO:
                return new Cappuccino();
            default:
                throw new IllegalArgumentException("Unsupported coffee type: " + type);
        }
    }
}
