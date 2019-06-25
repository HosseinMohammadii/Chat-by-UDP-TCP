import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TestUDPClient {
    static DatagramSocket socket;
    Broadcaster broadcaster;
    static byte[] buf = new byte[256];
    private volatile boolean exit = false;
    private String ip;
    private String received;

    public static void main(String[] args) {

        try {
            socket = new DatagramSocket(42153);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);

        try {
            socket.receive(packet);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                    = new String(packet.getData(), 0, packet.getLength());
            String s = String.valueOf(packet.getAddress());
            String[] ss = s.split("/");
            String packetaddress = ss[1];
            System.out.println(received + " " + packetaddress + " " + packet.getPort() + " port ghabli" + port);
        } catch (IOException k) {
            k.printStackTrace();
        }
    }
}
