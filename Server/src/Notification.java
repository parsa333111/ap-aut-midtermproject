import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * notification thread
 */
public class Notification {
    private int port;
    private ConcurrentLinkedQueue<ClientHandler> clients = new ConcurrentLinkedQueue<ClientHandler>();

    /**
     * constructor of notification
     * @param port
     */
    public Notification(int port) {
        FileInputStream fIn = null;
        this.port = port;
    }

    /**
     * server starter
     */
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Notification is running on port :" + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new ClientHandler(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * send a notification to all
     * @param sender of notification
     * @param msg to send
     */
    private void sendNotification(String sender, Msg msg) {
        Iterator iteratorClients = clients.iterator();
        while (iteratorClients.hasNext()) {
            ClientHandler clientHandler = (ClientHandler) iteratorClients.next();
            System.out.println("**NOTIFY : " + clientHandler.clientName + " " + clientHandler.ip);
            if(clientHandler.clientName.equals(sender)) {
                clientHandler.sendToClient(msg);
                return;
            }
        }
    }

    public static int t = 0;

    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutput objectOutput;
        private ObjectInput objectInput;

        private String ip;

        private String clientName;
        /**
         * constructor
         * @param socket
         */
        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                this.ip = "";
                this.objectInput = new ObjectInputStream(socket.getInputStream());
                this.objectOutput = new ObjectOutputStream(socket.getOutputStream());
                this.clientName = "";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /**
         * geter of clientname
         * @return clientname
         */
        public String getClientName() {
            return clientName;
        }
        /**
         * run thread to give notification or send
         */
        @Override
        public void run() {
            try {
                while (true) {
                    Msg msg = (Msg) objectInput.readObject();
                    System.out.println("*******notify : " + msg.getOwner() + " " + msg.getType() + " " + msg.getText());
                    if(msg.getType().equals("join")) {
                        System.out.println(msg.getOwner().toString() + " is joined to server");
                        this.ip = msg.getId();
                        this.clientName = msg.getOwner();
                        clients.add(this);
                    }
                    else {
                        //ChatChannel chatChannel = Discord.dataBase.getChannel(msg.getText());
                        sendNotification(msg.getOwner(), new Msg(msg.getId(), msg.getOwner(), msg.getText(), "Notification"));
                    }
                }
            } catch (ClassNotFoundException e) {
                System.out.println("new error");
                e.printStackTrace();
            } catch (IOException e) {
                clients.remove(this);
            }

        }

        /**
         * send to client a file or msg
         * @param msg that wanna to send
         */
        private synchronized void sendToClient(Msg msg) {
            try {
                objectOutput.writeObject(msg);
                //objectOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}