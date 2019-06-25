import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPRun2 implements Runnable {
    private Socket socket;
    private String ans = "";
    String line;
    StringBuilder fullText = new StringBuilder();

    public TCPRun2(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        StringBuilder sentence = new StringBuilder();
        String line = "";
        String ans = "";
        boolean flag = true;
        StringBuilder fullText = new StringBuilder();


        int turn = 0;
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

        Recieve recieve = new Recieve(inFromTaraf,"server");
        Thread t1 = new Thread(recieve,"RECEIVED");
        t1.start();


        while (true) {
            try {
                line = inFromUser.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (line.equals("over")){
                line = line + "\n";
                System.out.print("Closing and send " + line);
                try {
                    outToTaraf.writeBytes(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            line = line + "\n";
            System.out.print("sent: " + line);
            try {
                outToTaraf.writeBytes(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

