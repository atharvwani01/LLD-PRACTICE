package LowLevelDesign.CoffeeVendingMachine_AtharvWani;

//User can select the pizza(Paneer, Chicken)
//User should also be able to decorate the pizza with extra sauce / cheese burst
//The pizzas price will be decided based on the decorations and its base price
//We will have inventory service which will supply the ingredients to the vending machine
//User should be able to insert money
//Vending machine will dispense the pizza and it will have SelectingState, InsertMoneyState, PaidState for the machine

import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum Ingredient{
    PANEER, CHICKEN, CAPSICUM, CORN, ONION, CHEESE, SAUCE
}
enum PizzaType{
    PANEER, CHICKEN
}
enum DecorationType{
    CHEESE_BURST, EXTRA_SAUCE
}
abstract class Pizza{
    String pizzaType;
    public String getPizzaType(){
        return pizzaType;
    }

    abstract int getPrice();
    abstract Map<Ingredient, Integer> getRecipe();
}
abstract class PizzaDecorator extends Pizza{
    Pizza basePizza;
    public PizzaDecorator(Pizza pizza){
        basePizza = pizza;
    }
}
class CheeseBurstPizzaDecorator extends PizzaDecorator{

    Map<Ingredient, Integer> cheeseBurstRecipe = Map.of(Ingredient.CHEESE, 10);

    public CheeseBurstPizzaDecorator(Pizza pizza){
        super(pizza);
    }
    public int getPrice(){
        return basePizza.getPrice() + 80;
    }
    public Map<Ingredient, Integer> getRecipe() {
        Map<Ingredient, Integer> baseRecipeList = basePizza.getRecipe();
        cheeseBurstRecipe.forEach((i, q) -> baseRecipeList.merge(i, q, Integer::sum));
        return baseRecipeList;
    }
}
class ExtraSaucePizzaDecorator extends PizzaDecorator{

    Map<Ingredient, Integer> extraSauceRecipe = Map.of(Ingredient.SAUCE, 10);

    public ExtraSaucePizzaDecorator(Pizza pizza){
        super(pizza);
    }
    public int getPrice(){
        return basePizza.getPrice() + 50;
    }

    @Override
    Map<Ingredient, Integer> getRecipe() {
        Map<Ingredient, Integer> baseRecipeList = basePizza.getRecipe();
        extraSauceRecipe.forEach((i,q) -> baseRecipeList.merge(i, q, Integer::sum));
        return baseRecipeList;
    }
}
class PaneerPizza extends Pizza{
    public PaneerPizza(){
        pizzaType = "Paneer Pizza";
    }

    int getPrice() {
        return 300;
    }

    @Override
    Map<Ingredient, Integer> getRecipe() {
        return new HashMap<>(Map.of(Ingredient.PANEER, 10, Ingredient.CHEESE, 20, Ingredient.CORN, 5));
    }

}
class ChickenPizza extends Pizza{
    public ChickenPizza(){
        pizzaType = "Chicken Pizza";
    }

    int getPrice() {
        return 350;
    }

    @Override
    Map<Ingredient, Integer> getRecipe() {
        return new HashMap<>(Map.of(Ingredient.CHICKEN, 10, Ingredient.CHEESE, 20, Ingredient.CORN, 5));
    }
}
class InventoryService{
    private static volatile InventoryService instance;
    Map<Ingredient, Integer> ingredients;
    private InventoryService(){
        ingredients = new HashMap<>();
    }
    public synchronized static InventoryService getInstance(){
        if (instance == null){
            instance = new InventoryService();
        }
        return instance;
    }
    public void addIngredient(Ingredient ingredient, Integer quantity){
        ingredients.merge(ingredient, quantity, Integer::sum);
        System.out.println("Successfully added " + ingredient.toString() + " with quantity " + quantity + " to inventory !");
    }

    public boolean canWeGetIngredients(Map<Ingredient, Integer> recipe){
        for(Map.Entry<Ingredient, Integer> ingredientIntegerEntry : recipe.entrySet()){
            if(!ingredients.containsKey(ingredientIntegerEntry.getKey()) || !(ingredients.get(ingredientIntegerEntry.getKey()) >= ingredientIntegerEntry.getValue())){
                return false;
            }
        }
        return true;
    }

    public void getIngredient(Ingredient ingredient, int quantity){
        if(ingredients.containsKey(ingredient) && ingredients.get(ingredient) >= quantity){
            ingredients.computeIfPresent(ingredient, (i, q) -> q - quantity);
        }
        else{
            System.out.println("Ingredient " + ingredient.toString() + " not in sufficient quantity in the inventory !");
        }
    }

    public void topUpIngredients(int quantity) {
        addIngredient(Ingredient.CHEESE, quantity);
        addIngredient(Ingredient.CORN, quantity);
        addIngredient(Ingredient.CHICKEN, quantity);
        addIngredient(Ingredient.PANEER, quantity);
        addIngredient(Ingredient.SAUCE, quantity);
        addIngredient(Ingredient.ONION, quantity);
    }

}
interface PizzaVendingMachineState{
    void selectPizza(PizzaVendingMachine pizzaVendingMachine, Pizza pizza);
    void insertMoney(PizzaVendingMachine pizzaVendingMachine, int money);
    void dispensePizza(PizzaVendingMachine pizzaVendingMachine);
    void cancelPizza(PizzaVendingMachine pizzaVendingMachine);
}
class PizzaFactory{
    public static Pizza create(PizzaType pizzaType){
        return switch (pizzaType){
            case PANEER -> new PaneerPizza();
            case CHICKEN -> new ChickenPizza();
        };
    }
}
class SelectingState implements PizzaVendingMachineState{

    @Override
    public void selectPizza(PizzaVendingMachine pizzaVendingMachine, Pizza pizza) {
        pizzaVendingMachine.selectedPizza = pizza;
        System.out.println("Pls pay the money now - " + pizza.getPrice());
        pizzaVendingMachine.pizzaVendingMachineState = new InsertMoneyState();
    }

    @Override
    public void insertMoney(PizzaVendingMachine pizzaVendingMachine, int money) {
        System.out.println("Select the pizza pls");
    }

    @Override
    public void dispensePizza(PizzaVendingMachine pizzaVendingMachine) {
        System.out.println("Select the pizza pls");
    }

    @Override
    public void cancelPizza(PizzaVendingMachine pizzaVendingMachine) {
        pizzaVendingMachine.selectedPizza = null;
    }
}

class InsertMoneyState implements PizzaVendingMachineState{

    @Override
    public void selectPizza(PizzaVendingMachine pizzaVendingMachine, Pizza pizza) {
        System.out.println("Pizza already selected");
    }

    @Override
    public void insertMoney(PizzaVendingMachine pizzaVendingMachine, int money) {
        pizzaVendingMachine.money += money;
        System.out.println("Added money " + money);
        if(pizzaVendingMachine.selectedPizza.getPrice() <= pizzaVendingMachine.money){
            System.out.println("Sufficient money " + money + " inserted !");
            pizzaVendingMachine.pizzaVendingMachineState = new PaidState();
        }
    }

    @Override
    public void dispensePizza(PizzaVendingMachine pizzaVendingMachine) {
        System.out.println("Pls insert money first");
    }

    @Override
    public void cancelPizza(PizzaVendingMachine pizzaVendingMachine) {
        pizzaVendingMachine.selectedPizza = null;
    }
}

class PaidState implements PizzaVendingMachineState{

    @Override
    public void selectPizza(PizzaVendingMachine pizzaVendingMachine, Pizza pizza) {
        System.out.println("Pizza already selected");
    }

    @Override
    public void insertMoney(PizzaVendingMachine pizzaVendingMachine, int money) {
        System.out.println("Money already inserted");
    }

    @Override
    public void dispensePizza(PizzaVendingMachine pizzaVendingMachine) {
        System.out.println("Pizza dispensing .....");
        InventoryService inventoryService = InventoryService.getInstance();
        Pizza currPizza = pizzaVendingMachine.selectedPizza;
        Map<Ingredient, Integer> recipe = currPizza.getRecipe();

        if(inventoryService.canWeGetIngredients(recipe)){
            recipe.forEach((inventoryService::getIngredient));
        }
        else{
            System.out.println("Insufficient ingredients for the selected pizza");
            cancelPizza(pizzaVendingMachine);
        }

        if(pizzaVendingMachine.money >= currPizza.getPrice()){
            pizzaVendingMachine.money -= currPizza.getPrice();
            System.out.println("Money deducted !");
            pizzaVendingMachine.pizzaVendingMachineState = new SelectingState();
        }
        else{
            cancelPizza(pizzaVendingMachine);
        }

    }

    @Override
    public void cancelPizza(PizzaVendingMachine pizzaVendingMachine) {
        pizzaVendingMachine.selectedPizza = null;
    }
}

class PizzaVendingMachine{
    private static volatile PizzaVendingMachine instance;
    Pizza selectedPizza;
    int money;
    PizzaVendingMachineState pizzaVendingMachineState;

    private PizzaVendingMachine(){
        selectedPizza = null;
        money = 0;
        pizzaVendingMachineState = new SelectingState();
    }

    public synchronized static PizzaVendingMachine getInstance(){
        if(instance == null){
            instance = new PizzaVendingMachine();
        }
        return instance;
    }

    public void selectPizza(PizzaType pizzaType, List<DecorationType> decorations){
        Pizza pizza = PizzaFactory.create(pizzaType);
        for(DecorationType decorationType : decorations){
            switch (decorationType){
                case EXTRA_SAUCE:
                    pizza = new ExtraSaucePizzaDecorator(pizza);
                    break;
                case CHEESE_BURST:
                    pizza = new CheeseBurstPizzaDecorator(pizza);
                    break;
            }
        }
        pizzaVendingMachineState.selectPizza(this, pizza);
    }

    public void insertMoney(int money){
        pizzaVendingMachineState.insertMoney(this, money);
    }

    public void dispensePizza(){
        pizzaVendingMachineState.dispensePizza(this);
    }


}

public class Solution {
    public static void main(String[] args) {

        InventoryService inventoryService = InventoryService.getInstance();

        inventoryService.topUpIngredients(50);

        PizzaVendingMachine pizzaVendingMachine = PizzaVendingMachine.getInstance();


        pizzaVendingMachine.selectPizza(PizzaType.CHICKEN, List.of(DecorationType.CHEESE_BURST, DecorationType.EXTRA_SAUCE));
        pizzaVendingMachine.insertMoney(500);
        pizzaVendingMachine.dispensePizza();



    }
}
