package DecoratorPattern;

public class CoffeShop {
    public static void main(String[] args) {
        Coffee coffee = new Espresso();
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);

        System.out.println("The Description is " + coffee.getDescription());
        System.out.println("The Cost is " + coffee.getCost());
    }
}
