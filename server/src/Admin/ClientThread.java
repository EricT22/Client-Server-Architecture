package Admin;

import java.net.Socket;

public class ClientThread extends Thread{
    private int sessionID;
    private boolean connected;
    private long lastHeartbeat;
    private Socket socket;

    public ClientThread(int id, Socket iSocket){
        sessionID = id;
        socket = iSocket;
    }

    public void stopClient(){

    }

    @Override
    public void run(){

    }

    public long getTimeSinceSeen(){
        return 0;
    }
}
