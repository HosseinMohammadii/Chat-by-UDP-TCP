import java.io.DataInputStream;
import java.io.IOException;

public class Recieve implements Runnable{
    String ans;
    int turn;
    DataInputStream dis;
    String source;
    private volatile boolean exit = false;

    public Recieve(DataInputStream diss , String s){
        dis = diss;
        source = s ;
    }
    @Override
    public void run() {
        while (!exit) {
            try {
                ans = dis.readLine();
            } catch (IOException e) {
//                e.printStackTrace();
            }

            if (ans.equals("over")){
                System.out.print("Close message arrived and closing ");
                if(source.equals("client")){
                    System.out.println("getting line from client");
                    ChatClient.stop();
                }
                if(source.equals("server")){
                    System.out.println("getting line from server");
                    ChatServer.stop();
                }
                break;
            }
            System.out.println("Received: " + ans);

        }
    }
    public void stop(){
        exit = true;
    }
}
