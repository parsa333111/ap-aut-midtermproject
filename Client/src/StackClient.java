import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.Socket;

public class StackClient {
    /**
     * handle call
     * @throws LineUnavailableException may throw that
     * @throws IOException may throw that
     */
    public void main() throws LineUnavailableException, IOException {
        Socket client = new Socket("127.0.0.1", 5454);
        Thread inputThread = new Thread(new InputThread(client));
        Thread outputThread = new Thread(new OutputThread(client));
        inputThread.start();
        outputThread.start();
    }
}
