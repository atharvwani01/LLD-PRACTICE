# 🏭 Abstract Factory Pattern — UI Theme Story

Once upon a time, we were building a UI with two themes:

- **Light theme** (white backgrounds, dark text)
- **Dark theme** (dark backgrounds, light text)

Each theme had its own versions of widgets:

- `Button`
- `Checkbox`
- `TextField`

At first, we just instantiated whatever widget classes we needed on each page.

---

## 🌩️ The Trouble Begins

Pretty soon, problems appeared:

1. **Inconsistent families**  
   On one page, a developer picked a `DarkButton` but a `LightCheckbox`.  
   Result: a mismatched UI (part light, part dark).

2. **Scattered construction**  
   Every page decided which concrete widget to `new` up.  
   Switching from Light → Dark theme meant editing dozens of places.

3. **Closed for change**  
   Adding a new family (like a HighContrast theme) required hunting down every constructor call and updating it. Risky.

4. **Duplication**  
   Each page repeated the decision logic: *which widget do I build?*

5. **Hard to test**  
   Pages were tightly coupled to concrete widget classes, making it hard to inject test doubles.

**Example (bad):**
```java
class LoginPageBad {
    void render() {
        Button login = new DarkButton();        // dark
        TextField email = new LightTextField(); // light (oops, mismatch!)
        Checkbox remember = new LightCheckbox();

        email.render("user@example.com", 30);
        remember.render(true);
        login.render(140, 48);
    }
}
```
💡 The Insight
All these widgets belong to a family (a theme).
The mistake was letting pages pick concrete classes one by one.

So we said: “What if pages only talked to a factory that knows how to build a whole family consistently?”

🛠️ The Refactor — Enter Abstract Factory
We defined an Abstract Factory:

```java
interface WidgetFactory {
    Button createButton();
    Checkbox createCheckbox();
    TextField createTextField();
}
```
Concrete factories for each family:

```java
class LightThemeFactory implements WidgetFactory {
    public Button createButton()   { return new LightButton(); }
    public Checkbox createCheckbox() { return new LightCheckbox(); }
    public TextField createTextField() { return new LightTextField(); }
}

class DarkThemeFactory implements WidgetFactory {
    public Button createButton()   { return new DarkButton(); }
    public Checkbox createCheckbox() { return new DarkCheckbox(); }
    public TextField createTextField() { return new DarkTextField(); }
}
```
Now pages just ask the factory:
```java
class LoginPage {
    private final WidgetFactory factory;
    LoginPage(WidgetFactory factory) { this.factory = factory; }

    void render() {
        Button login = factory.createButton();
        TextField email = factory.createTextField();
        Checkbox remember = factory.createCheckbox();

        email.render("user@example.com", 30);
        remember.render(true);
        login.render(140, 48);
    }
}
```
Switch theme by swapping factories in one place:

```java
public class MainAF {
    public static void main(String[] args) {
        WidgetFactory factory = new DarkThemeFactory(); // or LightThemeFactory

        new LoginPage(factory).render();
        new SettingsPage(factory).render();
    }
}
```
✨ The Payoff
Consistency
One factory → all widgets from the same family. No more mixed Light + Dark.

Single switch point
To change the app theme, swap the factory at bootstrap.

Open for extension
Add a new HighContrastThemeFactory without touching existing pages.

Duplication removed
Pages no longer know which concrete class to instantiate.

Testability
You can inject a fake WidgetFactory in tests.

🧠 How to Remember (story in my head)
Before: Every page newed up its own mix of widgets. Inconsistencies crept in.

After: Pages don’t care about widget classes. They just ask a WidgetFactory.

Key mental model: Abstract Factory builds whole families of objects together.

🗺️ Quick Visual
```diagram

               WidgetFactory (interface)
              /       |        \
   createButton()  createCheckbox()  createTextField()
        |                 |                |
   --------------   --------------   ---------------
   |            |   |            |   |             |
LightThemeFactory  DarkThemeFactory   HighContrastFactory
   | (creates all Light)   | (creates all Dark)   | (all HC)
   
 ```

✅ Moral of the Story
Whenever you see:

multiple related objects that must match (families),

scattered new calls that risk inconsistency,

or the need to switch entire families at once…