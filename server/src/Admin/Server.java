package Admin;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import Database.SystemDatabase;
import Database.UserDatabase;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.BasicAuthenticator;

public class Server implements Runnable {
    private static final int PORT = 8001;

    private static UserDatabase userDB;
    private static SystemDatabase systemDB;

    private HttpServer APIServer;
    private ConcurrentMap<Integer, ClientThread> clients;
    private ServerSocket socket;

    private volatile boolean active = true;

    private EmailDispatcher emailDispatch;

    public Server() {

    }

    public void configureAPIServer() throws IOException {
        
        //Create an API Server
        APIServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        //Create a list for contexts that we require username and password authentication on.
        ArrayList<HttpContext> secureContexts = new ArrayList<HttpContext>();

        secureContexts.add(APIServer.createContext("/api/login", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                // TODO Add credential checking and failure response, update corresponding
                // client thread
                String responseText = "Logged in: "
                        + exchange.getRequestHeaders().getFirst("Authorization")
                                .substring(exchange.getRequestHeaders().getFirst("Authorization").indexOf(
                                        " ") + 1);
                System.out.println(responseText);
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            }
            exchange.close();
        })));

        APIServer.createContext("/api/recovery", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                // TODO Lookup user email with parsed username from request, utilize email
                // dispatcher
                String responseText = "Recovery email sent to the specified user.";
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            }
            exchange.close();
        }));

        secureContexts.add(APIServer.createContext("/api/write", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                // TODO Finish the BasicAuthenticator
                String responseText = "Data written to database";
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            }
            exchange.close();
        })));

        secureContexts.add(APIServer.createContext("/api/read", (exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                // TODO Finish the BasicAuthenticator, return data to user
                String responseText = "Data read from database";
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            }
            exchange.close();
        })));

        secureContexts.add(APIServer.createContext("/api/logout", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                // TODO Finish the BasicAuthenticator, update corresponding client thread
                String responseText = "Succesfully logged out.";
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            }
            exchange.close();
        })));

        secureContexts.forEach((c) -> c.setAuthenticator(new BasicAuthenticator("pwdProtected") {
            @Override
            public boolean checkCredentials(String username, String password) {
                // TODO Implement checking user db for user and corresponding password
                return true;
            };
        }));

        APIServer.setExecutor(null);
        APIServer.start();
    }

    @Override
    public void run() {
        System.out.println("Starting the Server");
        try {
            configureAPIServer();
            System.out.println("API Server is online at " + APIServer.getAddress());
            socket = new ServerSocket();
            System.out.println("Server Socket is online at " + socket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (active) {
            
        }
        System.out.println("Server thread ending");
    }

    public void stop() {
        APIServer.stop(0);
        active = false;
    }
}