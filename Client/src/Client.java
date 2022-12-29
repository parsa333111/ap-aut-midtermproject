import jdk.swing.interop.SwingInterOpUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String ip;
    private final int port;
    private final String id;
    private ClientData client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream outputStream;
    private Scanner scanner;

    private ChatHandler chatHandler;

    private ServerHandler serverHandler;

    public static Notification notification;

    public static FileOutPut fileOutPut;


    /**
     * constructor
     * @param ip of client
     * @param port of client
     * @param id of client
     */
    public Client(String ip, int port, String id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.client = new ClientData("", "", "", "");
    }
    /**
     * updtae client data and sign in
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    private void UpdClient() throws IOException, ClassNotFoundException {
        outputStream.writeObject(new Msg(id, client.getUsername(), client.getPassword(), "SignInUser"));
        Msg upd = (Msg) objectInputStream.readObject();
        client = upd.getClientData();
    }
    /**
     * need for notification(handle notification)
     */
    private void notificationHandleStart() {
        notification = new Notification("127.0.0.1", 2001, RandomToken.randomString(20), client.getUsername());
        notification.start();
        fileOutPut = new FileOutPut("127.0.0.1", 2002, RandomToken.randomString(20), client.getUsername());
        fileOutPut.start();
    }
    /**
     * stop notification handling
     * @throws IOException may throw that
     * @throws InterruptedException may throw that
     */
    private void notificationHandleStop() throws IOException, InterruptedException {
        notification.stop();
        fileOutPut.stop();
    }

    /**
     * enter menu for client
     */
    private void enterMenu() {

        while(true) {
            try {
                System.out.println("1. Sign Up\n2. Sign In");
                int type = Integer.parseInt(scanner.nextLine());
                if (type != 1 && type != 2) {
                    System.out.println("Enter a Number in Range [1, 2]");
                    continue;
                }
                if (type == 1) {
                    System.out.print("Username : ");
                    String username = scanner.nextLine();
                    System.out.print("Password : ");
                    String password = scanner.nextLine();
                    System.out.print("Confirm Password : ");
                    String confPass = scanner.nextLine();
                    System.out.print("Email : ");
                    String email = scanner.nextLine();
                    System.out.print("phoneNumber (This can be avoided) : ");
                    String phoneNumber = scanner.nextLine();
                    if (!username.matches("^[A-Za-z\\d]{6,}$")) {
                        System.out.println("Invalid Username form");
                        continue;
                    }
                    if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
                        System.out.println("Invalid password form");
                        continue;
                    }
                    if(!email.matches("^[a-zA-Z\\d_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z\\d.-]+$")) {
                        System.out.println("Invalid email form");
                        continue;
                    }
                    if(!phoneNumber.equals("") && !phoneNumber.matches("^(\\+98|0)?9\\d{9}$")) {
                        System.out.println("Invalid Phone Number");
                        continue;
                    }
                    if(!password.equals(confPass)) {
                        System.out.println("Password wasn't same as confirm password");
                        continue;
                    }
                    outputStream.writeObject(new Msg(id, "", username, "CheckUser"));
                    Msg msg = (Msg) objectInputStream.readObject();
                    if(msg.getText().equals("True")) {
                        System.out.println("Username Used Already");
                        continue;
                    }
                    System.out.println("Successfully Sign Up");
                    Msg send = new Msg(id, "", "", "AddUser");
                    send.setClientData(new ClientData(username, password, email, phoneNumber));

                    outputStream.writeObject(send);
                    continue;
                }
                else {
                    System.out.print("Username : ");
                    String username = scanner.nextLine();
                    System.out.print("Password : ");
                    String password = scanner.nextLine();
                    if (!username.matches("^[A-Za-z\\d]{6,}$")) {
                        System.out.println("Invalid Username form");
                        continue;
                    }
                    if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
                        System.out.println("Invalid password form");
                        continue;
                    }
                    outputStream.writeObject(new Msg(id, username, password, "SignInUser"));
                    Msg msg = (Msg) objectInputStream.readObject();
                    if(msg.getType().equals("InvalidUsername")){
                        System.out.println("Invalid Username");
                        continue;
                    }
                    else if(msg.getType().equals("InvalidPassword")) {
                        System.out.println("Invalid Password");
                        continue;
                    }
                    else {
                        System.out.println("Welcome");
                        client = msg.getClientData();
                        chatHandler = new ChatHandler(objectInputStream, outputStream, scanner, id, client.getUsername());
                        serverHandler = new ServerHandler(objectInputStream, outputStream, scanner, id, client.getUsername());
                        notificationHandleStart();
                    }
                }
                break;
            }
            catch(NumberFormatException e) {
                System.out.println("Enter a Valid Number");
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    /**
     * handle friends menu for client
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     * @throws InterruptedException may throw that
     */
    private void FriendsMenu() throws IOException, ClassNotFoundException, InterruptedException {
        while (true) {

            UpdClient();
            int cnt = 0;
            for (String friend : client.getFriends()) {
                cnt = cnt + 1;
                outputStream.writeObject(new Msg(id, friend, "", "IsOnline"));
                Msg msg = (Msg)objectInputStream.readObject();
                String status = "";
                if(msg.getType().equals("Online")) {
                    outputStream.writeObject(new Msg(id, friend, "", "getStatus"));
                    msg = (Msg)objectInputStream.readObject();
                    status = msg.getText();
                }
                else {
                    status = "Offline";
                }
                System.out.println(cnt + ". " + friend + " " + status);
            }
            System.out.println("\n1. Add Friend\n2. Remove Friend\n3. Request List\n4. Open Private Chat\n5. Open voice chat\n6. Exit");
            int typ = Integer.parseInt(scanner.nextLine());
            if (typ == 6) {
                break;
            }
            if (typ != 1 && typ != 2 && typ != 3 && typ != 4 && typ != 5) {
                System.out.println("Invalid Input, enter a number in range [1, 6]");
                continue;
            }
            if (typ == 1) {
                UpdClient();
                System.out.print("Enter Friend Username : ");
                String user = scanner.nextLine();
                if (!user.matches("^[A-Za-z\\d]{6,}$")) {
                    System.out.println("Invalid Username form");
                    continue;
                }
                if (client.getUsername().equals(user)) {
                    System.out.println("Absolutely You are friend with yourself :)");
                    continue;
                }
                Msg send = new Msg(id, client.getUsername(), user, "RequestFriend");
                outputStream.writeObject(send);
                Msg msg = (Msg) objectInputStream.readObject();
                //noinspection SingleStatementInBlock,IfCanBeSwitch
                if (msg.getType().equals("UsernameNotFound")) {
                    System.out.println("Username Not Found");
                } else if (msg.getType().equals("AddedBefore")) {
                    System.out.println("This User Added to Friend Before");
                } else if (msg.getType().equals("BlockUser")) {
                    System.out.println("This User Block You!");
                } else if (msg.getType().equals("RequestSend")) {
                    System.out.println("Request Send");
                } else {
                    System.out.println("ServerError");
                }
            }
            else if(typ == 2) {
                UpdClient();
                cnt = 0;
                for (String friend : client.getFriends()) {
                    cnt = cnt + 1;
                    System.out.println(cnt + ". " + friend);
                }
                System.out.println("Enter Id : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if(tp > cnt || tp < 1) {
                    System.out.println("out of Range Id Numbers");
                    continue;
                }
                outputStream.writeObject(new Msg(id, client.getUsername(), client.getFriends().get(tp - 1), "RemoveFriend"));
                System.out.println("Successfully Removed");
            }
            else if(typ == 3) {
                UpdClient();
                System.out.println("1. Add Friend\n2. Delete From Request List");
                int ht = Integer.parseInt(scanner.nextLine());
                if(ht != 1 && ht != 2) {
                    System.out.println("Out Of Range");
                    continue;
                }
                cnt = 0;
                for (String friend : client.getFriendRequests()) {
                    cnt = cnt + 1;
                    System.out.println(cnt + ". " + friend);
                }
                if(ht == 1)
                    System.out.print("Enter 0 to Exit or Id of FriendRequest to add to Friend List : ");
                else
                    System.out.print("Enter 0 to Exit or Id of Request to delete From List : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if(tp > cnt || tp < 0) {
                    System.out.println("out of Range Id Numbers");
                    continue;
                }
                if(tp == 0) {
                    continue;
                }
                if(ht == 1) {
                    System.out.println(client.getFriendRequests().get(tp - 1));
                    System.out.println("Successfully Add Friend");
                    outputStream.writeObject(new Msg(id, client.getUsername(), client.getFriendRequests().get(tp - 1), "AddFriend"));
                }
                else {
                    System.out.println(client.getFriendRequests().get(tp - 1));
                    System.out.println("Successfully Remove Request");
                    outputStream.writeObject(new Msg(id, client.getUsername(), client.getFriendRequests().get(tp - 1), "RemoveRequest"));
                }
            }
            else if(typ == 5) {
                UpdClient();
                cnt = 0;
                for (String friend : client.getFriends()) {
                    cnt = cnt + 1;
                    System.out.println(cnt + ". " + friend);
                }
                System.out.print("Enter Id : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if (tp > cnt || tp < 1) {
                    System.out.println("out of Range Id Numbers");
                    continue;
                }
                System.out.println("Welcome to voiceChat \nyour friend must come to your meeting");
                Msg send = new Msg("", "", "", "SetServerForCall");
                outputStream.writeObject(send);
                Thread.sleep(5000);
                Socket client = new Socket("127.0.0.1", 5454);
                Thread inputThread;
                Thread outputThread;
                try {
                    inputThread = new Thread(new InputThread(client));
                    outputThread = new Thread(new OutputThread(client));
                    inputThread.start();
                    outputThread.start();
                    System.out.println("Enter any thing to finish call");
                    new Scanner(System.in).nextLine();
                    inputThread.stop();
                    outputThread.stop();
                    Msg msg = new Msg("", "", "", "DeleteCallThread");
                    outputStream.writeObject(msg);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                UpdClient();
                cnt = 0;
                for (String friend : client.getFriends()) {
                    cnt = cnt + 1;
                    System.out.println(cnt + ". " + friend);
                }
                System.out.print("Enter Id : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if (tp > cnt || tp < 1) {
                    System.out.println("out of Range Id Numbers");
                    continue;
                }
                System.out.println("Welcome to Chat ");
                String friend = client.getFriends().get(tp - 1);
                outputStream.writeObject(new Msg(id, client.getUsername(), friend, "GetPrivateChat"));
                Msg msg = (Msg) objectInputStream.readObject();
                chatHandler.run(msg.getChatChannel());
            }
        }
    }
    /**
     * handle block menu for client
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    private void BlocksMenu() throws IOException, ClassNotFoundException {
        while (true) {
            UpdClient();
            int cnt = 0;
            for(String block : client.getBlocks()) {
                cnt++;
                System.out.println(cnt + ". " + block);
            }
            System.out.println("\n1. Add BlockUser\n2. Remove BlockUser\n3. Exit");
            int typ = Integer.parseInt(scanner.nextLine());
            if(typ == 3) {
                break;
            }
            if(typ != 2 && typ != 1) {
                System.out.println("Invalid input form enter a number in range 1 to 3");
                continue;
            }
            if(typ == 1) {
                UpdClient();
                System.out.print("Enter Friend Username : ");
                String user = scanner.nextLine();
                if (!user.matches("^[A-Za-z\\d]{6,}$")) {
                    System.out.println("Invalid Username form");
                    continue;
                }
                if (client.getUsername().equals(user)) {
                    System.out.println("Absolutely You are friend with yourself :)");
                    continue;
                }
                Msg send = new Msg(id, client.getUsername(), user, "BlockUser");
                outputStream.writeObject(send);
                Msg msg = (Msg) objectInputStream.readObject();
                //noinspection SingleStatementInBlock,IfCanBeSwitch
                if (msg.getType().equals("UsernameNotFound")) {
                    System.out.println("Username Not Found");
                } else if (msg.getType().equals("AddedBefore")) {
                    System.out.println("This User Added to Friend Before");
                } else if (msg.getType().equals("SuccessfullyAddBlockList")) {
                    System.out.println("Successfully Added to BlockList");
                } else {
                    System.out.println("ServerError");
                }
            }
            else {
                UpdClient();
                cnt = 0;
                for (String block : client.getBlocks()) {
                    cnt = cnt + 1;
                    System.out.println(cnt + ". " + block);
                }
                System.out.print("Enter Id : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if(tp > cnt || tp < 1) {
                    System.out.println("out of Range Id Numbers");
                    continue;
                }
                outputStream.writeObject(new Msg(id, client.getUsername(), client.getBlocks().get(tp - 1), "RemoveBlock"));
                System.out.println("Successfully Removed");
            }
        }
    }
    /**
     * client can join server with this method using link of server
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    private void JoinServer() throws IOException, ClassNotFoundException {
        UpdClient();
        System.out.println("Enter Link :");
        String link = scanner.nextLine();
        Msg send = new Msg(id, client.getUsername(), link, "JoinServer");
        outputStream.writeObject(send);
        Msg msg = (Msg) objectInputStream.readObject();
        if(msg.getType().equals("ServerNotFound")) {
            System.out.println("Invalid Server");
        }
        else if(msg.getType().equals("ClientBeforeInServer")) {
            System.out.println("Client Before In Server");
        }
        else {
            System.out.println("Successfully Add To Server");
        }
    }
    /**
     * client create server with this method
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    private void CreateServer() throws IOException, ClassNotFoundException {
        UpdClient();
        System.out.println("Enter Name Of Server");
        String serverName = scanner.nextLine();
        Msg send = new Msg(id, client.getUsername(), serverName, "CreateServer");
        outputStream.writeObject(send);
        System.out.println("Server Successfully Added");
    }
    /**
     * print server list for client
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     * @throws InterruptedException may throw that
     */
    private void ServerList() throws IOException, ClassNotFoundException, InterruptedException {
        UpdClient();
        int cnt = 0;
        for(String server : client.getServers()) {
            cnt++;
            Msg send = new Msg(id, client.getUsername(), server, "GetServer");
            outputStream.writeObject(send);
            Msg msg = (Msg) objectInputStream.readObject();
            System.out.println(cnt + ".[\nId : " + msg.getServer().getName() + "\nOwner : " + msg.getServer().getOwner() + "\nlink : " + msg.getServer().getId() + "\n]");
        }
        while(true) {
            UpdClient();
            System.out.println("1. Enter a Server\n2. Exit");
            System.out.print("Id : ");
            int type = Integer.parseInt(scanner.nextLine());
            if(type == 1) {
                cnt = 0;
                for (String server : client.getServers()) {
                    cnt++;
                    Msg send = new Msg(id, client.getUsername(), server, "GetServer");
                    outputStream.writeObject(send);
                    Msg msg = (Msg) objectInputStream.readObject();
                    System.out.println(cnt + ".[\nId : " + msg.getServer().getName() + "\nOwner : " + msg.getServer().getOwner() + "\nlink : " + msg.getServer().getId() + "\n]");
                }
                System.out.print("Enter The Id of Server : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if (tp < 1 || tp > cnt) {
                    System.out.println("Invalid Input");
                    continue;
                }
                serverHandler.run(client.getServers().get(tp - 1));
            }
            else {
                return;
            }
        }
    }
    /**
     * print status menu for client
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    private void StatusMenu() throws IOException, ClassNotFoundException {
        UpdClient();
        System.out.println("Current Status : " + client.getStatus());
        System.out.println("1. Online\n2. Idle\n3. Do Not Disturb\n4. Invisible");
        int type = Integer.parseInt(scanner.nextLine());
        if(type == 1)
            client.setStatus("Online");
        else if(type == 2)
            client.setStatus("Idle");
        else if(type == 3)
            client.setStatus("Do Not Disturb");
        else if(type == 4)
            client.setStatus("Invisible");
        else
            System.out.println("Invalid Input Form");
        outputStream.writeObject(new Msg(id, client.getUsername(), client.getStatus(), "SetStatus"));
    }
    /**
     * print setting menu for client
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    private void SettingMenu() throws IOException, ClassNotFoundException {
        while(true) {
            System.out.println("1. Change Status\n2. Photo\n3. Exit");
            int type = Integer.parseInt(scanner.nextLine());
            if(type == 1)
                StatusMenu();
            else if(type == 2) {
                photoMenu();
            }
            else if(type == 3)
                break;
            else
                System.out.println("Invalid Input Range");
        }
    }
    /**
     * client have saved massage with this method
     * @throws IOException may throw that
     * @throws InterruptedException may throw that
     * @throws ClassNotFoundException may throw that
     */
    private void savedMassage() throws IOException, InterruptedException, ClassNotFoundException {
        System.out.println("Welcome to Chat ");
        outputStream.writeObject(new Msg(id, client.getUsername(), client.getUsername(), "GetPrivateChat"));
        Msg msg = (Msg) objectInputStream.readObject();
        chatHandler.run(msg.getChatChannel());
    }
    /**
     * handle and print client first menu
     */
    private void clientMenu() {
        while(true) {
            try {
                UpdClient();
                System.out.println("0. LogOut\n1. Friends\n2. Block Users\n3. Join A Server By Link\n4. Create A Server\n5. ServerList\n6. SavedMassage\n7. Setting");
                int type = Integer.parseInt(scanner.nextLine());
                if(type == 0) {
                    notificationHandleStop();
                    break;
                }
                if(type == 1) {
                    FriendsMenu();
                }
                else if(type == 2) {
                    BlocksMenu();
                }
                else if(type == 3) {
                    JoinServer();
                }
                else if(type == 4) {
                    CreateServer();
                }
                else if(type == 5) {
                    ServerList();
                }
                else if(type == 6) {
                    savedMassage();
                }
                else if(type == 7) {
                    SettingMenu();
                }
                else {
                    System.out.println("Invalid Type Range");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a Valid Number");
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * client starting menu create and use socket
     */
    public void start(){
        try {
            Socket socket = new Socket(ip, port);
            //ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            //outputStream.writeObject(new Msg(name,"","join"));
            //Scanner scanner = new Scanner(System.in);
            scanner = new Scanner(System.in);
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            Msg msg = new Msg(id, client.getUsername(),"", "join");
            outputStream.writeObject(msg);

            while(true) {
                enterMenu();

                clientMenu();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * menu for photo setting which print menu
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void photoMenu() throws IOException, ClassNotFoundException {
        System.out.println("1.show photo");
        System.out.println("2.change photo");
        String operation = scanner.nextLine();
        if(operation.equals("1")) {
            showPhoto();
        }
        else if(operation.equals("2")) {
            changePhoto();
        }
        else {
            System.out.println("Invalid input");
        }
    }
    /**
     * change photo method which client use for change photo
     * @throws IOException may throw that
     */
    public void changePhoto() throws IOException {
        System.out.println("Enter location of new photo");
        String newPhoto = scanner.nextLine();
        Msg send = new Msg(id, client.getUsername(), newPhoto, "ChangePhoto");
        outputStream.writeObject(send);
        System.out.println("successfully changed");
    }

    /**
     * show photo to clients
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void showPhoto() throws IOException, ClassNotFoundException {
        Msg send = new Msg(id, client.getUsername(), "", "GivePhoto");
        outputStream.writeObject(send);
        String photo = ((Msg) objectInputStream.readObject()).getText();
        if(photo == null) {
            System.out.println("Not set photo yet!");
        }
        else {
            System.out.println("Your photo : " + photo);
        }
    }
}