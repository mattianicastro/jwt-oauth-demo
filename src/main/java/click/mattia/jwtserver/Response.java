package click.mattia.jwtserver;

public class Response {
    private ResponseCode responseCode;
    private String response;

    public Response(ResponseCode responseCode, String response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        if(response==null) {
            return "";
        }
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


}
