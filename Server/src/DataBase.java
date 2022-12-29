import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * DataBase Of Server That Save Into A file
 */
public class DataBase implements Serializable {
    private  ArrayList <ClientData> clientDatas = new ArrayList<>();

    private int maxIdClient;

    private Hashtable<String, String> privateChat;

    private ArrayList<ChatChannel> chatChannels;

    private ArrayList<Server> servers;

    /**
     * Constructor DataBase
     */
    public DataBase() {
        this.clientDatas = new ArrayList<>();
        this.privateChat = new Hashtable<>();
        this.chatChannels = new ArrayList<>();
        this.servers = new ArrayList<>();
        this.maxIdClient = 1;
    }

    /**
     * Change Status Of A client
     * @param clientId client Id
     * @param status status that must set
     */
    public void changeStatus(String clientId, String status) {
        ClientData clientData = getClient(clientId);
        clientData.setStatus(status);
    }

    /**
     * @return Arraylist of clientdata that in database
     */
    public ArrayList<ClientData> getClientDatas() {
        return clientDatas;
    }

    /**
     * check that a server be in a database or not
     * @param id id of server
     * @return true if the server be in database else false
     */
    public boolean checkServer(String id) {
        for(Server server : servers) {
            if(server.getId().equals(id))
                return true;
        }
        return false;
    }

    /**
     * get the chatchannel with id
     * @param id id of the chat channel
     * @return null if chatchannel dos'nt exist else return chatchannel
     */
    public ChatChannel getChannel(String id) {
        for(ChatChannel chatChannel : chatChannels) {
            if(chatChannel.getId().equals(id))
                return chatChannel;
        }
        return null;
    }

    /**
     * set a new massage to a chatchannel
     * @param chatId channel id
     * @param sender sender username
     * @param text of channel
     */
    public void newMassage(String chatId, String sender, String text) {
        Massage massage = new Massage(sender, text, chatId);
        ChatChannel chatChannel = getChannel(chatId);
        chatChannel.getMassages().add(massage);
    }

    /**
     * pin a massage into a chat channel
     * @param chatId id of chat channel
     * @param sender of the pin massage
     * @param text of pin massage
     */
    public void newPinMassage(String chatId, String sender, String text) {
        Massage massage = new Massage(sender, text, chatId);
        ChatChannel chatChannel = getChannel(chatId);
        chatChannel.setPinMassage(massage);
    }

    /**
     * get private chat of two person
     * @param firstPerson username of first person
     * @param secondPerson username of second person
     * @return the chatchannel of two person
     */
    public ChatChannel getPrivateChatChannel(String firstPerson, String secondPerson) {
        String mix1 = firstPerson + "#" + secondPerson;
        String mix2 = secondPerson + "#" + firstPerson;
        if(!privateChat.containsKey(mix1)) {
            ChatChannel chatChannel = new ChatChannel("PrivateChat/" + mix1);
            chatChannels.add(chatChannel);
            privateChat.put(mix1, chatChannel.getId());
            privateChat.put(mix2, chatChannel.getId());
        }
        return getChannel(privateChat.get(mix1));
    }

    /**
     * max client id geter
     * @return maxidclient
     */
    public int getMaxIdClient() {
        return maxIdClient;
    }

    /**
     * seter of max id client
     * @param maxId client
     */
    public void setMaxIdClient(int maxId) {
        this.maxIdClient = maxId;
    }

    /**
     * add client to database
     * @param clientData of client
     */
    public void addClient(ClientData clientData) {
        clientDatas.add(clientData);
    }

    /**
     * get client by it's username
     * @param username of client
     * @return null if there not exist a client with this username else return client data
     */
    public ClientData getClient(String username) {
        for(ClientData clientData : clientDatas) {
            if(clientData.getUsername().equals(username))
                return clientData;
        }
        return null;
    }

    /**
     * check that a client exist with this username or not
     * @param username of client to check
     * @return true if it is else false
     */
    public boolean checkClient(String username) {
        for(ClientData clientData : clientDatas) {
            if(clientData.getUsername().equals(username))
                return true;
        }
        return false;
    }

    /**
     * check password of a client with client username
     * @param username of client
     * @param password of check
     * @return true if password is correct or false
     */
    public boolean checkPassword(String username, String password) {
        for(ClientData clientData : clientDatas) {
            if(clientData.getUsername().equals(username) && clientData.getPassword().equals(password))
                return true;
        }
        return false;
    }

    /**
     * check that a user friend with a client or not
     * @param client clientdata of client
     * @param user that wanna to check
     * @return true if friend else false
     */
    public boolean checkFriend(ClientData client, String user) {
        for(String str : client.getFriends()) {
            if(str.equals(user))
                return true;
        }
        return false;
    }

    /**
     * add cleint to server
     * @param username of client
     * @param serverId of server
     * @return true if add else false
     */
    public boolean addClientToServer(String username, String serverId) {
        for(Server server : servers) {
            if(server.getId().equals(serverId)) {
                if(server.checkClient(username))
                    return false;
                ClientData clientData =  getClient(username);
                clientData.addServer(server.getId());
                server.addUser(username);
                server.getUserBlockChannel().put(username, new ArrayList<>());
                return true;
            }
        }
        return false;
    }

    /**
     * get server with id
     * @param id of server
     * @return server else null
     */
    public Server getServer(String id) {
        for(Server server : servers) {
            if(server.getId().equals(id))
                return server;
        }
        return null;
    }

    /**
     * add a channel to block list user
     * @param serverId of server
     * @param clientId of channel
     * @param chatId of friend id
     */
    public void addBlockChannelFromUser(String serverId, String clientId, String chatId) {
        Server server = getServer(serverId);
        server.getUserBlockChannel().get(clientId).add(chatId);
    }

    /**
     * add a channel to server
     * @param id of server
     * @param name of channel
     */
    public void addChannelToServer(String id, String name) {
        ChatChannel chatChannel = new ChatChannel(name);
        Server server = getServer(id);
        server.getChatChannels().add(chatChannel);
        chatChannels.add(chatChannel);
    }

    /**
     * add a server to database
     * @param server to add
     */
    public void addServer(Server server) {
        servers.add(server);
    }

    /**
     * check block list of a client
     * @param client to check
     * @param user to check
     * @return true if block else false
     */
    public boolean checkBlock(ClientData client, String user) {
        for(String str : client.getBlocks()) {
            if(str.equals(user))
                return true;
        }
        return false;
    }

    /**
     * get roles of a user from server
     * @param server of server
     * @param user of server
     * @return role
     */
    public String getRoles(String server, String user) {
        System.out.println(server + " gov " + user);
        if(!checkServer(server)) {
            System.out.println("Server Not found 404");
            return "-1";
        }
        if(!getServer(server).checkClient(user)) {
            System.out.println("User not found 404");
            return "-2";
        }
        return getServer(server).getRole(user);
    }

    /**
     * change server name
     * @param server to change name
     * @param newName new name
     */
    public void changeServerName(String server, String newName) {
        System.out.println("successfully change server name");
        getServer(server).setName(newName);
    }

    /**
     *
     * @param server
     * @param channel
     * @return
     */
    public boolean inServer(String server, String channel) {
        for (ChatChannel chatChannel : getServer(server).getChatChannels()) {
            if (channel.equals(chatChannel.getId()))
                return true;
        }
        return false;
    }

    public void deleteChannelFromServer(String server, String channel) {
        if(!checkServer(server)) {
            System.out.println("Server not found");
        }
        if(!inServer(server, channel)) {
            System.out.println("Channel not found");
        }
        else {
            getServer(server).remove(channel);
        }
    }

    public void setRole(String server, String role, String user) {
        getServer(server).setRoles(user, role);
    }

    public void removeUserFromServer(String server, String user) {
        if(getServer(server).getUsers().contains(user)) {
            getServer(server).removeUser(user);
            getClient(user).removeServer(server);
            System.out.println("successfully delete from server");
        }
        else {
            System.out.println("User not found");
        }
    }

    /**
     * get time of enter a client from server
     * @param user to gettime
     * @param serverId of server
     * @return time
     */
    public String getTimeClientFromServer(String user, String serverId) {
        Server server = getServer(serverId);
        for(String key : server.getEnterTime().keySet()) {
            if(key.equals(user))
                return server.getEnterTime().get(key);
        }
        return null;
    }

    /**
     * get status of client
     * @param clientId to get status
     * @return status
     */
    public String getStatus(String clientId) {
        ClientData clientData = getClient(clientId);
        return clientData.getStatus();
    }

    /**
     * set role name with works
     * @param serverId of server
     * @param name of role
     * @param role works
     */
    public void setRoleName(String serverId, String name, String role) {
        getServer(serverId).setRoleName(name, role);
    }

    /**
     * get role name data
     * @param serverId of server
     * @param roleName of role
     * @return roles
     */
    public String getRoleNameData(String serverId, String roleName) {
        Server server = getServer(serverId);
        for(String name : server.getRolesName().keySet()) {
            if(name.equals(roleName)) {
                return server.getRolesName().get(name);
            }
        }
        return null;
    }

    /**
     * add a file to channel ro database
     * @param chatId of channel
     * @param file location
     */
    public void addFileToChannel(String chatId, String file) {
        ChatChannel chatChannel = getChannel(chatId);
        chatChannel.getFiles().add(file);
    }

    /**
     * remove massage from a chat
     * @param chatId of chatchannel
     * @param massageId for delete
     */
    public void removeMassageFromChat(String chatId, int massageId) {
        ChatChannel chatChannel = getChannel(chatId);
        chatChannel.getMassages().remove(massageId);
    }

    /**
     * edit a massage from chart
     * @param chatId chatchannel
     * @param massageId to edit
     * @param newMassage to edit
     */
    public void editMassageFromChat(String chatId, int massageId, String newMassage) {
        ChatChannel chatChannel = getChannel(chatId);
        Massage massage = chatChannel.getMassages().get(massageId);
        massage.setText(newMassage);
    }

    /**
     * change photo of a client
     * @param user photo
     * @param photo photo
     */
    public void changePhoto(String user, String photo) {
        getClient(user).setPhoto(photo);
    }

    /**
     * show photo of client
     * @param user that wanna show
     * @return string of photo
     */
    public String showPhoto(String user) {
        String ret = getClient(user).getPhoto();
        return ret;
    }

    /**
     * remove a server by owner
     * @param serverId
     */
    public void removeServer(String serverId) {
        Server server = getServer(serverId);
        for(String users : server.getUsers()) {
            ClientData clientData = getClient(users);
            clientData.removeServer(serverId);
        }
        servers.remove(server);
    }
}
