import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    String address;
    int port;
    static ServerSocket serverSocket;
    private static volatile boolean exit = false;
    public static void main(String argb[]) throws Exception {
        Socket socket = null;
        try {
            serverSocket.setSoTimeout(6000);
            socket = serverSocket.accept();
            System.out.println("Client accepted");
        } catch (IOException e) {
            System.out.println("couldn't to accept client.starting broadcasting and listening");
            Run.startBroadcast();
            Run.startListen();
        }


        StringBuilder sentence = new StringBuilder();
        String line = "";
        String ans = "";
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
            System.out.println("Server : Input data stream initialized correct");
        } catch (IOException e) {
            System.out.println("Server : Input data stream not not initialized correct");
            e.printStackTrace();
        }
        //DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
        String in;
        Recieve recieve = new Recieve(inFromTaraf,"server");
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
            socket.close();
            System.out.println("closed Server socket ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void stop() {
        exit = true;
    }
}
