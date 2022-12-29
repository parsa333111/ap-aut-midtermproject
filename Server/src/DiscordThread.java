public class DiscordThread extends Thread {
    /**
     * rin of discord thread
     */
    @Override
    public void run() {
        Discord discord = new Discord(2000);
        discord.startServer();
    }
}
