package AbstractFactoryPattern.bad;

class DarkTextField implements TextField {
    @Override
    public void render(String text, int maxLen) {
        System.out.println("Dark TextField text: " + text + " maxLen: " + maxLen);
    }
}
