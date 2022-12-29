import java.io.Serializable;
import java.util.ArrayList;

public class ClientData implements Serializable {
    private ArrayList<String> FriendRequests;
    private String username;
    private ArrayList<String> Friends;

    private String password;
    private String email;

    private String photo;
    private ArrayList<String> Blocks;

    private ArrayList<String> servers;

    private String phoneNumber;
    private String condition;

    private String CurrentChat;
    private String status;
    /**
     * constructor
     * @param username of client
     * @param password of client
     * @param email of client
     * @param phoneNumber of client
     */
    public ClientData(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.condition = "Online";
        this.Friends = new ArrayList<>();
        this.Blocks = new ArrayList<>();
        this.FriendRequests = new ArrayList<>();
        this.CurrentChat = "";
        this.servers = new ArrayList<>();
        this.status = "Online";
    }

    /**
     * constructor
     * @param data which copy to new client data
     */
    public ClientData(ClientData data) {
        FriendRequests = new ArrayList<>(data.getFriendRequests());
        this.username = data.username;
        Friends = new ArrayList<>(data.getFriends());
        this.password = data.getPassword();
        this.email = data.getEmail();
        Blocks = new ArrayList<>(data.getBlocks());
        this.phoneNumber = data.getPhoneNumber();
        this.condition = data.getCondition();
        this.servers = new ArrayList<>(data.getServers());
        this.status = data.getStatus();
    }

    /**
     * getter and setter ,ethod
     */
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public void removeServer(String server) {
        servers.remove(server);
    }

    public void addServer(String name) {
        this.servers.add(name);
    }

    public ArrayList<String> getServers() {
        return servers;
    }

    public void setServers(ArrayList<String> servers) {
        this.servers = servers;
    }

    public String getCurrentChat() {
        return CurrentChat;
    }

    public void setCurrentChat(String currentChat) {
        CurrentChat = currentChat;
    }

    public ArrayList<String> getFriendRequests() {
        return FriendRequests;
    }

    public void setFriendRequests(ArrayList<String> friendRequests) {
        FriendRequests = friendRequests;
    }

    public ArrayList<String> getBlocks() {
        return Blocks;
    }

    public void setBlocks(ArrayList<String> blocks) {
        Blocks = blocks;
    }

    public ArrayList<String> getFriends() {
        return Friends;
    }

    public void setFriends(ArrayList<String> friends) {
        Friends = friends;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void ChangePhoto(String newPhoto) {
        photo = newPhoto;
    }
}
