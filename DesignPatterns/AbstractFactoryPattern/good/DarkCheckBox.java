package AbstractFactoryPattern.good;

class DarkCheckBox implements CheckBox {
    @Override
    public void render(boolean isSelected) {
        System.out.println("Dark Checkbox is selected/not selected : " + isSelected);
    }
}
