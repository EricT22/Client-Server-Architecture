package Admin;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import Database.SystemDatabase;
import Database.UserDatabase;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.BasicAuthenticator;

public class Server extends Thread {
    private static final int API_PORT = 8080;
    private static final int PORT = 8000;
    private static final int MAX_CLIENTS = 2;

    private static UserDatabase userDB;
    private static SystemDatabase systemDB;

    private HttpServer APIServer;
    private static ConcurrentMap<Integer, ClientThread> clientMap;
    public static ConcurrentMap<Integer, Boolean> sessionRegistry;
    private ServerSocket socket;

    private final AtomicBoolean active = new AtomicBoolean(true);

    private EmailDispatcher emailDispatch;

    public Server() throws IOException {
    }

    public void configureAPIServer() throws IOException {

        // Create a list for contexts that we require username and password
        // authentication on.
        ArrayList<HttpContext> secureContexts = new ArrayList<HttpContext>();

        secureContexts.add(APIServer.createContext("/api/login", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                int sessionID = Integer.parseInt(exchange.getRequestHeaders().getFirst("Session"));
                if (clientMap.get(sessionID) == null) {
                    exchange.sendResponseHeaders(403, -1);
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
                    System.out.println("Session " + sessionID + " Logged In");
                }
            }
            exchange.close();
        })));

        APIServer.createContext("/api/recovery", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                int sessionID = Integer.parseInt(exchange.getRequestHeaders().getFirst("Session"));
                if (clientMap.get(sessionID) == null) {
                    exchange.sendResponseHeaders(403, -1);
                } else {
                    // TODO Lookup user email with parsed username from request, utilize email
                    // dispatcher
                    String responseText = "Recovery email sent to the specified user.";
                    exchange.sendResponseHeaders(200, responseText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseText.getBytes());
                    output.flush();
                    System.out
                            .println("Session " + sessionID + " Requested Acct Recovery "
                                    + new String(exchange.getRequestBody().readAllBytes()));
                }
            }
            exchange.close();
        }));

        APIServer.createContext("/api/register", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                int sessionID = Integer.parseInt(exchange.getRequestHeaders().getFirst("Session"));
                if (clientMap.get(sessionID) == null) {
                    exchange.sendResponseHeaders(403, -1);
                } else {
                    String raw = new String(exchange.getRequestBody().readAllBytes());
                    System.out.println("Session " + sessionID + " Registered New Account" + raw);
                    String responseText = "Account Created Successfully";
                    exchange.sendResponseHeaders(200, responseText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseText.getBytes());
                    output.flush();
                }
            }
            exchange.close();
        }));

        secureContexts.add(APIServer.createContext("/api/write", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                int sessionID = Integer.parseInt(exchange.getRequestHeaders().getFirst("Session"));
                if (clientMap.get(sessionID) == null) {
                    exchange.sendResponseHeaders(403, -1);
                } else {
                    System.out.println("Session " + sessionID + " Wrote to Database: "
                            + new String(exchange.getRequestBody().readAllBytes()));
                    String responseText = "Data written to database";
                    exchange.sendResponseHeaders(200, responseText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseText.getBytes());
                    output.flush();
                }
            }
            exchange.close();
        })));

        secureContexts.add(APIServer.createContext("/api/read", (exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                int sessionID = Integer.parseInt(exchange.getRequestHeaders().getFirst("Session"));
                if (clientMap.get(sessionID) == null) {
                    exchange.sendResponseHeaders(403, -1);
                } else {
                    // TODO Finish the BasicAuthenticator, return data to user
                    String responseText = "Data read from database";
                    exchange.sendResponseHeaders(200, responseText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseText.getBytes());
                    output.flush();
                    System.out.println("Session " + sessionID + " Read From Database");
                }
            }
            exchange.close();
        })));

        secureContexts.add(APIServer.createContext("/api/logout", (exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
            } else {
                int sessionID = Integer.parseInt(exchange.getRequestHeaders().getFirst("Session"));
                if (clientMap.get(sessionID) == null) {
                    exchange.sendResponseHeaders(403, -1);
                } else {
                    String responseText = "Succesfully logged out.";
                    exchange.sendResponseHeaders(200, responseText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseText.getBytes());
                    output.flush();
                    System.out.println("Session " + sessionID + " Logged Out");
                }
            }
            exchange.close();
        })));

        secureContexts.forEach((c) -> c.setAuthenticator(new BasicAuthenticator("pwdProtected") {
            @Override
            public boolean checkCredentials(String username, String password) {
                if (username.trim().equals("")) {
                    return false;
                }
                return true;
            };
        }));
        APIServer.setExecutor(null);
    }

    @Override
    public void run() {
        System.out.println("Starting the Server");

        clientMap = new ConcurrentHashMap<Integer, ClientThread>();
        sessionRegistry = new ConcurrentHashMap<Integer, Boolean>();

        for (int i = 0; i < MAX_CLIENTS; i++) {
            sessionRegistry.put(i, false);
        }

        try {
            APIServer = HttpServer.create(new InetSocketAddress(API_PORT), 0);
            configureAPIServer();
            APIServer.start();
            System.out.println("API Server is online at " + APIServer.getAddress());
            socket = new ServerSocket(PORT);
            System.out.println("Server Socket is online at " + socket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        active.set(true);

        while (active.get()) {
            try {
                Socket cSocket = socket.accept();
                DataOutputStream toClient = new DataOutputStream(cSocket.getOutputStream());
                // The client will take -1 to mean that their connection request rejected.
                int sessionID = getNextAvailableSession();
                toClient.writeInt(sessionID);
                System.out
                        .println(sessionID != -1 ? "Session Created ID " + sessionID : "Server Full, Session Rejected");
                if (sessionID != -1) {
                    ClientThread cThread = new ClientThread(sessionID, cSocket);
                    clientMap.put(sessionID, cThread);
                    cThread.start();
                }
            } catch (Exception e) {
            }
        }
        System.out.println("Server thread ending");
    }

    public int getNextAvailableSession() {
        System.out.println("Searching for a new Session ID w/ Max of " + MAX_CLIENTS);
        boolean found = false;
        int nextSession = -1;
        for (int i = 0; i < sessionRegistry.size() && !found; i++) {
            if (!sessionRegistry.get(i)) {
                nextSession = i;
                found = true;
                sessionRegistry.put(i, true);
            }
        }
        System.out.println(!found ? "Rejecting Connection" : "Allocating a new Session ID " + nextSession);
        return nextSession;
    }

    public int getActiveUsers() {
        return ClientThread.getConnectionCount();
    }

    public void shutdownServer() throws Exception {
        System.out.println("Attempting to stop server.");
        socket.close();
        clientMap.forEach((key, value) -> {
            value.stopClient();
        });
        active.set(false);
        APIServer.stop(0);
        System.out.println("Server stopped.");
    }
}