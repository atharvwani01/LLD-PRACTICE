package AbstractFactoryPattern.good;

class LightCheckBox implements CheckBox {
    @Override
    public void render(boolean isSelected){
        System.out.println("Light Checkbox is selected/not selected : " + isSelected);
    }
}
