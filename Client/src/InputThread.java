import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;

public class InputThread implements Runnable {
    private static AudioInputStream audioInputStream;
    private static AudioFormat format;
    private static int port = 6969;
    private static float rate = 44100.0f;
    private static DataLine.Info dataLineInfo;
    private static SourceDataLine sourceDataLine;
    private Socket client;
    byte[] receiveData = new byte[4096];
    /**
     * input format
     * @param client socket which connect between client and server
     * @throws LineUnavailableException may throw that
     * @throws IOException may throw that
     */
    public InputThread(Socket client) throws LineUnavailableException, IOException {
        this.client = client;
        format = new AudioFormat(rate, 16, 2, true, false);
        dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
    }
    /**
     * speaker thread for receive data from server and decode it
     */
    public void run() {
        try {
            sourceDataLine.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        sourceDataLine.start();
        while (true) {
            try {
                client.getInputStream().read(receiveData);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                ByteArrayInputStream baiss = new ByteArrayInputStream(receivePacket.getData());
                audioInputStream = new AudioInputStream(baiss, format, receivePacket.getLength());
                toSpeaker(receivePacket.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * method for decode sound bytes
     * @param soundBytes of sound
     */
    public static void toSpeaker(byte[] soundBytes) {
        sourceDataLine.write(soundBytes, 0, soundBytes.length);
    }
}
