package LowLevelDesign.CoffeeVendingMachine.Entities;

import LowLevelDesign.CoffeeVendingMachine.Decorator.CaramelSyrupDecorator;
import LowLevelDesign.CoffeeVendingMachine.Decorator.Coffee;
import LowLevelDesign.CoffeeVendingMachine.Decorator.ExtraSugarDecorator;
import LowLevelDesign.CoffeeVendingMachine.Enums.CoffeeType;
import LowLevelDesign.CoffeeVendingMachine.Enums.ToppingType;
import LowLevelDesign.CoffeeVendingMachine.Factory.CoffeeFactory;
import LowLevelDesign.CoffeeVendingMachine.State.ReadyState;
import LowLevelDesign.CoffeeVendingMachine.State.VendingMachineState;

import java.util.List;

public class CoffeeVendingMachine {
    private static CoffeeVendingMachine instance;
    private Coffee selectedCoffee;
    private VendingMachineState state;
    private int moneyInserted;

    public CoffeeVendingMachine(){
        this.state = new ReadyState();
        this.moneyInserted = 0;
    }

    public static CoffeeVendingMachine getInstance() {
        if (instance == null) {
            return new CoffeeVendingMachine();
        }
        return instance;
    }

    public void selectCoffee(CoffeeType type, List<ToppingType> toppings) {
        // 1. Create the base coffee using the factory
        Coffee coffee = CoffeeFactory.createCoffee(type);

        // 2. Wrap it with decorators
        for (ToppingType topping : toppings) {
            switch (topping) {
                case EXTRA_SUGAR:
                    coffee = new ExtraSugarDecorator(coffee);
                    break;
                case CARAMEL_SYRUP:
                    coffee = new CaramelSyrupDecorator(coffee);
                    break;
            }
        }
        // Let the state handle the rest
        this.state.selectCoffee(this, coffee);
    }

    public void insertMoney(int amount) { state.insertMoney(this, amount); }
    public void dispenseCoffee() { state.dispenseCoffee(this); }
    public void cancel() { state.cancel(this); }

    // --- Getters and Setters used by State objects ---
    public void setState(VendingMachineState state) { this.state = state; }
    public VendingMachineState getState() { return state; }
    public void setSelectedCoffee(Coffee selectedCoffee) { this.selectedCoffee = selectedCoffee; }
    public Coffee getSelectedCoffee() { return selectedCoffee; }
    public void setMoneyInserted(int moneyInserted) { this.moneyInserted = moneyInserted; }
    public int getMoneyInserted() { return moneyInserted; }

    public void reset() {
        this.selectedCoffee = null;
        this.moneyInserted = 0;
    }


}
