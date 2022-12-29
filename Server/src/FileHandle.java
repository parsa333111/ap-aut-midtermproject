import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileHandle {
    private int port;
    private ConcurrentLinkedQueue<ClientHandler> clients = new ConcurrentLinkedQueue<ClientHandler>();

    /**
     * constructor filehandler
     * @param port that connection socket
     */
    public FileHandle(int port) {
        FileInputStream fIn = null;
        this.port = port;
    }

    /**
     * server starter
     */
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("FileHandler is running on port :" + port);
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

    /**
     * clienthandler thread
     */
    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutput objectOutput;
        private ObjectInput objectInput;

        private String ip;

        private OutputStream os;

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
         * run thread to give files or send
         */
        @Override
        public void run() {
            try {
                while (true) {
                    Object object =  objectInput.readObject();
                    if(object instanceof Msg) {
                        Msg msg = (Msg) object;
                        System.out.println("*******File : " + msg.getOwner() + " " + msg.getType() + " " + msg.getText());
                        if(msg.getType().equals("join")) {
                            System.out.println(msg.getOwner().toString() + " is joined to server");
                            this.ip = msg.getId();
                            this.clientName = msg.getOwner();
                            clients.add(this);
                        }
                        else if(msg.getType().equals("StartFile")) {
                            os = new FileOutputStream(msg.getChatId() + "#" + msg.getText());
                            Discord.dataBase.addFileToChannel(msg.getChatId(), msg.getChatId() + "#" + msg.getText());
                        }
                        else if(msg.getType().equals("GetFile")) {
                            File myfile = new File(msg.getText());

                            FileInputStream fis = new FileInputStream(myfile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            byte[] bytes = bis.readAllBytes();
                            objectOutput.writeObject(bytes);
                            bis.close();
                        }
                    }
                    else {
                        byte[] byteArray = (byte[]) object;
                        os.write(byteArray, 0, byteArray.length);
                        os.close();
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
         * @param msg to send
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
