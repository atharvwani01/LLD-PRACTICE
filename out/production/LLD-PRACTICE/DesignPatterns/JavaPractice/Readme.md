# My Java Playground – Learnings from `Demo.java`

This file is a walkthrough of everything explored in the `Demo` program.  
It’s meant as a reference to quickly recall **Java fundamentals**.

---

## 1. Class `A`
- Has a **static block**: runs once when the class is loaded.
- Has a **static variable** (`name`) and an **instance variable** (`age`).
- Constructor runs when you create an object.
- Methods can print or act on instance state.

### Key Learning
- **Static block** executes once per class loading.
- **Constructor** executes for every new object.
- Static vs instance variables.

---

## 2. Threads
### Extending `Thread`
```java
class Atharv extends Thread {
    public void run() { ... }
}
```
### Implementing `Runnable`
```java
class Vaishali implements Runnable {
    public void run() { ... }
}
```
### Using Lambda as `Runnable`
```java
Runnable r = () -> { ... };
```
### Key Learning
`start()` actually spawns a new thread.

`run()` just calls the method in the current thread.

Prefer Runnable over extending Thread (more flexible).

## 3. Inheritance & Constructors
Vehicle has two constructors: default and parameterized.

BMW, Audi, Suzuki extend Vehicle.

BMW.run(int) calls super.run(speed) then adds its own.

X5 extends BMW and overrides sayHello.

### Key Learning
Parent constructor runs first.

`super()` can be used to call parent constructor/method.

Overriding lets subclasses specialize behavior.

Polymorphism: a Vehicle reference can point to BMW, Audi, etc.

## 4. Abstract Class
```java
abstract class Tyre {
    public abstract void price();
}
```
Cannot be instantiated directly.

Subclasses (MRF) must implement price().

You can also use an anonymous class:

```java
Tyre genericTyre = new Tyre() {
   public void price() { ... }
};
```
### Key Learning
Abstract class = partial implementation.

Anonymous classes for quick one-off behavior.

## 5. Interfaces
```java
interface Food {
    String name = "Drink & Eat"; // implicitly public static final
    void inmethod();
}
```
Implemented by Drink and Eat.

Inherited by Fanta, OrganicFood, JunkFood.

BadFood extends GoodFood and adds price().

### Key Learning
Interface fields are always public static final (constants).

Interfaces support multiple inheritance (class → one parent, interface → many).

Default behavior specialization via inheritance chain.

Food Hierarchy (Class Diagram)
```plaintext

              Food (interface)
                  |
        ------------------------
        |                      |
     Drink                    Eat
       |                       |
     Fanta                   (end)
       |
  (inherits Drink)


GoodFood (interface) --- BadFood (interface)
       |                      |
  OrganicFood             JunkFood
```

## 6. Synchronization
```java
class Counter {
    int count = 0;
    synchronized void increment() { count++; }
}
```
synchronized ensures atomic increment.

T1 and T2 both increment the same Counter.

After join(), counter.count is correct (2,000,000).

### Key Learning
Without synchronization, race conditions occur.

`join()` ensures the main thread waits for child threads.

### 7. Autoboxing & Equality
```java
Integer a = 9;
int b = 9;
if (a == b) { ... }
```
Works because a is auto-unboxed into int.

For object comparisons, use .equals().

## 8. Collections & Streams
```java
Collection<Integer> col = new ArrayList<>();
for (int i=1; i<=100; i++) col.add(i);
```
`Predicate`: n -> n%2==0.

`Function`: n -> n+2.

`Stream pipeline`:

```java
List<Integer> l3 = col.stream()
    .filter(n -> n%2==0)
    .map(n -> n+2)
    .collect(Collectors.toList());
```
Iterate with Iterator or forEach.

### Key Learning
Stream<T> is lazy and consumed once.

Use `collect()` for a List.

Use anyMatch, map, filter for functional style.

### 9. Key Takeaways
Static vs instance: static tied to class, instance to object.

Threading: `start()` spawns, synchronized prevents races.

Inheritance: constructor chaining, overriding.

Abstract class vs interface: abstract = partial implementation, interface = contract.

Streams: powerful alternative to loops.

Autoboxing: == works for Integer vs int, but prefer `.equals()` for object equality.

### 📌 Practice Tips
Replace synchronized with AtomicInteger in Counter.

Try adding multiple threads (T3, T4) and see results.

Use Collectors.groupingBy on col to practice advanced streams.

Modify BMW constructor to call super("Car") and observe chaining.

