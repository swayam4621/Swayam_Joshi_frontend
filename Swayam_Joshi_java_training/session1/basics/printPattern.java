import java.util.Scanner;

public class printPattern {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of rows for the triangle: ");
        int rows = sc.nextInt();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }

        // for square
        System.out.print("Enter the side length of the square: ");
        int side = sc.nextInt();

        for (int i = 1; i <= side; i++) {
            for (int j = 1; j <= side; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
        sc.close();
    }
}
