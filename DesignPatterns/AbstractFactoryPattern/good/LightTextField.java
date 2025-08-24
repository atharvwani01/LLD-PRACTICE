package AbstractFactoryPattern.good;

class LightTextField implements TextField {
    @Override
    public void render(String text, int maxLen) {
        System.out.println("Light TextField with Text : " + text + " and maxLen : " + maxLen);
    }
}
