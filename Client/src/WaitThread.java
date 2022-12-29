import java.util.Scanner;

public class WaitThread extends Thread {
    /**
     * thread for waiting may need it in call
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("1.Exit from waiting");
                int operation = scanner.nextInt();
                if(operation == 1) break;
            }
            catch (NumberFormatException numberFormatException) {
                System.out.println("Invalid input");
            }
        }
    }
}
