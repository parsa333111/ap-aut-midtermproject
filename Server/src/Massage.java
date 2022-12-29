import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Massage implements Serializable {
    private String sender;
    private String text;
    private String chat_id;

    private String time;

    private int like, dislike, lol;

    private ArrayList<String> userReaction;
    /**
     * constructor
     * @param sender of Massage
     * @param text of Massage
     * @param chat_id which massage send
     */
    public Massage(String sender, String text, String chat_id) {
        this.sender = sender;
        this.text = text;
        this.chat_id = chat_id;
        this.userReaction = new ArrayList<>();
        this.time = CurrentTime.currentTime();
    }
    /**
     * constructor
     * @param massage which copy to new massage
     */
    public Massage(Massage massage) {
        this.sender = massage.getSender();
        this.text = massage.getText();
        this.chat_id = massage.getChat_id();
        this.userReaction = new ArrayList<>(massage.getUserReaction());
        this.like = massage.like;
        this.dislike = massage.dislike;
        this.lol = massage.lol;
        this.time = massage.getTime();
    }
    /**
     * getter and setter method
     */
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public int getLol() {
        return lol;
    }

    public void setLol(int lol) {
        this.lol = lol;
    }

    public ArrayList<String> getUserReaction() {
        return userReaction;
    }

    public void setUserReaction(ArrayList<String> userReaction) {
        this.userReaction = userReaction;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }
    /**
     * check user was reacted to specified massage
     * @param userip is ip of user
     * @return true if client was reacted else return false
     */
    public boolean checkUserReact(String userip) {
        for(String user : userReaction) {
            if(user.equals(userip)) {
                return true;
            }
        }
        return false;
    }
    /**
     * @param react = 1 -> like, react = 2 -> dislike, react = 3 -> lol
     */
    public void reactToMassage(String user, int react) {
        if(checkUserReact(user)) {
            System.out.println("You can't react twice");
        }
        else {
            if(react == 1) like++;
            if(react == 2) dislike++;
            if(react == 3) lol++;
            userReaction.add(user);
        }
    }
    /**
     * show react of massage method
     */
    public void showDetail() {
        System.out.println("Like : " + like + "\ndislike : " + dislike + "\nlol : " + lol);
    }
}
