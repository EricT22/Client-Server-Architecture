package Client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class APIRequest {
    private static String serverIP;
    private RequestScheme scheme;
    private String payload;
    private CompletableFuture<HttpResponse<String>> response = null;
    private HttpRequest request = null;

    public static void setIP(String ip) {
        serverIP = ip;
    }

    public static APIRequest makeRequest(RequestScheme scheme, String data) {
        APIRequest apiReq = new APIRequest();
        switch (scheme) {
            case LOGIN:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create("http://" + serverIP + "/api/login"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .build();
                break;
            case ACCT_RECOVERY:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/recovery"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .build();
                break;
            case READ_DATA:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/login"))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                break;
            case WRITE_DATA:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/login"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .build();
                break;
            case LOGOUT:
                apiReq.request = HttpRequest.newBuilder()
                        .uri(URI.create(serverIP + "/api/login"))
                        .method("POST", HttpRequest.BodyPublishers.noBody())
                        .build();
                break;
            default:
                break;
        }
        return apiReq;
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
        System.out.println(response.get().body());
        return response.get().statusCode() == 200;
    }

    public String getResponse() {
        return "";
    }
}
