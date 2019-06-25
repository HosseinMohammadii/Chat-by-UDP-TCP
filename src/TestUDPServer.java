import java.io.IOException;
import java.net.*;

public class TestUDPServer {

    public static void main(String[] args) throws SocketException {
        String broadcastMessage = "Hello Test Listener.";
        byte[] buffer = broadcastMessage.getBytes();
        DatagramSocket socket = null;
        socket = new DatagramSocket();
        InetAddress address = null;
        try {
            address = InetAddress.getByName("192.168.43.246");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 42153);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Test Message Sent!" + "-" + packet.getAddress() + "-" + packet.getPort());
    }

}
