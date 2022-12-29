import java.io.Serializable;
import java.util.ArrayList;
public class ChatChannel implements Serializable {

    private String id;
    private String name;
    private ArrayList<Massage> massages;

    private Massage pinMassage;

    private ArrayList<String> files;
    /**
     * constructor
     * @param name of chat channel
     */
    public ChatChannel(String name) {
        this.id = RandomToken.randomString(50);
        this.name = name;
        this.massages = new ArrayList<>();
        this.pinMassage = new Massage("", "", id);
        this.files = new ArrayList<>();
    }

    /**
     * constructor
     * @param chatChannel make new chatChannel
     */
    public ChatChannel(ChatChannel chatChannel) {
        this.id = chatChannel.getId();
        this.name = chatChannel.getName();
        this.massages = new ArrayList<>();
        for(Massage massage : chatChannel.massages)
            massages.add(new Massage(massage));
        this.pinMassage = new Massage(chatChannel.getPinMassage());
        this.files = new ArrayList<>(chatChannel.files);
    }

    /**
     * getter and setter method
     */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Massage> getMassages() {
        return massages;
    }

    public void setMassages(ArrayList<Massage> massages) {
        this.massages = massages;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Massage getPinMassage() {
        return pinMassage;
    }

    public void setPinMassage(Massage pinMassage) {
        this.pinMassage = pinMassage;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }
}
