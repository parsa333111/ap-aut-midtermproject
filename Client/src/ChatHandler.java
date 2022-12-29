import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class ChatHandler {
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream outputStream;
    private final Scanner scanner;
    private final String id;
    private final String clientUsername;

    private ChatChannel chatChannel;

    /**
     * handle chat for client with other client or clients
     * @param objectInputStream input for client
     * @param outputStream out for client
     * @param scanner console input for client
     * @param id of client
     * @param clientUsername user name of client
     */
    public ChatHandler(ObjectInputStream objectInputStream, ObjectOutputStream outputStream, Scanner scanner, String id, String clientUsername) {
        this.objectInputStream = objectInputStream;
        this.outputStream = outputStream;
        this.scanner = scanner;
        this.id = id;
        this.clientUsername = clientUsername;
    }

    /**
     * constructor
     * @param channel which client using now
     * @throws IOException may throw that
     * @throws InterruptedException may throw that
     * @throws ClassNotFoundException may throw that
     */
    void run(ChatChannel channel) throws IOException, InterruptedException, ClassNotFoundException {
        chatChannel = channel;
        System.out.println("*For Exit From Chat Print Print '#bye'");
        System.out.println("*For Reaction To A Massage Print #reaction");
        System.out.println("*For Pin A Massage Print #pin");
        System.out.println("*For Notify a Member Enter His Name after @");
        System.out.println("*For Download File From Channel Print #download");
        System.out.println("*For Upload a file To Channel Print #upload");
        System.out.println("*For Delete a Massage Enter #delete");
        System.out.println("*For Edit a Massage Enter #edit");
        if(!chatChannel.getPinMassage().getSender().equals(""))
            System.out.println("Pin Massage : " + "[" + chatChannel.getPinMassage().getSender() + "] : " + chatChannel.getPinMassage().getText());
        for(Massage massage : chatChannel.getMassages()) {
            System.out.println("[" + massage.getSender() + "] : " + massage.getText());
        }
        Listener listener = new Listener(objectInputStream);
        Thread listenerThread = new Thread(listener);
        listenerThread.start();
        String text;
        do {
            text = scanner.nextLine();
            if(text.charAt(0) == '@') {
                text = text.substring(1);
                Client.notification.outputStream.writeObject(new Msg(id, text, "New Notification From : " + clientUsername + " From Chat : " + chatChannel.getName() + " With Id : " + chatChannel.getId(), "newNotification"));
                continue;
            }
            else if(text.equals("#delete")) {
                int cnt = 0;
                // updateChatChannel(chatChannel.getId());
                for (Massage massage : chatChannel.getMassages()) {
                    System.out.println(cnt + ". [" + massage.getSender() + "] : " + massage.getText());
                    cnt++;
                }
                System.out.println("Enter Index of massage between [0, " + cnt + "):");
                int index = Integer.parseInt(scanner.nextLine());
                if (index < 0 || index >= cnt) {
                    System.out.println("out of index you returned to chat");
                    continue;
                } else {
                    Msg msg = new Msg(id, clientUsername, chatChannel.getId(), "RemoveMassage");
                    msg.setIndex(index);
                    outputStream.writeObject(msg);
                    continue;
                }
            }
            else if(text.equals("#edit")) {
                int cnt = 0;
                // updateChatChannel(chatChannel.getId());
                for (Massage massage : chatChannel.getMassages()) {
                    System.out.println(cnt + ". [" + massage.getSender() + "] : " + massage.getText());
                    cnt++;
                }
                System.out.println("Enter Index of massage between [0, " + cnt + "):");
                int index = Integer.parseInt(scanner.nextLine());
                if (index < 0 || index >= cnt) {
                    System.out.println("out of index you returned to chat");
                    continue;
                } else {
                    System.out.print("New Massage : ");
                    String newText = scanner.nextLine();
                    Msg msg = new Msg(id, newText, chatChannel.getId(), "EditMassage");
                    msg.setIndex(index);
                    outputStream.writeObject(msg);
                    continue;
                }
            }
            else if(text.equals("#download")) {
                int cnt = 0;
                for(String file : chatChannel.getFiles())
                    System.out.println(++cnt + ". " + file);
                System.out.print("Enter The File Id Wanna To Download : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if(tp < 1 || tp > cnt) {
                    System.out.println("OutOfRange");
                    continue;
                }
                Client.fileOutPut.recivie(chatChannel.getFiles().get(tp - 1));
            }
            else if(text.equals("#upload")) {
                System.out.print("Enter Files Location : ");
                String location = scanner.nextLine();
                Client.fileOutPut.sendFile(chatChannel.getId(), clientUsername, location);

            }
            else if(text.equals("#bye")) {
                continue;
            }
            else if(text.equals("#pin")) {
                System.out.print("Enter New Pin Massage : ");
                Msg send = new Msg(id, clientUsername, text, "SetPinMassage");
                send.setChatId(chatChannel.getId());
                outputStream.writeObject(send);
            }
            else if(text.equals("#reaction")) {
                System.out.println("Enter show or react:");
                String operation = scanner.nextLine();
                if(operation.equals("react")) {
                    int id = 0;
                    // updateChatChannel(chatChannel.getId());
                    for (Massage massage : chatChannel.getMassages()) {
                        System.out.println(id + ". [" + massage.getSender() + "] : " + massage.getText());
                        id++;
                    }
                    System.out.println("Enter Index of massage between [0, " + id + "):");
                    int index = Integer.parseInt(scanner.nextLine());
                    if (index < 0 || index >= id) {
                        System.out.println("out of index you returned to chat");
                        continue;
                    } else {
                        id = index;
                        for (Massage massage : chatChannel.getMassages()) {
                            if (id == 0) {
                                System.out.println("1.like\n2.dislike\n3.lol");
                                int typeOf = Integer.parseInt(scanner.nextLine());
                                if (typeOf <= 0 || typeOf >= 4) {
                                    System.out.println("Invalid Input");
                                    System.out.println("You backed to chat");
                                } else {
                                    massage.reactToMassage(this.id, typeOf);
                                    System.out.println(massage.toString());
                                }
                            }
                            id--;
                        }
                        continue;
                    }
                }
                else if(operation.equals("show")) {
                    int id = 0;
                    //     updateChatChannel(chatChannel.getId());
                    for (Massage massage : chatChannel.getMassages()) {
                        System.out.println(id + ". [" + massage.getSender() + "] : " + massage.getText());
                        id++;
                    }
                    System.out.println("Enter Index of massage between [0, +"+id+"):");
                    int index = Integer.parseInt(scanner.nextLine());
                    if (index < 0 || index >= id) {
                        System.out.println("out of index you returned to chat");
                        continue;
                    } else {
                        id = index;
                        for (Massage massage : chatChannel.getMassages()) {
                            if (id == 0) {
                                massage.showDetail();
                                System.out.println(massage.toString());
                            }
                            id--;
                        }
                        System.out.println("You back to chat");
                        continue;
                    }
                }
                else {
                    System.out.println("Invalid input");
                    continue;
                }
            }
            else {
                Msg send = new Msg(id, clientUsername, text, "ChatMassage");
                send.setChatId(chatChannel.getId());
                outputStream.writeObject(send);
            }
        } while (!text.equals("#bye"));
        listener.stop();
        outputStream.writeObject(new Msg(id, clientUsername, "", "ByeFromChat"));
        Thread.sleep(500);
    }

    /**
     *
     * @param channel which client using now
     * @param isHistory need history
     * @param isPin have pinned
     * @param history have history
     * @param serverName is name of server which client use
     * @throws IOException may throw that
     * @throws InterruptedException may throw that
     * @throws ClassNotFoundException may throw that
     */
    void run(ChatChannel channel, boolean isHistory, boolean isPin, String history, String serverName) throws IOException, InterruptedException, ClassNotFoundException {
        outputStream.writeObject(new Msg(id, clientUsername, channel.getId(), "CurrentChannel"));
        chatChannel = channel;
        System.out.println("*For Exit From Chat Print Print '#bye'");
        System.out.println("*For Reaction To A Massage Print #reaction");
        System.out.println("*For Notify a Member Enter His Name after @");
        System.out.println("*For Download File From Channel Print #download");
        System.out.println("*For Upload a file To Channel Print #upload");
        System.out.println("*For Delete a Massage Enter #delete");
        System.out.println("*For Edit a Massage Enter #edit");
        if(isPin)
            System.out.println("*For Pin A Massage Print #pin");
        if(!chatChannel.getPinMassage().getSender().equals(""))
            System.out.println("Pin Massage : " + "[" + chatChannel.getPinMassage().getSender() + "] : " + chatChannel.getPinMassage().getText());
        for(Massage massage : chatChannel.getMassages()) {
            if(isHistory || massage.getTime().compareTo(history) > 0)
                System.out.println("[" + massage.getSender() + "] : " + massage.getText());
        }
        Listener listener = new Listener(objectInputStream);
        Thread listenerThread = new Thread(listener);
        listenerThread.start();
        String text;
        do {
            text = scanner.nextLine();
            if(text.charAt(0) == '@') {
                text = text.substring(1);
                Client.notification.outputStream.writeObject(new Msg(id, text, "New Notification From : " + clientUsername + "From Server : " + serverName + " and From Chat : " + chatChannel.getName() + " With Id : " + chatChannel.getId(), "newNotification"));
                continue;
            }
            else if(text.equals("#delete")) {
                int cnt = 0;
                // updateChatChannel(chatChannel.getId());
                for (Massage massage : chatChannel.getMassages()) {
                    System.out.println(cnt + ". [" + massage.getSender() + "] : " + massage.getText());
                    cnt++;
                }
                System.out.println("Enter Index of massage between [0, " + cnt + "):");
                int index = Integer.parseInt(scanner.nextLine());
                if (index < 0 || index >= cnt) {
                    System.out.println("out of index you returned to chat");
                    continue;
                } else {
                    Msg msg = new Msg(id, clientUsername, chatChannel.getId(), "RemoveMassage");
                    msg.setIndex(index);
                    outputStream.writeObject(msg);
                    continue;
                }
            }
            else if(text.equals("#edit")) {
                int cnt = 0;
                // updateChatChannel(chatChannel.getId());
                for (Massage massage : chatChannel.getMassages()) {
                    System.out.println(cnt + ". [" + massage.getSender() + "] : " + massage.getText());
                    cnt++;
                }
                System.out.println("Enter Index of massage between [0, " + cnt + "):");
                int index = Integer.parseInt(scanner.nextLine());
                if (index < 0 || index >= cnt) {
                    System.out.println("out of index you returned to chat");
                    continue;
                } else {
                    System.out.print("New Massage : ");
                    String newText = scanner.nextLine();
                    Msg msg = new Msg(id, newText, chatChannel.getId(), "EditMassage");
                    msg.setIndex(index);
                    outputStream.writeObject(msg);
                    continue;
                }
            }
            else if(text.equals("#download")) {
                int cnt = 0;
                for(String file : chatChannel.getFiles())
                    System.out.println(++cnt + ". " + file);
                System.out.print("Enter The File Id Wanna To Download : ");
                int tp = Integer.parseInt(scanner.nextLine());
                if(tp < 1 || tp > cnt) {
                    System.out.println("OutOfRange");
                    continue;
                }
                Client.fileOutPut.recivie(chatChannel.getFiles().get(tp - 1));
            }
            else if(text.equals("#upload")) {
                System.out.print("Enter Files Location : ");
                String location = scanner.nextLine();
                Client.fileOutPut.sendFile(chatChannel.getId(), clientUsername, location);

            }
            else if(text.equals("#bye")) {
                continue;
            }
            else if(text.equals("#pin")) {
                System.out.print("Enter New Pin Massage : ");
                text = scanner.nextLine();
                Msg send = new Msg(id, clientUsername, text, "SetPinMassage");
                send.setChatId(chatChannel.getId());
                outputStream.writeObject(send);
            }
            else if(text.equals("#reaction")) {
                System.out.println("Enter show or react:");
                String operation = scanner.nextLine();
                if(operation.equals("react")) {
                    int id = 0;
                    // updateChatChannel(chatChannel.getId());
                    for (Massage massage : chatChannel.getMassages()) {
                        System.out.println(id + ". [" + massage.getSender() + "] : " + massage.getText());
                        id++;
                    }
                    System.out.println("Enter Index of massage between [0, " + id + "):");
                    int index = Integer.parseInt(scanner.nextLine());
                    if (index < 0 || index >= id) {
                        System.out.println("out of index you returned to chat");
                        continue;
                    } else {
                        id = index;
                        for (Massage massage : chatChannel.getMassages()) {
                            if (id == 0) {
                                System.out.println("1.like\n2.dislike\n3.lol");
                                int typeOf = Integer.parseInt(scanner.nextLine());
                                if (typeOf <= 0 || typeOf >= 4) {
                                    System.out.println("Invalid Input");
                                    System.out.println("You backed to chat");
                                } else {
                                    massage.reactToMassage(this.id, typeOf);
                                    System.out.println(massage.toString());
                                }
                            }
                            id--;
                        }
                        continue;
                    }
                }
                else if(operation.equals("show")) {
                    int id = 0;
                    //     updateChatChannel(chatChannel.getId());
                    for (Massage massage : chatChannel.getMassages()) {
                        System.out.println(id + ". [" + massage.getSender() + "] : " + massage.getText());
                        id++;
                    }
                    System.out.println("Enter Index of massage between [0, +"+id+"):");
                    int index = Integer.parseInt(scanner.nextLine());
                    if (index < 0 || index >= id) {
                        System.out.println("out of index you returned to chat");
                        continue;
                    } else {
                        id = index;
                        for (Massage massage : chatChannel.getMassages()) {
                            if (id == 0) {
                                massage.showDetail();
                                System.out.println(massage.toString());
                            }
                            id--;
                        }
                        System.out.println("You back to chat");
                        continue;
                    }
                }
                else {
                    System.out.println("Invalid input");
                    continue;
                }
            }
            else {
                Msg send = new Msg(id, clientUsername, text, "ChatMassage");
                send.setChatId(chatChannel.getId());
                outputStream.writeObject(send);
            }
        } while (!text.equals("#bye"));
        listener.stop();
        outputStream.writeObject(new Msg(id, clientUsername, "", "ByeFromChat"));
        Thread.sleep(500);
    }
    /**
     * listener thread for receive text
     */
    private class Listener implements Runnable{
        ObjectInputStream objInputStream;

        public Listener(ObjectInputStream objInputStream) {
            this.objInputStream = objInputStream;
        }

        private volatile boolean shutdown;
        @Override
        public void run() {
            while (!shutdown) {
                try {
                    Msg msg = (Msg) objInputStream.readObject();
                    if(msg.getType().equals("GetChatChannel")) {
                        chatChannel = msg.getChatChannel();
                        continue;
                    }
                    if(!msg.getOwner().equals(clientUsername) || !msg.getText().equals("bye"))
                        System.out.println("[" + msg.getOwner() + "] : " + msg.getText());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Exit From The Chat");
        }

        /**
         * stop thread
         */
        public void stop() {
            shutdown = true;
        }
    }
    /**
     *
     * @param chatChannelid which we need to update it
     * @throws IOException may throw that
     * @throws ClassNotFoundException may throw that
     */
    public void updateChatChannel(String chatChannelid) throws IOException, ClassNotFoundException {
        outputStream.writeObject(new Msg(id, clientUsername, chatChannelid, "GetChatChannel"));
    }
}
