public class usingOperators {
    public static void main(String[] args) {
        int a = 10;
        int b = 5;
        boolean x = true;
        boolean y = false;

        // 1. Arithmetic Operators
        System.out.println("--- Arithmetic Operators ---");
        System.out.println("addition (a + b): " + (a + b));
        System.out.println("subtraction (a - b): " + (a - b));
        System.out.println("multiplication (a * b): " + (a * b));
        System.out.println("division (a / b): " + (a / b));
        System.out.println("Modulo (a % b): " + (a % b));

        // 2. Relational Operators
        System.out.println("\n--- Relational Operators ---");
        System.out.println("is equal to (a == b): " + (a == b));
        System.out.println("is not equal to (a != b): " + (a != b));
        System.out.println("greater than (a > b): " + (a > b));
        System.out.println("less than or equal to (a <= b): " + (a <= b));

        // 3. Logical Operators
        System.out.println("\n--- Logical Operators ---");
        System.out.println("AND (x && y): " + (x && y)); //true if both are true
        System.out.println("OR (x || y): " + (x || y));   //tru if at least one is true
        System.out.println("NOT (!x): " + (!x));         //opposite
        
       
    }
}