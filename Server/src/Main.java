/**
 * @author Mohammad Hosseini
 * @author Parsa Hashemi Khorsand
 */
public class Main {

    /**
     * starter of project with run 3 thread
     * @param args
     */
    public static void main(String[] args) {
        Thread threadDiscord = new Thread(new DiscordThread());
        threadDiscord.start();
        Thread threadNotification = new Thread(new NotificationThread());
        threadNotification.start();
        Thread threadFile = new Thread(new FileThread());
        threadFile.start();
    }
}