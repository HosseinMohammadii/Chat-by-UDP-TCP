import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChatClient implements Runnable {
    String address;
    int port;
    Socket socket = null;
    private static volatile boolean exit = false;



    public ChatClient(InetAddress serverAddress , int serverPort) throws IOException {

        port = serverPort;
        address =  String.valueOf(serverAddress).substring(1);
//        System.out.println("system address is " + address);
//        System.out.println(InetAddress.getLocalHost().getHostAddress());
//        System.out.println("real port is " +socket.getPort()+ "    "+socket.getLocalPort() );

    }

    public int getPort(){
        return port;
    }



    @Override
    public void run() {


        try {
            socket = new Socket(address,port);
            System.out.println("client socket created successfully");
        } catch (IOException e) {
            Run.startBroadcast();
            Run.startListen();
            e.printStackTrace();
        }
        System.out.println("Connected");

        StringBuilder sentence = new StringBuilder();
        String line = "";
        String ans;
        boolean flag = true;
        StringBuilder fullText = new StringBuilder();


        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        DataOutputStream outToTaraf = null;
        try {
            outToTaraf = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataInputStream inFromTaraf = null;
        try {
            inFromTaraf = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
        String in;
        Recieve recieve = new Recieve(inFromTaraf,"client");
        Thread t1 = new Thread(recieve,"RECEIVED");
        t1.start();


        while (!exit) {
            try {
                line = inFromUser.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (line.equals("over") && !exit) {
                line = line + "\n";
                System.out.println("Closing and send close Mes to other person");
                try {
                    outToTaraf.writeBytes(line);
                } catch (IOException e) {
                    System.out.println("connection lost");
//                    e.printStackTrace();
                }
                recieve.stop();
                stop();
                System.out.println("stop receiving message");
                break;
            }

            line = line + "\n";
            if (!exit) {
                try {
                    outToTaraf.writeBytes(line);
                    System.out.print("sent: " + line);
                } catch (IOException e) {
//                e.printStackTrace();
                    System.out.println("connection lost");
                    exit =true;
                }
            }

        }

        System.out.println("finished getting new line");
        try {
            socket.close();
            System.out.println("closed client socket");
            stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        exit = true;
    }
}
