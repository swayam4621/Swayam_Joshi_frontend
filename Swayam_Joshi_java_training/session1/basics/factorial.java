import java.util.Scanner;

public class factorial {
    // there are two ways to calculate factorial
    // 1. iterative Approach (Using a loop)
    public static long factorialIterative(int n) {
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    // 2 recursive Approach (Function calling itself)
    public static long factorialRecursive(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a number: ");
        
        if (sc.hasNextInt()) {
            int num = sc.nextInt();

            if (num < 0) {
                System.out.println("factorial for negative numbers does not exist");
            } else {
                System.out.println("FACTORIAL OF GIVEN NUMBER IS: ");
                System.out.println("iterative approach: " + factorialIterative(num));
                System.out.println("recursive approach: " + factorialRecursive(num));
            }
        } else {
            System.out.println("Please enter a valid integer.");
        }
        sc.close();
    }
}