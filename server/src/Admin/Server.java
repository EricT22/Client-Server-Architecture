package Admin;

import java.util.concurrent.ConcurrentMap;

import Database.SystemDatabase;
import Database.UserDatabase;

import java.net.ServerSocket;
import com.sun.net.httpserver.HttpServer;

public class Server implements Runnable {
    private static final int PORT = 0; // idk change later

    private static UserDatabase userDB;
    private static SystemDatabase systemDB;

    private HttpServer APIServer;
    private ConcurrentMap<Integer, ClientThread> clients;
    private ServerSocket socket;

    private EmailDispatcher emailDispatch;

    private int nextID;

    public Server() {

    }

    public void configureServer() {

    }

    @Override
    public void run() {

    }

    public void stop() {

    }
}