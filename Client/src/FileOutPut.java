import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FileOutPut {
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
     * @param ip for create connection between client and server
     * @param port  for create connection between client and server
     * @param id of client
     * @param client username of client
     */
    public FileOutPut(String ip, int port, String id, String client) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.client = client;
    }

    /**
     * create connection between server and client
     */
    public void start() {
        try {
            socket = new Socket(ip, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(new Msg(id, client,"", "join"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * send file between client and server
     * @param chatId is id of chatChannel
     * @param name of file
     * @param location of file
     * @throws IOException may throw that
     */
    public void sendFile(String chatId, String name, String location) throws IOException {
        File myfile = new File(location);
        FileInputStream fis = new FileInputStream(myfile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        Msg msg = new Msg(id, client, location, "StartFile");
        msg.setChatId(chatId);
        outputStream.writeObject(msg);
        byte[] bytes = bis.readAllBytes();
        outputStream.writeObject(bytes);
        bis.close();
    }

    /**
     *
     * @param location of file which receive from server to save
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void recivie(String location) throws IOException, ClassNotFoundException {
        outputStream.writeObject(new Msg(id, client, location, "GetFile"));

        OutputStream os = new FileOutputStream(client + "#" + location);
        byte[] byteArray = (byte[]) objectInputStream.readObject();
        os.write(byteArray, 0, byteArray.length);
        os.close();
    }

    /**
     * close socket
     * @throws IOException may throw that
     * @throws InterruptedException may throw that
     */
    public void stop() throws IOException, InterruptedException {
        socket.close();
    }
}
