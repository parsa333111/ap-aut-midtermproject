import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class SpeakThreadForCall extends Thread{
    /**
     * thread for speaking in voice chat
     */
    @Override
    public void run() {
        System.setProperty("java.net.preferIPv4Stack", "true");

        TargetDataLine line;
        DatagramPacket dgp;

        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;

        float rate = 44100.0f;
        int channel = 2;
        int sampleSize = 16;
        boolean bigEndian = false;

        InetAddress addr;
        int port = 5005;

        System.out.println("SERVER START : " + port);

        AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channel, (sampleSize / 8) * channel, rate, bigEndian);

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if(!AudioSystem.isLineSupported(info)) {
            System.out.println("NOT support");
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            byte[] data = new byte[4096];

            addr = InetAddress.getByName(CallIPHandler.speak);

            MulticastSocket socket = new MulticastSocket();

            while(true) {
                line.read(data, 0, data.length);
                dgp = new DatagramPacket(data, data.length, addr, port);
                socket.send(dgp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}