import java.util.Scanner;

//encapsulation
class Student {
    private String name;
    private int rollNumber;
    private double marks;

    //constructor
    public Student(String name, int rollNumber, double marks) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.marks = marks;
    }

    public String getName() { 
        return name; 
        }
    public void setName(String name) {
        this.name = name; 
        }
     
    public void study() {
        System.out.println(name + " is studying general subjects.");
    }

    public void displayDetails() {
        System.out.println("\n--- Student Details ---");
        System.out.println("Name: " + name + " | Roll No: " + rollNumber + " | Marks: " + marks);
    }
}
 //the GraduateStudent class inherits the student class
class GraduateStudent extends Student {
    private String researchTopic;

    public GraduateStudent(String name, int rollNumber, double marks, String researchTopic) {
        //parent class constructor
        super(name, rollNumber, marks);
        this.researchTopic = researchTopic;
    }

    //@override used for method overriding
    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Research Topic: " + researchTopic);
    }
    public void study(String subject) {
        System.out.println(getName() + " is specializing in " + subject + " for research.");
    }
}

public class StudentSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("--- Register Graduate Student ---");
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Roll Number: ");
        int roll = sc.nextInt();
        System.out.print("Enter Marks: ");
        double marks = sc.nextDouble();
        sc.nextLine(); // Consume buffer
        System.out.print("Enter Research Topic: ");
        String topic = sc.nextLine();

        //object created of GraduateStudent class
        GraduateStudent grad = new GraduateStudent(name, roll, marks, topic);

        grad.displayDetails();
        grad.study();           //calls parent version 
        grad.study("Big Data"); //calls child version 

        sc.close();
    }
}