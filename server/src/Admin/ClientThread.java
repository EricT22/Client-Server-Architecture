package Admin;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientThread extends Thread {
    private int sessionID;
    private long lastHeartbeat = -1;
    private Socket socket;

    private AtomicBoolean active = new AtomicBoolean(false);

    public ClientThread(int id, Socket iSocket) throws SocketException {
        sessionID = id;
        socket = iSocket;
        socket.setSoTimeout(300000);
    }

    public void stopClient() throws IOException {
        System.out.println("Attempting to stop Client Thread on Session ID" + sessionID);
        active.set(false);
        socket.close();
    }

    @Override
    public void run() {
        active.set(true);

        while (active.get()) {
            try {
                int status = (new DataInputStream(socket.getInputStream())).read();
                lastHeartbeat = System.currentTimeMillis();
                System.out.println("Heartbeat detected for Session ID" + sessionID);
            } catch (SocketTimeoutException e) {
                active.set(false);
                System.out.println("Client connection on ID" + sessionID + " timed out");
                try {
                    stopClient();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Client Thread for Session ID" + sessionID + " ended");
    }

    public long getTimeSinceSeen() {
        if (lastHeartbeat == -1) {
            lastHeartbeat = System.currentTimeMillis();
        }
        return System.currentTimeMillis() - lastHeartbeat;
    }
}
