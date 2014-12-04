package guda.httpproxy.http.server.proxy;

import java.io.IOException;
import java.net.Socket;

import guda.httpproxy.http.HttpRequest;
import guda.httpproxy.http.HttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpProxy implements Runnable {
    private final  Logger logger = LoggerFactory.getLogger(getClass());
    private Socket socket = null;
    private HttpProxyServer httpProxyServer = null;


    public HttpProxy(HttpProxyServer hps, Socket s) {
        this.socket = s;
        this.httpProxyServer = hps;

        Thread thread = new Thread(this);

        thread.setDaemon(true);

        thread.start();
    }


    protected HttpProxyServer getHttpProxyServer() {
        return this.httpProxyServer;
    }

    protected void setHttpProxyServer(HttpProxyServer httpProxyServer) {
        this.httpProxyServer = httpProxyServer;
    }

    protected Socket getSocket() {
        return this.socket;
    }

    protected void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            logger.info("[Thread: " + Thread.currentThread().getName() + "] Start ......");
            HttpRequest httpRequest = HttpRequestFactory.read(socket.getInputStream(), socket.getOutputStream());
            String host = httpProxyServer.getProxyHost();
            int port = httpProxyServer.getProxyPort();

            new Dispatcher(httpProxyServer, host, port).dispatch(httpRequest);

            logger.info("[Thread: " + Thread.currentThread().getName() + "]: End: " + httpRequest.getRequestURL());
        } catch (IOException e) {
            logger.error("Warnning:",e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}