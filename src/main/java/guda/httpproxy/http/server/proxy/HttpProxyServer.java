package guda.httpproxy.http.server.proxy;

import java.net.Socket;

import guda.httpproxy.http.server.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpProxyServer extends SocketServer {
    private final  Logger logger = LoggerFactory.getLogger(getClass());
    private String proxyHost;

    private int proxyPort;


    public static void main(String[] args) {
        new HttpProxyServer(7272, "localhost", 8081).startup();
    }

    public HttpProxyServer() {
    }


    public HttpProxyServer(int _port) {
        super(_port);
    }


    public HttpProxyServer(String _host, int _port) {
        super(_host, _port);
    }


    public HttpProxyServer(int _port, String _proxyHost, int _proxyPort) {
        super(_port);

        this.proxyHost = _proxyHost;
        this.proxyPort = _proxyPort;
    }


    public void service(Socket socket) {
        logger.info(" new service......");
        new HttpProxy(this, socket);
    }


    public String getProxyHost() {
        return proxyHost;
    }


    public void setProxyHost(String _proxyHost) {
        this.proxyHost = _proxyHost;
    }


    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int _proxyPort) {
        this.proxyPort = _proxyPort;
    }
}
