package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartbeatThread extends Thread {
    private AtomicBoolean active = new AtomicBoolean(false);
    private Socket socket;
    private ClientGUI parent;

    public HeartbeatThread(Socket iSocket, ClientGUI iGUI) throws SocketException {
        this.socket = iSocket;
        this.socket.setSoTimeout(20000);
        this.parent = iGUI;
    }

    @Override
    public void run() {
        active.set(true);

        while (active.get()) {
            try {
                (new DataOutputStream(socket.getOutputStream())).writeInt(200);
                Thread.sleep(5000);
                (new DataInputStream(socket.getInputStream())).readInt();
            } catch (SocketTimeoutException | SocketException e1) {
                stopHeartbeat();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopHeartbeat() {
        parent.swapToPage("CONNECT");
        active.set(false);
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
