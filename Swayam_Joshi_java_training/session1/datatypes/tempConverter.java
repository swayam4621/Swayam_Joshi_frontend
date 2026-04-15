import java.util.Scanner;

public class tempConverter {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("--- Temperature Converter ---");
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");
        System.out.print("Choose 1 or 2: ");
        int choice = sc.nextInt();

        if (choice == 1) {
            System.out.print("Enter temperature in Celsius: ");
            double celsius = sc.nextDouble();
            double fahrenheit = (celsius * 9.0 / 5.0) + 32;
            System.out.println(celsius + "°C is equal to " + fahrenheit + "°F");
        } 
        else if (choice == 2) {
            System.out.print("Enter temperature in Fahrenheit: ");
            double fahrenheit = sc.nextDouble();
            double celsius = (fahrenheit - 32) * 5.0 / 9.0;
            System.out.println(fahrenheit + "°F is equal to " + celsius + "°C");
        } 
        else {
            System.out.println("Invalid choice. Please select 1 or 2.");
        }

        sc.close();
    }
}