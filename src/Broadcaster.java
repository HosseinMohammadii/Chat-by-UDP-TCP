

import java.io.IOException;
import java.net.*;

public class Broadcaster implements Runnable{

    private volatile boolean exit = false;
    Listener listener;
    DatagramSocket socket = null;
    volatile int state=0;
    int id = 0;
    int idLength;
    private int listenerID;
    private InetAddress responseAddress;
    private int responsePort;

    public Broadcaster(Listener l){
        this.listener=l;
    }

    public Broadcaster( int id){
        this.id=id;
        idLength = (int) Math.log10(id) + 1;

    }

    public void setListener(Listener l){
        this.listener = l;
    }

    @Override
    public void run() {

        while (!exit) {



        String broadcastMessage = "bc-find-"+id+"-";
//            String broadcastMessage = "bc-find-" + 501 + "-";
            String confirmMessage = "bc-confirm-" + id + "-";
            byte[] buffer = broadcastMessage.getBytes();
//        System.out.println("brrrbrbrbrbrbrbrbrbrbr " + broadcastMessage + "  " + buffer.length);
            byte[] responseBuffer = new byte[256];


            InetAddress address = null;
            try {
                address = InetAddress.getByName("255.255.255.255");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length, address, 17639);


            while (state == 0 && !exit) {
                try {
                    socket.setBroadcast(true);

                    socket.send(packet);

                } catch (IOException e) {

                    System.out.println("it seems connection lost");
                    stop();
//                    e.printStackTrace();
                }
                System.out.println("Message Sent!" + "-" + packet.getAddress() + "-" + packet.getPort());
                System.out.println("Waiting for response");
                try {

                    DatagramPacket responsePacket
                            = new DatagramPacket(responseBuffer, responseBuffer.length);

                    socket.setBroadcast(false);
                    socket.setSoTimeout(3000);

                    socket.receive(responsePacket);

                    responseAddress = responsePacket.getAddress();
                    responsePort = responsePacket.getPort();

                    responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, responseAddress, responsePort);

                    String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    System.out.println("got Response Message " + responseMessage);
                    String[] responseMessages = responseMessage.split("-");
                    String moduleType = responseMessages[0];
                    String messageType = responseMessages[1];
                    String tmp = responseMessages[2];
                    int udpConfirmPort;
                    if (responseMessages.length > 4) {
                        udpConfirmPort = Integer.valueOf(responseMessages[3]);
                        if (responsePort == udpConfirmPort)
                            System.out.println("udpport in res Mes is equal with packet port");
                    }

                    if (moduleType.equals("ls") && messageType.equals("response")) {
                        System.out.println("valid response message");
                        listenerID = Integer.valueOf(tmp);
                    } else
                        continue;

                    if (!exit) {
                        state = 1;
                        listener.stop();
                    }
                }
                catch (SocketException e) {
//                e.printStackTrace();
                }
                catch (IOException e) {
//                e.printStackTrace();
//                    System.out.println("it seems connection lost.");
//                    stop();
                }

            }

            while (state == 1) {
                try {
                    int tcpServerPort = Run.startChatServer();
                    Run.runChatServer();
                    confirmMessage = confirmMessage + tcpServerPort + "-";
                    byte[] confirmBuffer = confirmMessage.getBytes();
                    DatagramPacket confirmPacket =
                            new DatagramPacket(confirmBuffer, confirmBuffer.length, responseAddress, responsePort);
                    socket.send(confirmPacket);
                    System.out.println("confirm message sent to " + listenerID);
                    socket.close();
                    stop();


                }
                catch (IOException e) {
//                    e.printStackTrace();
                    System.out.println("some thing went wrong.trying again");
                    resume();
                    Run.startListen();
                }
            }
            socket.close();
        }
    }

    public void stop(){
        exit = true;
        state = -1;
        System.out.println("BroadCast should stop");
        socket.close();
    }

    public void resume(){
        state=0;
        exit = false;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("BroadCast should resume from first");
    }
}






