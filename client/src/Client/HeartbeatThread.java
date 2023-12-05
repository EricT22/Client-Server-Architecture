package Client;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartbeatThread extends Thread{
    private AtomicBoolean active = new AtomicBoolean(false);
    private Socket socket;
    
    public HeartbeatThread(Socket iSocket){
        this.socket = iSocket;
    }

    @Override
    public void run(){

    }

    public void stopHeartbeat(){
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
