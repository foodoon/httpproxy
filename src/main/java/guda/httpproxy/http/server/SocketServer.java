package guda.httpproxy.http.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public abstract class SocketServer implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String host;
    private int port;

    public SocketServer() {
        this(80);
    }

    public SocketServer(int _port) {
        try {
            this.host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.port = _port;
    }

    public SocketServer(String _host, int _port) {
        this.host = _host;
        this.port = _port;
    }


    public void startup() {
        Thread thread = new Thread(this);

        thread.setDaemon(true);
        thread.start();

        logger.info(this.getClass().getName() + " Start on port " + this.port);
        logger.info("Hit Enter to stop.");

        try {
            System.in.read();
        } catch (Throwable t) {
        }
    }


    public abstract void service(Socket socket);

    public void run() {
        ServerSocket socketServer = null;

        try {
            socketServer = new ServerSocket(this.getPort());

            while (true) {
                this.service(socketServer.accept());
            }
        } catch (IOException e) {
            logger.error("",e);
        } finally {
            if (socketServer != null) {
                try {
                    socketServer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}