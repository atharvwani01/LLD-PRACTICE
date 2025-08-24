package AbstractFactoryPattern.good;

public class LightThemeFactory implements WidgetFactory{

    @Override
    public Button createButton() {
        return new LightButton();
    }

    @Override
    public TextField createTextField() {
        return new LightTextField();
    }

    @Override
    public CheckBox createCheckBox() {
        return new LightCheckBox();
    }
}
