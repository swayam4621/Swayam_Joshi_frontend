import java.util.Scanner;

public class Exceptions {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        try {
            System.out.print("enter numerator ");
            int a = sc.nextInt();
            System.out.print("enter denominator: ");
            int b = sc.nextInt();

            int result = a / b;
            System.out.println("Result: " + result);

        } catch (ArithmeticException e) {
            System.out.println("Exception cannot divide by zero");
        } catch (Exception e) {
            System.out.println("Exception occured " + e.getMessage());
        } finally {
            System.out.println("Execution complete...");
            sc.close();
        }
    }
}
