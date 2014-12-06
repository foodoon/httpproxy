package guda.httpproxy.model;

import java.util.Map;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpResponse {

    private Map<String,String> header;

    private String body;

    private byte[] responseStream;

    public DeviceHttpResponse(byte[] response){
        responseStream = response;
    }

    public byte[] getResponseStream() {
        return responseStream;
    }

    public void setResponseStream(byte[] responseStream) {
        this.responseStream = responseStream;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
