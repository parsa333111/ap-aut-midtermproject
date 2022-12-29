public class NotificationThread extends Thread {
    /**
     * run the server thread
     */
    @Override
    public void run() {
        Notification notification = new Notification(2001);
        notification.startServer();
    }
}
