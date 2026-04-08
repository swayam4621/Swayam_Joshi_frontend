abstract class Animal {
    String name;
    
    //abstract methods have no body
    abstract void makeSound();

    //concrete method
    void sleep() {
        System.out.println("sleeping");
    }
}

//an nterface defines a blueprint of a class
interface Pet {
    void play(); //abstract by default
}

class Dog extends Animal implements Pet {
    @Override
    void makeSound() {
        System.out.println("bark");
    }

    @Override
    public void play() {
        System.out.println("dog is playing fetch.");
    }
}

public class AbstractClasses_Interfaces {
    public static void main(String[] args) {
        Dog myDog = new Dog();
        myDog.makeSound();
        myDog.play();
        myDog.sleep();
    }
}
