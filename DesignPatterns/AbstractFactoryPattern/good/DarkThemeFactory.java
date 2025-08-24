package AbstractFactoryPattern.good;

class DarkThemeFactory implements WidgetFactory{
    @Override
    public Button createButton() {
        return new DarkButton();
    }

    @Override
    public TextField createTextField() {
        return new DarkTextField();
    }

    @Override
    public CheckBox createCheckBox() {
        return new DarkCheckBox();
    }
}
