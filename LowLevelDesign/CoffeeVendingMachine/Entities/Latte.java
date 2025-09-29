package LowLevelDesign.CoffeeVendingMachine.Entities;

import LowLevelDesign.CoffeeVendingMachine.Decorator.Coffee;
import LowLevelDesign.CoffeeVendingMachine.Enums.Ingredient;

import java.util.Map;

public class Latte extends Coffee {
    public Latte() {
        this.coffeeType = "Latte";
    }

    // Latte's implementation of the template hook
    @Override
    protected void addCondiments() {
        System.out.println("- Adding steamed milk.");
    }

    @Override
    public int getPrice() {
        return 220;
    }

    @Override
    public Map<Ingredient, Integer> getRecipe() {
        return Map.of(Ingredient.COFFEE_BEANS, 7, Ingredient.WATER, 30, Ingredient.MILK, 150);
    }
}