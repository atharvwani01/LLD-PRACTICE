package JavaPractice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class A{
    public static String name = "static name";
    String age = "age";
    static {
        System.out.println("A static block");
    }
    public A(){
        System.out.println("Inside Constructor of A");
    }
    public void print(){
        System.out.println("Inside A print method");
    }
}

class Atharv extends Thread {
    public void run(){
        for(int i=0; i<100; i++){
            System.out.println("Inside atharv method" + i);
        }
    }
}
class Ayushi extends Thread {
    public void run(){
        for(int i=0; i<100; i++){
            System.out.println("Inside ayushi method " + i);
        }
    }
}
class Vaishali implements Runnable {

    @Override
    public void run() {
        for(int i=0; i<100; i++){
            System.out.println("Inside vaishali method " + i);
        }
    }
}
class Vehicle {
    private String classType;
    public Vehicle(){
        System.out.println("Inside Vehicle Constructor");
    }
    public Vehicle(String classType){
        this.classType = classType;
        System.out.println("Inside Vehicle Constructor with classType " + classType);
    }

    void run(int speed){
        System.out.println("Inside Vehicle run method " + speed);
    }

    void sayHello(){
        System.out.println("Inside Vehicle sayHello method");
    }
}
class BMW extends Vehicle{
    String color;
    public BMW(){
        System.out.println("Inside BMW Constructor");
    }
    public BMW(String color){
        this.color = color;
        System.out.println("Inside BMW Constructor with color " + color);
    }
    void run(int speed){
        super.run(speed);
        System.out.println("Inside BMW run method " + speed);
    }
//    public void sayHello(){
//        System.out.println("Inside BMW sayHello method");
//        super.sayHello();
//    }

}

class Audi extends Vehicle{
    void run(int speed){
        System.out.println("Inside Audi run method " + speed);
    }
}
class Suzuki extends Vehicle{
    void run(int speed){
        System.out.println("Inside Suzuki run method " + speed);
    }
}
class X5 extends BMW{
    @Override
    public void sayHello(){
        super.sayHello();
        System.out.println("Inside X5 sayHello method");
    }
}
abstract class Tyre{
    public String name;
    public Tyre(){
        System.out.println("Inside Tyre Constructor");
    }
    public abstract void price();
}
class MRF extends Tyre{

    @Override
    public void price() {
        System.out.println("Inside MRF price and its costly");
    }
}
interface Food{
    String name = "Drink & Eat";
    void inmethod();
}
class Drink implements Food{
    @Override
    public void inmethod(){
        System.out.println("Inside Drink inmethod and we drink it");
    }
}
class Eat implements Food{
    @Override
    public void inmethod(){
        System.out.println("Inside Eat inmethod and we eat it");
    }
}
class Fanta extends Drink{
    @Override
    public void inmethod(){
        System.out.println("Inside Fanta inmethod and we drink it");
    }
}
interface GoodFood extends Food{
}
class OrganicFood implements GoodFood{

    @Override
    public void inmethod() {
        System.out.println("Inside OrganicFood and we inmethod");
    }
}
interface BadFood extends GoodFood{
    void price();
}
class JunkFood implements BadFood{

    @Override
    public void inmethod() {
        System.out.println("Inside JunkFood and we inmethod");
    }
    @Override
    public void price() {
        System.out.println("Inside JunkFood and we have low price");
    }
}
class Counter{
    int count = 0;
    synchronized void increment(){
        count++;
    }
}
class T1 extends Thread{
    private final Counter counter;
    public T1(Counter counter){
        this.counter = counter;
    }
    public void run(){
        for(int i=0; i<1000000; i++){
            counter.increment();
        }
    }
}
class T2 extends Thread{
    private final Counter counter;
    public T2(Counter counter){
        this.counter = counter;
    }
    public void run(){
        for(int i=0; i<1000000; i++){
            counter.increment();
        }
    }
}
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello World");
        A a = new A();
        a.print();

        Atharv ath = new Atharv();
        ath.start();

        Ayushi ayu = new Ayushi();
        ayu.start();

        Runnable r = new Vaishali();
        Thread t = new Thread(r);
        t.start();

        Runnable thread1 = () -> {
                for(int i=0; i<10; i++){
                    System.out.println("Inside thread1");
                }
            };

        thread1.run();

        Vehicle v = new Vehicle();
        v.run(20);

        Vehicle bmw = new BMW("Yellow");
        bmw.run(20);

        Vehicle x5 = new X5();
        x5.sayHello();


        Tyre mrftyre = new MRF();
        mrftyre.price();

        Tyre generictyre = new Tyre() {
            @Override
            public void price() {
                System.out.println("Inside generic price and its not costly");
            }
        };
        generictyre.price();

        Food drink = new Drink();
        drink.inmethod();
        Food eat = new Eat();
        eat.inmethod();

        Food fanta = new Fanta();
        fanta.inmethod();

        Food junkfood = new JunkFood();
        junkfood.inmethod();

        Integer a1 = 9;
        int b = 9;

        if(a1 == b){
            System.out.println("BOTH ARE SAME");
        }

        Counter counter = new Counter();
        T1 t1 = new T1(counter);
        t1.start();
        T2 t2 = new T2(counter);
        t2.start();

        t1.join();
        t2.join();

        System.out.println("The value of counter is " + counter.count);


        Collection<Integer> col = new ArrayList<Integer>();
        for(int i=1; i<=100; i++){
            col.add(i);
        }

        List<Integer> l1 = new ArrayList<>();
        l1.add(3);
        l1.add(4);

        System.out.println(col);
        System.out.println(l1);

        for(Integer i : col){
            System.out.println(i);
        }
        Predicate<Integer> predicate = n -> n%2==0;
        Function<Integer, Integer> function = n -> n+2;

        Stream<Integer> l2 = col.stream().filter(n -> n%2==0).map(n -> n+2);
        List<Integer> l3 = col.stream().filter(n -> n%2==0).map(n -> n+2).collect(Collectors.toList());

        Iterator<Integer> it = l3.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }


    }
}
