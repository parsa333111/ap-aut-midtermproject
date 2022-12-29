import java.awt.image.AreaAveragingScaleFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;

public class Server implements Serializable {

    private String id;
    private String name;
    private String owner;
    private ArrayList<ChatChannel> chatChannels;

    private HashMap<String, String> roles;
    private ArrayList<String> users;

    private HashMap<String, ArrayList<String>> userBlockChannel;

    private HashMap<String, String> rolesName;
    private HashMap<String, String> enterTime;

    /**
     * constructor
     * @param name of server
     * @param owner of server
     */
    public Server(String name, String owner) {
        roles = new HashMap<>();
        roles.put(owner, "Admin");
        this.rolesName = new HashMap<>();
        rolesName.put("Admin", "1111111111");
        rolesName.put("Member", "1000000000");
        this.enterTime = new HashMap<>();
        enterTime.put(owner, CurrentTime.currentTime());
        this.id = RandomToken.randomString(50);
        this.name = name;
        this.owner = owner;
        this.users = new ArrayList<>();
        this.chatChannels = new ArrayList<>();
        this.userBlockChannel = new HashMap<>();
    }

    /**
     * constructor
     * @param server cpoy server
     */
    public Server(Server server) {
        this.id = server.getId();
        this.name = server.getName();
        this.owner = server.getOwner();
        this.chatChannels = new ArrayList<>();
        this.users = new ArrayList<>(server.getUsers());
        for(ChatChannel chatChannel : server.chatChannels) {
            this.chatChannels.add(new ChatChannel(chatChannel));
        }
        this.userBlockChannel = new HashMap<>();
        for(String key : server.userBlockChannel.keySet()) {
            this.userBlockChannel.put(key, new ArrayList<>(server.userBlockChannel.get(key)));
        }
        this.roles = new HashMap<>();
        for(String key : server.roles.keySet()) {
            this.roles.put(key, server.roles.get(key));
        }
        this.rolesName = new HashMap<>();
        for(String key : server.rolesName.keySet()) {
            this.rolesName.put(key, server.rolesName.get(key));
        }
        this.enterTime = new HashMap<>();
        for(String key : server.enterTime.keySet()) {
            this.enterTime.put(key, server.enterTime.get(key));
        }
    }

    /**
     * get role of client in server
     * @param user which role is need
     * @return role of user
     */
    public String getRole(String user) {
        if(roles.containsKey(user)) {
            return roles.get(user);
        }
        else {
            return "Member";
        }
    }

    /**
     * check client exist in server
     * @param username of checking client
     * @return truen if client joined in server
     */
    public boolean checkClient(String username) {
        for(String user : users) {
            if(user.equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * getter and setter
     */
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<ChatChannel> getChatChannels() {
        return chatChannels;
    }

    public void setChatChannels(ArrayList<ChatChannel> chatChannels) {
        this.chatChannels = chatChannels;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    /**
     * method for adding user to server
     * @param name of client
     */
    public void addUser(String name) {
        rolesName.put(name, "Member");
        enterTime.put(name, CurrentTime.currentTime());
        users.add(name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * method for remove client from channel
     * @param channel which removed
     */
    public void remove(String channel) {
        for(ChatChannel chatChannel : chatChannels) {
            if(chatChannel.getId().equals(channel)) {
                System.out.println("successfully deleted");
                chatChannels.remove(chatChannel);
                return;
            }
        }
    }

    /**
     * set role for user
     * @param user which role seted
     * @param role which gave to user
     */
    public void setRoles(String user, String role) {
        roles.put(user, role);
    }

    /**
     * set role name
     * @param name of new role
     * @param role abilities in server
     */
    public void setRoleName(String name, String role) {
        rolesName.put(name, role);
    }

    /**
     * remove user from server
     * @param user which removed
     */
    public void removeUser(String user) {
        users.remove(user);
    }

    /**
     * setter and getter method
     */
    public HashMap<String, String> getRoles() {
        return roles;
    }

    public void setRoles(HashMap<String, String> roles) {
        this.roles = roles;
    }

    public HashMap<String, ArrayList<String>> getUserBlockChannel() {
        return userBlockChannel;
    }

    public void setUserBlockChannel(HashMap<String, ArrayList<String>> userBlockChannel) {
        this.userBlockChannel = userBlockChannel;
    }

    public HashMap<String, String> getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(HashMap<String, String> enterTime) {
        this.enterTime = enterTime;
    }

    public HashMap<String, String> getRolesName() {
        return rolesName;
    }

    public void setRolesName(HashMap<String, String> rolesName) {
        this.rolesName = rolesName;
    }
}
