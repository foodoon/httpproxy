package guda.httpproxy.http.server.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import guda.httpproxy.http.HttpRequest;
import guda.httpproxy.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Dispatcher {

    private final static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    public String host = null;

    public int port = 80;

    private HttpProxyServer httpProxyServer = null;

    public Dispatcher() {
    }


    public Dispatcher(HttpProxyServer hps, String _host, int _port) {
        this.host = _host;
        this.port = _port;
        this.httpProxyServer = hps;
    }


    public void dispatch(HttpRequest request) {
        //Socket socket = null;

        try {

            //socket = this.createSocket(host, port);

            //InputStream socketInputStream = socket.getInputStream();
           // OutputStream socketOutputStream = socket.getOutputStream();
           // this.request(request, socketOutputStream);

           // HttpResponse httpResponse = HttpResponseFactory.read(socketInputStream, socketOutputStream);
            CloseableHttpClient client = HttpClientBuilder.create().build();
            if("get".equalsIgnoreCase(request.getMethod())){
                HttpGet httpGet = new HttpGet();
                //httpGet.setProtocolVersion(new ProtocolVersion());

                if(request.getHeaders()!=null) {
                    Header[] header = new Header[request.getHeaders().size()];
                    Map<String,String>  headers = request.getHeaders();
                    Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
                    int i = 0;
                    while(iterator.hasNext()){
                        Map.Entry<String, String> next = iterator.next();
                        BasicHeader baseHeader = new BasicHeader(next.getKey(),next.getValue());
                        header[i++] = baseHeader;
                    }
                    httpGet.setHeaders(header);
                }
                httpGet.setURI(new URI(request.getRequestURL()));
                CloseableHttpResponse execute = client.execute(httpGet);
                if(execute.getStatusLine().getStatusCode() == 200){
                    byte[] bytes = EntityUtils.toByteArray(execute.getEntity());
                    logger.info("response:" + new String(bytes));
                    request.getOutputStream().write(bytes);
                    request.getOutputStream().flush();
                }
            }
            //  this.resonse(httpResponse, request.getOutputStream());
        } catch (Exception e) {
            logger.error("Warning" , e);
        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                }
//            }
        }
    }


    private void request(HttpRequest request, OutputStream outputStream) throws IOException {
        logger.info("[request]" + request.toString());
        this.updateHttpRequest(request);
        String headers = request.toString().replaceAll("/r/n", "\r\n");
        logger.info("[header]" + headers);
        outputStream.write(headers.getBytes());
        ContentPump.pumpContentStream(request.getInputStream(), outputStream, request.getContentLength());
    }


    private void resonse(HttpResponse response, OutputStream outputStream) throws IOException {
        logger.info("response" + response.toString());
        this.updateHttpResponse(response);

        String headers = response.toString().replaceAll("/r/n", "\r\n");
        ;

        logger.info("response header" + headers);

        outputStream = new guda.httpproxy.io.PoolOutputStream(new OutputStream[]{outputStream, System.out});

        outputStream.write(headers.getBytes());

        if (response.getHeader("Transfer-Encoding") != null) {
            InputStream inputStream = response.getInputStream();
            ChunkedPump.pumpChunkedStream(inputStream, outputStream);
        } else {
            InputStream inputStream = response.getInputStream();
            ContentPump.pumpContentStream(inputStream, outputStream, response.getContentLength());
        }

        outputStream.flush();
    }


    private Socket createSocket(String host, int port) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(host);
        logger.info("connect to host:["+host + "],port:["+port+"]");
        return new Socket(inetAddress, port);
    }


    private String getHostPort(String _host, int _port) {
        if (_port > 0 && _port < 65536 && _port != 80) {
            _host += ":" + _port;
        }

        return _host;
    }


    private void updateHttpRequest(HttpRequest request) {
        String header = request.getHeader("Host");

        if (header != null) {
            request.setHeader("Host", getHostPort(this.host, this.port));
        }

        header = request.getHeader("Referer");

        if (header != null) {
            header = this.replaceHostPort(header, this.host, this.port);

            request.setHeader("Referer", header);
        }
    }


    private void updateHttpResponse(HttpResponse response) {
        String header = response.getHeader("Location");

        if (header != null) {    //
            header = this.replaceHostPort(header, httpProxyServer.getHost(), httpProxyServer.getPort());

            response.setHeader("Location", header);
        }
    }


    private String replaceHostPort(String srcUrl, String _host, int _port) {
        try {
            URL url = new URL(srcUrl);

            String path = url.getPath();
            String query = url.getQuery();

            if (query != null && query.length() > 0) {
                path += "?" + query;
            }

            if (_port > 0 && _port < 65536 && _port != 80) {
                _host += ":" + _port;
            }
            return ("http://" + _host + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return srcUrl;
    }

    protected String getHost() {
        return this.host;
    }

    protected void setHost(String host) {
        this.host = host;
    }

    protected int getPort() {
        return this.port;
    }

    protected void setPort(int port) {
        this.port = port;
    }

    protected HttpProxyServer getHttpProxyServer() {
        return this.httpProxyServer;
    }

    protected void setHttpProxyServer(HttpProxyServer httpProxyServer) {
        this.httpProxyServer = httpProxyServer;
    }

    public static void main(String[] args) {
        Dispatcher d = new Dispatcher();

        String x = null;

        x = d.replaceHostPort("http://127.0.0.1:8080", "192.168.1.6", 7272);

        System.out.println(x);

        x = d.replaceHostPort("http://127.0.0.1:8080/", "192.168.1.6", 7272);

        System.out.println(x);

        x = d.replaceHostPort("http://127.0.0.1:8080/test.jsp", "192.168.1.6", 7272);

        System.out.println(x);
    }
}