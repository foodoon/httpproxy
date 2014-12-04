package guda.httpproxy.http;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;


public class HttpResponse {
    private String httpProtocol = null;

    private int statusCode = 0;

    private String statusString = null;

    private String contentType = null;

    private Map headers = new LinkedHashMap();

    private InputStream inputStream;

    private OutputStream outputStream;

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public String getHeader(String name) {
        return (String) (this.headers.get(name));
    }

    public int getContentLength() {
        String value = (String) this.headers.get("Content-Length");

        int contentLength = -1;

        if (value == null) {
            return contentLength;
        }

        try {
            contentLength = Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }

        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.getHttpProtocol()).append(" ").append(this.getStatusCode()).append(" ").append(this.getStatusString()).append("/r/n");

        for (Iterator iterator = this.headers.keySet().iterator(); iterator.hasNext(); ) {
            String name = (String) (iterator.next());

            buf.append(name).append(": ").append(this.headers.get(name)).append("/r/n");
        }

        buf.append("/r/n");

        return buf.toString();
    }
}



