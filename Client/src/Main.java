import java.util.Scanner;

public class Main {
    /**
     * first of client app
     * @param args unused parameter
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

//        System.out.println("insert your name");
//        String name = scanner.nextLine();


        Client client = new Client("127.0.0.1", 2000, RandomToken.randomString(20));
        client.start();
        //"192.168.136.18"
    }
}