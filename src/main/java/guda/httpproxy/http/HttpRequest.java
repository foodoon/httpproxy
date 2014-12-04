package guda.httpproxy.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class HttpRequest {
    private String method = null;
    private String requestURI = null;
    private String requestURL = null;
    private String queryString = null;
    private String httpProtocol = null;

    private Map<String,String>  headers = new LinkedHashMap<String,String> ();

    private InputStream inputStream;

    private OutputStream outputStream;


    public Map<String,String> getHeaders() {
        return headers;
    }


    public void setHeaders(Map headers) {
        this.headers = headers;
    }


    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }


    public String getHeader(String name) {
        return (String) (this.headers.get(name));
    }


    public String getHttpProtocol() {
        return httpProtocol;
    }


    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }


    public String getMethod() {
        return method;
    }


    public void setMethod(String method) {
        this.method = method;
    }


    public String getQueryString() {
        return queryString;
    }


    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }


    public String getRequestURI() {
        return requestURI;
    }


    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }


    public String getRequestURL() {
        return requestURL;
    }


    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }


    public String getContentType() {
        return (String) this.headers.get("Content-Type");
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

    public String getRemoteHost() {
        String host = (String) this.headers.get("Host");

        if (host != null) {
            int k = host.indexOf(":");

            if (k > -1) {
                return host.substring(0, k);
            }

            return host;
        }

        return null;
    }

    public int getRemotePort() {
        String host = (String) this.headers.get("Host");

        int port = 80;

        if (host != null) {
            int k = host.indexOf(":");

            if (k > -1) {
                try {
                    port = Integer.parseInt(host.substring(k + 1));
                } catch (NumberFormatException e) {
                }

                return port;
            }

            return port;
        }

        return port;
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

        buf.append(this.method).append(" ").append(this.getRequestURL()).append(" ").append(this.getHttpProtocol()).append("/r/n");

        for (Iterator iterator = this.headers.keySet().iterator(); iterator.hasNext(); ) {
            String name = (String) (iterator.next());

            buf.append(name).append(": ").append(this.headers.get(name)).append("/r/n");
        }

        buf.append("/r/n");

        return buf.toString();
    }
}
