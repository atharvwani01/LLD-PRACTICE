package AbstractFactoryPattern.good;

interface WidgetFactory {
    Button createButton();
    TextField createTextField();
    CheckBox createCheckBox();
}
