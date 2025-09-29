package LowLevelDesign.CoffeeVendingMachine.Decorator;

import LowLevelDesign.CoffeeVendingMachine.Enums.Ingredient;

import java.util.Map;

public abstract class Coffee {
    protected String coffeeType = "Unknown Coffee";
    public String getCoffeeType() {
        return coffeeType;
    }

    public void prepare() {
        System.out.println("\nPreparing your " + this.getCoffeeType() + "...");
        grindBeans();
        brew();
        addCondiments(); // The "hook" for base coffee types
        pourIntoCup();
        System.out.println(this.getCoffeeType() + " is ready!");
    }

    private void grindBeans() {
        System.out.println("- Grinding fresh coffee beans.");
    }

    private void brew() {
        System.out.println("- Brewing coffee with hot water.");
    }

    private void pourIntoCup() {
        System.out.println("- Pouring into a cup.");
    }

    protected abstract void addCondiments();

    public abstract int getPrice();

    public abstract Map<Ingredient, Integer> getRecipe();

}
