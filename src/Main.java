import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Main{
    private static DatagramSocket socket = null;

    public static void main(String[] args) throws IOException, InterruptedException {
        broadcast("Hello Listener", InetAddress.getByName("255.255.255.255"));
    }
    public static void broadcast(String broadcastMessage, InetAddress address) throws IOException, InterruptedException {

        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 4445);

        while(true){
        socket.send(packet);
        Thread.sleep(2000);
        if(broadcastMessage.equals("end"))
            break;
        }
        socket.close();
    }
}

