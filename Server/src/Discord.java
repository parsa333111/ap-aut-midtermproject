import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Discord {
    private int port;
    private ConcurrentLinkedQueue<ClientHandler> clients = new ConcurrentLinkedQueue<ClientHandler>();
    public static DataBase dataBase;

    /**
     * constructor of discord
     * @param port of socket
     */
    public Discord(int port) {
        FileInputStream fIn = null;
        try {
            fIn = new FileInputStream("save.bin");
            ObjectInputStream in = new ObjectInputStream(fIn);
            dataBase = (DataBase) in.readObject();
            fIn.close();
            in.close();
        } catch (FileNotFoundException e) {
            dataBase = new DataBase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.port = port;
    }

    /**
     * server starter thread
     */
    public void startServer() {
        UpdThread updThread = new UpdThread();
        Thread updThreadThread = new Thread(updThread);
        updThreadThread.start();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("server is running on port :" + port);
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
     * send a massage to all clients
     * @param sender of massage
     * @param msg that wanna to send
     */
    private void sendToAll(String sender, Msg msg) {
        Iterator iteratorClients = clients.iterator();
        while (iteratorClients.hasNext()) {
            ClientHandler clientHandler = (ClientHandler) iteratorClients.next();

            if (clientHandler.ip.equals(sender) && msg.getType().equals("msg")) {
                //ignore the msg
            } else if (msg.getType().equals("msg")) {
                //send to others
                clientHandler.sendToClient(msg);
            } else if (msg.getType().equals("join")) {
                //join will send to all even who join the chat room
                clientHandler.sendToClient(msg); // commands return to all clients
            }
        }
    }

    /**
     * send a massage to a client
     * @param sender of massage
     * @param msg to send
     */
    private void msgSendToClient(String sender, Msg msg) {
        Iterator iteratorClients = clients.iterator();
        while (iteratorClients.hasNext()) {
            ClientHandler clientHandler = (ClientHandler) iteratorClients.next();
            System.out.println(clientHandler.ip + " % " + sender);
            if (clientHandler.ip.equals(sender)) {
                System.out.println(msg.getType() + " " + sender + "#");
                if(msg.getType().equals("SuccessfullyUpdate")) {
                    for(String st : msg.getClientData().getFriends()) {
                        System.out.println(st);
                    }
                }
                clientHandler.sendToClient(msg);
                break;
            }
        }
    }

    /**
     * check that a user is online or not
     * @param clientId ofuser
     * @return true if online else false
     */
    private boolean IsOnline(String clientId) {
        Iterator iteratorClients = clients.iterator();
        while (iteratorClients.hasNext()) {
            ClientHandler clientHandler = (ClientHandler) iteratorClients.next();
            System.out.println(clientHandler.ip + " outshow " + clientHandler.getClientName());
            if(clientHandler.getClientName().equals(clientId))
                return true;
        }
        return false;
    }

    /**
     * send to all in chat a massage
     * @param sender of sender
     * @param msg to send
     */
    private void sendToAllInChat(String sender, Msg msg) {
        Iterator iteratorClients = clients.iterator();
        while (iteratorClients.hasNext()) {
            ClientHandler clientHandler = (ClientHandler) iteratorClients.next();
            System.out.println(clientHandler.ip + " " + dataBase.getClient(clientHandler.getClientName()).getCurrentChat().equals(msg.getChatId()));
            if (!clientHandler.ip.equals(sender) && dataBase.getClient(clientHandler.getClientName()).getCurrentChat().equals(msg.getChatId())) {
                System.out.println(msg.getType() + " " + sender + "#");
                clientHandler.sendToClient(msg);
                break;
            }
        }
    }


    public static int t = 0;

    /**
     * client handler thread
     */
    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutput objectOutput;
        private ObjectInput objectInput;

        private String ip;

        private String clientName;

        /**
         * constructor of clienthandler
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
         * geter of name
         * @return
         */
        public String getClientName() {
            return clientName;
        }

        /**
         * thread run to requst and response
         */
        @Override
        public void run() {
            try {
                while (true) {
                    Msg msg = (Msg) objectInput.readObject();
                    System.out.println(msg.getOwner() + " " + msg.getType() + " " + msg.getText());
                    if(msg.getType().equals("UpdateClient")) {
                        Msg send = new Msg(msg.getId(), msg.getOwner(), "", "SuccesfullyUpdate");
                        send.setClientData(dataBase.getClient(msg.getOwner()));
                        for(String s : send.getClientData().getFriends())
                            System.out.println("hello " + s);
                        msgSendToClient(msg.getId(), send);
                    }
                    else if(msg.getType().equals("CheckUser")) {
                        if(!dataBase.checkClient(msg.getText()))
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "False", "CheckUser"));
                        else
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "True", "CheckUser"));
                    }
                    else if(msg.getType().equals("AddUser")) {
                        System.out.println("gav");
                        dataBase.getClientDatas().add(msg.getClientData());
                    }
                    else if(msg.getType().equals("SignInUser")) {
                        String username = msg.getOwner();
                        String password = msg.getText();
                        if (!dataBase.checkClient(username)) {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "InvalidUsername"));
                        } else if (dataBase.checkPassword(username, password)) {
                            Msg send = new Msg(msg.getId(), msg.getOwner(), "", "SuccessfullyUpdate");
                            send.setClientData(new ClientData(dataBase.getClient(username)));
                            msgSendToClient(msg.getId(), send);
                            this.clientName = username;
                        } else {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "InvalidPassword"));
                        }
                    }
                    else if (msg.getType().equals("join")) {
                        System.out.println(msg.getOwner().toString() + " is joined to server");
                        this.ip = msg.getId();
                        clients.add(this);
                    }
                    else if (msg.getType().equals("RequestFriend")) {
                        if (!dataBase.checkClient(msg.getText())) {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "UsernameNotFound"));
                        } else if (dataBase.checkFriend(dataBase.getClient(msg.getOwner()), msg.getText())) {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "AddBefore"));
                        } else if (dataBase.checkBlock(dataBase.getClient(msg.getText()), msg.getOwner())) {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "BlockUser"));
                        } else {
                            dataBase.getClient(msg.getText()).getFriendRequests().add(msg.getOwner());
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "RequestSend"));
                        }
                    }
                    else if (msg.getType().equals("AddFriend")) {
                        System.out.println("New Add Friend" + msg.getOwner() + " " + msg.getText());
                        dataBase.getClient(msg.getOwner()).getFriends().add(msg.getText());
                        dataBase.getClient(msg.getText()).getFriends().add(msg.getOwner());
                        dataBase.getClient(msg.getOwner()).getFriendRequests().remove(msg.getText());
                        System.out.println("******");
                        for(String out : dataBase.getClient(msg.getOwner()).getFriendRequests())
                            System.out.println(out);
                    }
                    else if(msg.getType().equals("GetPrivateChat")) {
                        Msg send = new Msg(msg.getId(), msg.getOwner(), "", "GetChannel");
                        ChatChannel chatChannel = dataBase.getPrivateChatChannel(msg.getOwner(), msg.getText());
                        dataBase.getClient(msg.getOwner()).setCurrentChat(chatChannel.getId());
                        send.setChatChannel(new ChatChannel(chatChannel));
                        msgSendToClient(msg.getId(), send);
                    }
                    else if(msg.getType().equals("ByeFromChat")) {
                        ChatChannel chatChannel = dataBase.getChannel(msg.getText());
                        dataBase.getClient(msg.getOwner()).setCurrentChat("");
                        msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "bye", "SuccessfullyOutFromChat"));
                    }
                    else if(msg.getType().equals("CurrentChannel")) {
                        ChatChannel chatChannel = dataBase.getChannel(msg.getText());
                        dataBase.getClient(msg.getOwner()).setCurrentChat(chatChannel.getId());
                    }
                    else if(msg.getType().equals("RemoveFriend")) {
                        dataBase.getClient(msg.getOwner()).getFriends().remove(msg.getText());
                        dataBase.getClient(msg.getText()).getFriends().remove(msg.getOwner());
                    }
                    else if(msg.getType().equals("BlockUser")) {
                        if (!dataBase.checkClient(msg.getText())) {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "UsernameNotFound"));
                        } else if (dataBase.checkBlock(dataBase.getClient(msg.getOwner()), msg.getText())) {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "AddBefore"));
                        } else {
                            if(dataBase.checkFriend(dataBase.getClient(msg.getOwner()), msg.getText())) {
                                dataBase.getClient(msg.getOwner()).getFriends().remove(msg.getText());
                                dataBase.getClient(msg.getText()).getFriends().remove(msg.getOwner());
                            }
                            dataBase.getClient(msg.getOwner()).getBlocks().add(msg.getText());
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "SuccessfullyAddBlockList"));
                        }
                    }
                    else if(msg.getType().equals("RemoveBlock")) {
                        dataBase.getClient(msg.getOwner()).getBlocks().remove(msg.getText());
                    }
                    else if(msg.getType().equals("ChatMassage")) {
                        dataBase.newMassage(msg.getChatId(), msg.getOwner(), msg.getText());
                        sendToAllInChat(msg.getId(), msg);
                    }
                    else if(msg.getType().equals("SetPinMassage")) {
                        dataBase.newPinMassage(msg.getChatId(), msg.getOwner(), msg.getText());
                        msg.setText("New Pin Massage : " + msg.getText());
                        sendToAllInChat(msg.getId(), msg);
                    }
                    else if(msg.getType().equals("GetChatChannel")) {
                        ChatChannel chatChannel = new ChatChannel(dataBase.getChannel(msg.getText()));
                        Msg send = new Msg(msg.getId(), msg.getOwner(), msg.getText(), "GetChatChannel");
                        send.setChatChannel(chatChannel);
                        msgSendToClient(msg.getId(), send);
                    }
                    else if(msg.getType().equals("JoinServer")) {
                        if(dataBase.checkServer(msg.getText())) {
                            if(dataBase.addClientToServer(msg.getOwner(), msg.getText())) {
                                msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "SuccessfullyAddToServer"));
                            }
                            else {
                                msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "ClientBeforeInServer"));
                            }
                        }
                        else {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "ServerNotFound"));
                        }
                    }
                    else if(msg.getType().equals("CreateServer")) {
                        Server server = new Server(msg.getText(), msg.getOwner());
                        dataBase.addServer(server);
                        dataBase.addClientToServer(msg.getOwner(), server.getId());
                        System.out.println("server create : " + server.getId() + " " + msg.getOwner());
                    }
                    else if(msg.getType().equals("GetServer")) {
                        if(dataBase.checkServer(msg.getText())) {
                            Server server = new Server(dataBase.getServer(msg.getText()));
                            Msg send = new Msg(msg.getId(), msg.getOwner(), "", "ReceiveServer");
                            send.setServer(server);
                            msgSendToClient(msg.getId(), send);
                        }
                        else {
                            msgSendToClient(msg.getId(), new Msg(msg.getId(), msg.getOwner(), "", "ServerNotFound"));
                        }
                    }
                    else if(msg.getType().equals("AddChannel")) {
                        dataBase.addChannelToServer(msg.getText(), msg.getChatId());
                    }
                    else if(msg.getType().equals("GetRoleName")) {
                        String role = dataBase.getRoles(msg.getText(), msg.getOwner());
                        Msg send = new Msg(msg.getId(), msg.getOwner(), role, "GetRole");
                        msgSendToClient(msg.getId(), send);
                    }
                    else if(msg.getType().equals("GetRole")) {
                        String role = dataBase.getRoleNameData(msg.getOwner(), msg.getText());
                        Msg send = new Msg(msg.getId(), msg.getOwner(), role, "GetRole");
                        msgSendToClient(msg.getId(), send);
                    }
                    else if(msg.getType().equals("ChangeServerName")) {
                        dataBase.changeServerName(msg.getOwner(), msg.getText());
                    }
                    else if(msg.getType().equals("DeleteChannelFromServer")) {
                        dataBase.deleteChannelFromServer(msg.getOwner(), msg.getText());
                    }
                    else if(msg.getType().equals("SetRole")) {
                        dataBase.setRole(msg.getOwner(), msg.getText(), msg.getChatId());
                    }
                    else if(msg.getType().equals("SetServerForCall")) {
                        StackServer stackServer = new StackServer();
                        Thread thread = new Thread(new StackServer());
                        thread.start();
                        OpenThread.addThread(thread);
                    }
                    else if(msg.getType().equals("DeleteCallThread")) {
                        //Thread thread = OpenThread.getLast();
                        OpenThread.delete();
                    }
                    else if(msg.getType().equals("RemoveUserFromServer")) {
                        dataBase.removeUserFromServer(msg.getOwner(), msg.getText());
                    }
                    else if(msg.getType().equals("AddBlockChannel")) {
                        dataBase.addBlockChannelFromUser(msg.getOwner(), msg.getText(), msg.getChatId());
                    }
                    else if(msg.getType().equals("SetStatus")) {
                        dataBase.changeStatus(msg.getOwner(), msg.getText());
                    }
                    else if(msg.getType().equals("ChangePhoto")) {
                        dataBase.changePhoto(msg.getOwner(), msg.getText());
                    }
                    else if(msg.getType().equals("GivePhoto")) {
                        String photo = dataBase.showPhoto(msg.getOwner());
                        sendToClient(new Msg(msg.getId(), msg.getOwner(), photo, "GivePhoto"));
                    }
                    else if(msg.getType().equals("IsOnline")) {
                        if(IsOnline(msg.getOwner()))
                            sendToClient(new Msg(msg.getId(), msg.getOwner(), "", "Online"));
                        else
                            sendToClient(new Msg(msg.getId(), msg.getOwner(), "", "Offline"));
                    }
                    else if(msg.getType().equals("getStatus")) {
                        sendToClient(new Msg(msg.getId(), msg.getOwner(), dataBase.getStatus(msg.getOwner()), "Status"));
                    }
                    else if(msg.getType().equals("RemoveRequest")) {
                        dataBase.getClient(msg.getOwner()).getFriendRequests().remove(msg.getText());
                    }
                    else if(msg.getType().equals("AddRole")) {
                        dataBase.setRoleName(msg.getOwner(), msg.getText(), msg.getChatId());
                    }
                    else if(msg.getType().equals("getTime")) {
                        sendToClient(new Msg(msg.getId(), msg.getOwner(), dataBase.getTimeClientFromServer(msg.getOwner(), msg.getText()), "GivePhoto"));
                    }
                    else if(msg.getType().equals("RemoveMassage")) {
                        dataBase.removeMassageFromChat(msg.getText(), msg.getIndex());
                    }
                    else if(msg.getType().equals("EditMassage")) {
                        dataBase.editMassageFromChat(msg.getText(), msg.getIndex(), msg.getOwner());
                    }
                    else if (msg.getType().equals("DeleteServer")) {
                        dataBase.removeServer(msg.getOwner());
                    }
                    else {
                        System.out.println(msg.getOwner() + " -- " + msg.getText());
                        //sendToAll(msg.getOwner(), msg);
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
         * send a massage to client with socket connection
         * @param msg
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