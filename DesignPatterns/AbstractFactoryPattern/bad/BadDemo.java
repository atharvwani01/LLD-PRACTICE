package AbstractFactoryPattern.bad;

class LoginPageBad {
    void render() {
        Button login = new DarkButton();
        TextField email = new LightTextField();
        CheckBox rememberme = new LightCheckBox();

        login.render(12, 12);
        email.render("user@example.com", 40);
        rememberme.render(true);
    }
}

public class BadDemo {
    public static void main(String[] args) {
        new LoginPageBad().render();
    }
}
