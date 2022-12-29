public class FileThread extends Thread {
    /**
     * thread of file
     */
    @Override
    public void run() {
        FileHandle fileHandle = new FileHandle(2002);
        fileHandle.startServer();
    }
}
