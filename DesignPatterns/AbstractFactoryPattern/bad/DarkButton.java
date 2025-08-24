package AbstractFactoryPattern.bad;

public class DarkButton implements Button{
    @Override
    public void render(int width, int height){
        System.out.println("Dark Button with width " + width + " and height " + height);
    }
}
