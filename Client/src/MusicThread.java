import javax.print.attribute.standard.Media;
import java.nio.file.Paths;

public class MusicThread extends Thread {
    private String path;
    /**
     * constructor with path of music which we want to play
     * @param path of music
     */
    public MusicThread(String path) {
        this.path = path;
    }
    /**
     * play music thread
     */
    @Override
    public void run() {
        //javafx needed
        /*
        Media media = new Media(Paths.get(path).toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
         */
    }
}
