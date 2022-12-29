import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.Socket;

/**
 * server thread of voide massage
 */
public class ServerThread implements Runnable {
    Socket client;

    byte[] data = new byte[4096];

    public ServerThread(Socket client) throws LineUnavailableException {
        this.client = client;
    }

    /**
     * run of thread
     */
    @Override
    public void run() {
        while (true) {
            try {
                client.getInputStream().read(data);
                broadcastToAllClients(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * send a data to all clients
     * @param data to send
     * @throws IOException for Exception
     */
    public void broadcastToAllClients(byte[] data) throws IOException {
        for (Socket client : StackServer.clients) {
            if (client == this.client)
                continue;
            client.getOutputStream().write(data);
        }

    }
}
