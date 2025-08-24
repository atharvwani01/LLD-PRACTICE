package AbstractFactoryPattern.good;

class LoginPage {
    private final WidgetFactory factory;
    LoginPage(WidgetFactory factory) {
        this.factory = factory;
    }

    void render() {
        Button login = factory.createButton();         // consistent family
        TextField email = factory.createTextField();
        CheckBox remember = factory.createCheckBox();

        email.render("user@example.com", 30);
        remember.render(true);
        login.render(140, 48);
    }
}

public class DemoGood {
    public static void main(String[] args) {
        WidgetFactory widgetFactory = new DarkThemeFactory();
        new LoginPage(widgetFactory).render();
    }
}
