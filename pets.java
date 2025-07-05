package lab9;

class Person {
    String name;
    int age;
    Pet ownedPet;
}

abstract class Pet {
    String name;
    Person owner;
    int age;
    float weight;

    abstract void makeNoise();
}

class Cat extends Pet {
    String favouriteBed;
    int numMiceEaten;
    void eat(String food) {
        System.out.println(this.name + " is eating " + food);
    }
    void sleep() {
        System.out.println(this.name + " is sleeping");
    }
    void hunt(String prey) {
        System.out.println(this.name + " is hunting " + prey);
    }
    void purr() {
        System.out.println(this.name + " is purring");
    }
    void makeNoise() {
        purr();
    }
}

class Dog extends Pet {
    String favouriteBone;
    void bark() {
        System.out.println(this.name + " is barking");
    }
    void makeNoise() {
        bark();
    }
}

class Duck extends Pet {
    String favouriteBread;
    float lengthOfBeak;
    void quack() {
        System.out.println(this.name + " is quacking");
    }
    void makeNoise() {
        quack();
    }
}
