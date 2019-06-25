import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class Run{

    static int chatClientPort;
    static int chatServerPort;
    static int clientID;
    static ChatClient chatClient;
    static ChatServer chatServer;
    static boolean serverStarted = false;
    static boolean clientStarted = false;
    static int BLPort;
    static Thread t1 ;
    static Thread t2 ;
    static Thread t3 ;
    static Thread t4 ;
    static Broadcaster broadcaster;
    static Listener listener;
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter your id");
        clientID = s.nextInt();
//        System.out.println("Enter port you wannt to work with");
//        BLPort = s.nextInt();
        broadcaster = new Broadcaster(clientID);
        listener = new Listener(broadcaster,clientID);
        broadcaster.setListener(listener);
        startListen(listener);
        startBroadcast(broadcaster);

    }

    public static int startChatClient(InetAddress serverAddress , int serverPort) throws IOException {
        chatClient = new ChatClient(serverAddress,serverPort);
        chatClientPort = chatClient.getPort();
        return chatClientPort;
    }

    public static int startChatServer() throws IOException {
        chatServer = new ChatServer();
        chatServerPort = chatServer.getPort();
        return chatServerPort;
    }

    public static void runChatClient() throws IOException {
        t3 = new Thread(chatClient , "chatClient");
        t3.start();
    }
    public static void runChatServer() throws IOException {
        t4 = new Thread(chatServer , "chatServer");
        t4.start();
    }
    public static void startBroadcast(Broadcaster broadcaster){
        broadcaster.resume();
        t1 = new Thread(broadcaster , "broadcaster");
        t1.start();
    }
    public static void startBroadcast(){
        broadcaster.resume();
        t1 = new Thread(broadcaster , "broadcaster");
        t1.start();
    }

    public static void startListen(Listener listener){
        listener.resume();
        t2 = new Thread(listener , "listener");
        t2.start();
    }

    public static void startListen(){
        listener.resume();
        t2 = new Thread(listener , "listener");
        t2.start();
    }
}
