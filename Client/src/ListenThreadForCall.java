import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class ListenThreadForCall extends Thread {

    static AudioInputStream ais;
    static AudioFormat format;
    static int port = 5005;
    static float rate = 44100.0f;

    static  DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;
    /**
     * listen thread for call
     */
    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(CallIPHandler.listen);

            MulticastSocket mSocket = new MulticastSocket(port);

            mSocket.joinGroup(group);

            byte[] recieveData = new byte[4096];

            format = new AudioFormat(rate, 16, 2, true, false);

            dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);
            sourceDataLine.start();

            DatagramPacket recievePacket = new DatagramPacket(recieveData, recieveData.length);
            ByteArrayInputStream baiss = new ByteArrayInputStream(recievePacket.getData());

            while (true) {
                mSocket.receive(recievePacket);
                ais = new AudioInputStream(baiss, format, recievePacket.getLength());
                toSpeaker(recievePacket.getData());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * decode bytes
     */
    public  static void toSpeaker(byte soundbytes[]) {
        try {
            sourceDataLine.write(soundbytes, 0, soundbytes.length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}