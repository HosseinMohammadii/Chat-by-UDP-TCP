import java.io.*;
import java.net.*;

public class TCPClient {
    private static volatile boolean exit = false;
    public static void main(String argv[]) throws Exception {

        Socket clientSocket = null;
        try {
            clientSocket = new Socket("127.0.0.1",8423);
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
            outToTaraf = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataInputStream inFromTaraf = null;
        try {
            inFromTaraf = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
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

            if (line.equals("over")){
                line = line + "\n";
                System.out.println("Closing and send close Mes to other person");
                try {
                    outToTaraf.writeBytes(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recieve.stop();
                stop();
                System.out.println("stop receiving message");
                break;
            }

            line = line + "\n";
            try {
                outToTaraf.writeBytes(line);
            } catch (IOException e) {
//                e.printStackTrace();
            }
            System.out.print("sent: " + line);
        }

        System.out.println("finished getting new line");
        try {
            clientSocket.close();
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
