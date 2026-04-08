import java.util.Scanner;

public class FibonacciSequence {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Fibonacci sequence upto: ");
        
        if (sc.hasNextInt()) {
            int limit = sc.nextInt();
            
            int firstTerm = 0;
            int secondTerm = 1;
            
            System.out.println("Fibonacci Sequence up to " + limit + ":");
            
            //using a while loop because we don't know the exact 
            // number of iterations
            while (firstTerm <= limit) {
                System.out.print(firstTerm + " ");
                int nextTerm = firstTerm + secondTerm;
                firstTerm = secondTerm;
                secondTerm = nextTerm;
            }
            System.out.println(); 
            
        } else {
            System.out.println("invalid input");
        }
        
        sc.close();
    }
}