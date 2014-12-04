package guda.httpproxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import guda.httpproxy.util.ArrayUtil;
import guda.httpproxy.util.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpRequestFactory {
    private final static Logger logger = LoggerFactory.getLogger(HttpRequestFactory.class);

    public static HttpRequest createHttpRequest(String url, String cookies) throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        URL httpUrl = new URL(url);
        String host = httpUrl.getHost();
        int port = httpUrl.getPort();
        String requestURI = httpUrl.getPath();
        String requestURL = httpUrl.getPath();
        String query = httpUrl.getQuery();
        if (query != null && query.length() > 0) {
            requestURL += "?" + query;
        }
        if (port > 0 && port < 65536 && port != 80) {
            host += ":" + port;
        }
        httpRequest.setMethod("GET");
        httpRequest.setRequestURI(requestURI);
        httpRequest.setRequestURL(requestURL);
        httpRequest.setHttpProtocol("HTTP/1.1");
        httpRequest.setHeader("Accept", "*" + "/" + "*");
        httpRequest.setHeader("Accept-Language", "zh-cn");
        httpRequest.setHeader("Accept-Encoding", "gzip, deflate");
        httpRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)");
        httpRequest.setHeader("Host", host);
        httpRequest.setHeader("Connection", "Keep-Alive");
        if (cookies != null) {
            httpRequest.setHeader("Cookie", cookies);
        }
        return httpRequest;
    }

    public static HttpRequest read(InputStream in, OutputStream out) throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        byte[] bytes = null;
        byte[] LF = new byte[]{0x0A};
        byte[] CRLF = new byte[]{0x0D, 0x0A};
        if ((bytes = IO.readLine(in)) != null) {
            if (!ArrayUtil.equals(bytes, CRLF) && !ArrayUtil.equals(bytes, LF)) {
                String header = new String(bytes).trim();
                java.util.StringTokenizer st = new java.util.StringTokenizer(header, " ");
                if (st.hasMoreTokens()) {
                    httpRequest.setMethod(st.nextToken());
                }

                if (st.hasMoreTokens()) {
                    String url = st.nextToken();

                    httpRequest.setRequestURL(url);

                    if (url != null && (url = url.trim()).length() > 0) {
                        int k = url.indexOf("?");

                        if (k < 0) {
                            k = url.indexOf("&");
                        }

                        if (k > -1) {
                            httpRequest.setRequestURI(url.substring(0, k));
                            httpRequest.setQueryString(url.substring(k + 1));
                        }
                    } else {
                        url = "/";
                    }
                }
                if (st.hasMoreTokens()) {
                    httpRequest.setHttpProtocol(st.nextToken());
                }
            }
        }

        while ((bytes = IO.readLine(in)) != null) {
            if (ArrayUtil.equals(bytes, CRLF) || ArrayUtil.equals(bytes, LF)) {
                break;
            }
            String header = new String(bytes);
            int k = header.indexOf(":");
            if (k > -1) {
                String name = header.substring(0, k).trim();
                String value = header.substring(k + 1).trim();

                httpRequest.setHeader(name, value);
            }
        }
        httpRequest.setInputStream(in);
        httpRequest.setOutputStream(out);

        return httpRequest;
    }


}
 
