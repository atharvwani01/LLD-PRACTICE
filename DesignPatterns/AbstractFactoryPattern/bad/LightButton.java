package AbstractFactoryPattern.bad;

class LightButton implements Button {
    @Override
    public void render(int width, int height) {
        System.out.println("Light Button with width " + width + " and height " + height);
    }
}
