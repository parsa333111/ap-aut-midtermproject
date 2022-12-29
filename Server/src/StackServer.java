import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class StackServer extends Thread {
    public static ArrayList<Socket> clients = new ArrayList<>();

    /**
     * handle joining of client for group call
     */
    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(5454);
            while (true) {
                Socket client = server.accept();
                clients.add(client);
                Thread thread = new Thread(new ServerThread(client));
                thread.start();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

