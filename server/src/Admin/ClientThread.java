package Admin;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientThread extends Thread {
    private int sessionID;
    private long lastHeartbeat = -1;
    private Socket socket;
    private int sessionTimeout = 60000;
    private static int connectedCount = 0;

    private AtomicBoolean active = new AtomicBoolean(false);

    public static int getConnectionCount(){
        return connectedCount;
    }

    public ClientThread(int id, Socket iSocket) throws SocketException {
        sessionID = id;
        socket = iSocket;
    }

    public boolean isActive(){
        return active.get();
    }

    public void stopClient() {
        System.out.println("Attempting to stop Client Thread on Session ID" + sessionID);
        active.set(false);
        try {
            socket.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        connectedCount--;
        System.out.println("Client Thread for Session ID " + sessionID + " ended");
    }

    @Override
    public void run() {
        active.set(true);
        try {
            socket.setSoTimeout(sessionTimeout);
        } catch (SocketException e) {
        }
        connectedCount++;
        while (active.get()) {
            try {
                int status = (new DataInputStream(socket.getInputStream())).read();
                lastHeartbeat = System.currentTimeMillis();
            } catch (Exception e) {
                active.set(false);
                System.out.println("Client connection on ID " + sessionID + " timed out");
                stopClient();
            }
        }
    }

    public long getTimeSinceSeen() {
        if (lastHeartbeat == -1) {
            lastHeartbeat = System.currentTimeMillis();
        }
        return System.currentTimeMillis() - lastHeartbeat;
    }
}
