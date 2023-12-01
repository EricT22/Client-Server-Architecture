package Admin;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import Database.SystemDatabase;
import Database.UserDatabase;

import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.BasicAuthenticator;

public class Server implements Runnable {
    private static final int PORT = 0; // idk change later

    private static UserDatabase userDB;
    private static SystemDatabase systemDB;

    private HttpServer APIServer;
    private ConcurrentMap<Integer, ClientThread> clients;
    private ServerSocket socket;

    private EmailDispatcher emailDispatch;

    private int nextID;

    public Server() throws Exception {
        configureServer();
    }

    public void configureServer() throws IOException {
        ArrayList<HttpContext> contexts = new ArrayList<HttpContext>();
        APIServer = HttpServer.create(new InetSocketAddress(PORT),0);

        contexts.forEach((c) -> c.setAuthenticator(new BasicAuthenticator() {

        }));
    }

    @Override
    public void run() {

    }

    public void stop() {

    }
}