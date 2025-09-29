package LowLevelDesign.CoffeeVendingMachine.Decorator;

import LowLevelDesign.CoffeeVendingMachine.Enums.Ingredient;

import java.util.Map;

public abstract class CoffeeDecorator extends Coffee {
    protected Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    @Override
    public int getPrice() {
        return decoratedCoffee.getPrice();
    }

    @Override
    public Map<Ingredient, Integer> getRecipe() {
        return decoratedCoffee.getRecipe();
    }

    @Override
    protected void addCondiments() {
        decoratedCoffee.addCondiments();
    }

    @Override
    public void prepare() {
        decoratedCoffee.prepare();
    }
}