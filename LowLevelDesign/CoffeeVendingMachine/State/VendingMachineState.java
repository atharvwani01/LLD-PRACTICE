package LowLevelDesign.CoffeeVendingMachine.State;

import LowLevelDesign.CoffeeVendingMachine.Entities.CoffeeVendingMachine;
import LowLevelDesign.CoffeeVendingMachine.Decorator.Coffee;

public interface VendingMachineState {
    void selectCoffee(CoffeeVendingMachine coffeeVendingMachine, Coffee coffee);
    void insertMoney(CoffeeVendingMachine machine, int amount);
    void dispenseCoffee(CoffeeVendingMachine machine);
    void cancel(CoffeeVendingMachine machine);
}
