package Client;

import java.net.http.HttpRequest;

public class APIRequest {
    private String serverIP;
    private RequestScheme scheme;
    private String payload;
    private String response;
    private HttpRequest request;

    public void setIP(String ip){

    }

    public APIRequest makeRequest(RequestScheme scheme, String request){
        // delete this when done
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString(){
        return "";
    }

    public boolean execute(){
        return false;
    }

    public String getResponse(){
        return "";
    }
}
