

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

class Listener implements Runnable {

    DatagramSocket socket;
    DatagramSocket responseSocket;
    Broadcaster broadcaster;
    String broadcasterMessage = "";
    InetAddress broadcasterAddress = null;
    int broadcasterPort = 0;
    int broadcasterID = 0;
    int confirmerID;

    byte[] buf = new byte[256];
    private volatile boolean exit = false;
    private String ip;
    volatile int state = 0;
    int id = 0;


    public Listener(Broadcaster b , int id){
        this.id=id;
        this.broadcaster = b;

    }



    @Override
    public void run() {

        while (!exit) {


            /**get system ip**/
            try (final DatagramSocket ds = new DatagramSocket()) {
                ds.connect(InetAddress.getByName("8.8.8.8"),10002);
                ip = socket.getLocalAddress().getHostAddress();
                ip= InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean loopback = false;

            /** first state
             * listen in a loop until a valid broadcast is received
             * every second it starts to listen again*/
            while(state == 0 && !exit) {
                try {
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length);
                    socket.setSoTimeout(1000);
                    socket.receive(packet);

                    broadcasterAddress = packet.getAddress();
                    broadcasterPort = packet.getPort();

                    packet = new DatagramPacket(buf, buf.length, broadcasterAddress, broadcasterPort);
                    broadcasterMessage = new String(packet.getData(), 0, packet.getLength());

                    String packetaddress = String.valueOf(broadcasterAddress).substring(1);


        //            if (broadcasterAddress.equals(packet.getAddress())) {
        //                System.out.println("same");
        //            } else {
        //                System.out.println("not the same");
        //            }

                    String[] broadcasterMessages = broadcasterMessage.split("-");
                    String moduleType = broadcasterMessages[0];
                    String messageType = broadcasterMessages[1];

                    if(moduleType.equals("bc") && messageType.equals("find")){
                        broadcasterID = Integer.valueOf(broadcasterMessages[2]);
                    }

                    if(id != broadcasterID && !exit){
                        state=1;
                        System.out.println("Valid broadcast message arrived " +broadcasterMessage +
                                " " + packetaddress + " " + packet.getPort() + " " + ip);
                        broadcaster.stop();

                    }


                    Enumeration e = null;
                    try {
                        e = NetworkInterface.getNetworkInterfaces();
                    } catch (SocketException e1) {
                        e1.printStackTrace();
                    }
                    while (e.hasMoreElements()) {
                        NetworkInterface n = (NetworkInterface) e.nextElement();
                        Enumeration ee = n.getInetAddresses();
                        while (ee.hasMoreElements()) {
                            InetAddress i = (InetAddress) ee.nextElement();
        //            System.out.println(i.getHostAddress());
                            if (packetaddress.equals(i.getHostAddress())) {
                                loopback = true;
                                break;
                            }
                        }
                    }

                } catch (SocketException e) {
//                    e.printStackTrace();
                }
                catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("connection lost.");
//                    stop();
//                    broadcaster.stop();
                }



            }

            while(state==1 && !exit ) {
                try {
                    System.out.println("Listener--Response Phase");



                    String responseText = "ls-response-" + id + "-"+responseSocket.getLocalPort()+"-";
                    byte[] responseBuffer = responseText.getBytes();
                    DatagramPacket responsePacket
                            = new DatagramPacket(responseBuffer, responseBuffer.length, broadcasterAddress, broadcasterPort);
                    responseSocket.send(responsePacket);
                    System.out.println("Response Message sent to " + broadcasterID);
                    state = 2;
                } catch (IOException k) {
                    k.printStackTrace();
                }
                broadcaster.stop();
            }


            int state2Counter = 0;

            while(state==2 && state2Counter<2 && !exit){
                state2Counter++;
                byte[] coBuffer = new byte[256];
                DatagramPacket packetCo
                        = new DatagramPacket(coBuffer, coBuffer.length);
                try {
                    responseSocket.setSoTimeout(2000);
                    responseSocket.receive(packetCo);
                    String confirm = new String(packetCo.getData(), 0, packetCo.getLength());
                    String[] confirms = confirm.split("-");
                    String moduleType = confirms[0];
                    String messageType = confirms[1];



                    if(packetCo.getAddress().equals(broadcasterAddress) && packetCo.getPort()==broadcasterPort
                                && moduleType.equals("bc") && messageType.equals("confirm")){
                        confirmerID = Integer.valueOf(confirms[2]);
                    }
                    else{
                        continue;
                    }
                    System.out.println("Confirm Message arrived from " + confirmerID);

                    if(broadcasterID==confirmerID && confirms.length >=4){
                        int serverPort = Integer.valueOf(confirms[3]);

                        Thread.sleep(2000);
                        System.out.println("chat client starting running on address "+broadcasterAddress + " and port" + serverPort);
                        Run.startChatClient(broadcasterAddress,serverPort);
                        Run.runChatClient();
                        stop();
                    }
                    else
                        continue;
                    stop();



                } catch (IOException e) {
//                    e.printStackTrace();
    //                if()
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }

            }

            if(state2Counter >=2 && !exit){
                System.out.println("Listener -- Mission failed");
                state = 0;
                Run.startBroadcast();
            }


            if(loopback)
                System.out.println("loopback");

    //        broadcaster.stop();


        }
    }

    public void stop(){
        exit = true;
        state=-1;
        System.out.println("listen should stop");
        socket.close();
        responseSocket.close();
    }
    public void resume(){
        exit = false;
        state = 0;
        broadcasterAddress = null;
        broadcasterPort = 0;
        broadcasterID = 0;
        confirmerID = 0;
        try {
            socket = new DatagramSocket(17639);
            responseSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("Listen should resume from first");
    }
}