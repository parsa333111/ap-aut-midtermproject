import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Notification {
    private final String ip;
    private final int port;
    private final String id;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream outputStream;

    private String client;

    private volatile boolean shutdown;

    private Socket socket;
    /**
     * constructor
     * @param ip of client which we want send notification
     * @param port which notification listen on
     * @param id of client which we want send notification
     * @param client of client which we want send notification
     */
    public Notification(String ip, int port, String id, String client) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.client = client;
    }
    /**
     * make connection in this method between client and server
     */
    public void start() {
        try {
            socket = new Socket(ip, port);
            //ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            //outputStream.writeObject(new Msg(name,"","join"));
            //Scanner scanner = new Scanner(System.in);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            Msg msg = new Msg(id, client,"", "join");
            outputStream.writeObject(msg);
            Listener listener = new Listener(objectInputStream);
            Thread listenerThread = new Thread(listener);
            listenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * listener method for notification
     */
    private class Listener implements Runnable{
        ObjectInputStream objInputStream;

        public Listener(ObjectInputStream objInputStream) {
            this.objInputStream = objInputStream;
        }

        @Override
        public void run() {
            while (!shutdown) {
                try {
                    Msg msg = (Msg) objInputStream.readObject();
                    System.out.println(msg.getText());
                } catch (IOException | ClassNotFoundException e) {
                    //e.printStackTrace();
                }
            }
        }

    }
    /**
     * stop and close socket of notification
     * @throws IOException may throw that
     * @throws InterruptedException may throw that
     */
    public void stop() throws IOException, InterruptedException {
        shutdown = true;
        socket.close();
    }
}
