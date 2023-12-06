package Admin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientThread extends Thread {
    private int sessionID;
    private long lastHeartbeat = -1;
    private Socket socket;
    private int sessionTimeout = 60000;
    private static int connectedCount = 0;
    private static int loggedCount = 0;
    private boolean loggedIn = false;
    private boolean locked = false;
    private static int lockedCount = 0;

    private static String uName = null;

    private static Vector<String> usernames = new Vector<>();
    private static Vector<String> lockednames = new Vector<>();

    private AtomicBoolean active = new AtomicBoolean(false);

    public synchronized static int getConnectionCount() {
        return connectedCount;
    }

    public static String getLoggedNames() {
        if (usernames.size() == 0) {
            return "";
        }
        String out = "";
        for (int i = 0; i < usernames.size() - 1; i++) {
            out = out + usernames.get(i) + ", ";
        }
        out = out + usernames.get(usernames.size() - 1);
        return out;
    }

    public static String getLockedNames() {
        if (lockednames.size() == 0) {
            return "";
        }
        String out = "";
        for (int i = 0; i < lockednames.size() - 1; i++) {
            out = out + lockednames.get(i) + ", ";
        }
        out = out + lockednames.get(lockednames.size() - 1);
        return out;
    }

    public synchronized static int getLoggedCount() {
        return loggedCount;
    }

    public static void clearLoggedList() {
        usernames = new Vector<>();
    }

    public static void clearLockedList() {
        lockednames = new Vector<>();
    }

    public ClientThread(int id, Socket iSocket) throws SocketException {
        sessionID = id;
        socket = iSocket;
    }

    public boolean isActive() {
        return active.get();
    }

    public synchronized void login(String s) {
        String creds = new String(Base64.getDecoder().decode(s.substring(s.indexOf(" ") + 1)));
        usernames.add(creds.substring(0, creds.indexOf(":")));
        uName = creds.substring(0, creds.indexOf(":"));
        loggedIn = true;
        loggedCount++;
        if (locked) {
            unlock(s);
        }
    }

    public synchronized void lock(String s) {
        System.out.println("Locking " + s);
        lockednames.add(s);
        locked = true;
        lockedCount++;
    }

    public synchronized void logout(String s) {
        String creds = new String(Base64.getDecoder().decode(s.substring(s.indexOf(" ") + 1)));
        usernames.remove(creds.substring(0, creds.indexOf(":")));
        loggedIn = false;
        loggedCount--;
    }

    public synchronized void unlock(String s) {
        if(!locked){
            return;
        }
        String creds = new String(Base64.getDecoder().decode(s.substring(s.indexOf(" ") + 1)));
        lockednames.remove(creds.substring(0, creds.indexOf(":")));
        locked = false;
        lockedCount--;
    }

    public void stopClient() {
        System.out.println("Attempting to stop Client Thread on Session ID" + sessionID);
        active.set(false);
        Server.sessionRegistry.put(sessionID, false);
        try {
            socket.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        connectedCount--;
        if (loggedIn) {
            loggedIn = false;
            loggedCount--;
            usernames.remove(uName);
        }
        if (connectedCount < 0) {
            connectedCount = 0;
        }
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
                int hb = (new DataInputStream(socket.getInputStream())).read();
                (new DataOutputStream(socket.getOutputStream())).write(200);
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
