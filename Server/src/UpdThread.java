import java.io.*;

/**
 * the Thread to save database in file
 */
public class UpdThread implements Runnable {

    /**
     * run of thread
     */
    @Override
    public void run() {
        while (true) {
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream("save.bin");
                ObjectOutputStream out = new ObjectOutputStream(fOut);
                out.writeObject(Discord.dataBase);
                out.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
