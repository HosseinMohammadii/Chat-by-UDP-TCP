import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class TCPRun implements Runnable {
    private Socket socket;
    private String ans = "";
    public TCPRun(Socket socket){
        this.socket=socket;
    }
    @Override
    public void run() {
        try{
            Scanner ss = new Scanner(System.in);
        DataInputStream inFromClient = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        //BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(sSocket.getOutputStream()))
        String line;
        while (!(line = inFromClient.readLine()).equals("over")) {
            System.out.println("Received: " + line);
            ans = ss.nextLine();
            outToClient.writeBytes(line + "\n");
            System.out.println("Sent: " + ans);
        }
        }
        catch (Exception e) {
        }
        }
    }

