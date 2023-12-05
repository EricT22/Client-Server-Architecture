package Client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class APIRequest {
    private static String serverIP;
    private RequestScheme scheme;
    private String payload;
    private CompletableFuture<HttpResponse<String>> response = null;
    private HttpRequest request = null;
    private static int sessionID = -1;

    public static void setIP(String ip) {
        serverIP = ip;
    }

    public static void setSessionID(int i){
        sessionID = i;
    }

    public APIRequest(RequestScheme iScheme, String data) {
        scheme = iScheme;
        payload = data;
    }

    public static APIRequest makeRequest(RequestScheme scheme, String data) {
        // TODO replace YTWRta with username:password encoded into base64
        APIRequest apiReq = new APIRequest(scheme, data);

        switch (scheme) {
            case LOGIN:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create("http://" + serverIP + "/api/login"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(apiReq.getPayload().getBytes()))
                        .header("Session", Integer.toString(sessionID))
                        .build();
                break;
            case ACCT_RECOVERY:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/recovery"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .header("Session", Integer.toString(sessionID))
                        .build();
                break;
            case READ_DATA:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/read"))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(apiReq.getPayload().getBytes()))
                        .header("Session", Integer.toString(sessionID))
                        .build();
                break;
            case WRITE_DATA:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/write"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(apiReq.getPayload().getBytes()))
                        .header("Session", Integer.toString(sessionID))
                        .build();
                break;
            case LOGOUT:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/logout"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(apiReq.getPayload().getBytes()))
                        .header("Session", Integer.toString(sessionID))
                        .build();
                break;
            case REGISTER:

                break;
            default:
                break;
        }
        return apiReq;
    }

    private String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "";
    }

    public boolean execute() throws InterruptedException, ExecutionException {
        System.out.println(request.toString());
        if (request == null) {
            return false;
        }
        response = HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
        if (response.get().statusCode() != 200) {
            System.out.println(response.get().body());
            return false;
        }
        switch (scheme) {
            case LOGIN:
                System.out.println("Sucessfully logged in with credentials: " + new String(Base64.getDecoder()
                        .decode(response.get().body().substring(response.get().body().indexOf(": ") + 2))));
                break;
            default:
                System.out.println(response.get().body());
                break;
        }
        return true;
    }

    public String getResponse() {
        return "";
    }
}
