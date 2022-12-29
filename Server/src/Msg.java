import java.io.Serializable;
/**
 * this class transfer between client and server for communication
 */
public class Msg  implements Serializable {
    private String id;
    private String text;
    private ClientData clientData;
    private ChatChannel chatChannel;

    private String type; //join msg

    private String owner;

    private String chatId;

    private Server server;

    private int index;
    /**
     * constructor
     * @param id of msg
     * @param owner of msg
     * @param text of msg
     * @param type of msg
     */
    public Msg(String id, String owner, String text, String type) {
        this.id = id;
        this.owner = owner;
        this.text = text;
        this.type = type;
    }
    /**
     * getter and setter method
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    public void setChatChannel(ChatChannel chatChannel) {
        this.chatChannel = chatChannel;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
    public String getText() {
        return text;
    }
    public String getType() {
        return type;
    }
}