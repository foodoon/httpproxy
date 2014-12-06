package guda.httpproxy.model;

import java.util.Map;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpRequest {


   public static final String User_Agent = "User-Agent";
    public static final String Host = "Host";
    public static final String Accept = "Accept";
    public static final String Accept_Language = "Accept-Language";
    public static final String Accept_Charset = "Accept-Charset";
    public static final String Accept_Encoding = "Accept-Encoding";
    public static final String Connection = "Connection";

    private String status;

    private Map<String,String> header;
    private String method;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
