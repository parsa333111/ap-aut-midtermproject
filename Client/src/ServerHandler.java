import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerHandler {
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream outputStream;
    private final Scanner scanner;
    private final String id;
    private final String clientUsername;

    private final ChatHandler chatHandler;

    private Server server;

    private final ArrayList<String> roles;
    private String serverId;

    /**
     * constructor
     * @param objectInputStream of client
     * @param outputStream of client
     * @param scanner for give input from console
     * @param id of client
     * @param clientUsername username of client
     */
    public ServerHandler(ObjectInputStream objectInputStream, ObjectOutputStream outputStream, Scanner scanner, String id, String clientUsername) {
        this.objectInputStream = objectInputStream;
        this.outputStream = outputStream;
        this.scanner = scanner;
        this.id = id;
        this.clientUsername = clientUsername;
        chatHandler = new ChatHandler(objectInputStream, outputStream, scanner, id, clientUsername);
        roles = new ArrayList<>();
        roles.add("OpenChannels");
        roles.add("CreateChannel");
        roles.add("RemoveChannel");
        roles.add("RemoveUser");
        roles.add("blockUserFromChannel");
        roles.add("RenameServer");
        roles.add("CreateRoles");
        roles.add("GetRole");
        roles.add("HistoryOfChat");
        roles.add("PinMassage");
    }

    /**
     * update server information
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void updServer() throws IOException, ClassNotFoundException {
        Msg send = new Msg(id, clientUsername, serverId, "GetServer");
        outputStream.writeObject(send);
        Msg msg = (Msg) objectInputStream.readObject();
        if (msg.getType().equals("ServerNotFound")) {
            System.out.println("Server Not Found");
            return;
        }
        server = msg.getServer();
    }

    /**
     * show list of server which client joined
     * @param username of client
     * @return numbet of server which client joined
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public int showChannels(String username) throws IOException, ClassNotFoundException {
        updServer();
        ArrayList <String> st = server.getUserBlockChannel().get(username);
        int cnt = 0;
        for(ChatChannel chatChannel : server.getChatChannels()) {
            if(st.contains(chatChannel.getId()))
                continue;
            cnt++;
            System.out.println(cnt + ". [\nname : " + chatChannel.getName() + "\nLink : " + chatChannel.getId() + "\n]");
        }
        return cnt;
    }

    /**
     * show user of server
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void showUsers() throws IOException, ClassNotFoundException {
        updServer();
        int cnt = 0;
        for(String user : server.getUsers()) {
            cnt++;
            System.out.println(cnt + ". " + user);
        }
    }

    /**
     *
     * @param username is id of server
     * @param tp is tp'th of chatchannel which client is joined
     * @return desire chatchannel
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public ChatChannel getChannels(String username, int tp) throws IOException, ClassNotFoundException {
        ArrayList <String> st = server.getUserBlockChannel().get(username);
        int cnt = 0;
        for(ChatChannel chatChannel : server.getChatChannels()) {
            if(st.contains(chatChannel.getId()))
                continue;
            cnt++;
            if(cnt == tp)
                return chatChannel;
        }
        return null;
    }

    /**
     * handle opening of channel
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     * @throws InterruptedException may throw that
     */
    public void openChannel() throws IOException, ClassNotFoundException, InterruptedException {
        while(true) {
            int en = showChannels(clientUsername);
            System.out.println(en + 1 + ". Exit");
            System.out.print("Enter Id : ");
            int tp = Integer.parseInt(scanner.nextLine());
            if (tp > en + 1 || tp < 1) {
                System.out.println("Invalid Input Out Of Range");
                continue;
            }
            if(tp == en + 1) {
                break;
            }
            else {
                outputStream.writeObject(new Msg(id, clientUsername, serverId, "GetRoleName"));
                Msg msg = (Msg) objectInputStream.readObject();
                outputStream.writeObject(new Msg(id, serverId, msg.getText(), "GetRole"));
                msg = (Msg) objectInputStream.readObject();
                String role = msg.getText();
                boolean isHistory = role.charAt(8) != '0';
                boolean isPin = role.charAt(9) != '0';
                outputStream.writeObject(new Msg(id, clientUsername, serverId, "getTime"));
                msg = (Msg) objectInputStream.readObject();
                chatHandler.run(getChannels(clientUsername, tp), isHistory, isPin, msg.getText(), server.getName());
            }
        }
    }

    /**
     * remove channel for specified client
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void removeChannel() throws IOException, ClassNotFoundException {
        while(true) {
            updServer();
            int en = showChannels(clientUsername);
            System.out.println(en + 1 + ". Exit");
            System.out.print("Enter The Id Of The Channel Wanna to Remove : ");
            int tp = Integer.parseInt(scanner.nextLine());
            if (tp > en + 1 || tp < 1) {
                System.out.println("Invalid Input Out Of Range");
            }
            if(tp == en + 1) {
                break;
            }
            else {
                outputStream.writeObject(new Msg(id, serverId, getChannels(clientUsername, tp).getId(), "DeleteChannelFromServer"));
            }
        }
    }

    /**
     * method for renaming server name
     * @throws IOException may throw that
     */
    public void reNameServer() throws IOException {
        System.out.println("New Name Of Server : ");
        String newName = scanner.nextLine();
        outputStream.writeObject(new Msg(id, serverId, newName, "ChangeServerName"));
    }

    /**
     * method for creating channel
     * @throws IOException may throw that
     */
    public void createChannel() throws IOException {
        System.out.print("Enter ChatChannel Name : ");
        String name = scanner.nextLine();
        Msg send = new Msg(id, clientUsername, serverId, "AddChannel");
        send.setChatId(name);
        outputStream.writeObject(send);
    }

    /**
     * remove user
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void removeUser() throws IOException, ClassNotFoundException {
        while(true) {
            showUsers();
            System.out.println(server.getUsers().size() + 1 + ". Exit");
            System.out.print("Enter Id : ");
            int tp = Integer.parseInt(scanner.nextLine());
            if (tp > server.getUsers().size() + 1 || tp < 1) {
                System.out.println("Invalid Input Out Of Range");
            }
            if (tp == server.getUsers().size() + 1) {
                break;
            } else {
                outputStream.writeObject(new Msg(id, serverId, server.getUsers().get(tp - 1), "RemoveUserFromServer"));
            }
        }
    }

    /**
     * blocj user from accessing chat channel
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void blockUserFromChannel() throws IOException, ClassNotFoundException {
        while(true) {
            showUsers();
            System.out.println(server.getUsers().size() + 1 + ". Exit");
            int tp = Integer.parseInt(scanner.nextLine());
            if (tp > server.getUsers().size() + 1 || tp < 1) {
                System.out.println("Invalid Input Out Of Range");
            }
            if (tp == server.getUsers().size() + 1) {
                break;
            } else {
                String username = server.getUsers().get(tp - 1);
                int en = showChannels(username);
                System.out.print("Channel to Remove Asses : ");
                int t = Integer.parseInt(scanner.nextLine());
                if (t > en || t < 1) {
                    System.out.println("Invalid Input Out Of Range");
                }
                else {
                    Msg msg = new Msg(id, serverId, username, "AddBlockChannel");
                    msg.setChatId(getChannels(username, t).getId());
                    outputStream.writeObject(msg);
                }
            }
        }
    }

    /**
     * create role
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void createRoles() throws IOException, ClassNotFoundException {
        System.out.println("Enter The Name Of Role : ");
        String Name = scanner.nextLine();
        int cnt = 0;
        for(String role : roles)
            System.out.println(++cnt + ". " + role);
        System.out.println("Enter a 10 digit number that the digit number i is 1 that has that role 0 not : ");
        String role = scanner.nextLine();
        if (role.length() != 10) {
            System.out.println("Invalid Input Role Must 10 digit number");
            return;
        }
        Msg msg = new Msg(id, serverId, Name, "AddRole");
        msg.setChatId(role);
        outputStream.writeObject(msg);
    }

    /**
     * get role of someone in server
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void getRole() throws IOException, ClassNotFoundException {
        while(true) {
            showUsers();
            System.out.println(server.getUsers().size() + 1 + ". Exit");
            int tp = Integer.parseInt(scanner.nextLine());
            if (tp > server.getUsers().size() + 1 || tp < 1) {
                System.out.println("Invalid Input Out Of Range");
            }
            if (tp == server.getUsers().size() + 1) {
                break;
            } else {
                String username = server.getUsers().get(tp - 1);
                int cnt = 0;
                for(String role : server.getRolesName().keySet()) {
                    System.out.println(++cnt + ". " + role);
                }
                System.out.print("Id Of The Role : ");
                int t = Integer.parseInt(scanner.nextLine());
                String newRole = "";
                if(t > cnt || t <= 0) {
                    System.out.println("OutOfRange");
                    continue;
                }
                else {
                    cnt = 0;
                    for(String role : server.getRolesName().keySet()) {
                        ++cnt;
                        if(cnt == t) {
                            newRole = role;
                            break;
                        }
                    }
                }
                System.out.println(serverId + " " + newRole + " " + username);
                Msg msg = new Msg(id, serverId, newRole, "SetRole");
                msg.setChatId(username);
                outputStream.writeObject(msg);
            }
        }
    }

    /**
     * handle chatting in server
     * @param ServerId is id of server
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     * @throws InterruptedException may throw that
     */
    void run(String ServerId) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("Enter Play to play music or Enter any thing to skip");
        String playMusic = scanner.nextLine();
        if(playMusic.equals("play")) {
            System.out.println("Enter Music address");
            String path = scanner.nextLine();
            playMusic(path);
        }
        System.out.println("Enter voicechat to Enter voice chat");
        String voice = scanner.nextLine();

        if (voice.equals("voicechat")) {
            System.out.println("Welcome to voiceChat \nyour friends must come to your meeting");
            Msg send = new Msg("", "", "", "SetServerForCall");
            outputStream.writeObject(send);
            Thread.sleep(2000);
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
        serverId = ServerId;
        for( ; ; ) {
            updServer();
            for (String user : server.getUsers()) {
                outputStream.writeObject(new Msg(id, user, "", "IsOnline"));
                Msg msg = (Msg)objectInputStream.readObject();
                String status = "";
                if(msg.getType().equals("Online")) {
                    outputStream.writeObject(new Msg(id, user, "", "getStatus"));
                    msg = (Msg)objectInputStream.readObject();
                    status = msg.getText();
                }
                else {
                    status = "Offline";
                }

                outputStream.writeObject(new Msg(id, user, serverId, "GetRoleName"));
                msg = (Msg)objectInputStream.readObject();
                System.out.println(user + " , " + status + " , " + msg.getText());
            }
            outputStream.writeObject(new Msg(id, clientUsername, serverId, "GetRoleName"));
            Msg msg = (Msg) objectInputStream.readObject();
            outputStream.writeObject(new Msg(id, serverId, msg.getText(), "GetRole"));
            msg = (Msg) objectInputStream.readObject();
            String role = msg.getText();
            if(clientUsername.equals(server.getOwner())) {
                System.out.println("0. Delete Server");
            }
            int cnt = 0;
            for(int i = 0; i < 8; i++) {
                if(role.charAt(i) == '0')
                    continue;
                cnt++;
                System.out.println(cnt + ". " + roles.get(i));
            }
            System.out.println((cnt + 1) + ". Exit");
            System.out.print("Enter Id You Wanna To do : ");
            int type = Integer.parseInt(scanner.nextLine());
            if(type == cnt + 1) {
                break;
            }
            if(type == 0 && clientUsername.equals(server.getOwner())) {
                outputStream.writeObject(new Msg(id, serverId, "", "DeleteServer"));
                return;
            }
            if(type > cnt || type < 1) {
                System.out.println("Out Of Range");
                continue;
            }
            cnt = 0;
            for(int i = 0; i < 8; i++) {
                if(role.charAt(i) == '0')
                    continue;
                cnt++;
                if(cnt == type) {
                    switch ((i + 1)) {
                        case 1:
                            openChannel();
                            break;
                        case 2:
                            createChannel();
                            break;
                        case 3:
                            removeChannel();
                            break;
                        case 4:
                            removeUser();
                            break;
                        case 5:
                            blockUserFromChannel();
                            break;
                        case 6:
                            reNameServer();
                            break;
                        case 7:
                            createRoles();
                            break;
                        default:
                            getRole();
                            break;
                    }
                }
            }
        }
    }

    /**
     * playe music in difference thread
     * @param path of music
     */
    public void playMusic(String path) {
        Thread thread = new Thread(new MusicThread(path));
        thread.start();
    }
}
