package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartbeatThread extends Thread {
    private AtomicBoolean active = new AtomicBoolean(false);
    private Socket socket;

    public HeartbeatThread(Socket iSocket) {
        this.socket = iSocket;
    }

    @Override
    public void run() {
        active.set(true);

        while (active.get()) {
            try {
                (new DataOutputStream(socket.getOutputStream())).writeInt(1);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopHeartbeat() {
        active.set(false);
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
